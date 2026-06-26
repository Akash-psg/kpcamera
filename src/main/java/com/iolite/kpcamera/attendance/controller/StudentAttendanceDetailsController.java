package com.iolite.kpcamera.attendance.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceDetailsResponse;
import com.iolite.kpcamera.attendance.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.File;


@RestController
@RequestMapping("/v1/studentattendancedetails")
public class StudentAttendanceDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(StudentAttendanceDetailsController.class);

    @Autowired
    private AttendanceService attendanceService;
    
    @Value("${csv.file.path}")
    private String csvFilePath;

    @GetMapping(value = "/getstudentattendance")
    public ResponseEntity<List<StudentApkDtoResponse>> getStudentAttendanceDetails() {
        try {
            logger.info("Fetching student attendance details for the last 7 days.");
            List<StudentApkDtoResponse> response = attendanceService.getStudentAttendanceFromCurrentDatetoLastOneWeek();

            if (response.isEmpty()) {
                logger.warn("No student attendance records found.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching student attendance details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/getstudentdetails")
    public List<AttendanceReqestModel> getStudentDetails(
            @RequestParam Long recordId,
            @RequestParam String startDate,
            @RequestParam String dateandtime,
            @RequestParam String startTime,
            @RequestParam Long roomId,
            @RequestParam String subName,
            @RequestParam Long subId,
            @RequestParam String megBoxIp) throws Exception {
    	SimpleDateFormat teacherScanTimeFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
		Date teacherSubmittedStartTime = teacherScanTimeFormat.parse(dateandtime);
		String roomSubIds = roomId + "@" + subId;
		Date attendanceCapturedEndTime = teacherScanTimeFormat.parse(dateandtime);
		attendanceCapturedEndTime.setMinutes(attendanceCapturedEndTime.getMinutes()+20);
		return attendanceService.getStudentAttendanceForUI(roomSubIds, teacherSubmittedStartTime, attendanceCapturedEndTime, megBoxIp);
    }
    
    @GetMapping("/downloadcsv")
    public ResponseEntity<Resource> downloadCsv(
            @RequestParam(required = false) String date,
            @RequestParam String periodName,
            @RequestParam String subName,
            @RequestParam String className,
            @RequestParam(required = false, defaultValue = "false") boolean isTeacherMarked) {
        try {
            if (date == null || date.isEmpty()) {
                date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            }

            // Reconstruct path: csvFilePath + date + "\" + periodName + "\" + [teacher_]subName_className.csv
            String fileName = (isTeacherMarked ? "teacher_" : "") + subName + "_" + className + ".csv";
            String fullPath = csvFilePath + date + "\\" + periodName + "\\" + fileName;
            
            File file = new File(fullPath);
            if (!file.exists()) {
                logger.warn("CSV file not found: {}", fullPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading CSV file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
