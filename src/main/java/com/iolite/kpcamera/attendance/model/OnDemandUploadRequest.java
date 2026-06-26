	package com.iolite.kpcamera.attendance.model;
	
	public class OnDemandUploadRequest {
	
	    private String studentId;
	    private String photo; // ← Base64 string
	
	    public String getStudentId() { return studentId; }
	    public void setStudentId(String studentId) {
	        this.studentId = studentId;
	    }
	

	    public String getPhoto() { return photo; }
	    public void setPhoto(String photo) { 
	        this.photo = photo; 
	    }
	}