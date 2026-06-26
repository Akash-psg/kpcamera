package com.iolite.kpcamera.attendance.model.responses;

public class RoomResDto {

	private String status;
	private String message;
	private RoomResDataDto data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RoomResDataDto getData() {
		return data;
	}

	public void setData(RoomResDataDto data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RoomResDto [status=" + status + ", message=" + message + ", data=" + data + "]";
	}
}
