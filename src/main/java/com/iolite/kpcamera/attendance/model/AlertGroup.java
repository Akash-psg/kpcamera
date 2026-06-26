package com.iolite.kpcamera.attendance.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlertGroup {

	@JsonProperty("groupName")
	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [groupName=");
		builder.append(groupName);
		builder.append("]");
		return builder.toString();
	}
}
