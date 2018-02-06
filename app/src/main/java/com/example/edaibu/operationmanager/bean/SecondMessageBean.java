package com.example.edaibu.operationmanager.bean;


import java.util.List;

// FIXME generate failure  field _$Mobile132XXXXXXXNameLocation249
// FIXME generate failure  field _$Mobile132XXXXXXXNameLocation255

/**
 * Created by ${gyj} on 2018/1/8.
 */

public class SecondMessageBean {


    /**
     * mobile : 132XXXXXXX
     * name : 张三
     * location : [{"longitude":116.56123,"latitude":39.013289},{"longitude":116.56923,"latitude":39.213289}]
     */

    private String mobile;
    private String name;
    private String roleId;
    private List<LocationBean> localtion;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<LocationBean> getLocaltion() {
        return localtion;
    }

    public void setLocaltion(List<LocationBean> localtion) {
        this.localtion = localtion;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class LocationBean {
        /**
         * longitude : 116.56123
         * latitude : 39.013289
         */

        private double longitude;
        private double latitude;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
