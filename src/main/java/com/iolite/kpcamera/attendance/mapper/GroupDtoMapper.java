package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.GroupDetails;

public class GroupDtoMapper implements RowMapper<GroupDetails>{

	@Override
	public GroupDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		GroupDetails dto = new GroupDetails();
		dto.setSubjectId(rs.getInt("subject_id"));
		dto.setPeriodId(String.valueOf(rs.getInt("pid")));
		dto.setTeacherId("teacherId");
		dto.setRoomId(rs.getString("roomid"));
		dto.setStudentId(rs.getInt("stuid"));
		dto.setStartTime(rs.getString("start_time"));
		dto.setEndTime(rs.getString("end_time"));
		dto.setClassId(String.valueOf(rs.getInt("classId")));
		dto.setTeacherName(rs.getString("techname"));
		dto.setClassName(rs.getString("clsname"));
		dto.setPeriodName(rs.getString("pname"));
		dto.setStudentName(rs.getString("stuname"));
		dto.setRegNo(rs.getString("regno"));
		dto.setSubName(rs.getString("subject"));
		return dto;
	}

}
