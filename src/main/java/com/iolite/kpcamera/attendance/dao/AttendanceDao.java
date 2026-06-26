package com.iolite.kpcamera.attendance.dao;

import java.util.List;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;

public interface AttendanceDao {

	int[][] saveAttendance(List<AttendanceReqestModel>  attendanceReqList,int size);
    
	List<StudentApkDtoResponse> getStudentAttendance(
            String cameraIp,   // ← parameter name change
            String subjectId, 
            String startDate);
	
	public List<StudentApkDtoResponse> getStudentAttendanceBasedOnDate(String startDate);

	List<StudentApkDtoResponse> getStudentAttendanceAndTokenBasedOnDate(String startDate);
	
	public List<StudentApkDtoResponse> getStudentAttendanceFromCurrentDatetoLastOneWeek() throws Exception;
	
	 int[][] saveAttendanceNew(List<AttendanceReqestModel> attendanceReqList, int size);
	 List<StudentApkDtoResponse> getStudentAttendanceByCamAndTime(
		        String cameraIp,
		        String startTime,
		        String startDate);
	 void updateStudentStatusToUnknown(
		        List<String> studentIds,
		        String status);
	 List<StudentApkDtoResponse> getStudentsByCamAndTime(
		        String cameraIp, String startTime, String startDate);
	 
	 void saveAbsentBackup(String status, String fileName, String fileContent);

}