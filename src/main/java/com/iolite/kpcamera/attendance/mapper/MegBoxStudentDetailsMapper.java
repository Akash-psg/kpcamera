package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.MegBoxStudentDetailsDto;

public class MegBoxStudentDetailsMapper implements RowMapper<MegBoxStudentDetailsDto>{

	@Override
    public MegBoxStudentDetailsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        MegBoxStudentDetailsDto dto = new MegBoxStudentDetailsDto();
        dto.setStudentId(rs.getString("sid"));        // ← no more Integer.parseInt
        dto.setFaceToken(rs.getString("facetoken"));
        return dto;
    }

}
