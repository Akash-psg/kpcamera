package com.iolite.kpcamera.attendance.service.impl;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.iolite.kpcamera.attendance.configure.RestTemplateConfiguration;
import com.iolite.kpcamera.attendance.dao.AttendanceDao;
import com.iolite.kpcamera.attendance.dao.MegboxStudentDetailsDao;
import com.iolite.kpcamera.attendance.dao.StudentDetailsDao;
import com.iolite.kpcamera.attendance.model.AlertsEvent;
import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.FCMReqDto;
import com.iolite.kpcamera.attendance.model.StudentAttendanceDetails;
import com.iolite.kpcamera.attendance.model.Top1;
import com.iolite.kpcamera.attendance.model.responses.RecongnizedStudentResDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceDetailsResponse;
import com.iolite.kpcamera.attendance.service.AttendanceService;
import com.iolite.kpcamera.attendance.service.MegBoxAttendanceService;
import com.iolite.kpcamera.attendance.thread.ImagesUnbindingThread;
import com.iolite.kpcamera.attendance.util.Base64Coder;
import com.iolite.kpcamera.attendance.util.CameraUtils;
import com.iolite.kpcamera.attendance.util.OrganizationUtil;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	AttendanceDao dao;

	@Autowired
	MegBoxAttendanceService service;

	List<String> duplicateData = new ArrayList<String>();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MegboxStudentDetailsDao megboxStudentDetailsDao;

	@Autowired
    AttendanceService attendanceService;
	
	
	@Value("${andriod.attendance.submit.url}")
	private String submitAttendanceUrl;

	static final Logger lg = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	public static final String getYearForWorkDiaryByUserId = "select year from academic_year "
			+ " inner join academic_duration on academic_duration.academic_year_id = academic_year.id "
			+ " inner join emp_location ON emp_location.id = academic_duration.emp_location_id "
			+ " inner join department on department.emp_location_id = emp_location.id "
			+ " where department.id in (select ifnull(employee.department_id, ifnull(guest_faculty.department_id, users.department_id)) "
			+ " from users " + " left join employee ON employee.id = users.employee_id and employee.active=1 "
			+ " left join guest_faculty ON guest_faculty.id = users.guest_id and guest_faculty.active=1 where users.id=:userId) "
			+ " and  :attDate between academic_duration.start_date and academic_duration.end_date and academic_duration.is_active=1";

	public static final String getWorkActivityId = "select id from work_diary_activity where is_active=1 and is_teaching=1";

	public static String WORK_DIARY_DUPLICATE_CHECK = "select employee_work_diary_details.work_diary_id, employee_work_diary.user_id from employee_work_diary_details "
			+ "inner join employee_work_diary ON employee_work_diary.id = employee_work_diary_details.work_diary_id "
			+ "where employee_work_diary_details.from_time = :fromTime "
			+ "and employee_work_diary_details.to_time = :toTime "
			+ "and employee_work_diary_details.activity_id = :activityId "
			+ "and  employee_work_diary.user_id = :userId " + "and employee_work_diary.is_active=1 "
			+ "and employee_work_diary_details.is_active =1 " + "and employee_work_diary.date = :date "
			+ "and employee_work_diary.academic_year =:academicYear";
	

