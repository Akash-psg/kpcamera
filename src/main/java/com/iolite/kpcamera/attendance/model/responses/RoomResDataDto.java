package com.iolite.kpcamera.attendance.model.responses;

public class RoomResDataDto {

	private String org_room_id;
	private String room_id;

	public String getOrg_room_id() {
		return org_room_id;
	}

	public void setOrg_room_id(String org_room_id) {
		this.org_room_id = org_room_id;
	}

	public String getRoom_id() {
		return room_id;
	}

	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}

	@Override
	public String toString() {
		return "RoomResDataDto [org_room_id=" + org_room_id + ", room_id=" + room_id + "]";
	}

}
