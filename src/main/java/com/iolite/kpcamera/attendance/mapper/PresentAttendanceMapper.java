package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;

public class PresentAttendanceMapper implements RowMapper<AttendanceReqestModel>{

	@Override
	public AttendanceReqestModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		AttendanceReqestModel model = new AttendanceReqestModel();
		model.setStudentId(rs.getString("stuid"));
		model.setRegNo(rs.getString("regNo"));
		model.setPeriodName(rs.getString("pname"));
		model.setSubName(rs.getString("subname"));
		model.setClassName(rs.getString("clsname"));
		if(rs.getBoolean("ispresent")) {
			model.setIsRecongStudent("100");
		}
		else {
			model.setIsRecongStudent("200");
		}
		return model;
	}

}
