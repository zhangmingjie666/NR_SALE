package com.joe.app.outbound.data.listener;

import android.content.Context;

import com.joe.app.baseutil.api.OnRequestCallback;
import com.squareup.okhttp.Request;

/**
 * Created by Joe on 2016/6/7.
 * Email-joe_zong@163.com
 */
public abstract class OnNetRequest{
    private Context mContext;
    private boolean isShowLoading;
    private String loadingText;

    public OnNetRequest(Context aContext,boolean isShowLoading, String loadingText){
        this.mContext = aContext;
        this.isShowLoading = isShowLoading;
        this.loadingText = loadingText;
    }

    public OnNetRequest(Context aContext){
        this.mContext = aContext;
        this.isShowLoading = false;
    }
    private OnNetRequest(){}

    public abstract void onSuccess(String msg);

    public abstract void onFail();


    public String getLoadingText() {
        return loadingText;
    }

    public boolean isShowLoading() {
        return isShowLoading;
    }
}
