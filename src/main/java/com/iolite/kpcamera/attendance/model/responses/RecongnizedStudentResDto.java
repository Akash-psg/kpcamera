package com.iolite.kpcamera.attendance.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecongnizedStudentResDto {

	@JsonProperty("data")
	private RecongnizedStudentResDataDto data;

	public RecongnizedStudentResDataDto getData() {
		return data;
	}

	public void setData(RecongnizedStudentResDataDto data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RecongnizedStudentResDto [data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
