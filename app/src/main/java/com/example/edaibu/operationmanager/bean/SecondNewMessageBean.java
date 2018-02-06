package com.example.edaibu.operationmanager.bean;

/**
 * Created by ${gyj} on 2018/1/8.
 */

public class SecondNewMessageBean {


    /**
     * name : 张三
     * mobile : 132XXXXX
     * longitude : 116
     * latitude : 39
     */

    private String name;
    private String mobile;
    private double longitude;
    private double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
