package com.iolite.kpcamera.attendance.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.AcademicDao;
import com.iolite.kpcamera.attendance.mapper.AcademicYearMapper;
import com.iolite.kpcamera.attendance.model.AcademicYear;
@Repository
public class AcademicDaoImpl implements AcademicDao{
	/*
	@Autowired
	private JdbcTemplate cristMysoreJDBC;
	
	@Override
	public String getCurrentAcademicYear() {
		String year = null;
		List<AcademicYear> academicYearList = cristMysoreJDBC.query("select * from academic_year where is_current = 1;", new AcademicYearMapper());
		for(AcademicYear accYear : academicYearList) {
			year = accYear.getAcademicYear();
		}
		return year;
	}
	*/

}
