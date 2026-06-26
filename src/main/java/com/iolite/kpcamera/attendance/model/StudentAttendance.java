package com.iolite.kpcamera.attendance.model;

import java.util.List;

import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceResponse;

public class StudentAttendance {

	
	private Integer subjectId;
	private String periodId;
	private String teacherId;
	private String roomId;
	private Integer studentId;
	private String startTime;
	private String endTime;
	private String classId;
	private String teacherName;
	private String className;
	private String periodName;
	private String subName;
	private Integer floorNo;
	private String megBoxIp;
	private String token;
	private String attDate;
	private String hoursheld;
	private String dateandtime;
	private String attTypeId;
	private String batchId;
	private String activityId;
	private String userId;
	private String cameraIp;
	private String isTT;
	private String version;
	private String substitueClass;
	private String workDiaryActivityId;
	private String totalHours;
	private String remarks;
	private String workDiaryAcademicYear;
	private String attId;

	private List<StudentAttendanceResponse> StudentAttendanceDetails;

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

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
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

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getCameraIp() {
	    return cameraIp;
	}
	public void setCameraIp(String cameraIp) {
	    this.cameraIp = cameraIp;
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

	public Integer getFloorNo() {
		return floorNo;
	}

	public void setFloorNo(Integer floorNo) {
		this.floorNo = floorNo;
	}

	public String getMegBoxIp() {
		return megBoxIp;
	}

	public void setMegBoxIp(String megBoxIp) {
		this.megBoxIp = megBoxIp;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public List<StudentAttendanceResponse> getStudentAttendanceDetails() {
		return StudentAttendanceDetails;
	}

	public void setStudentAttendanceDetails(List<StudentAttendanceResponse> studentAttendanceDetails) {
		StudentAttendanceDetails = studentAttendanceDetails;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RecongnizedAttendanceRequestDto [subjectId=");
		builder.append(subjectId);
		builder.append(", periodId=");
		builder.append(periodId);
		builder.append(", teacherId=");
		builder.append(teacherId);
		builder.append(", roomId=");
		builder.append(roomId);
		builder.append(", studentId=");
		builder.append(studentId);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", classId=");
		builder.append(classId);
		builder.append(", teacherName=");
		builder.append(teacherName);
		builder.append(", className=");
		builder.append(className);
		builder.append(", periodName=");
		builder.append(periodName);
		builder.append(", subName=");
		builder.append(subName);
		builder.append(", floorNo=");
		builder.append(floorNo);
		builder.append(", megBoxIp=");
		builder.append(megBoxIp);
		builder.append(", token=");
		builder.append(token);
		builder.append(", attDate=");
		builder.append(attDate);
		builder.append(", hoursheld=");
		builder.append(hoursheld);
		builder.append(", dateandtime=");
		builder.append(dateandtime);
		builder.append(", attTypeId=");
		builder.append(attTypeId);
		builder.append(", batchId=");
		builder.append(batchId);
		builder.append(", activityId=");
		builder.append(activityId);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", isTT=");
		builder.append(isTT);
		builder.append(", version=");
		builder.append(version);
		builder.append(", substitueClass=");
		builder.append(substitueClass);
		builder.append(", workDiaryActivityId=");
		builder.append(workDiaryActivityId);
		builder.append(", totalHours=");
		builder.append(totalHours);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", workDiaryAcademicYear=");
		builder.append(workDiaryAcademicYear);
		builder.append(", attId=");
		builder.append(attId);
		builder.append(", StudentAttendanceDetails=");
		builder.append(StudentAttendanceDetails);
		builder.append("]");
		return builder.toString();
	}
	
	
}
