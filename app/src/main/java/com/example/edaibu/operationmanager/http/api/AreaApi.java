package com.example.edaibu.operationmanager.http.api;


import com.example.edaibu.operationmanager.bean.AreaBean;
import com.example.edaibu.operationmanager.constant.HttpConstant;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by lyn on 2017/4/17.
 */
public interface AreaApi {
    @GET(HttpConstant.AREA_URL)
    Call<AreaBean> getArea(@QueryMap Map<String, String> map);



}
