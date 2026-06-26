package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.responses.StudentResDataDto;

public class StudentMiddlewareMapper implements RowMapper<StudentResDataDto>{

	@Override
	public StudentResDataDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		StudentResDataDto dto =new StudentResDataDto();
		dto.setPersonnel_id(rs.getString("pId"));
		dto.setOrg_personnel_id(rs.getString("sId"));
		return dto;
	}

}
