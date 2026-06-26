package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.RoomReqDto;

public class RoomMapper implements RowMapper<RoomReqDto>{

	@Override
	public RoomReqDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoomReqDto dto = new RoomReqDto();
		dto.setOrg_room_id(rs.getString("ID"));
		dto.setRoom_no(rs.getString("ROOM_NO"));
		dto.setFloor_no(rs.getString("FLOOR_NO"));
		return dto;
	}

}
