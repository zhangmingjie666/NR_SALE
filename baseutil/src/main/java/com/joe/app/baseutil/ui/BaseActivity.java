package com.joe.app.baseutil.ui;

import android.app.Activity;
import android.os.Bundle;

import com.joe.app.baseutil.Event;
import com.joe.app.baseutil.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Joe on 2016/6/7.
 * Email-joe_zong@163.com
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBusUtil.getInstance().getEventBus().register(this);
        EventBus.getDefault().register(this);
        ActivityCollector.addActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBusUtil.getInstance().getEventBus().unregister(this);
        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);

    }

    @Subscribe
    public void OnEvent(Event event){

    }
}
