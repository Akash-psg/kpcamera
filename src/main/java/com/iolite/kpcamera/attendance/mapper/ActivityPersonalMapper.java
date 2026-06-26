package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.StudentDto;

public class ActivityPersonalMapper implements RowMapper<StudentDto>{

	@Override
	public StudentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		StudentDto dto = new StudentDto();
		dto.setOrg_personnel_id(rs.getString("stuid"));
		return dto;
	}

}
