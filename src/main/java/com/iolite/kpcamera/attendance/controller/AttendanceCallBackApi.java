package com.iolite.kpcamera.attendance.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iolite.kpcamera.attendance.dao.AttendanceDao;
import com.iolite.kpcamera.attendance.model.CameraIpDetails;
import com.iolite.kpcamera.attendance.model.OnDemandUploadRequest;
import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.StudentAttendance;
import com.iolite.kpcamera.attendance.model.StudentDto;
import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;
import com.iolite.kpcamera.attendance.model.responses.OnDemandUploadResponse;
import com.iolite.kpcamera.attendance.model.responses.PreSignedUrlDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceDetailsResponse;
import com.iolite.kpcamera.attendance.model.responses.SubmitAttendanceResponse;
import com.iolite.kpcamera.attendance.service.AttendanceService;
import com.iolite.kpcamera.attendance.service.MegBoxAttendanceService;
import com.iolite.kpcamera.attendance.service.RoomIpService;
import com.iolite.kpcamera.attendance.util.CameraUtils;

@RestController
@RequestMapping("/v1/attendance")
public class AttendanceCallBackApi {
	static final Logger lg = LoggerFactory.getLogger(AttendanceCallBackApi.class);
	
	@Autowired
     AttendanceService service;
	
	@Autowired
	MegBoxAttendanceService services;
	
	@Autowired
	AttendanceDao dao;
	
	@Autowired
	RoomIpService roomIpService;
	
	
	@GetMapping(value = "/getAttendance/{jobId}",produces = MediaType.APPLICATION_JSON_VALUE )
	public StudentAttendanceDetailsResponse getAttendanceForApk(@PathVariable("jobId") String jobId) throws Exception {
		lg.info("Start AttendanceCallBackApi {} : ",jobId);
		StudentAttendanceDetailsResponse response = null;
		if(jobId!= null && !jobId.contains("@")) {
			response = new StudentAttendanceDetailsResponse();
			response.setStatus("Invalid Job Id");
			response.setStatusCode(HttpStatus.BAD_REQUEST.name());
			return response;
		}
		try {
			//response = service.getStudentAttendance(jobId);
			if(response.getStudentAttendanceDetails() != null) {
				response.setStatus("completed");
				response.setStatusCode(HttpStatus.OK.name());
			}
			else {
				response.setStatus("inprogress");
				response.setStatusCode(HttpStatus.OK.name());
			}
		}catch (Exception e) {
			lg.error("Error getAttendanceForApk:{}",e);
		}
		lg.info("end AttendanceCallBackApi:{}",response);
		return response;
	}
	
