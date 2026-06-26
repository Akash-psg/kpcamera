package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;

public class StudentAttendanceWeekDataMapper implements RowMapper<StudentApkDtoResponse> {

	@Override
	public StudentApkDtoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
		StudentApkDtoResponse response = new StudentApkDtoResponse();
		response.setStartDate(rs.getString("start_date"));
		response.setDateandtime(rs.getString("date_and_time"));
		response.setRoomId(Integer.parseInt(rs.getString("room_id")));
		response.setStartTime(rs.getString("start_time"));
		response.setSubName(rs.getString("subject_name"));
		response.setId(rs.getInt("id"));
		response.setSubjectId(rs.getInt("subject_id"));
		response.setMegBoxIp(rs.getString("meg_box_ip"));
		return response;
	}

}
