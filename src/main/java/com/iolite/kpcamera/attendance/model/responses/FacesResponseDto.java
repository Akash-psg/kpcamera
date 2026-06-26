package com.iolite.kpcamera.attendance.model.responses;


public class FacesResponseDto {

	private String code;
	private String message;
	private String timestamp;
	private String time_cost;
	private FacesResponseDataDto data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getTime_cost() {
		return time_cost;
	}
	public void setTime_cost(String time_cost) {
		this.time_cost = time_cost;
	}
	public FacesResponseDataDto getData() {
		return data;
	}
	public void setData(FacesResponseDataDto data) {
		this.data = data;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FacesResponseDto [code=");
		builder.append(code);
		builder.append(", message=");
		builder.append(message);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", time_cost=");
		builder.append(time_cost);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
