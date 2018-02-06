package com.example.edaibu.operationmanager.util;

/**
 * Created by ${gyj} on 2018/1/4.
 */

public class DataHolder {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }
}