//	@Override
//	public StudentAttendanceDetailsResponse getStudentAttendance(String jobId, Date teacherSubmittedTime, Date attendanceCapturedEndTime) {
//		StudentAttendanceDetailsResponse response = new StudentAttendanceDetailsResponse();
//		String[] jobData = jobId.split("@");
//		String roomId = jobData[0];
//		String subjectId = jobData[1];
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		String startDate = sdf.format(new Date());
//		try {
//			List<StudentApkDtoResponse> daoList = dao.getStudentAttendance(roomId, subjectId, startDate);
//			StudentApkDtoResponse studentApkDtoResponse = daoList.get(0);
//			if (CollectionUtils.isNotEmpty(daoList)) {
//				Map<String, String> studentCameraCapturedMap = new HashMap<String, String>();
//				Map<String, AttendanceReqestModel> map = convertStudentReqDetailsToMap(daoList);
//				List<String> duplicateStudentList = new ArrayList<String>();
//				List<RecongnizedStudentResDto> facesResDtoList = service.getRecongnizedData(
//						studentApkDtoResponse.getMegBoxIp(), teacherSubmittedTime, attendanceCapturedEndTime);
//				List<AttendanceReqestModel> attendanceReqList = new ArrayList<AttendanceReqestModel>();
//				if (facesResDtoList != null && !CollectionUtils.isEmpty(facesResDtoList)) {
//					for (RecongnizedStudentResDto facesResDto : facesResDtoList) {
//						List<AlertsEvent> alertsEvent = facesResDto.getData().getAlertsEvent();
//						// Prepared Recognized Student details
//						for (AlertsEvent alert : alertsEvent) {
//							Top1 top1 = alert.getTop1();
//							if (top1.getGroup().get(0).getGroupName()
//									.equalsIgnoreCase(top1.getAlertGroup().get(0).getGroupName())
//									&& map.containsKey(
//											top1.getDescription() + "-" + top1.getGroup().get(0).getGroupName())
//									&& (!duplicateStudentList.contains(
//											top1.getDescription() + "-" + top1.getGroup().get(0).getGroupName()))) {
//								AttendanceReqestModel details = map
//										.get(top1.getDescription() + "-" + top1.getGroup().get(0).getGroupName());
//								details.setIsRecongStudent("100");
//								studentCameraCapturedMap.put(details.getRegNo(), details.getIsRecongStudent());
//								attendanceReqList.add(details);
//								duplicateStudentList
//										.add(top1.getDescription() + "-" + top1.getGroup().get(0).getGroupName());
//							}
//						}
//
//					}
//					for (Map.Entry<String, AttendanceReqestModel> data : map.entrySet()) {
//						AttendanceReqestModel details = map.get(data.getKey());
//						if ((!duplicateStudentList.contains(data.getKey()))) {
//							details.setIsRecongStudent("200");
//							studentCameraCapturedMap.put(details.getRegNo(), details.getIsRecongStudent());
//							attendanceReqList.add(details);
//							duplicateStudentList.add(data.getKey());
//						}
//					}
//				} else {
//					for (Map.Entry<String, AttendanceReqestModel> data : map.entrySet()) {
//						AttendanceReqestModel details = map.get(data.getKey());
//						if ((!duplicateStudentList.contains(data.getKey()))) {
//							studentCameraCapturedMap.put(details.getRegNo(), details.getIsRecongStudent());
//							details.setIsRecongStudent("200");
//							attendanceReqList.add(details);
//							duplicateStudentList.add(data.getKey());
//						}
//					}
//				}
//				if (duplicateStudentList != null && !duplicateStudentList.isEmpty()) {
//					Map<Integer, String> studentTokenDetails = megboxStudentDetailsDao.getMegboxStudentDetails("1", studentApkDtoResponse.getMegBoxIp());
//					ExecutorService executor = Executors.newFixedThreadPool(25);
//					Runnable studentThread = new ImagesUnbindingThread(duplicateStudentList,
//							studentApkDtoResponse.getMegBoxIp(), studentTokenDetails);
//					executor.execute(studentThread);
//					executor.shutdown();
//					while (!executor.isTerminated()) {
//					}
//					lg.info("Finished all threads!");
//				}
//				if (CollectionUtils.isNotEmpty(attendanceReqList)) {
//					List<StudentAttendanceDetails> studentAttendanceDetailsList = new ArrayList<StudentAttendanceDetails>();
//					StudentAttendanceDetails studentAttendanceDetails = null;
//					for (AttendanceReqestModel dto : attendanceReqList) {
//						studentAttendanceDetails = new StudentAttendanceDetails();
//						studentAttendanceDetails.setRegNo(dto.getRegNo());
//						studentAttendanceDetails.setStudentId(Integer.parseInt(dto.getStudentId()));
//						studentAttendanceDetails.setIsRecongStudent(dto.getIsRecongStudent());
//						studentAttendanceDetails.setStudentName(dto.getStudentName());
//						studentAttendanceDetails.setIsCocurricularLeave(dto.getIs_on_leave());
//						studentAttendanceDetails.setIsOnleave(dto.getIs_on_leave());
//						if (CollectionUtils.isEmpty(studentAttendanceDetailsList)) {
//							response.setClassId(dto.getClassId());
//							response.setPeriodId(dto.getPeriodId());
//							response.setPeriodName(dto.getPeriodName());
//							response.setClassName(dto.getClassName());
//							response.setTeacherName(dto.getTeacherName());
//							response.setSubName(dto.getSubName());
//							response.setStartTime(dto.getStartTime());
//							response.setEndTime(dto.getEndTime());
//							response.setRoomId(Integer.parseInt(dto.getRoomId()));
//							response.setSubjectId(Integer.parseInt(dto.getSubId()));
//							response.setTecherId(dto.getTeacherId());
//							response.setActivityId(dto.getActivityId());
//							response.setAttDate(dto.getAttDate());
//							response.setAttId(dto.getAttId());
//							response.setAttTypeId(dto.getAttTypeId());
//							response.setBatchId(dto.getBatchId());
//							response.setDateandtime(dto.getDateandtime());
//							response.setHoursheld(dto.getHoursheld());
//							response.setIsTT(dto.getIsTT());
//							response.setRemarks(dto.getRemarks());
//							response.setSubstitueClass(dto.getSubstitueClass());
//							response.setTotalHours(dto.getTotalHours());
//							response.setUserId(dto.getUserId());
//							response.setVersion(dto.getVersion());
//							response.setWorkDiaryAcademicYear(dto.getWorkDiaryAcademicYear());
//							response.setWorkDiaryActivityId(dto.getWorkDiaryActivityId());
//						}
//						studentAttendanceDetailsList.add(studentAttendanceDetails);
//					}
//					response.setStudentAttendanceDetails(studentAttendanceDetailsList);
//				}
//
//				if (response != null) {
//					getNewGetAttId(response);
//				}
//				
//				List<AttendanceReqestModel> attendanceList = studentDao.getTeacherMarkedAttendanceData(studentApkDtoResponse);
//				lg.info("attendanceList {}",attendanceList);
//				if(studentCameraCapturedMap != null && !studentCameraCapturedMap.isEmpty()) {
//					service.convertCSVFile(attendanceList, studentCameraCapturedMap);
//				}
//				
//			}
//		} catch (Exception e) {
//
//		}
//		return response;
//	}
//	
//	
	
	
	// Best (highest score) capture for one student in one session
	private static class EvidenceRow {
	    String studentId;
	    double score;
	    String recognizedAt;
	    String captureUrl;
	    String fullUrl;
	    String trackId;
	}

	private static String toFullUrl(String raw, String megBoxIp) {
	    if (raw == null) {
	        return null;
	    }
	    // Same URL handling as getStudentAttendanceForUI (leading "." -> https://box-ip)
	    return raw.startsWith(".") ? "https://" + megBoxIp + raw.substring(1) : raw;
	}

	// ALL the getters I couldn't confirm are in this one method.
	private void collectBestEvidence(Map<String, EvidenceRow> best, String alertKey,
	        AlertsEvent alert, Top1 top1, String megBoxIp) {
	    try {
	        double score = Double.parseDouble(String.valueOf(top1.getSearchScore()));
	        EvidenceRow current = best.get(alertKey);
	        if (current == null || score > current.score) {
	            EvidenceRow row = new EvidenceRow();
	            row.studentId = top1.getDescription();
	            row.score = score;
	            row.recognizedAt = String.valueOf(alert.getTimestamp());
	            row.captureUrl = toFullUrl(alert.getCaptureImageURL(), megBoxIp);
	            row.fullUrl = toFullUrl(alert.getCaptureFullImageURL(), megBoxIp);
	            row.trackId = String.valueOf(alert.getTrackId());
	            best.put(alertKey, row);
	        }
	    } catch (Exception e) {
	        // Evidence must never break attendance
	        lg.warn("Could not collect evidence for {}: {}", alertKey, e.getMessage());
	    }
	}
	
	//RoomId - cameraIp Modification
	@Override
	public StudentAttendanceDetailsResponse
	        getStudentAttendance(
	                String jobId,
	                Date teacherSubmittedTime,
	                Date attendanceCapturedEndTime) {

	    StudentAttendanceDetailsResponse response =
	            new StudentAttendanceDetailsResponse();

	    String[] jobData = jobId.split("@");
	    String cameraIp  = jobData[0];
	    String startTime = jobData[1];

	    SimpleDateFormat sdf =
	            new SimpleDateFormat("dd/MM/yyyy");
	    String startDate = sdf.format(new Date());

	    lg.info("=== getStudentAttendance START ===");
	    lg.info("jobId: {}, cameraIp: {}, startTime: {}",
	            jobId, cameraIp, startTime);
	    lg.info("teacherSubmittedTime: {}", teacherSubmittedTime);
	    lg.info("attendanceCapturedEndTime: {}",
	            attendanceCapturedEndTime);

	    try {
	        List<StudentApkDtoResponse> daoList =
	                dao.getStudentsByCamAndTime(
	                        cameraIp, startTime, startDate);

	        lg.info("daoList size: {}",
	                daoList == null ? 0 : daoList.size());

	        if (CollectionUtils.isEmpty(daoList)) {
	            lg.warn("No students found for cameraIp: {}, " +
	                    "startTime: {}, startDate: {}",
	                    cameraIp, startTime, startDate);
	            return response;
	        }

	        StudentApkDtoResponse studentApkDtoResponse =
	                daoList.get(0);

	        lg.info("megBoxIp from DB: {}",
	                studentApkDtoResponse.getMegBoxIp());

	        Map<String, String> studentCameraCapturedMap =
	                new HashMap<>();
	        Map<String, AttendanceReqestModel> map =
	                convertStudentReqDetailsToMap(daoList);

	        lg.info("=== MAP KEYS (size: {}) ===", map.size());
	        for (String key : map.keySet()) {
	            lg.info("Map key: {}", key);
	        }

	        List<String> duplicateStudentList =
	                new ArrayList<>();
	        Map<String, EvidenceRow> bestEvidence = new HashMap<>();

	        List<RecongnizedStudentResDto> facesResDtoList =
	                service.getRecongnizedData(
	                        studentApkDtoResponse.getMegBoxIp(),
	                        teacherSubmittedTime,
	                        attendanceCapturedEndTime);

	        List<AttendanceReqestModel> attendanceReqList =
	                new ArrayList<>();

	        int totalAlerts = 0;
	        if (facesResDtoList != null) {
	            for (RecongnizedStudentResDto dto :
	                    facesResDtoList) {
	                if (dto.getData() != null &&
	                        dto.getData().getAlertsEvent() != null) {
	                    totalAlerts +=
	                            dto.getData()
	                               .getAlertsEvent()
	                               .size();
	                }
	            }
	        }
	        lg.info("Total alert events from MegBox: {}",
	                totalAlerts);

	        if (facesResDtoList != null &&
	                !CollectionUtils.isEmpty(facesResDtoList) &&
	                totalAlerts > 0) {

	            for (RecongnizedStudentResDto facesResDto :
	                    facesResDtoList) {

	                if (facesResDto.getData() == null ||
	                        CollectionUtils.isEmpty(
	                            facesResDto.getData()
	                                       .getAlertsEvent())) {
	                    continue;
	                }

	                List<AlertsEvent> alertsEvent =
	                        facesResDto.getData().getAlertsEvent();

	                for (AlertsEvent alert : alertsEvent) {
	                    Top1 top1 = alert.getTop1();

	                    String description =
	                            top1.getDescription();
	                    String groupName =
	                            top1.getGroup().get(0)
	                                .getGroupName();
	                    String alertGroupName =
	                            top1.getAlertGroup().get(0)
	                                .getGroupName();

	                    String alertKey =
	                            description + "|" + groupName;

	                    lg.info("Alert → description: {}, " +
	                            "groupName: {}, alertGroupName: {}",
	                            description, groupName,
	                            alertGroupName);
	                    lg.info("alertKey: {}, mapContainsKey: {}",
	                            alertKey,
	                            map.containsKey(alertKey));
	                    if (groupName.equalsIgnoreCase(alertGroupName)
	                            && map.containsKey(alertKey)) {
	                        collectBestEvidence(bestEvidence, alertKey, alert, top1,
	                                studentApkDtoResponse.getMegBoxIp());
	                    }

	                    if (groupName.equalsIgnoreCase(
	                                alertGroupName)
	                            && map.containsKey(alertKey)
	                            && !duplicateStudentList
	                                .contains(alertKey)) {

	                        AttendanceReqestModel details =
	                                map.get(alertKey);
	                        details.setIsRecongStudent("100");
	                        lg.info("✅ STUDENT RECOGNIZED " +
	                                "AS PRESENT: {}", description);
	                        studentCameraCapturedMap.put(
	                                details.getRegNo(),
	                                details.getIsRecongStudent());
	                        attendanceReqList.add(details);
	                        duplicateStudentList.add(alertKey);
	                    }
	                }
	            }

	            // Mark unrecognized as absent
	            for (Map.Entry<String, AttendanceReqestModel>
	                    data : map.entrySet()) {
	                AttendanceReqestModel details =
	                        map.get(data.getKey());
	                if (!duplicateStudentList.contains(
	                        data.getKey())) {
	                    if (!"300".equals(
	                            details.getIsRecongStudent())) {
	                        details.setIsRecongStudent("200");
	                    }
	                    lg.info("❌ STUDENT MARKED AS ABSENT: {}",
	                            details.getStudentId());
	                    studentCameraCapturedMap.put(
	                            details.getRegNo(),
	                            details.getIsRecongStudent());
	                    attendanceReqList.add(details);
	                    duplicateStudentList.add(data.getKey());
	                }
	            }

	        } else {
	            // No alerts — all absent or unknown
	            lg.info("No alerts from MegBox — marking " +
	                    "all students absent/unknown");
	            for (Map.Entry<String, AttendanceReqestModel>
	                    data : map.entrySet()) {
	                AttendanceReqestModel details =
	                        map.get(data.getKey());
	                if (!duplicateStudentList.contains(
	                        data.getKey())) {
	                    if (!"300".equals(
	                            details.getIsRecongStudent())) {
	                        details.setIsRecongStudent("200");
	                    }
	                    lg.info("❌ STUDENT MARKED AS ABSENT: {}",
	                            details.getStudentId());
	                    studentCameraCapturedMap.put(
	                            details.getRegNo(),
	                            details.getIsRecongStudent());
	                    attendanceReqList.add(details);
	                    duplicateStudentList.add(data.getKey());
	                }
	            }
	        }
	     // Save best capture per student as proof (never affects the main flow)
	        try {
	            List<Object[]> evidenceRows = new ArrayList<>();
	            for (EvidenceRow r : bestEvidence.values()) {
	                evidenceRows.add(new Object[] {
	                        r.studentId, cameraIp, startTime, r.recognizedAt,
	                        r.score, r.captureUrl, r.fullUrl, r.trackId });
	            }
	            dao.saveRecognitionEvidence(evidenceRows);
	            lg.info("Recognition evidence saved: {} students", evidenceRows.size());
	        } catch (Exception evEx) {
	            lg.error("Failed to save recognition evidence: {}", evEx.getMessage());
	        }

	        // ← NEW: Save absent students backup
	        // Uses cameraIp + startTime instead of roomId/subjectId
	        try {
	            List<AttendanceReqestModel> absentStudentList =
	                    attendanceReqList.stream()
	                    .filter(dto -> "200".equals(
	                            dto.getIsRecongStudent()))
	                    .collect(Collectors.toList());

	            if (!absentStudentList.isEmpty()) {
	                List<Map<String, Object>> backupList =
	                        absentStudentList.stream()
	                        .map(student -> {
	                            Map<String, Object> studentMap =
	                                    new HashMap<>();
	                            studentMap.put("studentId",
	                                    student.getStudentId());
	                            studentMap.put("studentName",
	                                    student.getStudentName());
	                            studentMap.put("regNo",
	                                    student.getRegNo());
	                            studentMap.put("cameraIp",
	                                    student.getCameraIp());
	                            studentMap.put("startTime",
	                                    student.getStartTime());
	                            return studentMap;
	                        })
	                        .collect(Collectors.toList());

	                String fileContent =
	                        new ObjectMapper()
	                        .writeValueAsString(backupList);

	                // ← CHANGED: cameraIp + startTime
	                // (no roomId/subjectId — those are gone)
	                String fileName =
	                        "absent_" +
	                        cameraIp.replace(".", "_") + "_" +
	                        startTime.replace(":", "") + "_" +
	                        System.currentTimeMillis() +
	                        ".json";

	                dao.saveAbsentBackup(
	                        "ACTIVE", fileName, fileContent);

	                lg.info("✅ Absent backup saved: {} " +
	                        "({} students)",
	                        fileName,
	                        absentStudentList.size());
	            } else {
	                lg.info("No absent students — " +
	                        "skipping backup");
	            }
	        } catch (Exception backupEx) {
	            // Don't let backup failure crash the main flow
	            lg.error("Failed to save absent backup: {}",
	                     backupEx.getMessage());
	        }

	        lg.info("duplicateStudentList size: {}",
	                duplicateStudentList.size());
	        for (String key : duplicateStudentList) {
	            lg.info("duplicateStudentList entry: {}", key);
	        }

	        // Unbind ALL students from MegBox
	        if (!duplicateStudentList.isEmpty()) {
	            Map<String, String> studentTokenDetails =
	                    megboxStudentDetailsDao
	                    .getMegboxStudentDetails(
	                            "1",
	                            studentApkDtoResponse
	                                .getMegBoxIp());

	            lg.info("studentTokenDetails size: {}",
	                    studentTokenDetails.size());

	            ExecutorService executor =
	                    Executors.newFixedThreadPool(25);
	            Runnable studentThread =
	                    new ImagesUnbindingThread(
	                            duplicateStudentList,
	                            studentApkDtoResponse.getMegBoxIp(),
	                            studentTokenDetails);
	            executor.execute(studentThread);
	            executor.shutdown();
	            while (!executor.isTerminated()) {}
	            lg.info("Finished all threads!");
	        }

	        // Build response
	        if (CollectionUtils.isNotEmpty(attendanceReqList)) {
	            List<StudentAttendanceDetails>
	                    studentAttendanceDetailsList =
	                    new ArrayList<>();

	            for (AttendanceReqestModel dto :
	                    attendanceReqList) {

	                StudentAttendanceDetails
	                        studentAttendanceDetails =
	                        new StudentAttendanceDetails();
	                studentAttendanceDetails.setRegNo(
	                        dto.getRegNo());
	                studentAttendanceDetails.setStudentId(
	                        dto.getStudentId());
	                studentAttendanceDetails
	                        .setIsRecongStudent(
	                            dto.getIsRecongStudent());
	                studentAttendanceDetails.setStudentName(
	                        dto.getStudentName());
	                studentAttendanceDetails
	                        .setIsCocurricularLeave(
	                            dto.getIs_on_leave());
	                studentAttendanceDetails.setIsOnleave(
	                        dto.getIs_on_leave());

	                if (CollectionUtils.isEmpty(
	                        studentAttendanceDetailsList)) {
	                    response.setClassId(dto.getClassId());
	                    response.setPeriodId(dto.getPeriodId());
	                    response.setPeriodName(dto.getPeriodName());
	                    response.setClassName(dto.getClassName());
	                    response.setTeacherName(
	                            dto.getTeacherName());
	                    response.setSubName(dto.getSubName());
	                    response.setStartTime(dto.getStartTime());
	                    response.setEndTime(dto.getEndTime());
	                    response.setTecherId(dto.getTeacherId());
	                    response.setActivityId(
	                            dto.getActivityId());
	                    response.setAttDate(dto.getAttDate());
	                    response.setAttId(dto.getAttId());
	                    response.setAttTypeId(
	                            dto.getAttTypeId());
	                    response.setBatchId(dto.getBatchId());
	                    response.setDateandtime(
	                            dto.getDateandtime());
	                    response.setHoursheld(dto.getHoursheld());
	                    response.setIsTT(dto.getIsTT());
	                    response.setRemarks(dto.getRemarks());
	                    response.setSubstitueClass(
	                            dto.getSubstitueClass());
	                    response.setTotalHours(
	                            dto.getTotalHours());
	                    response.setUserId(dto.getUserId());
	                    response.setVersion(dto.getVersion());
	                    response.setWorkDiaryAcademicYear(
	                            dto.getWorkDiaryAcademicYear());
	                    response.setWorkDiaryActivityId(
	                            dto.getWorkDiaryActivityId());
	                    response.setCameraIp(dto.getCameraIp());
	                }
	                studentAttendanceDetailsList.add(
	                        studentAttendanceDetails);
	            }
	            response.setStudentAttendanceDetails(
	                    studentAttendanceDetailsList);
	        }

	        if (response != null) {
	            getNewGetAttId(response);
	        }

	    } catch (Exception e) {
	        lg.error("Error in getStudentAttendance", e);
	    }

	    lg.info("=== getStudentAttendance END ===");
	    return response;
	}
