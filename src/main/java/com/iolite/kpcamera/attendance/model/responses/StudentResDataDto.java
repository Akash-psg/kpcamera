package com.iolite.kpcamera.attendance.model.responses;

public class StudentResDataDto {

	private String org_id;
	private String personnel_id;
	private String org_personnel_id;
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public String getPersonnel_id() {
		return personnel_id;
	}
	public void setPersonnel_id(String personnel_id) {
		this.personnel_id = personnel_id;
	}
	public String getOrg_personnel_id() {
		return org_personnel_id;
	}
	public void setOrg_personnel_id(String org_personnel_id) {
		this.org_personnel_id = org_personnel_id;
	}
	
	@Override
	public String toString() {
		return "StudentResDataDto [org_id=" + org_id + ", personnel_id=" + personnel_id + ", org_personnel_id="
				+ org_personnel_id + "]";
	}
}
