package com.iolite.kpcamera.attendance.dao;

import java.util.List;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.GroupDetails;

public interface GroupDetailsInKpDao {

	List<GroupDetails>  getGroupStudentFromKP(String orgId, String name, String startTimeFilter, String endTimeFilter);
	
	List<AttendanceReqestModel> getPresentedStudentData(String data[]);

}
