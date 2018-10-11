package com.joe.app.baseutil.util;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joe on 2016/6/9.
 * Email-joe_zong@163.com
 */
public class EventBusUtil {
    public static EventBusUtil instance;
    private EventBus eventBus;
    public static EventBusUtil getInstance(){
        if(instance == null){
            instance = new EventBusUtil();
        }
        return instance;
    }
    private EventBusUtil(){
        eventBus = new EventBus();
    }

    public EventBus getEventBus(){
        return eventBus;
    }
}
