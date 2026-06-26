package com.iolite.kpcamera.attendance.model.responses;



public class PreSignedUrlDto {

	  private String studentId;
	  private String preSignedUrl;

	    public String getStudentId() {
	        return studentId;
	    }

	    public void setStudentId(String studentId) {
	    	this.studentId = studentId;

	    }

	    public String getPreSignedUrl() {
	        return preSignedUrl;
	    }

	    public void setPreSignedUrl(String preSignedUrl) {
	        this.preSignedUrl = preSignedUrl;

	    }

	}
