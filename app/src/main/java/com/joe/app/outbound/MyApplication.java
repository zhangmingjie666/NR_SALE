package com.joe.app.outbound;

import android.app.Application;
import android.content.Context;
import android.device.ScanDevice;

/**
 * Created by ZDD on 2016/6/4.
 */
public class MyApplication extends Application{
    public static MyApplication instance;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        try {
            Class.forName("android.os.IScanService$Stub");

            // 功能：设置扫描键盘输出；
            // 参数：1 键盘输出模式；0 广播方式输出。
            ScanDevice scanDevice = new ScanDevice();
            scanDevice.setOutScanMode(0);

        } catch (ClassNotFoundException e) {
        }


    }

    public static MyApplication getInstance(){
        return instance;
    }
    public static Context getContext() {
        return context;
    }
}
