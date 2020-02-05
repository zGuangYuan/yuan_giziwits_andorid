package com.yuan_giziwits_andorid.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class SharePreferenceUtils {

    private static final String SP_NAME="config";
    /**
     * 存储封装
     * @param mContext  上下文
     * @param key       键值
     * @param value     数值
     */
    public static void putString(Context mContext,String key,String value){
        //拿到本地的SharePreference的对象，只能本地应用才能读取
        SharedPreferences sp =mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        //拿到SharePreference的操作对象
        SharedPreferences.Editor editor = sp.edit();
        //存储
        editor.putString(key,value);
        //应用
        editor.apply();
    }

    /**
     * @param mContext   上下文
     * @param key        键值
     * @param defaultval 默认值
     * @return
     */
    public static  String getString(Context mContext,String key,String defaultval){
        //拿到本地的SharePreference的对象，只能本地应用才能读取
        SharedPreferences sp =mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        //取出数据,如果该键下的数值为null，就会取出默认值
        return sp.getString(key,defaultval);
    }
}
