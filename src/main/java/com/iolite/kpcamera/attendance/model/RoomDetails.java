package com.iolite.kpcamera.attendance.model;

public class RoomDetails {
	
	private String org_room_id;
	
	public RoomDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getOrg_room_id() {
		return org_room_id;
	}

	public void setOrg_room_id(String org_room_id) {
		this.org_room_id = org_room_id;
	}

	@Override
	public String toString() {
		return "RoomDetails [org_room_id=" + org_room_id + "]";
	}

}
