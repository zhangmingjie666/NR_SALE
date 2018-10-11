package com.joe.app.outbound.ui.activity;

import android.content.Intent;
import android.os.Looper;
import android.os.Bundle;

import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.SharedPreference;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.ResponseBean;

public class ValidateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                validate();
                Looper.loop();
            }
        }).start();

    }

    public void validate() {

        String tokenId = SharedPreference.getTokenId();

        if (tokenId.equals("")) {

            Intent intent = new Intent(ValidateActivity.this, LoginActivity.class);

            startActivity(intent);

            return;

        }

        Api api = new Api(this, new OnNetRequest(this, true, "请稍等...") {
            @Override
            public void onSuccess(String msg) {

                ResponseBean response = JSONUtils.fromJson(msg, ResponseBean.class);

                if (response != null && response.status) {

                    SharedPreference.setTokenId(response.result);

                    Intent intent = new Intent(ValidateActivity.this, MainActivity.class);

                    startActivity(intent);

                }

            }

            @Override
            public void onFail() {
                SharedPreference.setTokenId("");

                Intent intent = new Intent(ValidateActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });
        api.validate(tokenId);
    }
}
