package com.iolite.kpcamera.attendance.model;

public class StudentAttendanceDetails {

	private String studentName;
	private String isRecongStudent;
	private String studentId;
	private String regNo;
	private String isOnleave;
	private String isCocurricularLeave;
	
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getIsRecongStudent() {
		return isRecongStudent;
	}
	public void setIsRecongStudent(String isRecongStudent) {
		this.isRecongStudent = isRecongStudent;
	}
	public String  getStudentId() {
		return studentId;
	}
	public void setStudentId(String  studentId) {
		this.studentId = studentId;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getIsOnleave() {
		return isOnleave;
	}
	public void setIsOnleave(String isOnleave) {
		this.isOnleave = isOnleave;
	}
	public String getIsCocurricularLeave() {
		return isCocurricularLeave;
	}
	public void setIsCocurricularLeave(String isCocurricularLeave) {
		this.isCocurricularLeave = isCocurricularLeave;
	}
}
