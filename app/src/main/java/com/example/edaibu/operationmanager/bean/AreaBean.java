package com.example.edaibu.operationmanager.bean;

import java.util.List;

/**
 * Created by ${gyj} on 2017/12/8.
 */

public class AreaBean {

    /**
     * code : 200
     * msg : null
     * data : [{"id":"5944d804dad297215da22080","name":"武当山片区"}]
     */

    private int code;
    private Object msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 5944d804dad297215da22080
         * name : 武当山片区
         */

        private String id;
        private String name;
        private List<List<Double>> coordinates;
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
