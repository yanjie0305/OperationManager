package com.example.edaibu.operationmanager.bean;

/**
 * Created by ${gyj} on 2017/11/24.
 */

public class NewMessageBean {
    /**
     * uid : 595a1c93de00ba310ebf78e1
     * name : undefined
     * mobile : 13528570670
     * roleId : 1
     * userLongitude : 113.745844
     * userLatitude : 23.032929
     */

    private String uid;
    private String name;
    private String mobile;
    private String roleId;
    private String userLongitude;
    private String userLatitude;
    private String logTime;

    public NewMessageBean(String uid, String name, String mobile, String roleId, String userLongitude, String userLatitude, String logTime) {
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.roleId = roleId;
        this.userLongitude = userLongitude;
        this.userLatitude = userLatitude;
        this.logTime = logTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }
    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }


}
