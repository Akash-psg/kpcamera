package com.iolite.kpcamera.attendance.mapper;

public class RoomAndRoomDetailsMapperDto {
	
	private int id;
	private String groupName;
	private String cameraIp;
	private String floor;
	private String boxIp;
	private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCameraIp() {
		return cameraIp;
	}
	public void setCameraIp(String cameraIp) {
		this.cameraIp = cameraIp;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getBoxIp() {
		return boxIp;
	}
	public void setBoxIp(String boxIp) {
		this.boxIp = boxIp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
