package com.iolite.kpcamera.attendance.model;

public class MegBoxStudentDetailsDto {

	
	private String studentId;
	private String faceToken;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getFaceToken() {
		return faceToken;
	}
	public void setFaceToken(String faceToken) {
		this.faceToken = faceToken;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MegBoxStudentDetailsDto [studentId=");
		builder.append(studentId);
		builder.append(", faceToken=");
		builder.append(faceToken);
		builder.append("]");
		return builder.toString();
	}
}
