package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.AcademicYear;

public class AcademicYearMapper implements RowMapper<AcademicYear>{

	@Override
	public AcademicYear mapRow(ResultSet rs, int rowNum) throws SQLException {
		AcademicYear year = new AcademicYear();
		year.setAcademicYear(rs.getString("year"));
		return year;
	}

}
