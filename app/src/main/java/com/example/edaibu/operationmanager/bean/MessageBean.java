package com.example.edaibu.operationmanager.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ${gyj} on 2017/11/7.
 */

public class MessageBean implements Serializable {

    /**
     * roleId : 2
     * name : 王立平
     * uid : 592967cf45cef39ff5769097
     * mobile : 15718822826
     * coords : [{"userLongitude":"116.32277","userLatitude":"40.042208","logTime":"1511510278000"},{"userLongitude":"116.32277","userLatitude":"40.042208","logTime":"1511510278000"}]
     */

    private String roleId;
    private String name;
    private String uid;
    private String mobile;
    private List<CoordsBean> coords;

    public MessageBean(String roleId, String name, String uid, String mobile, List<CoordsBean> coords) {
        this.roleId = roleId;
        this.name = name;
        this.uid = uid;
        this.mobile = mobile;
        this.coords = coords;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<CoordsBean> getCoords() {
        return coords;
    }

    public void setCoords(List<CoordsBean> coords) {
        this.coords = coords;
    }

    public static class CoordsBean {
        /**
         * userLongitude : 116.32277
         * userLatitude : 40.042208
         * logTime : 1511510278000
         */

        private String userLongitude;
        private String userLatitude;
        private String logTime;

        public CoordsBean(String userLongitude, String userLatitude, String logTime) {
            this.userLongitude = userLongitude;
            this.userLatitude = userLatitude;
            this.logTime = logTime;
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

}
