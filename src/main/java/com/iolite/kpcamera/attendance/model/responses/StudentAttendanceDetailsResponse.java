package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

import com.iolite.kpcamera.attendance.model.StudentAttendanceDetails;

public class StudentAttendanceDetailsResponse {
	
	private String status;
	private String statusCode;
	private Integer subjectId;
	private String periodId;
	private String techerId;
	 private String cameraIp;
	private String classId;
	private String teacherName;
	private String className;
	private String periodName;
	private String subName;
	private String startTime;
	private String endTime;
	private String attDate;
	private String hoursheld;
	private String dateandtime;
	private String attTypeId; 
	private String batchId;
	private String activityId;
	private String userId;
	private String isTT;
	private String version;
	private String substitueClass;
	private String workDiaryActivityId;
	private String totalHours;
	private String remarks;
	private String workDiaryAcademicYear;
	private String attId;
	
	
	
	
	private List<StudentAttendanceDetails>  studentAttendanceDetails;
	
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	
	
	
	
	public String getPeriodId() {
		return periodId;
	}
	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}
	
//	public Integer getRoomId() {
//		return roomId;
//	}
//	public void setRoomId(Integer roomId) {
//		this.roomId = roomId;
//	}
	
	
	 public String getCameraIp() {
	        return cameraIp;
	    }
	    public void setCameraIp(String cameraIp) {
	        this.cameraIp = cameraIp;
	    }
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public String getSubName() {
		return subName;
	}
	public void setSubName(String subName) {
		this.subName = subName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<StudentAttendanceDetails> getStudentAttendanceDetails() {
		return studentAttendanceDetails;
	}
	public void setStudentAttendanceDetails(List<StudentAttendanceDetails> studentAttendanceDetails) {
		this.studentAttendanceDetails = studentAttendanceDetails;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getTecherId() {
		return techerId;
	}
	public void setTecherId(String techerId) {
		this.techerId = techerId;
	}
	public String getAttDate() {
		return attDate;
	}
	public void setAttDate(String attDate) {
		this.attDate = attDate;
	}
	public String getHoursheld() {
		return hoursheld;
	}
	public void setHoursheld(String hoursheld) {
		this.hoursheld = hoursheld;
	}
	public String getDateandtime() {
		return dateandtime;
	}
	public void setDateandtime(String dateandtime) {
		this.dateandtime = dateandtime;
	}
	public String getAttTypeId() {
		return attTypeId;
	}
	public void setAttTypeId(String attTypeId) {
		this.attTypeId = attTypeId;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIsTT() {
		return isTT;
	}
	public void setIsTT(String isTT) {
		this.isTT = isTT;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSubstitueClass() {
		return substitueClass;
	}
	public void setSubstitueClass(String substitueClass) {
		this.substitueClass = substitueClass;
	}
	public String getWorkDiaryActivityId() {
		return workDiaryActivityId;
	}
	public void setWorkDiaryActivityId(String workDiaryActivityId) {
		this.workDiaryActivityId = workDiaryActivityId;
	}
	public String getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getWorkDiaryAcademicYear() {
		return workDiaryAcademicYear;
	}
	public void setWorkDiaryAcademicYear(String workDiaryAcademicYear) {
		this.workDiaryAcademicYear = workDiaryAcademicYear;
	}
	public String getAttId() {
		return attId;
	}
	public void setAttId(String attId) {
		this.attId = attId;
	}
}
