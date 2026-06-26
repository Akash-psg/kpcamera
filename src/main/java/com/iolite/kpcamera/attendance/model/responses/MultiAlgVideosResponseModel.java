package com.iolite.kpcamera.attendance.model.responses;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Root Object
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiAlgVideosResponseModel {

    public Data data;
	
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
    
}





