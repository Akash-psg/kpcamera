package com.iolite.kpcamera.attendance.model;

public class Schedule {

	private String start_time;
	private String end_time;
	private String day_of_week;
	
	
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getDay_of_week() {
		return day_of_week;
	}
	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}
	@Override
	public String toString() {
		return "Schedule [start_time=" + start_time + ", end_time=" + end_time + ", day_of_week=" + day_of_week + "]";
	}
	
	
}
