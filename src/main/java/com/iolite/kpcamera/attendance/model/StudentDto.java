package com.iolite.kpcamera.attendance.model;

public class StudentDto {

	private String org_personnel_id;
	private String email_id;
	private String role;
	private String status;
	public String getOrg_personnel_id() {
		return org_personnel_id;
	}
	public void setOrg_personnel_id(String org_personnel_id) {
		this.org_personnel_id = org_personnel_id;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "StudentDto [org_personnel_id=" + org_personnel_id + ", email_id=" + email_id + ", role=" + role
				+ ", status=" + status + "]";
	}
	
	

}