//	private Map<String, AttendanceReqestModel> convertStudentReqDetailsToMap(List<StudentApkDtoResponse> response) {
//		Map<String, AttendanceReqestModel> map = new HashMap<>();
//		for (StudentApkDtoResponse studentAttendance : response) {
//			AttendanceReqestModel model = new AttendanceReqestModel();
//			model.setClassName(studentAttendance.getClassName());
//			model.setEndTime(studentAttendance.getEndTime());
//			model.setPeriodName(studentAttendance.getPeriodName());
//			model.setRoomId(String.valueOf(studentAttendance.getRoomId()));
//			model.setStartTime(studentAttendance.getStartTime());
//			model.setSubId(String.valueOf(studentAttendance.getSubjectId()));
//			model.setSubName(studentAttendance.getSubName());
//			model.setTeacherId(studentAttendance.getTecherId());
//			model.setTeacherName(studentAttendance.getTeacherName());
//			model.setClassId(studentAttendance.getClassId());
//			model.setIsRecongStudent("200");
//			model.setPeriodId(studentAttendance.getPeriodId());
//			model.setMegBoxIp(studentAttendance.getMegBoxIp());
//			model.setToken(studentAttendance.getToken());
//			model.setStudentId(studentAttendance.getStudentId());
//			model.setStudentName(studentAttendance.getStudentName());
//			model.setRegNo(studentAttendance.getRegno());
//			model.setIs_on_leave(studentAttendance.getIs_on_leave());
//			model.setIs_cocurricular_leave(studentAttendance.getIs_cocurricular_leave());
//			model.setActivityId(studentAttendance.getActivityId());
//			model.setAttDate(studentAttendance.getAttDate());
//			model.setAttId(studentAttendance.getAttId());
//			model.setAttTypeId(studentAttendance.getAttTypeId());
//			model.setBatchId(studentAttendance.getBatchId());
//			model.setDateandtime(studentAttendance.getDateandtime());
//			model.setHoursheld(studentAttendance.getHoursheld());
//			model.setIsTT(studentAttendance.getIsTT());
//			model.setRemarks(studentAttendance.getRemarks());
//			model.setSubstitueClass(studentAttendance.getSubstitueClass());
//			model.setTotalHours(convertDecimalHoursToTime(Double.parseDouble(studentAttendance.getTotalHours())));
//			model.setUserId(studentAttendance.getUserId());
//			model.setVersion(studentAttendance.getVersion());
//			model.setWorkDiaryAcademicYear(studentAttendance.getWorkDiaryAcademicYear());
//			model.setWorkDiaryActivityId(studentAttendance.getWorkDiaryActivityId());
//			map.put(model.getStudentId()+"-"+studentAttendance.getRoomId(), model);
//		}
//		return map;
//	}
	
	// RoomID - cameraIp Mapping
	
	private Map<String, AttendanceReqestModel> 
    convertStudentReqDetailsToMap(
            List<StudentApkDtoResponse> response) {

Map<String, AttendanceReqestModel> map = 
        new HashMap<>();

for (StudentApkDtoResponse studentAttendance : 
        response) {

    AttendanceReqestModel model = 
            new AttendanceReqestModel();
    model.setClassName(
            studentAttendance.getClassName());
    model.setEndTime(
            studentAttendance.getEndTime());
    model.setPeriodName(
            studentAttendance.getPeriodName());
    model.setRoomId(String.valueOf(
            studentAttendance.getRoomId()));
    model.setCameraIp(
            studentAttendance.getCameraIp()); // ← NEW
    model.setStartTime(
            studentAttendance.getStartTime());
    model.setSubId(String.valueOf(
            studentAttendance.getSubjectId()));
    model.setSubName(
            studentAttendance.getSubName());
    model.setTeacherId(
            studentAttendance.getTecherId());
    model.setTeacherName(
            studentAttendance.getTeacherName());
    model.setClassId(
            studentAttendance.getClassId());
    model.setIsRecongStudent(
    	    "300".equals(studentAttendance.getIsRecongStudent())
    	        ? "300"
    	        : "200"
    	);
    model.setPeriodId(
            studentAttendance.getPeriodId());
    model.setMegBoxIp(
            studentAttendance.getMegBoxIp());
    model.setToken(
            studentAttendance.getToken());
    model.setStudentId(
            studentAttendance.getStudentId());
    model.setStudentName(
            studentAttendance.getStudentName());
    model.setRegNo(
            studentAttendance.getRegno());
    model.setIs_on_leave(
            studentAttendance.getIs_on_leave());
    model.setIs_cocurricular_leave(
            studentAttendance
                .getIs_cocurricular_leave());
    model.setActivityId(
            studentAttendance.getActivityId());
    model.setAttDate(
            studentAttendance.getAttDate());
    model.setAttId(
            studentAttendance.getAttId());
    model.setAttTypeId(
            studentAttendance.getAttTypeId());
    model.setBatchId(
            studentAttendance.getBatchId());
    model.setDateandtime(
            studentAttendance.getDateandtime());
    model.setHoursheld(
            studentAttendance.getHoursheld());
    model.setIsTT(
            studentAttendance.getIsTT());
    model.setRemarks(
            studentAttendance.getRemarks());
    model.setSubstitueClass(
            studentAttendance.getSubstitueClass());

    // ✅ Use totalHours directly
    // NO convertDecimalHoursToTime here!
    // That method is in AttendanceServiceImpl only
    model.setTotalHours(
            studentAttendance.getTotalHours());

    model.setUserId(
            studentAttendance.getUserId());
    model.setVersion(
            studentAttendance.getVersion());
    model.setWorkDiaryAcademicYear(
            studentAttendance
                .getWorkDiaryAcademicYear());
    model.setWorkDiaryActivityId(
            studentAttendance
                .getWorkDiaryActivityId());

    // ✅ map key uses cameraIp
    map.put(
    	    model.getStudentId() + "|" +          // ← changed from "-" to "|"
    	    		studentAttendance.getGroupName(),
    	    model);
}
return map;
}
	
	public static String convertDecimalHoursToTime(double time) {
        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 60); // 0.5 * 60 = 30
        int seconds = 0;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

