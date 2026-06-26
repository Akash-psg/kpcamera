package com.iolite.kpcamera.attendance.dao.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iolite.kpcamera.attendance.dao.StudentDetailsDao;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService{
	
	@Autowired
	private StudentDetailsDao studentDao;
	
	@Override
	public Map<String, String> getStudentDetailsforAPK() {
		Map<String, String> map = studentDao.getStudentDetailsforAPK().stream().collect(Collectors.toMap(StudentApkDtoResponse::getStudentId,
				StudentApkDtoResponse::getRegno));
		return map;
	}
}
