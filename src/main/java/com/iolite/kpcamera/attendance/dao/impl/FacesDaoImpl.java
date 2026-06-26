package com.iolite.kpcamera.attendance.dao.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.FacesDao;
import com.iolite.kpcamera.attendance.model.responses.FacesResponseDto;

@Repository
public class FacesDaoImpl implements FacesDao{
	
	static final Logger lg = LoggerFactory.getLogger(FacesDaoImpl.class); 

	@Autowired
	private JdbcTemplate middleWareJDBC;
	
	@Override
	public boolean saveStudentFacesDetails(FacesResponseDto facesResDto, String studentId, Integer orgId, String megBoxIp) {
	    boolean isdded = true;
	    lg.debug("inside saveStudentFacesDetails studentId : {}, face_token : {}", studentId, facesResDto.getData().getFaceToken());
	    try {
	        middleWareJDBC.update(
	            "INSERT INTO faces(student_id,image_id,face_token,orgId,created_date,last_updated_date,is_active,is_upload_failed, megbox_ip) VALUES(?,?,?,?,?,?,?,?,?)",
	            studentId,   // ← String, no more Integer.parseInt
	            facesResDto.getData().getImageId(),
	            facesResDto.getData().getFaceToken(),
	            orgId,
	            new Date(),
	            new Date(),
	            true,
	            false,
	            megBoxIp);
	        lg.debug("end of saveStudentFacesDetails studentId : {}, face_token : {}", studentId, facesResDto.getData().getFaceToken());
	    } catch (Exception e) {
	        isdded = false;
	        lg.error("face details entry failed", e);
	    }
	    return isdded;
	}

	@Override
	public void failedStudentImage(String studentId, 
	                               String megBoxIp) {
	    lg.debug("inside failedStudentImage studentId: {}, megBoxIp: {}", 
	             studentId, megBoxIp);
	    try {
	        if (studentId == null 
	                || studentId.trim().isEmpty()) {
	            lg.error("studentId is null or empty");
	            return;
	        }

	        middleWareJDBC.update(
	            "INSERT INTO faces(student_id,orgId,created_date,last_updated_date,is_active,is_upload_failed,megbox_ip) VALUES(?,?,?,?,?,?,?)",
	            Integer.parseInt(studentId),
	            1,
	            new Date(),
	            new Date(),
	            true,
	            true,
	            megBoxIp);

	        lg.debug("end of failedStudentImage");

	    } catch (Exception e) {
	        lg.error("face details failedStudentImage failed", e);
	    }
	}
	
	
}
