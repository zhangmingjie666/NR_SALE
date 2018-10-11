package com.joe.app.outbound.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joe.app.baseutil.ui.ActivityCollector;
import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.SharedPreference;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.ResponseBean;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.btn_login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tokenId = SharedPreference.getTokenId();

                Log.d("zhangmingjie", tokenId);

                userLogin();

            }
        });
    }


    public void userLogin() {

        TextView et_account = (TextView) findViewById(R.id.et_account);

        TextView et_password = (TextView) findViewById(R.id.et_password);

        String username = et_account.getText().toString().trim();

        String password = et_password.getText().toString().trim();


        Api api = new Api(this, new OnNetRequest(this, true, "请稍等...") {
            @Override
            public void onSuccess(String msg) {

                ResponseBean response = JSONUtils.fromJson(msg, ResponseBean.class);

                if (response != null && response.status) {

                    SharedPreference.setTokenId(response.result);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    startActivity(intent);

                } else {

                }

            }

            @Override
            public void onFail() {

            }
        });
        api.userLogin(username, password);
    }

    @Override
    public void onBackPressed() {

        ActivityCollector.finishAll();

    }
}
