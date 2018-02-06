package com.example.edaibu.operationmanager.http.api;


import com.example.edaibu.operationmanager.bean.CompanyNameBean;
import com.example.edaibu.operationmanager.constant.HttpConstant;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by lyn on 2017/4/17.
 */
public interface NameApi {
    @GET(HttpConstant.NAME_URL)
    Call<CompanyNameBean> getName(@QueryMap Map<String, String> map);



}
