package com.iolite.kpcamera.attendance.model.responses;

public class StudentResDto {

	private String status;
	private String message;
	private StudentResDataDto data;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public StudentResDataDto getData() {
		return data;
	}
	public void setData(StudentResDataDto data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "StudentResDto [status=" + status + ", message=" + message + ", data=" + data + "]";
	}
	
}
