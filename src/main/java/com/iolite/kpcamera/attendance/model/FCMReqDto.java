package com.iolite.kpcamera.attendance.model;

public class FCMReqDto {

	private String[] tokens;
	private Data data;
	public String[] getTokens() {
		return tokens;
	}
	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	
}
