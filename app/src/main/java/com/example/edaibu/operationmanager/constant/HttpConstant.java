package com.example.edaibu.operationmanager.constant;

/**
 * Created by ${gyj} on 2017/9/20.
 *
 */

public class HttpConstant {
    //获取公司名称域名（测试）
//    public static final String IP = "http://192.168.1.22:3033/";
    //获取公司名称域名（正式）
    public static final String NAME_IP = "http://api-developer.zxbike.cn/";
    //获取公司名称url
    public static final String NAME_URL = NAME_IP+"api/user/org";

//    public static final String BIKELIST = LIST_IP+"bike/company/area/list";
    //片区域名
//    public static final String AREA_IP ="http://bi.zxbike.top/";
    public static final String AREA_IP ="http://bi.zxbike.cc/";
    public static final String AREA_URL = AREA_IP+"admin/part/area/list";
    /**
     * 推送正式主机域名
     */
    //192.168.1.114

    public static final String HOST = "tcp://192.168.1.114:3031";
    /**
     * 推送测试域名
     */
//    public static final String HOST = "tcp://192.168.50.14:3003";
    /**
     * 推送客户端ID
     */
    public static final String CLIENT_ID = "edaibu_tv_bike";
    /**
     * 推送的用户名
     */
    public static final String UESR_NAME = "mosquitto";

}