package com.iolite.kpcamera.attendance.model;

public class Data {

    // ← CHANGED: roomId → cameraIp
    private String cameraIp;
    private String msg;

    public String getCameraIp() {
        return cameraIp;
    }
    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}