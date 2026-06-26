package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RoomAndRoomDetailsMapper implements RowMapper<RoomAndRoomDetailsMapperDto>{

	@Override
	public RoomAndRoomDetailsMapperDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoomAndRoomDetailsMapperDto dto = new RoomAndRoomDetailsMapperDto();
		dto.setId(rs.getInt("id"));
		dto.setBoxIp(rs.getString("boxIp"));
		dto.setDescription(rs.getString("description"));
		dto.setFloor(rs.getString("floor"));
		dto.setGroupName(rs.getString("groupName"));
		dto.setCameraIp(rs.getString("cameraIp"));
		return dto;
	}

}
