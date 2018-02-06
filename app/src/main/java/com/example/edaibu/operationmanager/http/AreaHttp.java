package com.example.edaibu.operationmanager.http;

import android.os.Handler;


import com.example.edaibu.operationmanager.bean.AreaBean;
import com.example.edaibu.operationmanager.constant.Constant;
import com.example.edaibu.operationmanager.constant.HttpConstant;
import com.example.edaibu.operationmanager.http.api.AreaApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ${gyj} on 2017/6/20.
 */

public class AreaHttp extends BaseRequst {

    /**
     * 获取公司，景区
     *
     * @param handler
     */
    public static void getArea(String companyNo , final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("companyNo", companyNo);
        Http.baseUrl = HttpConstant.AREA_IP;
        Http.getRetrofit().create(AreaApi.class).getArea(map).enqueue(new Callback<AreaBean>() {
            @Override
            public void onResponse(Call<AreaBean> call, Response<AreaBean> response) {
                sendMessage(handler, Constant.GET_AREA_SUCCESS, response.body());
            }

            @Override
            public void onFailure(Call<AreaBean> call, Throwable t) {
                sendMessage(handler, Constant.REQUST_ERROR, null);
            }
        });
    }
}
