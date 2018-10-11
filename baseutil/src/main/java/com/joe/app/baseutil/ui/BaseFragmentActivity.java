package com.joe.app.baseutil.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.joe.app.baseutil.Event;
import com.joe.app.baseutil.R;
import com.joe.app.baseutil.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Joe on 2016/6/4.
 * Email-joe_zong@163.com
 */
public class BaseFragmentActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        EventBusUtil.getInstance().getEventBus().register(this);
        EventBus.getDefault().register(this);
    }

    public void addFragment(BaseFragment baseFragment, boolean isAddBackStack, boolean isAnimation){
        if(baseFragment!=null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(isAnimation){
                transaction.setCustomAnimations( R.anim.page_slide_in_from_right, R.anim.page_slide_out_to_left, R.anim.page_slide_in_from_left,R.anim.page_slide_out_to_right);
            }
            transaction.replace(R.id.container_frame,baseFragment,baseFragment.getClass().getSimpleName());
            if(isAddBackStack){
                transaction.addToBackStack(baseFragment.getClass().getSimpleName());
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void backPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBusUtil.getInstance().getEventBus().unregister(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    @Subscribe
    public void OnEvent(Event event){

    }
}