//	// @Scheduled(cron = OrganizationUtil.ORG_UNBIND_SCHEDULE_TIME)
//	public void clearGroup() {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		String startDate = sdf.format(new Date());
//		List<StudentApkDtoResponse> daoList = dao.getStudentAttendanceBasedOnDate(startDate);
//		if (CollectionUtils.isNotEmpty(daoList)) {
//			Map<String, AttendanceReqestModel> map = convertStudentReqDetailsToMap(daoList);
//			Map<String, List<String>> unBindDataMap = new HashMap<String, List<String>>();
//			List<String> duplicateStudentList = new ArrayList<String>();
//			for (Map.Entry<String, AttendanceReqestModel> data : map.entrySet()) {
//				if (!unBindDataMap.containsKey(data.getValue().getMegBoxIp())) {
//					duplicateStudentList = unBindDataMap.get(data.getValue().getMegBoxIp());
//					duplicateStudentList.add(data.getKey());
//					unBindDataMap.put(data.getValue().getMegBoxIp(), duplicateStudentList);
//				} else {
//					duplicateStudentList.add(data.getKey());
//					unBindDataMap.put(data.getValue().getMegBoxIp(), duplicateStudentList);
//				}
//			}
//			for (Map.Entry<String, List<String>> unBindData : unBindDataMap.entrySet()) {
//				if (unBindData.getValue() != null && !unBindData.getValue().isEmpty()) {
//					Map<Integer, String> studentTokenDetails = null; //megboxStudentDetailsDao.getMegboxStudentDetails("1");
//					ExecutorService executor = Executors.newFixedThreadPool(10);
//					Runnable studentThread = new ImagesUnbindingThread(unBindData.getValue(), unBindData.getKey(),
//							studentTokenDetails);
//					executor.execute(studentThread);
//					executor.shutdown();
//					while (!executor.isTerminated()) {
//					}
//					lg.info("Finished all threads!");
//				}
//			}
//		}
//	}

