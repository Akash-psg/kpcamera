package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

public class ApkAttendanceResponse {

	private List<StudentApkDtoResponse> daoList;
	private String status;

	public List<StudentApkDtoResponse> getDaoList() {
		return daoList;
	}

	public void setDaoList(List<StudentApkDtoResponse> daoList) {
		this.daoList = daoList;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ApkAttendanceResponse [daoList=" + daoList + "]";
	}

	
	
}
