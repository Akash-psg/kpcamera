package com.iolite.kpcamera.attendance.model;

import java.util.List;

public class ScheduleReqDto {

	private String org_schedule_id;
	private String room_id;
	private String activity_id;
	private List<Schedule> schedule;
	private String status;
	
	
	public String getOrg_schedule_id() {
		return org_schedule_id;
	}
	public void setOrg_schedule_id(String org_schedule_id) {
		this.org_schedule_id = org_schedule_id;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public String getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}
	
	
	public List<Schedule> getSchedule() {
		return schedule;
	}
	public void setSchedule(List<Schedule> schedule) {
		this.schedule = schedule;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ScheduleReqDto [org_schedule_id=" + org_schedule_id + ", room_id=" + room_id + ", activity_id="
				+ activity_id + ", schedule=" + schedule + ", status=" + status + "]";
	}

	
}