//	@Scheduled(cron = OrganizationUtil.SEND_NOTIFICATION)
//	public void sendNotification() throws ParseException {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		String startDate = sdf.format(new Date());
//		List<StudentApkDtoResponse> daoList = dao.getStudentAttendanceAndTokenBasedOnDate(startDate);
//		lg.info("startDate to sendNotificationBasedOnSchdule : {}", startDate);
//		Date dateTime = new Date();
//		for (StudentApkDtoResponse data : daoList) {
//			SimpleDateFormat teacherScanTimeFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
//			Date teacherSubmittedStartTime = teacherScanTimeFormat.parse(data.getDateandtime());
//			String roomSubInfo = data.getRoomId() + "@" + data.getSubjectId() + "@"+ teacherSubmittedStartTime;
//			if(!duplicateData.contains(roomSubInfo)) {
//				Date attendanceCapturedEndTime = teacherScanTimeFormat.parse(data.getDateandtime());
//				int currentMinutes = attendanceCapturedEndTime.getMinutes();
//				int[] splitedMints = splitIntoBase10(currentMinutes);
//				
//				//attendanceCapturedEndTime.setMinutes(splitedMints[0]);
//				attendanceCapturedEndTime.setMinutes(attendanceCapturedEndTime.getMinutes()+20);
//				String roomSubIds = data.getRoomId() + "@" + data.getSubjectId();
//				
//				if(!CameraUtils.isIPActive(data.getMegBoxIp())) {
//					HttpEntity<FCMReqDto> request = CameraUtils.sendNotification(data, OrganizationUtil.SEND_ERROR_NOTIFICATION);
//					restTemplate.postForEntity("http://203.192.241.142:3000/send-notifications", request,
//							String.class);
//				}
//				
//				int result = dateTime.compareTo(attendanceCapturedEndTime);
//				if (result > 0) {
//					sendNotification(data, roomSubInfo, roomSubIds, teacherSubmittedStartTime, attendanceCapturedEndTime);
//				}
//			}
//		}
//	}
	// ROOM ID - CAMERA IP Modification 
	@Scheduled(cron = OrganizationUtil.SEND_NOTIFICATION)
	public void sendNotification() throws ParseException {

	    SimpleDateFormat sdf =
	            new SimpleDateFormat("dd/MM/yyyy");
	    String startDate = sdf.format(new Date());

	    List<StudentApkDtoResponse> daoList =
	            dao.getStudentAttendanceAndTokenBasedOnDate(
	                    startDate);

	    lg.info("startDate to sendNotification: {}",
	            startDate);

	    Date dateTime = new Date();

	    for (StudentApkDtoResponse data : daoList) {

	    	SimpleDateFormat teacherScanTimeFormat =
	    	        new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

	        Date teacherSubmittedStartTime = teacherScanTimeFormat.parse(data.getDateandtime());
	        // ← FIXED: startTime instead of subjectId
	        String roomSubInfo =
	                data.getCameraIp() + "@" +
	                data.getStartTime() + "@" +
	                teacherSubmittedStartTime;

	        if (!duplicateData.contains(roomSubInfo)) {

	            Date attendanceCapturedEndTime =
	                    teacherScanTimeFormat.parse(
	                            data.getDateandtime());

	            attendanceCapturedEndTime.setMinutes(
	                    attendanceCapturedEndTime
	                            .getMinutes() + 20);

	            // ← FIXED: startTime instead of subjectId
	            String roomSubIds =
	                    data.getCameraIp() + "@" +
	                    data.getStartTime();

	            int result = dateTime.compareTo(
	                    attendanceCapturedEndTime);

	            if (result > 0) {
	                sendNotification(
	                        data,
	                        roomSubInfo,
	                        roomSubIds,
	                        teacherSubmittedStartTime,
	                        attendanceCapturedEndTime);
	            }
	        }
	    }
	}


	public static int[] splitIntoBase10(int value) {
	    int roundedDown = (value / 10) * 10;
	    int remainder = value - roundedDown;
	    return new int[]{roundedDown, remainder};
	}

	private void sendNotification(StudentApkDtoResponse data, String roomSubInfo, String roomSubIds, Date teacherSubmittedTime, Date attendanceCapturedEndTime) {
		duplicateData.add(roomSubInfo);
		attendanceService.getStudentAttendance(roomSubIds, teacherSubmittedTime, attendanceCapturedEndTime);
	}

	@Override
	public void clearDuplicateData() {
		duplicateData.clear();
	}

	public void sendNotificationBasedOnSchdule(StudentApkDtoResponse data, String[] HoursAndMints) {
		try {
			FileInputStream serviceAccount = new FileInputStream(
					"E:\\project\\kpcamera\\src\\main\\resources\\firebase-admin.json");

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
			Notification notification = Notification.builder()
					.setTitle(data.getRoomId() != null ? String.valueOf(data.getRoomId()) : "")
					.setBody(data.getRoomId() != null ? String.valueOf(data.getRoomId())
							: "" + "@" + data.getSubjectId() != null ? String.valueOf(data.getSubjectId()) : "")
					.build();
			AndroidConfig androidConfig = getAndroidConfig("Student Attendance Info");
			Message message = Message.builder().setNotification(notification).setAndroidConfig(androidConfig)
					.setToken(data.getToken()).build();

			String response = FirebaseMessaging.getInstance().sendAsync(message).get();
			lg.info("Successfully sent message: " + response);
			duplicateData.add(
					data.getRoomId() + "@" + data.getSubjectId() + "@" + HoursAndMints[0] + "@" + HoursAndMints[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AndroidConfig getAndroidConfig(String topic) {
		return AndroidConfig.builder().setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
				.setPriority(AndroidConfig.Priority.HIGH)
				.setNotification(AndroidNotification.builder().setTag(topic).build()).build();
	}

	private void getNewGetAttId(
	        StudentAttendanceDetailsResponse response) {
	    try {
	        Map<String, Object> payload = new HashMap<>();

	        // ← CHANGED: dateandtime + cameraIpAddress instead of
	        // date/startTime/endTime. Both values already flow
	        // through this response object from getStudentAttendance
	        // (response.setDateandtime(...) / response.setCameraIp(...))
	        // — no upstream changes needed for these to be populated.
	        payload.put("dateandtime", response.getDateandtime());
	        payload.put("cameraIpAddress", response.getCameraIp());

	        List<Map<String, Object>> studentList =
	                new ArrayList<>();

	        if (response.getStudentAttendanceDetails() != null) {
	            for (StudentAttendanceDetails details :
	                    response.getStudentAttendanceDetails()) {

	                Map<String, Object> studentEntry =
	                        new HashMap<>();
	                studentEntry.put(
	                        "studentId",
	                        details.getStudentId());

	                String status;
	                if ("100".equals(
	                        details.getIsRecongStudent())) {
	                    status = "PRESENT";
	                } else if ("300".equals(
	                        details.getIsRecongStudent())) {
	                    status = "UNKNOWN";
	                } else {
	                    status = "ABSENT";
	                }
	                studentEntry.put("status", status);
	                studentList.add(studentEntry);
	            }
	        }

	        payload.put("students", studentList);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<Map<String, Object>> requestEntity =
	                new HttpEntity<>(payload, headers);

	        RestTemplate restTemplate =
	                RestTemplateConfiguration.getRestTemplate();

	        // ← CHANGED: capture the serialized JSON in a variable
	        // so it can be reused for the delivery log below,
	        // instead of serializing inline only for the log line
	        String requestJson =
	                new ObjectMapper().writeValueAsString(payload);

	        lg.info("Sending attendance results to Christ App: {}",
	                requestJson);

	        ResponseEntity<String> christResponse =
	                restTemplate.exchange(
	                        submitAttendanceUrl,
	                        HttpMethod.POST,
	                        requestEntity,
	                        String.class);

	        lg.info("Christ App response: {} - {}",
	                christResponse.getStatusCode(),
	                christResponse.getBody());

	        // ← NEW: log this delivery for proof purposes.
	        // Fires for EVERY session (absentees or not), unlike
	        // absent_student_backup which only fires when there
	        // are absent students.
	        dao.saveChristDeliveryLog(
	                response.getCameraIp(),
	                response.getDateandtime(),
	                requestJson,
	                String.valueOf(
	                        christResponse.getStatusCode().value()),
	                christResponse.getBody());

	    } catch (Exception e) {
	        lg.error("Error sending results to Christ App: {}",
	                e.getMessage());

	        // ← NEW: also log failed delivery attempts — proof
	        // that we tried, even if the call itself failed
	        // (network error, timeout, etc.)
	        try {
	            dao.saveChristDeliveryLog(
	                    response.getCameraIp(),
	                    response.getDateandtime(),
	                    "N/A",
	                    "ERROR",
	                    e.getMessage());
	        } catch (Exception logEx) {
	            lg.error("Failed to log delivery failure: {}",
	                    logEx.getMessage());
	        }
	    }
	}

	@Override
	public List<StudentApkDtoResponse> getStudentAttendanceFromCurrentDatetoLastOneWeek() throws Exception {
		return dao.getStudentAttendanceFromCurrentDatetoLastOneWeek();
	}
	
	
	@Override
	public List<AttendanceReqestModel> getStudentAttendanceForUI(String jobId, Date teacherSubmittedTime, 
			Date attendanceCapturedEndTime, String megBoxIp) {
		StudentAttendanceDetailsResponse response = new StudentAttendanceDetailsResponse();
		String[] jobData = jobId.split("@");
		String roomId = jobData[0];
		List<AttendanceReqestModel> attendanceReqList = new ArrayList<AttendanceReqestModel>();
		try {
				List<RecongnizedStudentResDto> facesResDtoList = service.getRecongnizedData(
						megBoxIp, teacherSubmittedTime, attendanceCapturedEndTime);
				if (facesResDtoList != null && !CollectionUtils.isEmpty(facesResDtoList)) {
					for (RecongnizedStudentResDto facesResDto : facesResDtoList) {
						List<AlertsEvent> alertsEvent = facesResDto.getData().getAlertsEvent();
						for (AlertsEvent alert : alertsEvent) {
							Top1 top1 = alert.getTop1();
							if(top1.getGroup().get(0).getGroupName()
									.equalsIgnoreCase(top1.getAlertGroup().get(0).getGroupName()) && top1.getGroup().get(0).getGroupName().equals(roomId)) {
								AttendanceReqestModel model = new AttendanceReqestModel();
								model.setRegNo(top1.getDescription());
								String image = alert.getCaptureFullImageURL().replaceFirst(".","https:/"+megBoxIp);
								model.setImage(image);
								attendanceReqList.add(model);
							}
						}

					}
				} 
		} catch (Exception e) {

		}
		return attendanceReqList;
}
	
  // unused
	public String callNewGetAttId(StringBuilder nameValuePairs, String students) throws Exception {

		
		/*
		 * UriComponentsBuilder builder =
		 * UriComponentsBuilder.fromHttpUrl(submitAttendanceUrl) .queryParam("bcafe1",
		 * Base64Coder.encodeString(nameValuePairs.toString())) .queryParam("students",
		 * students);
		 */
		
		String encodedBcafe1 = Base64.getEncoder()
		        .encodeToString(nameValuePairs.toString().getBytes(StandardCharsets.UTF_8));

		String url = UriComponentsBuilder
		        .fromHttpUrl(submitAttendanceUrl)
		        .queryParam("bcafe1", encodedBcafe1)
		        .queryParam("students", students)
		        .build(false)   // ⭐ VERY IMPORTANT
		        .toUriString();


	    HttpEntity<String> entity = new HttpEntity<>(null); // no extra headers here
	    RestTemplate restTemplateForAndriod = restTemplateForAndriod();
	    ResponseEntity<String> response = restTemplateForAndriod.exchange(
	    		url,
	            HttpMethod.POST,
	            entity,
	            String.class
	    );

	    return response.getBody();
	}
	public RestTemplate restTemplateForAndriod() {
	    List<Header> defaultHeaders = Arrays.asList(
	            new BasicHeader("User-Agent", "KpCamera")
	    );

	    CloseableHttpClient httpClient = HttpClients.custom()
	            .setDefaultHeaders(defaultHeaders)
	            .disableContentCompression()          // removes Accept-Encoding
	            .build();

	    HttpComponentsClientHttpRequestFactory factory =
	            new HttpComponentsClientHttpRequestFactory(httpClient);

	    return new RestTemplate(factory);
	}

}
