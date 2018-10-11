package com.joe.app.outbound.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.joe.app.baseutil.ui.ActivityCollector;
import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.EmployeeBean;
import com.joe.app.outbound.data.model.RetailBean;
import com.joe.app.outbound.data.model.RetailResponse;
import com.joe.app.outbound.data.model.SaleSendOrderBean;
import com.joe.app.outbound.ui.adapter.RetailAdapter;
import com.joe.app.outbound.ui.widget.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RetailActivity extends BaseActivity {

    @Bind(R.id.txtvActionbarTitle)
    TextView txtvActionbarTitle;

    @Bind(R.id.pullToRefreshLayout)
    PullToRefreshLayout pullToRefreshLayout;

    ListView pullListView;

    @Bind(R.id.etSearch)
    ClearEditText etSearch;

    @Bind(R.id.back)
    Button back;

    @Bind(R.id.plus)
    Button plus;

    private List<RetailBean> retailBeanList;
    private RetailAdapter adapter;

    String search="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail);
        ButterKnife.bind(this);
        initview();
        getRetailList();
    }
    public void initview(){
        txtvActionbarTitle.setText("零售发货");
        pullToRefreshLayout.setPullUpEnable(false);
        pullListView = (ListView) pullToRefreshLayout.getPullableView();
        adapter = new RetailAdapter();
        pullListView.setAdapter(adapter);
        pullToRefreshLayout.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getRetailList();
            }
            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
            }
        });
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RetailBean retailBean = (RetailBean) adapter.getItem(position);
                    Intent intent = new Intent(RetailActivity.this, RetailPackActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RetailBean.class.getSimpleName(),retailBean);
                    intent.putExtras(bundle);
                    startActivity(intent);

            }
        });

        //检索
        etSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search = etSearch.getText().toString().trim();

            }
            @Override
            public void afterTextChanged(Editable s) {
                getRetailList();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetailActivity.this,RetailDetailActivity.class));
            }
        });

    }

    //获取销售发货单

    public void getRetailList() {
        Api api = new Api(this, new OnNetRequest(this, true, "正在加载...") {
            @Override
            public void onSuccess(String msg) {
                RetailResponse response = JSONUtils.fromJson(msg, RetailResponse.class);
                if (response != null && response.result != null) {
                    if (response.result.size() > 0) {
                        retailBeanList = response.result;
                    }else{
                        retailBeanList = new ArrayList<>();
                    }
                    adapter.refresh(retailBeanList);
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            public void onFail() {
                adapter.refresh(retailBeanList);
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }
        });
        api.getRetailList(search);
    }
    @Override
    protected void onResume() {

        super.onResume();

        getRetailList();
    }
}