	// RoomId - CameraIP Modification
//	@PostMapping(path= "/saveStudentAttedenceDetails", consumes = "application/json", produces = "application/json")
//	public String getRecongnizedAttendanceData(@RequestBody StudentAttendance studentAttendance){
//		String ganaretdValue = "";
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			String startDate = sdf.format(new Date());
//			Date endDate = new Date();
//			Date startDateForBoXValidate = new Date();
//			startDateForBoXValidate.setMinutes(startDateForBoXValidate.getMinutes()-10);
//			String message = validateAndUpdateGroupDetails(studentAttendance.getMegBoxIp(), studentAttendance.getRoomId());
//			if(message != "") {
//				return message;
//			}
//
//			List<StudentApkDtoResponse> daoList =
//					dao.getStudentAttendance(studentAttendance.getRoomId(),
//							String.valueOf(studentAttendance.getSubjectId()),startDate);
//			if(CollectionUtils.isNotEmpty(daoList)) { return ganaretdValue =
//					"The Period Attendance is already taken or Inprogress"; }
//
//			ganaretdValue = services.getRecongnizedStudent(studentAttendance);
//		}catch (Exception e) {
//			lg.error("Error getRecongnizedAttendanceData",e);
//		}
//		lg.info("end AttendanceCallBackApi:{}",ganaretdValue);
//		return ganaretdValue;
//	}
	// RoomID - CAMERAIP modification
//	@PostMapping(path= "/saveStudentAttedenceDetails", consumes = "application/json", produces = "application/json")
//	public String getRecongnizedAttendanceData(@RequestBody StudentAttendance studentAttendance){
//		String ganaretdValue = "";
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			String startDate = sdf.format(new Date());
//			Date endDate = new Date();
//			Date startDateForBoXValidate = new Date();
//			startDateForBoXValidate.setMinutes(startDateForBoXValidate.getMinutes()-10);
//			String message = validateAndUpdateGroupDetails(studentAttendance.getMegBoxIp(), studentAttendance.getRoomId());
//			if(message != "") {
//				return message;
//			}
//
//			List<StudentApkDtoResponse> daoList =
//				    dao.getStudentAttendance(
//				            studentAttendance.getCameraIp(), // ← cameraIp
//				            String.valueOf(
//				                studentAttendance.getSubjectId()),
//				            startDate);
//			if(CollectionUtils.isNotEmpty(daoList)) { return ganaretdValue =
//					"The Period Attendance is already taken or Inprogress"; }
//
//			ganaretdValue = services.getRecongnizedStudent(studentAttendance);
//		}catch (Exception e) {
//			lg.error("Error getRecongnizedAttendanceData",e);
//		}
//		lg.info("end AttendanceCallBackApi:{}",ganaretdValue);
//		return ganaretdValue;
//	}
//
//	private String validateAndUpdateGroupDetails(String boxIP, String roomNum) {
//		try {
//			String message = "";
//			if(!CameraUtils.isIPActive(boxIP)) {
//				message = "The Box IP is not reachable";
//			}
//			Map<String, List<String>> camIpVsGroupNames = services.getCamDetails(boxIP);
//			RoomIpInbound roomIpInbound = new RoomIpInbound();
//			roomIpInbound.setIpAddress(boxIP);
//			roomIpInbound.setDescription("BoxIp and CameraDetails");
//			List<CameraIpDetails> cameraIps = new ArrayList<CameraIpDetails>();
//			for(Map.Entry<String, List<String>> camDetails : camIpVsGroupNames.entrySet()) {
//				CameraIpDetails cameraIpDetails = new CameraIpDetails();
//				cameraIpDetails.setCameraIp(camDetails.getKey());
//				cameraIpDetails.setFloor("");
//				cameraIpDetails.setGroupName(String.join(", ", camDetails.getValue()));
//				cameraIps.add(cameraIpDetails);
//				if(camDetails.getValue().contains(roomNum)) {
//					if(!CameraUtils.isIPActive(camDetails.getKey())) {
//						message = "The Camera IP is not reachable";
//					}
//				}
//			}
//			roomIpInbound.setCameraIps(cameraIps);
//			RoomIpOutbound roomIpOutbound =  roomIpService.getRoom(roomIpInbound);
//			if(roomIpOutbound != null) {
//				roomIpService.deleteRoomAndIp(roomIpOutbound.getId());
//			}
//			roomIpService.addRoomAndIp(roomIpInbound);
//			return message;
//		}  catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	@PostMapping(
		    path = "/uploadStudent",
		    consumes = "application/json",
		    produces = "application/json"
		)
		public OnDemandUploadResponse uploadStudentOnDemand(
		        @RequestBody OnDemandUploadRequest request) {

		    lg.info("Start uploadStudentOnDemand: {}", request.getStudentId());

		    OnDemandUploadResponse response = new OnDemandUploadResponse();

		    try {
		        response = services.uploadSingleStudent(request);

		    } catch (Exception e) {
		        lg.error("Error in uploadStudentOnDemand: {}", e);
		        response.setStatus("FAILED");
		        response.setMessage("Unexpected error: " + e.getMessage());
		    }

		    lg.info("End uploadStudentOnDemand: {}", response.getStatus());
		    return response;
		}
	
	// Method 1 - Dummy API (replaces Christ API)
	@GetMapping("/dummy")
	public PreSignedUrlDto getDummyPreSignedUrl(
	        @RequestParam String id) {

	    lg.info("Dummy API called for id: {}", id);

	    PreSignedUrlDto dto = new PreSignedUrlDto();
	    dto.setPreSignedUrl(
	        "https://akash-first-bucket-2026.s3.us-east-1.amazonaws.com/0190a7c2-4d8e-7b3f-a9c1-6e2d5f8b4c7a.jpg?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEGcaCXVzLWVhc3QtMSJHMEUCIQCarsD24mO6UtN4tffQebVVNlUnsoNigbjjaQ5ZpFPlnwIgX5HGlbG%2BJqSQzGDXo%2FJHIE9U3r6mpHtjzELHdX0I3KUqtQMIMBAAGgw1MzA2MDIzMzQ3NTEiDBAowHzotxuAtCgUPCqSA4coTzpmoZ%2FV8SYHKqhgcrEdp1B%2FBXUDcnE0v87bP32BzMsDCrXyZk2jULEXe%2B6HDHJQ1XQA%2BUjvTZ2xeB1HaXaIJvwbyLhdNgZqQUZExQjs8Ui%2FEmILzwjr1GSUh4w3suyjiLbE6l6l6HC2zPOnmzZUzYhJQKIDK5%2BzPnnPwaBuIeroXLzP1v9U%2F3wNoZqNcR56yHgq9NuYElo86xOlqk22uIugN85vCJX8UuZyynVTMv1jptQGIFaF2FjegqVLI3efrNxgd1XnKlu4%2BkCaJHkSvwV3XdcBf%2BkXWaAvmfqz0M5OAM8C3ire3QtpD4O8mehyNBqHRK%2F9U%2Bof0%2FpAur8tWc%2BUHqPWVtRVNRU%2FzSZ9rDJWaVQWKBohAfmEXavObUV2gEsH5bCKCXNPzRPAoOA2utipY%2FxBo6UGrMJbpMZn%2FGHfVej%2BZ%2Buz4dud43MWfPJPiKOeOrfBJgYmOgUurBTgpLa2Mx2dEJQR%2BLGMsN1wMtS%2BDSHFQt0UuS4zBavhh6wp9qnZXttIBGQ70OGt1nACKDDgzO3RBjreAr206GeDJVMTMC9lQZoCSPjTYAX%2Fp8xYYiJI%2F%2BU1nAwamqDJAlS5m1aSw8iSXphvKmhmzFWEnKxHSoIJXs74SliakEATrtTfac37P74FJ2pBOetilAd5SFnVvlAJE%2BPaPS9ENk%2Ba8ESYASttpSFD6w3O8HhmL%2FKJXBATbOfqWadzmpYvVkP8UQF1%2BQTkvJRG0ijiQOefjEDH1MiVAe4NR%2FWMXeLwlFb7LcjxwlfmKwc5bDCXGqfToNN8DzKEa3Jh5HVqYpwIBlUuHX6EEJqfmTkj3i7m0CHZABHhU0ehDsGJYB9D4nilrNTTt%2FZYrxUrtGz8j1geK6SLwu8h2w6J1uakh1wjh0GzT9su1zePnJZ5XcxXhwGVlwp6I87p3oOLT%2Bi%2FhlJRLBpHpr2QHn212DtAgYcU1iHI%2BdIrsbw4UxjkFrTf4Ri1HavtpvYLBw349lmppVTYWqCDQQcN7tT9&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAXXCS5DIP6OUDRBJB%2F20260624%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260624T064028Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=1f662454a26392fe87ca7063b950e420b5152121e1116bc9a0acfc6337a39bfd"
	    		
	    		);
	    return dto;
	}

	// Method 2 - Trigger uploadFaces() manually
	@GetMapping("/triggerUpload")
	public String triggerUpload() {
	    try {
	        lg.info("Manual trigger of uploadFaces()");
	        services.uploadFaces();
	        return "Upload triggered! Check logs and DB.";
	    } catch (Exception e) {
	        lg.error("Error triggering upload", e);
	        return "Error: " + e.getMessage();
	    }
	}
	private String validateAndUpdateGroupDetailsByCamIP(
	        String boxIP,
	        String camIp) {

	    try {
	        String message = "";

	        // Step 1 — Check if MegBox device is reachable
	        if (!CameraUtils.isIPActive(boxIP)) {
	            message = "The Box IP is not reachable";
	        }

	        // Step 2 — Get all cameras connected to MegBox
	        // Returns: { "192.168.1.5" → ["group1", "group2"] }
	        Map<String, List<String>> camIpVsGroupNames =
	                services.getCamDetails(boxIP);

	        // Step 3 — Build room IP inbound object
	        RoomIpInbound roomIpInbound = new RoomIpInbound();
	        roomIpInbound.setIpAddress(boxIP);
	        roomIpInbound.setDescription(
	                "BoxIp and CameraDetails");

	        List<CameraIpDetails> cameraIps =
	                new ArrayList<CameraIpDetails>();

	        // Step 4 — Loop each camera detail from MegBox
	        for (Map.Entry<String, List<String>>
	                camDetails : camIpVsGroupNames.entrySet()) {

	            CameraIpDetails cameraIpDetails =
	                    new CameraIpDetails();
	            cameraIpDetails.setCameraIp(
	                    camDetails.getKey());
	            cameraIpDetails.setFloor("");
	            cameraIpDetails.setGroupName(
	                    String.join(", ",
	                            camDetails.getValue()));
	            cameraIps.add(cameraIpDetails);

	            // Step 5 — Check if our specific camera
	            //          is reachable
	            if (camDetails.getValue().contains(camIp)) {
	                if (!CameraUtils.isIPActive(
	                        camDetails.getKey())) {
	                    message =
	                        "The Camera IP is not reachable";
	                }
	            }
	        }

	        roomIpInbound.setCameraIps(cameraIps);

	        // Step 6 — Check if room data already exists
	        RoomIpOutbound roomIpOutbound =
	                roomIpService.getRoom(roomIpInbound);

	        // Step 7 — Delete old room data if exists
	        if (roomIpOutbound != null) {
	            roomIpService.deleteRoomAndIp(
	                    roomIpOutbound.getId());
	        }

	        // Step 8 — Save fresh room + camera data
	        roomIpService.addRoomAndIp(roomIpInbound);

	        return message;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	@PostMapping(
	        path = "/submitAIAttendanceData",
	        consumes = "application/json",
	        produces = "application/json")
	public SubmitAttendanceResponse submitAIAttendanceData(
	        @RequestBody StudentAttendance studentAttendance) {

	    SubmitAttendanceResponse response = new SubmitAttendanceResponse();

	    try {
	        // ═══════════════════════════════════════
	        // STEP 1: CAPTURE SUBMISSION TIME (server-side)
	        // Christ no longer sends startTime/endTime/dateandtime.
	        // We timestamp receipt ourselves — this becomes the
	        // single "submittedAt" used for the jobId, the 20-min
	        // capture window, and the DB start_time/date_and_time.
	        // ═══════════════════════════════════════
	        SimpleDateFormat submittedFormat =
	                new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	        SimpleDateFormat dateOnlyFormat =
	                new SimpleDateFormat("dd/MM/yyyy");
	        Date now = new Date();
	        String submittedAt = submittedFormat.format(now);
	        String startDate = dateOnlyFormat.format(now);

	        // ═══════════════════════════════════════
	        // STEP 2: READ + VALIDATE INPUT
	        // ═══════════════════════════════════════
	        String cameraIp = studentAttendance.getCameraIpAddress();
	        String megBoxIp = studentAttendance.getMegBoxIp();

	        lg.info("submitAIAttendanceData: cameraIpAddress={}, " +
	                "megBoxIp={}, submittedAt={}",
	                cameraIp, megBoxIp, submittedAt);

	        if (cameraIp == null || cameraIp.trim().isEmpty()) {
	            response.setStatus("FAILED");
	            response.setMessage("cameraIpAddress is required");
	            return response;
	        }

	        if (CollectionUtils.isEmpty(studentAttendance.getUuidList())) {
	            response.setStatus("FAILED");
	            response.setMessage("uuidList is required and cannot be empty");
	            return response;
	        }

	        // ═══════════════════════════════════════
	        // STEP 3: RESOLVE MEGBOX IP IF NOT SENT
	        // ═══════════════════════════════════════
	        if (megBoxIp == null || megBoxIp.trim().isEmpty()) {

	            lg.info("MegBox IP not in payload, resolving from " +
	                    "DB for camera: {}", cameraIp);

	            megBoxIp = roomIpService.getMegBoxIpByCameraIp(cameraIp);

	            if (megBoxIp == null || megBoxIp.trim().isEmpty()) {
	                lg.error("MegBox IP not found for camera: {}", cameraIp);
	                response.setStatus("FAILED");
	                response.setMessage(
	                    "MegBox IP not found for Camera IP: " + cameraIp);
	                return response;
	            }

	            lg.info("Resolved MegBox IP: {} for camera: {}",
	                    megBoxIp, cameraIp);

	            studentAttendance.setMegBoxIp(megBoxIp);
	        }

	        // ═══════════════════════════════════════
	        // STEP 4: VALIDATE MEGBOX + CAMERA
	        // ═══════════════════════════════════════
	        String message = validateAndUpdateGroupDetailsByCamIP(
	                megBoxIp, cameraIp);

	        if (message != null && !message.isEmpty()) {
	            response.setStatus("FAILED");
	            response.setMessage(message);
	            return response;
	        }

	        // ═══════════════════════════════════════
	        // STEP 5: IN-PROGRESS CHECK
	        // No client-supplied startTime to match exactly anymore,
	        // so instead: is this camera's most recent session today
	        // still inside its 20-minute capture window?
	        // ═══════════════════════════════════════
	        List<StudentApkDtoResponse> lastSession =
	                dao.getLatestSessionForCamera(cameraIp, startDate);

	        if (CollectionUtils.isNotEmpty(lastSession)) {
	            StudentApkDtoResponse last = lastSession.get(0);
	            try {
	                Date lastSubmittedAt =
	                        submittedFormat.parse(last.getDateandtime());
	                Date windowEnd = new Date(
	                        lastSubmittedAt.getTime() + 20 * 60 * 1000L);

	                if (now.before(windowEnd)) {
	                    response.setStatus("IN_PROGRESS");
	                    response.setMessage(
	                        "Attendance capture already in progress " +
	                        "for Camera IP: " + cameraIp);
	                    return response;
	                }
	            } catch (java.text.ParseException pe) {
	                lg.warn("Could not parse last session timestamp " +
	                        "'{}' for camera {} — allowing new submission",
	                        last.getDateandtime(), cameraIp);
	            }
	        }

	        // ═══════════════════════════════════════
	        // STEP 6: PROCESS ATTENDANCE
	        // ═══════════════════════════════════════
	        String jobId = services.getRecongnizedStudentDetails(
	                studentAttendance, submittedAt);

	        response.setStatus("SUCCESS");
	        response.setJobId(jobId);
	        response.setMessage("Attendance submitted successfully");

	    } catch (Exception e) {
	        lg.error("Error submitAIAttendanceData", e);
	        response.setStatus("ERROR");
	        response.setMessage("Unexpected error: " + e.getMessage());
	    }

	    lg.info("end submitAIAttendanceData: {}", response.getJobId());
	    return response;
	}
	@GetMapping("/triggerNotification")
	public String triggerNotification() {
	    try {
	    	service.sendNotification();
	        return "Notification triggered! Check logs and DB.";
	    } catch (Exception e) {
	        lg.error("Error triggering notification", e);
	        return "Error: " + e.getMessage();
	    }
	}
	@GetMapping(value = "/evidence", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getEvidence(
	        @RequestParam("studentId") String studentId,
	        @RequestParam("date") String date,                       // dd/MM/yyyy
	        @RequestParam(value = "hour", required = false) Integer hour) {

	    String hourLike = (hour == null) ? null
	            : "% " + String.format("%02d", hour) + ":%";

	    List<Map<String, Object>> rows =
	            dao.getEvidenceForStudent(studentId, date, hourLike);

	    List<Map<String, Object>> result = new ArrayList<>();
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

	    for (Map<String, Object> r : rows) {
	        String sessionTime = String.valueOf(r.get("date_and_time"));
	        boolean hasCapture = r.get("capture_full_image_url") != null
	                || r.get("capture_image_url") != null;

	        String status;
	        try {
	            long end = fmt.parse(sessionTime).getTime() + 20 * 60 * 1000L;
	            if (hasCapture) {
	                status = "PRESENT";
	            } else if ("300".equals(String.valueOf(r.get("is_recong_student")))) {
	                status = "UNKNOWN";
	            } else if (System.currentTimeMillis() < end) {
	                status = "IN_PROGRESS";
	            } else {
	                status = "ABSENT";
	            }
	        } catch (Exception e) {
	            status = hasCapture ? "PRESENT" : "ABSENT";
	        }

	        Map<String, Object> item = new LinkedHashMap<>();
	        item.put("cameraIp", r.get("camera_ip"));
	        item.put("sessionTime", sessionTime);
	        item.put("status", status);
	        if (hasCapture) {
	            Map<String, Object> capture = new LinkedHashMap<>();
	            capture.put("recognizedAt", r.get("recognized_at"));
	            capture.put("score", r.get("search_score"));
	            capture.put("faceImageUrl", r.get("capture_image_url"));
	            capture.put("fullImageUrl", r.get("capture_full_image_url"));
	            item.put("bestCapture", capture);
	        }
	        result.add(item);
	    }
	    return result;
	}
}
