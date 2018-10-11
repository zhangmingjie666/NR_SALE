package com.joe.app.outbound.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.baseutil.util.MUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.event.ScanResultEvent;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.CompanyBeanResponse;
import com.joe.app.outbound.data.model.PackageResponseBean;
import com.joe.app.outbound.data.model.ResponseBean;
import com.joe.app.outbound.ui.dialog.InputPackageNumDialog;
import com.joe.app.outbound.ui.widget.ClearEditText;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RetailDetailActivity extends BaseActivity {

    @Bind(R.id.dye_company)
    TextView dye_company;

//    @Bind(R.id.etScanCode)
//    ClearEditText etScanCode;

    @Bind(R.id.add)
    Button add;


    public static final int EMPLOYEE_BACK = 5;
    private InputPackageNumDialog inputDialog;
    String company_ ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_detail);
        ButterKnife.bind(this);

        initview();
    }

    public void initview() {
        dye_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RetailDetailActivity.this, CompanyActivity.class);
                startActivityForResult(intent, EMPLOYEE_BACK);
            }
        });

//        etScanCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if
//                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    switch (event.getAction()) {
//                        case KeyEvent.ACTION_UP:
//                            //发送请求
//                            Log.d("addPackage","onEditorAction:"+actionId);
//                            MUtils.hideSoftInput(RetailDetailActivity.this);
//                            return true;
//                        default:
//                            return true;
//                    }
//                }
//                return false;
//            }
//        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRetail();
                finish();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EMPLOYEE_BACK:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("name");
                    String returnedId = data.getStringExtra("id");
                    dye_company.setText(returnedData);
                    company_ = returnedId;

                }
                break;
        }
    }
    //获取
    public void addRetail() {
        Api api = new Api(this, new OnNetRequest(this) {
            @Override
            public void onSuccess(String msg) {
                ResponseBean responseBean = JSONUtils.fromJson(msg, ResponseBean.class);
                if (responseBean != null && responseBean.status) {
                    UIHelper.showShortToast(RetailDetailActivity.this, "新增成功");
                }
            }
            @Override
            public void onFail() {
            }
        });
        api.addRetail(company_);
    }
//    @Subscribe(priority = 2)
//    public void OnScanResultEvent(final ScanResultEvent event){
//        EventBus.getDefault().cancelEventDelivery(event);
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if(inputDialog==null||!inputDialog.isShowing()){
//                    etScanCode.setText(event.getResult());
//                }
//            }
//        });
//    }
}
