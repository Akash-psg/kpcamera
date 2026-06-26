package com.iolite.kpcamera.attendance.model.outbound;

import java.util.List;

import com.iolite.kpcamera.attendance.model.CameraIpDetails;

public class RoomAndRoomDetailsOutbound {
	
	private int id;
	private String ipAddress;
    private String description;
    private List<CameraIpDetails> cameraIps;
    private boolean isUp;
    
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<CameraIpDetails> getCameraIps() {
		return cameraIps;
	}
	public void setCameraIps(List<CameraIpDetails> cameraIps) {
		this.cameraIps = cameraIps;
	}
	public boolean getIsUp() {
		return isUp;
	}
	public void setIsUp(boolean isUp) {
		this.isUp = isUp;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
