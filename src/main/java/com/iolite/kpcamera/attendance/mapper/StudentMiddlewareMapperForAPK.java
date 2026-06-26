package com.iolite.kpcamera.attendance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;

public class StudentMiddlewareMapperForAPK implements RowMapper<StudentApkDtoResponse>{


//	@Override
//	public StudentApkDtoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
//		StudentApkDtoResponse response = new StudentApkDtoResponse();
//		response.setStudentId(rs.getString("student_id"));
//		response.setRoomId(Integer.parseInt(rs.getString("room_id")));
//		response.setPeriodId(rs.getString("period_id"));
//		response.setSubjectId(Integer.parseInt(rs.getString("subject_id")));
//		response.setTecherId(rs.getString("teacher_id"));
//		response.setStartTime(rs.getString("start_time"));
//		response.setEndTime(rs.getString("end_time"));
//		response.setIsRecongStudent(rs.getString("is_recong_student"));
//		response.setClassId(rs.getString("class_id"));
//		response.setTeacherName(rs.getString("teacher_name"));
//		response.setClassName(rs.getString("class_name"));
//		response.setPeriodName(rs.getString("period_name"));
//		response.setStudentName(rs.getString("student_name"));
//		response.setSubName(rs.getString("subject_name"));
//		response.setRegno(rs.getString("reg_no"));
//		response.setReqReceivedTime(Integer.parseInt(rs.getString("req_received_time")));
//		response.setMegBoxIp(rs.getString("meg_box_ip"));
//		response.setToken(rs.getString("token"));
//		response.setIs_on_leave(rs.getString("is_on_leave"));
//		response.setIs_cocurricular_leave(rs.getString("is_cocurricular_leave"));
//		response.setAttDate(rs.getString("att_date"));
//		response.setAttId(rs.getString("att_id"));
//		response.setHoursheld(rs.getString("hourshe_id"));
//		response.setDateandtime(rs.getString("date_and_time"));
//		response.setAttTypeId(rs.getString("att_type_id"));
//		response.setUserId(rs.getString("user_id"));
//		response.setIsTT(rs.getString("is_tt"));
//		response.setVersion(rs.getString("version"));
//		response.setSubstitueClass(rs.getString("substitue_class"));
//		response.setWorkDiaryActivityId(rs.getString("work_diary_activity_id"));
//		response.setTotalHours(rs.getString("total_ours"));
//		response.setRemarks(rs.getString("remarks"));
//		return response;
//	}
	
	// RoomId - CameraIp Modification
	public StudentApkDtoResponse mapRow(
	        ResultSet rs, int rowNum)
	        throws SQLException {

	    StudentApkDtoResponse response =
	            new StudentApkDtoResponse();

	    response.setStudentId(
	            rs.getString("student_id"));

	    // ← FIXED: NULL safe for room_id
	    try {
	        String roomId = rs.getString("room_id");
	        if (roomId != null && !roomId.isEmpty()) {
	            response.setRoomId(
	                    Integer.parseInt(roomId));
	        }
	    } catch (Exception e) {
	        response.setRoomId(null);
	    }

	    response.setPeriodId(
	            rs.getString("period_id"));

	    // ← FIXED: NULL safe for subject_id
	    try {
	        String subjectId = rs.getString("subject_id");
	        if (subjectId != null && !subjectId.isEmpty()) {
	            response.setSubjectId(
	                    Integer.parseInt(subjectId));
	        }
	    } catch (Exception e) {
	        response.setSubjectId(null);
	    }

	    response.setTecherId(
	            rs.getString("teacher_id"));
	    response.setStartTime(
	            rs.getString("start_time"));
	    response.setEndTime(
	            rs.getString("end_time"));
	    response.setIsRecongStudent(
	            rs.getString("is_recong_student"));
	    response.setClassId(
	            rs.getString("class_id"));
	    response.setTeacherName(
	            rs.getString("teacher_name"));
	    response.setClassName(
	            rs.getString("class_name"));
	    response.setPeriodName(
	            rs.getString("period_name"));
	    response.setStudentName(
	            rs.getString("student_name"));
	    response.setSubName(
	            rs.getString("subject_name"));
	    response.setRegno(
	            rs.getString("reg_no"));

	    // ← FIXED: NULL safe for req_received_time
	    try {
	        String reqTime =
	                rs.getString("req_received_time");
	        if (reqTime != null && !reqTime.isEmpty()) {
	            response.setReqReceivedTime(
	                    Integer.parseInt(reqTime));
	        }
	    } catch (Exception e) {
	        response.setReqReceivedTime(0);
	    }

	    response.setMegBoxIp(
	            rs.getString("meg_box_ip"));
	    response.setToken(
	            rs.getString("token"));
	    response.setIs_on_leave(
	            rs.getString("is_on_leave"));
	    response.setIs_cocurricular_leave(
	            rs.getString("is_cocurricular_leave"));
	    response.setAttDate(
	            rs.getString("att_date"));
	    response.setAttId(
	            rs.getString("att_id"));
	    response.setHoursheld(
	            rs.getString("hourshe_id"));
	    response.setDateandtime(
	            rs.getString("date_and_time"));
	    response.setAttTypeId(
	            rs.getString("att_type_id"));
	    response.setUserId(
	            rs.getString("user_id"));
	    response.setIsTT(
	            rs.getString("is_tt"));
	    response.setVersion(
	            rs.getString("version"));
	    response.setSubstitueClass(
	            rs.getString("substitue_class"));
	    response.setWorkDiaryActivityId(
	            rs.getString("work_diary_activity_id"));
	    response.setTotalHours(
	            rs.getString("total_ours"));
	    response.setRemarks(
	            rs.getString("remarks"));

	    // ← camera_ip
	    try {
	        response.setCameraIp(
	                rs.getString("camera_ip"));
	    } catch (SQLException e) {
	        response.setCameraIp(null);
	    }
	    try {
	        String groupName = rs.getString("group_name");
	        // fallback: if null, strip dots from camera_ip
	        if (groupName == null || groupName.isEmpty()) {
	            String camIp = rs.getString("camera_ip");
	            groupName = (camIp != null)
	                    ? camIp.replace(".", "") : "";
	        }
	        response.setGroupName(groupName);
	    } catch (SQLException e) {
	        response.setGroupName("");
	    }

	    return response;
	}

}
