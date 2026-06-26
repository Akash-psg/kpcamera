package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.responses.RoomResDataDto;

public class GetRoomDetailsMapper implements RowMapper<RoomResDataDto>{

	@Override
	public RoomResDataDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoomResDataDto dto = new RoomResDataDto();
		dto.setOrg_room_id(rs.getString("org_room_id"));
		dto.setRoom_id(rs.getString("room_id"));
		return dto;
	}

}
