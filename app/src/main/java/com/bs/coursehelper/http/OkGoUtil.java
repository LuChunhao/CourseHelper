package com.bs.coursehelper.http;


import android.util.Log;

import com.bs.coursehelper.http.callbck.JsonCallback;
import com.lzy.okgo.OkGo;

import java.util.Map;


/**
 * 网络框架二次封装
 */

public class OkGoUtil {

    private static final String TAG = "OkGoUtil";

    /**
     *
     * @param url
     * @param tag
     * @param map
     * @param callback
     * @param <T>
     */
    public static <T> void getRequets(String url, Object tag, Map<String, String> map, JsonCallback<T> callback) {
        //  加密 时间戳等 请求日志打印
        Log.d(TAG, "method get");
        OkGo.<T>get(url)
                .tag(tag)
                .params(map)
                .execute(callback);
    }
    public static <T> void postRequest(String url, Object tag, Map<String, String> map, JsonCallback<T> callback) {
        // 加密 时间戳等 请求日志打印
        Log.d(TAG, "method post");
        OkGo.<T>post(url)
                .tag(tag)
                .params(map)
                .execute(callback);
    }

}
