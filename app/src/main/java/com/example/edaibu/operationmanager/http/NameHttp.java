package com.example.edaibu.operationmanager.http;

import android.os.Handler;


import com.example.edaibu.operationmanager.bean.CompanyNameBean;
import com.example.edaibu.operationmanager.constant.Constant;
import com.example.edaibu.operationmanager.constant.HttpConstant;
import com.example.edaibu.operationmanager.http.api.NameApi;
import com.example.edaibu.operationmanager.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by ${gyj} on 2017/6/20.
 */

public class NameHttp extends BaseRequst {

    /**
     * 获取公司，景区
     *
     * @param handler
     */
    public static void getName(String type , final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        Http.baseUrl = HttpConstant.NAME_IP;
        Http.getRetrofit().create(NameApi.class).getName(map).enqueue(new Callback<CompanyNameBean>() {
            @Override
            public void onResponse(Call<CompanyNameBean> call, Response<CompanyNameBean> response) {
                sendMessage(handler, Constant.GET_NAME_SUCCESS, response.body());
            }

            @Override
            public void onFailure(Call<CompanyNameBean> call, Throwable t) {
                sendMessage(handler, Constant.REQUST_ERROR, null);
            }
        });
    }
}
