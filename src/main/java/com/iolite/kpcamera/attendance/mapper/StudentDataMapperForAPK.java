package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;

public class StudentDataMapperForAPK implements RowMapper<StudentApkDtoResponse>{

	@Override
	public StudentApkDtoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
		StudentApkDtoResponse response = new StudentApkDtoResponse();
		response.setStudentId(rs.getString("studentid"));
		//response.setName(rs.getString("studentName"));
		response.setRegno(rs.getString("studentName")+"_"+rs.getString("regno"));
		return response;
	}

}
