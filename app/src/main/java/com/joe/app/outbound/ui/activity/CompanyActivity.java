package com.joe.app.outbound.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.CompanyBean;
import com.joe.app.outbound.data.model.CompanyBeanResponse;
import com.joe.app.outbound.data.model.EmployeeBean;
import com.joe.app.outbound.ui.adapter.CompanyAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CompanyActivity extends BaseActivity {

    @Bind(R.id.txtvActionbarTitle)
    TextView txtvActionbarTitle;

    @Bind(R.id.txtvLeft)
    TextView txtvLeft;

    @Bind(R.id.etCode)
    TextView etCode;

    ListView linkmanList;

    CompanyAdapter companyAdapter;
    String type = "2";

    String search ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        ButterKnife.bind(this);
        initview();
        getCompanyList();
        txtvActionbarTitle.setText("客户");
        txtvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void initview() {
        linkmanList = findViewById(R.id.linkman_list);
        companyAdapter = new CompanyAdapter();
        linkmanList.setAdapter(companyAdapter);
        linkmanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CompanyBean processOrder = (CompanyBean) companyAdapter.getItem(position);
                String employeename = processOrder.fullname;
                String company = processOrder.id;
                Intent intent = new Intent();
                intent.putExtra("name", employeename);
                intent.putExtra("id", company);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search = etCode.getText().toString().trim();
                getCompanyList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //获取
    public void getCompanyList() {
        Api api = new Api(this, new OnNetRequest(this) {
            @Override
            public void onSuccess(String msg) {
                CompanyBeanResponse companyBeanResponse = JSONUtils.fromJson(msg, CompanyBeanResponse.class);
                if (companyBeanResponse != null && companyBeanResponse.result != null) {
                    companyAdapter.refresh(companyBeanResponse.result);
                }
            }
            @Override
            public void onFail() {
            }
        });
        api.getCompanyList(type,etCode.getText().toString().trim());
    }
}
