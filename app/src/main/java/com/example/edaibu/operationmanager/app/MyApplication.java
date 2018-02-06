package com.example.edaibu.operationmanager.app;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.edaibu.operationmanager.service.MqttService;


/**
 * Created by ${gyj} on 2017/9/19.
 */

public class MyApplication extends Application {
    public static MyApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.GCJ02);
        application = this;
        MqttService.actionStart(this);
    }

}
