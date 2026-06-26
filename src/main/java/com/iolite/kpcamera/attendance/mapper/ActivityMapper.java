package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.ScheduleDto;
import com.iolite.kpcamera.attendance.util.OrganizationUtil;

public class ActivityMapper implements RowMapper<ScheduleDto> {

	@Override
	public ScheduleDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ScheduleDto dto = new ScheduleDto();
		dto.setOrg_activity_id(rs.getString("subject_id")+OrganizationUtil.SPLIT+
				rs.getString("pid")+OrganizationUtil.SPLIT+rs.getString("week_day")+OrganizationUtil.SPLIT+
				rs.getString("teacherId"));
		dto.setActivity_type(rs.getString("subject"));
		dto.setActivity_desc(rs.getString("subject")+"("+rs.getString("code")+")");
		dto.setStart_date(rs.getString("start_date"));
		dto.setEnd_date(rs.getString("end_date"));
		dto.setStatus(OrganizationUtil.STATUS);
		dto.setStartTime(rs.getString("start_time"));
		dto.setEndTime(rs.getString("end_time"));
		dto.setRoomId(rs.getString("roomid"));
		
		return dto;
	}
}
