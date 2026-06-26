package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;

public class TeacherMarkedAttendenceMapper implements RowMapper<AttendanceReqestModel>{

	@Override
	public AttendanceReqestModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		AttendanceReqestModel response = new AttendanceReqestModel();
		if(rs.getString("isPresent") != null && rs.getString("isPresent").equals("1")) {
			response.setIsRecongStudent("100");
		}else {
			response.setIsRecongStudent("200");
		}
		response.setTeacherName(rs.getString("userName"));
		response.setClassName(rs.getString("clsName"));
		response.setPeriodName(rs.getString("pName"));
		response.setStudentName(rs.getString("sName"));
		response.setSubName(rs.getString("subName"));
		response.setRegNo(rs.getString("regNo"));
		return response;
	}

}
