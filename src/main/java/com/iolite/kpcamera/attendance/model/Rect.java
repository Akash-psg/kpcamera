package com.iolite.kpcamera.attendance.model;

public class Rect {

	private String left;
	private String top;
	private String right;
	private String bottom;
	
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}
	public String getBottom() {
		return bottom;
	}
	public void setBottom(String bottom) {
		this.bottom = bottom;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rect [left=");
		builder.append(left);
		builder.append(", top=");
		builder.append(top);
		builder.append(", right=");
		builder.append(right);
		builder.append(", bottom=");
		builder.append(bottom);
		builder.append("]");
		return builder.toString();
	}
}
