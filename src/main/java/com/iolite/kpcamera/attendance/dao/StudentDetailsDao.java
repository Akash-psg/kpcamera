package com.iolite.kpcamera.attendance.dao;
import java.util.List;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.ScheduleDto;
import com.iolite.kpcamera.attendance.model.StudentDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentResDataDto;

public interface StudentDetailsDao {

	List<StudentDto> getStudentDetails();
	
	List<StudentResDataDto> getAwsPersonalId(int orgId) throws Exception;
	
	List<ScheduleDto> getActivityInfo(int orgId) throws Exception;

	List<StudentDto> getActivityPersonalData(String org_activity_id, int orgId);
	
	List<StudentApkDtoResponse> getStudentDetailsforAPK();
	
	
	List<AttendanceReqestModel> getTeacherMarkedAttendanceData(StudentApkDtoResponse studentApkDtoResponse);
	
}
