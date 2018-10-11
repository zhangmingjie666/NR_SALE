package com.joe.app.baseutil.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.joe.app.baseutil.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joe on 2016/6/4.
 */
public class BaseFragment extends Fragment {
    public BaseFragmentActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBusUtil.getInstance().getEventBus().register(this);
        EventBus.getDefault().register(this);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.mActivity = (BaseFragmentActivity)activity;
//    }

    public BaseFragmentActivity getParentActivity(){
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseFragmentActivity)context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBusUtil.getInstance().getEventBus().unregister(this);
        EventBus.getDefault().unregister(this);
    }
}
