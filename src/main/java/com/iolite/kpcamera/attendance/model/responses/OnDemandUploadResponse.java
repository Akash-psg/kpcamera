package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

public class OnDemandUploadResponse {
    
    private String status;
    private String studentId;
    private String message;
    private List<String> uploadedTo;
    private List<String> failedMegboxIps;


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }


    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getUploadedTo() { return uploadedTo; }
    public void setUploadedTo(List<String> uploadedTo) { 
        this.uploadedTo = uploadedTo; 
    }

    public List<String> getFailedMegboxIps() { return failedMegboxIps; }
    public void setFailedMegboxIps(List<String> failedMegboxIps) { 
        this.failedMegboxIps = failedMegboxIps; 
    }
}