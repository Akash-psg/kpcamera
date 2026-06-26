package com.iolite.kpcamera.attendance.model;

import java.util.List;

public class RoomIpInbound {

	private String ipAddress;
    private String description;
    private List<CameraIpDetails> cameraIps;
    
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
    
    
}
