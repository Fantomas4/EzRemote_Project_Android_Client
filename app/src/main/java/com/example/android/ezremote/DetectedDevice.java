package com.example.android.ezremote;

public class DetectedDevice {

    private String devId;
    private String devIp;
    private String devStatus;

    public DetectedDevice(String id, String ip, String status) {
        devId = id;
        devIp = ip;
        devStatus = status;

    }

    public String getDevId() {
        return this.devId;
    }

    public String getDevIp() {
        return this.devIp;
    }

    public String getDevStatus() {
        return this.devStatus;
    }
}
