package com.iolite.kpcamera.attendance.model.responses;

import com.iolite.kpcamera.attendance.model.Rect;

public class FacesResponseDataDto {

	private String imageId;
	private String faceToken;
	private Rect rect;
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getFaceToken() {
		return faceToken;
	}
	public void setFaceToken(String faceToken) {
		this.faceToken = faceToken;
	}
	public Rect getRect() {
		return rect;
	}
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FacesResponseDataDto [imageId=");
		builder.append(imageId);
		builder.append(", faceToken=");
		builder.append(faceToken);
		builder.append(", rect=");
		builder.append(rect);
		builder.append("]");
		return builder.toString();
	}
}
