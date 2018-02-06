package com.example.edaibu.operationmanager.util;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ${gyj} on 2017/11/24.
 */

public class GsonUtils {

    // 将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    public static <T> List<T> jsonToList(String json, Class<? extends T[]> clazz) {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);

    }
}
