package com.joe.app.outbound.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.joe.app.outbound.AppConstant;
import com.joe.app.outbound.MyApplication;

/**
 * Created by Joe on 2016/6/9.
 * Email-joe_zong@163.com
 */
public class SharedPreference {
    public static final SharedPreferences mSharedPreference = MyApplication.getInstance().getSharedPreferences("OutboundPreferences", Context.MODE_PRIVATE);

    public static final String EmplyeeId = ".employee_id";
    public static final String DOWNLOAD_TASK_ID = ".download_task_id";

    //用户名

    public static final String TokenId = ".token_id";

    public static String getTokenId() {
        return mSharedPreference.getString(TokenId, "");
    }

    public static void setTokenId(String tokenId) {
        mSharedPreference.edit().putString(TokenId, tokenId).apply();
    }
    public static String getEmplyeeId(){
        return mSharedPreference.getString(EmplyeeId,"");
    }

    public static void setEmplyeeId(String emplyeeId){
        mSharedPreference.edit().putString(EmplyeeId,emplyeeId).apply();
    }

    public static final String Host = ".host";

    public static String getHost() {
        String host = mSharedPreference.getString(Host, AppConstant.Host);
        return TextUtils.isEmpty(host)?AppConstant.Host:host;
    }

    public static void setHost(String host){
        mSharedPreference.edit().putString(Host,host).apply();
    }

    /**
     * 保存的当前下载任务id
     */
    public static void setDownloadTaskId(long downloadTaskId) {
        mSharedPreference.edit().putLong(DOWNLOAD_TASK_ID, downloadTaskId).apply();
    }

    /**
     * 获取保存的当前下载任务id
     */
    public static long getDownloadTaskId() {
        return mSharedPreference.getLong(DOWNLOAD_TASK_ID, -12306L);
    }

    /**
     * 移除保存的下载任务id
     */
    public static void deleteDownloadTaskId() {
        mSharedPreference.edit().remove(DOWNLOAD_TASK_ID);
    }

}
