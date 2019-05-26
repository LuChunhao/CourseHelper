package com.bs.coursehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 */

public class SPUtil {

    private final String TAG = "SharedPreferencesHelper";

    /**
     * 保存在手机里面的文件名
     */
    private final String FILE_NAME = "sp_cache";

    private SharedPreferences mSharedPreferences;// 单例

    private SPUtil() {

    }

    private static class Holder {
        private static SPUtil singleton = new SPUtil();
    }

    public static SPUtil getInstanse() {
        return Holder.singleton;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context){
        if (context == null) {
            Log.e("SharedPreferencesHelper", "上下文不能为空...");
            throw new IllegalArgumentException();
        }
        mSharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void setParam(String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        if ("String".equals(type)) {
            return mSharedPreferences.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return mSharedPreferences.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return mSharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return mSharedPreferences.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return mSharedPreferences.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().commit();
    }


    /**
     * 清除指定的数据
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAllKey() {
        return mSharedPreferences.getAll();
    }

}
