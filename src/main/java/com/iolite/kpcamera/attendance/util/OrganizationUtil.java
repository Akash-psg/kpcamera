package com.iolite.kpcamera.attendance.util;

public class OrganizationUtil {
	
	public static final String STATUS = "100";
	public static final String SUFFIX = "/";
	public static final String SPLIT = "_";
	public static final String ORG_SCHEDULE_TIME = "0 01 18 ? * *";
	public static final String SEND_NOTIFICATION = "0 */5 8-17 * * *";
	//public static String FILE_PATH = "E:/iolite/";
	public static String FILE_PATH = "/opt/FileServer/captureimages/";
	public static final String IMAGE_SUFFIX = ".jpg";
	public static String URL_FOR_IMAGE = "https://kp.christuniversity.in/KnowledgePro/images/StudentPhotos/";
	public static String SEND_ERROR_NOTIFICATION = "Network issue detected. Please take attendance manually.";
	public static String SEND_SUCCESS_NOTIFICATION = "Attendance has been collected successfully. Please review the attendance records.";
}
