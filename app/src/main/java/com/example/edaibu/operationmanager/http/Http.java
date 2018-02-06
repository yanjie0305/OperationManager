package com.example.edaibu.operationmanager.http;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ${gyj} on 2017/4/13.
 */

public class Http {
    public  static String baseUrl ="";
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static OkHttpClient getOkHttp() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(15, TimeUnit.SECONDS);
            builder.writeTimeout(15, TimeUnit.SECONDS);
            builder.readTimeout(15, TimeUnit.SECONDS);
            builder.addInterceptor(new LogInterceptor());
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }


    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(baseUrl);
            builder.addConverterFactory(GsonConverterFactory.create());
            builder.callFactory(getOkHttp());
            retrofit = builder.build();
        }
        return retrofit;
    }

}
