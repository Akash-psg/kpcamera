package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iolite.kpcamera.attendance.model.AlertsEvent;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecongnizedStudentResDataDto {
	
	@JsonProperty("nextPageToken")
	private String nextPageToken;
	
	@JsonProperty("alertsEvent")
	private List<AlertsEvent> alertsEvent;

	public List<AlertsEvent> getAlertsEvent() {
		return alertsEvent;
	}

	public void setAlertsEvent(List<AlertsEvent> alertsEvent) {
		this.alertsEvent = alertsEvent;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RecongnizedStudentResDataDto [nextPageToken=");
		builder.append(nextPageToken);
		builder.append(", alertsEvent=");
		builder.append(alertsEvent);
		builder.append("]");
		return builder.toString();
	}

}
