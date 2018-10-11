package com.joe.app.baseutil.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Joe on 2016/6/10.
 * Email-joe.zong@xiaoniubang.com
 */
public class MUtils {
    public static void hideSoftInput(Activity activity){
        ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideSoftInputOfView(Context aContext, View view){
        InputMethodManager imm = (InputMethodManager) ((Activity)aContext).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
