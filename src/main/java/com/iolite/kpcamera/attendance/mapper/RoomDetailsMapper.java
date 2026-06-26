package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;

public class RoomDetailsMapper implements RowMapper<RoomIpOutbound>{

	@Override
	public RoomIpOutbound mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoomIpOutbound dto = new RoomIpOutbound();
		dto.setId(rs.getInt("id"));
		dto.setBoxIp(rs.getString("box_ip"));
		dto.setDesc(rs.getString("discription"));
		return dto;
	}

}