package com.iolite.kpcamera.attendance.model;

import java.util.List;

public class StudentAttendance {

	private Integer subjectId;
	private String periodId;
	private String teacherId;
	private String roomId;
	private Integer studentId;
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
	private String attTypeId;
	private String batchId;
	private String activityId;
	private String userId;
	private String cameraIpAddress;   // ← RENAMED from cameraIp
	private String isTT;
	private String version;
	private String substitueClass;
	private String workDiaryActivityId;
	private String totalHours;
	private String remarks;
	private String workDiaryAcademicYear;
	private String attId;

	// ← NEW: replaces studentAttendanceDetails
	private List<String> uuidList;

	// startTime, endTime, dateandtime REMOVED —
	// server now captures submission time itself.
	// StudentAttendanceDetails list REMOVED — replaced by uuidList.

	public List<String> getUuidList() {
		return uuidList;
	}

	public void setUuidList(List<String> uuidList) {
		this.uuidList = uuidList;
	}

	public String getCameraIpAddress() {
		return cameraIpAddress;
	}

	public void setCameraIpAddress(String cameraIpAddress) {
		this.cameraIpAddress = cameraIpAddress;
	}

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

	@Override
	public String toString() {
		return "StudentAttendance [cameraIpAddress=" + cameraIpAddress
				+ ", megBoxIp=" + megBoxIp
				+ ", uuidList=" + uuidList + "]";
	}
}