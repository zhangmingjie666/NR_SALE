package com.joe.app.outbound.data;

import android.content.Context;

import com.joe.app.baseutil.api.Client;
import com.joe.app.baseutil.api.OnRequestCallback;
import com.joe.app.outbound.AppConstant;
import com.joe.app.outbound.MyApplication;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joe on 2016/6/7.
 * Email-joe_zong@163.com
 */
public class DataProvider {
    public static DataProvider mDataProvider;

    private Context mContext;
    private Client mClient;
    private EventBus mEventBus = new EventBus();

    public static DataProvider getInstance(){
        if(mDataProvider == null){
            mDataProvider = new DataProvider(MyApplication.getInstance());
        }
        return mDataProvider;
    }

    private DataProvider(Context aContext){
        this.mContext = aContext;
        mClient = Client.getInstance(AppConstant.Host);
    }

    public EventBus getEventBus(){
        return mEventBus;
    }
}
