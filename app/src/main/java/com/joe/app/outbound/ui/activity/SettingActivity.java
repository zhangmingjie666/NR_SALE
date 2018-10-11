package com.joe.app.outbound.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.joe.app.baseutil.Event;
import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.MUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.SharedPreference;
import com.joe.app.outbound.data.event.HostChangeEvent;
import com.joe.app.outbound.ui.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {


    @Bind(R.id.etHost)
    ClearEditText etHost;
    @Bind(R.id.btnConfirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        etHost.setText(SharedPreference.getHost());
    }

    @OnClick(R.id.btnConfirm)
    public void onClick() {
        MUtils.hideSoftInput(SettingActivity.this);
        String host = etHost.getText().toString();
        if(TextUtils.isEmpty(host)){
            UIHelper.showShortToast(SettingActivity.this,"请输入服务地址");
            return;
        }
        SharedPreference.setHost(host);
        UIHelper.showShortToast(SettingActivity.this, "更改成功");
        EventBus.getDefault().post(new HostChangeEvent());
        finish();
    }
}
