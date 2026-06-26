package com.iolite.kpcamera.attendance.model;

public class ScheduleDto {
	
	private String org_activity_id;
	private String activity_type;
	private String activity_desc;
	private String status;
	private String start_date;
	private String end_date;
	private String startTime;
	private String endTime;
	private String roomId;
	
	
	public String getOrg_activity_id() {
		return org_activity_id;
	}
	public void setOrg_activity_id(String org_activity_id) {
		this.org_activity_id = org_activity_id;
	}
	public String getActivity_type() {
		return activity_type;
	}
	public void setActivity_type(String activity_type) {
		this.activity_type = activity_type;
	}
	public String getActivity_desc() {
		return activity_desc;
	}
	public void setActivity_desc(String activity_desc) {
		this.activity_desc = activity_desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
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
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
	@Override
	public String toString() {
		return "ActivityDto [org_activity_id=" + org_activity_id + ", activity_type=" + activity_type
				+ ", activity_desc=" + activity_desc + ", status=" + status + ", start_date=" + start_date
				+ ", end_date=" + end_date + ", startTime=" + startTime + ", endTime=" + endTime + ", roomId=" + roomId
				+ "]";
	}
}
