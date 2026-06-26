package com.iolite.kpcamera.attendance.model.outbound;

public class RoomIpOutbound {

	private int id;
	private String boxIp;
	private String desc;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBoxIp() {
		return boxIp;
	}
	public void setBoxIp(String boxIp) {
		this.boxIp = boxIp;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "RoomIpOutbound [id=" + id + ", boxIp=" + boxIp + ", desc=" + desc + "]";
	}
	
}
