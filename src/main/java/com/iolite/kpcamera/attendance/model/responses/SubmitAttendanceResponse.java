package com.iolite.kpcamera.attendance.model.responses;

public class SubmitAttendanceResponse {
    private String status;
    private String jobId;
    private String message;

    // getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) {
        this.jobId = jobId; }
    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message; }
}
