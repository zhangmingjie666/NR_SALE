package com.joe.app.outbound.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.outbound.R;

public class UpdateActivity extends BaseActivity {
    boolean isShowDialog = false;

    public static void startInstance(Context context){
        Intent intent = new Intent(context,UpdateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        overridePendingTransition(R.anim.alpha_in,R.anim.alpha_out);
    }
}
