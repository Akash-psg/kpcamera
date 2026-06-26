package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.StudentDto;
import com.iolite.kpcamera.attendance.util.OrganizationUtil;
import com.iolite.kpcamera.attendance.util.RoleEnum;

public class StudentRequestMapper implements RowMapper<StudentDto>{
	static final Logger lg = LoggerFactory.getLogger(StudentRequestMapper.class);

	@Override
	public StudentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		StudentDto studentDto = new StudentDto();
		studentDto.setOrg_personnel_id(rs.getString(1));
		studentDto.setEmail_id(rs.getString(3));
		studentDto.setRole(RoleEnum.STUDENT.toString());
		studentDto.setStatus(OrganizationUtil.STATUS);
		return studentDto;
	}

}
