package com.iolite.kpcamera.attendance.model.responses;

public class AttendanceResponse {

	
	private String statusCode;
	private String status;
	private String message;
	private String org_id;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
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
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	@Override
	public String toString() {
		return "AttendanceResponse [statusCode=" + statusCode + ", status=" + status + ", message=" + message
				+ ", org_id=" + org_id + "]";
	}
	
	
}
