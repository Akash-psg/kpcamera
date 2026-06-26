package com.iolite.kpcamera.attendance.util;

import java.net.InetAddress;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.iolite.kpcamera.attendance.model.Data;
import com.iolite.kpcamera.attendance.model.FCMReqDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;

public class CameraUtils {

	public static String extractIPAddress(String url) {
		// Regular expression to match IP address
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        
        // Compile pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        
        if (matcher.find()) {
            return matcher.group();
        } else {
            System.out.println("No IP Address found.");
        }
		return matcher.group();
	}
	// Method to check if an IP is reachable
    public static boolean isIPActive(String ip) {
        try {
            InetAddress inet = InetAddress.getByName(ip);
            return inet.isReachable(1000); // Timeout of 3 seconds
        } catch (Exception e) {
            return false;
        }
    }
    
    public static HttpEntity<FCMReqDto> sendNotification(
            StudentApkDtoResponse data,
            String message) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        FCMReqDto fCMReqDto = new FCMReqDto();
        fCMReqDto.setTokens(
                new String[]{data.getToken()});

        Data dataDto = new Data();

        // ← FIXED: setCameraIp instead of setRoomId
        dataDto.setCameraIp(data.getCameraIp());
        dataDto.setMsg(message);

        fCMReqDto.setData(dataDto);

        HttpEntity<FCMReqDto> request =
                new HttpEntity<>(fCMReqDto, headers);
        return request;
    }
}
