package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iolite.kpcamera.attendance.model.CustomCocurricularLeaveDeserializer;

public class StudentAttendanceResponse {

	private String studentId;
	private String regNo;
	private String studentName;
	private String is_on_leave;
	@JsonDeserialize(using = CustomCocurricularLeaveDeserializer.class)
	private List<String> is_cocurricular_leave;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getIs_on_leave() {
		return is_on_leave;
	}
	public void setIs_on_leave(String is_on_leave) {
		this.is_on_leave = is_on_leave;
	}
	public List<String> getIs_cocurricular_leave() {
		return is_cocurricular_leave;
	}
	public void setIs_cocurricular_leave(List<String> is_cocurricular_leave) {
		this.is_cocurricular_leave = is_cocurricular_leave;
	}
}