package com.iolite.kpcamera.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertsEvent {

	@JsonProperty("top1")
	private Top1 top1;
	private String captureFullImageURL;
	private String channelName;
	
	@JsonProperty("trackId")
	private String trackId;
	@JsonProperty("timestamp")
	private String timestamp;
	@JsonProperty("captureImageURL")
	private String captureImageURL;

	public String getTrackId() { return trackId; }
	public void setTrackId(String trackId) { this.trackId = trackId; }

	public String getTimestamp() { return timestamp; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

	public String getCaptureImageURL() { return captureImageURL; }
	public void setCaptureImageURL(String captureImageURL) { this.captureImageURL = captureImageURL; }

	public Top1 getTop1() {
		return top1;
	}

	public void setTop1(Top1 top1) {
		this.top1 = top1;
	}

	public String getCaptureFullImageURL() {
		return captureFullImageURL;
	}

	public void setCaptureFullImageURL(String captureFullImageURL) {
		this.captureFullImageURL = captureFullImageURL;
	}
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
