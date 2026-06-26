package com.iolite.kpcamera.attendance.model;

public class RoomReqDto {

	private String org_room_id;
	private String facility_id;
	private String room_no;
	private String floor_no;

	public String getOrg_room_id() {
		return org_room_id;
	}

	public void setOrg_room_id(String org_room_id) {
		this.org_room_id = org_room_id;
	}

	public String getFacility_id() {
		return facility_id;
	}

	public void setFacility_id(String facility_id) {
		this.facility_id = facility_id;
	}

	public String getRoom_no() {
		return room_no;
	}

	public void setRoom_no(String room_no) {
		this.room_no = room_no;
	}

	public String getFloor_no() {
		return floor_no;
	}

	public void setFloor_no(String floor_no) {
		this.floor_no = floor_no;
	}

	@Override
	public String toString() {
		return "RoomReqDto [org_room_id=" + org_room_id + ", facility_id=" + facility_id + ", room_no=" + room_no
				+ ", floor_no=" + floor_no + "]";
	}

}
