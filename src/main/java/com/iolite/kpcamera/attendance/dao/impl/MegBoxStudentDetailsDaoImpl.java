package com.iolite.kpcamera.attendance.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.MegboxStudentDetailsDao;
import com.iolite.kpcamera.attendance.mapper.MegBoxStudentDetailsMapper;
import com.iolite.kpcamera.attendance.model.MegBoxStudentDetailsDto;

@Repository
public class MegBoxStudentDetailsDaoImpl implements MegboxStudentDetailsDao{

	@Autowired
	private JdbcTemplate middleWareJDBC;
		
		 @Override
		    public Map<String, String> getMegboxStudentDetails(
		            String orgId, String ip) {
	
		        String MIDDLEWARE_STUDNET_QUERY =
		                "select student_id as sid, " +
		                "face_token as facetoken " +
		                "from faces " +
		                "where is_active = 1 " +
		                "and megbox_ip = '" + ip + "'" +
		                " and face_token IS NOT NULL" +
		                " and face_token != ''";
	
		        List<MegBoxStudentDetailsDto> dtoList =
		                middleWareJDBC.query(
		                        MIDDLEWARE_STUDNET_QUERY,
		                        new MegBoxStudentDetailsMapper());
	
		        // ← Changed: Map<String, String>
		        Map<String, String> studentTokenMap =
		                dtoList.stream().collect(
		                        Collectors.toMap(
		                                MegBoxStudentDetailsDto::getStudentId,
		                                MegBoxStudentDetailsDto::getFaceToken));
	
		        return studentTokenMap;
		    }
	

}
