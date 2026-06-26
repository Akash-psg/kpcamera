package com.iolite.kpcamera.attendance.model.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Video {
    public String url;
    public List<AlgRule> algRules;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<AlgRule> getAlgRules() {
		return algRules;
	}
	public void setAlgRules(List<AlgRule> algRules) {
		this.algRules = algRules;
	}
    
    
}
