package com.iolite.kpcamera.attendance.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceDetailsResponse;

public interface AttendanceService {

	StudentAttendanceDetailsResponse getStudentAttendance(String jobId, Date teacherSubmittedTime, Date attendanceCapturedEndTime);

	void clearDuplicateData();
	
	public List<StudentApkDtoResponse> getStudentAttendanceFromCurrentDatetoLastOneWeek() throws Exception;
	
	public List<AttendanceReqestModel> getStudentAttendanceForUI(String jobId, Date teacherSubmittedTime, Date attendanceCapturedEndTime, String megBoxIp) throws Exception;
	
	void sendNotification() throws ParseException;

}
