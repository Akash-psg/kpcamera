package com.iolite.kpcamera.attendance.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iolite.kpcamera.attendance.dao.AcademicDao;
import com.iolite.kpcamera.attendance.service.AcademicYearService;
@Service
public class AcademicServiceImpl implements AcademicYearService{

	@Autowired
	AcademicDao dao;
	/*
	@Override
	public String getCurrentAcademicYear() {
		return dao.getCurrentAcademicYear();
	}
	*/
}
