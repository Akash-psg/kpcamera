package com.iolite.kpcamera.attendance.model;

public class CameraIpDetails {

	
	private String cameraIp;
    private String groupName;
    private String floor;
    private boolean isUp;
    
    
	public String getCameraIp() {
		return cameraIp;
	}
	public void setCameraIp(String cameraIp) {
		this.cameraIp = cameraIp;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public boolean getIsUp() {
		return isUp;
	}
	public void setIsUp(boolean isUp) {
		this.isUp = isUp;
	}
    
    
}
