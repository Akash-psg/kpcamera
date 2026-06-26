package com.iolite.kpcamera.attendance.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Top1 {

	@JsonProperty("faceToken")
	private String faceToken;
	@JsonProperty("description")
	private String description;
	@JsonProperty("group")
	private List<Group> group;
	@JsonProperty("alertGroup")
	private List<AlertGroup> alertGroup;
	
	public String getFaceToken() {
		return faceToken;
	}
	public void setFaceToken(String faceToken) {
		this.faceToken = faceToken;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Group> getGroup() {
		return group;
	}
	public void setGroup(List<Group> group) {
		this.group = group;
	}
	public List<AlertGroup> getAlertGroup() {
		return alertGroup;
	}
	public void setAlertGroup(List<AlertGroup> alertGroup) {
		this.alertGroup = alertGroup;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Top1 [faceToken=");
		builder.append(faceToken);
		builder.append(", description=");
		builder.append(description);
		builder.append(", group=");
		builder.append(group);
		builder.append(", alertGroup=");
		builder.append(alertGroup);
		builder.append("]");
		return builder.toString();
	}
	
	
}
