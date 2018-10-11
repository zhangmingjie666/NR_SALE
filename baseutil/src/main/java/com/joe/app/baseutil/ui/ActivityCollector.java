package com.joe.app.baseutil.ui;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MJ@ZHANG
 * @package: com.joe.app.outbound.ui.activity
 * @filename ActivityCollector
 * @date on 2018/8/6 13:21
 * @descibe TODO
 * @email zhangmingjie@huansi.net
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity: activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}
