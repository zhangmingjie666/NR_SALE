package com.joe.app.outbound.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.baseutil.util.MUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.SharedPreference;
import com.joe.app.outbound.data.event.HostChangeEvent;
import com.joe.app.outbound.data.event.ScanResultEvent;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.EmployeeBean;
import com.joe.app.outbound.data.model.EmployeeResponseBean;
import com.joe.app.outbound.data.model.SaleSendOrderBean;
import com.joe.app.outbound.data.model.SaleSendOrderResponse;
import com.joe.app.outbound.data.upgrade.UpgradeManager;
import com.joe.app.outbound.ui.adapter.SpinnerAdapter;
import com.joe.app.outbound.ui.widget.ClearEditText;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaleActivity extends BaseActivity {
    @Bind(R.id.txtvActionbarTitle)
    TextView txtvActionbarTitle;
    @Bind(R.id.spinner)
    Spinner spinner;
//    @Bind(R.id.update)
//    TextView update;
    @Bind(R.id.back)
    TextView back;

    @Bind(R.id.pullToRefreshLayout)
    PullToRefreshLayout pullToRefreshLayout;

    ListView pullListView;
    @Bind(R.id.etSearch)
    ClearEditText etSearch;

    private SalesendAdapter adapter;

    private SpinnerAdapter spinnerAdapter;
    private EmployeeBean currentEmployee;

    private List<SaleSendOrderBean> saleSendOrderBeanList;
    private EasterEggCounter mEasterEggCounter;

    private String currentEmployeeId;

//    private DataProvider mDataProvider;

    // APP更新
    private UpgradeManager upgradeManager;

    private String AllPermissions[] = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        ButterKnife.bind(this);
        setViews();
        setClickListeners();
        getSaleSendList(true);
        getEmployeeInfo();
    }

    private void setViews() {
        txtvActionbarTitle.setText("出库扫码");
        pullToRefreshLayout.setPullUpEnable(false);
        pullListView = (ListView) pullToRefreshLayout.getPullableView();
        adapter = new SalesendAdapter();
        pullListView.setAdapter(adapter);
        spinnerAdapter = new SpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);
        mEasterEggCounter = new EasterEggCounter();
//        update.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    /**
//                     * 第 1 步: 检查是否有相应的权限
//                     */
//                    boolean isAllGranted = checkPermissionAllGranted(AllPermissions);
//                    // 如果这3个权限全都拥有, 则直接执行备份代码
//                    if (isAllGranted) {
//                        upgradeManager.startUpgradeVersion();
//                        return;
//                    }
//
//                    /**
//                     * 第 2 步: 请求权限
//                     */
//                    // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
//                    ActivityCompat.requestPermissions(SaleActivity.this, AllPermissions, MY_PERMISSION_REQUEST_CODE);
//
//                }
//            });
//        // APP更新初始化
//        upgradeManager = new UpgradeManager();
//        upgradeManager.init(getApplicationContext());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setClickListeners() {
        pullToRefreshLayout.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getSaleSendList(false);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEmployee = (EmployeeBean) spinnerAdapter.getItem(position);
                SharedPreference.setEmplyeeId(currentEmployee.id);
                currentEmployeeId = currentEmployee.id;
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentEmployee == null || currentEmployee.id.equals("-1")) {
                    UIHelper.showShortToast(SaleActivity.this, "请选择员工");
                } else {
                    SaleSendOrderBean saleSendOrderBean = (SaleSendOrderBean) adapter.getItem(position);
                    Intent intent = new Intent(SaleActivity.this, SaleSendDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EmployeeBean.class.getSimpleName(), currentEmployee);
                    bundle.putSerializable(SaleSendOrderBean.class.getSimpleName(), saleSendOrderBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        etSearch.setOnInputChange(new ClearEditText.OnInputChange() {
            @Override
            public void onAfterTextChange() {
                adapter.getFilter().filter(etSearch.getText().toString().trim());
            }
        });
    }

    //获取员工信息
    public void getEmployeeInfo() {
        Api api = new Api(this, new OnNetRequest(this) {
            @Override
            public void onSuccess(String msg) {
                EmployeeResponseBean employeeResponseBean = JSONUtils.fromJson(msg, EmployeeResponseBean.class);
                if (employeeResponseBean != null && employeeResponseBean.result != null) {
                    spinnerAdapter.refresh(employeeResponseBean.result);
                    String employeeId = SharedPreference.getEmplyeeId();
                    if(TextUtils.isEmpty(employeeId)||employeeId.equals("-1")){
                        return;
                    }
                    for(int i = 0;i<spinnerAdapter.getCount();i++){
                        EmployeeBean bean = (EmployeeBean)spinnerAdapter.getItem(i);
                        if(bean.id.equals(employeeId)){
                            spinner.setSelection(i);

                            currentEmployeeId = bean.id;
                            getSaleSendList( false);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFail() {
                currentEmployee = null;
            }
        });
        api.getEmployeeInfo();
    }

    //获取销售发货单

    public void getSaleSendList(boolean isShowLoading) {
        Api api = new Api(this, new OnNetRequest(this, isShowLoading, "正在加载...") {
            @Override
            public void onSuccess(String msg) {
                SaleSendOrderResponse response = JSONUtils.fromJson(msg, SaleSendOrderResponse.class);
                if (response != null && response.result != null) {
                    if (response.result.size() > 0) {
                        saleSendOrderBeanList = response.result;
                    }else{
                        saleSendOrderBeanList = new ArrayList<>();
                    }
                    adapter.refresh(saleSendOrderBeanList);
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }






            @Override
            public void onFail() {
                adapter.refresh(saleSendOrderBeanList);
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }
        });
        api.getSaleSendOrderInfoList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSaleSendList( false);
    }

    @OnClick(R.id.txtvActionbarTitle)
    public void onTitleClickListener(){
        mEasterEggCounter.tapped();
    }

    @Subscribe
    public void onChangeHostEvent(HostChangeEvent event){
        UIHelper.post(new Runnable() {
            @Override
            public void run() {
                getSaleSendList(false);
            }
        });
    }


    class SalesendAdapter extends BaseAdapter {
        private Filter filter;

        List<SaleSendOrderBean> listData = new ArrayList<>();

        public void refresh(List<SaleSendOrderBean> ls) {
            if (ls == null) {
                listData = new ArrayList<>();
            } else {
                listData = ls;
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public Filter getFilter() {
            if (filter == null) {
                filter = new SaleSendOrderFilter();
            }
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_sale_send, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SaleSendOrderBean bean = listData.get(position);
            viewHolder.txtvCustomcode.setText(bean.customcode);
            viewHolder.txtvCustomerName.setText(bean.customer_name);
            viewHolder.txtvMaterial.setText(bean.material);
            if (TextUtils.isEmpty(bean.craft)) {
                viewHolder.txtvColor.setText(bean.color);
            } else {
                viewHolder.txtvColor.setText(bean.color + "[" + bean.craft + "]");
            }
            viewHolder.txtvBilldate.setText(bean.billdate);
            viewHolder.txtvPlanQuantity.setText(bean.plan_quantity);
            viewHolder.txtvQuantityString.setText(bean.quantity_string);
            return convertView;
        }
    }

    class SaleSendOrderFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults filterResults = new FilterResults();
            if (prefix == null || prefix.length() == 0 || saleSendOrderBeanList == null || saleSendOrderBeanList.size() == 0) {
                ArrayList<SaleSendOrderBean> l = new ArrayList<>(saleSendOrderBeanList);
                filterResults.values = l;
                filterResults.count = l.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                final List<SaleSendOrderBean> list = new ArrayList<>(saleSendOrderBeanList);
                final List<SaleSendOrderBean> newList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i).customcode.toString().trim();
                    if (id.contains(prefixString)) {
                        newList.add(list.get(i));
                    }
                }
                filterResults.values = newList;
                filterResults.count = newList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<SaleSendOrderBean> list = (List<SaleSendOrderBean>) results.values;
            adapter.refresh(list);
//            if (results.count > 0) {
//                adapter.refresh(list);
//            }
        }
    }


    static class ViewHolder {
        @Bind(R.id.txtv_customcode)
        TextView txtvCustomcode;
        @Bind(R.id.txtv_customer_name)
        TextView txtvCustomerName;
        @Bind(R.id.txtv_material)
        TextView txtvMaterial;
        @Bind(R.id.txtv_color)
        TextView txtvColor;
        @Bind(R.id.txtv_quantity_string)
        TextView txtvQuantityString;
        @Bind(R.id.txtv_plan_quantity)
        TextView txtvPlanQuantity;
        @Bind(R.id.txtv_billdate)
        TextView txtvBilldate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class EasterEggCounter {
        private static final int MAX_TAP_COUNT = 10;
        private static final long TIME_TO_DISMISS_MILLIS = 2500;

        private int mTapCount = 0;
        private Thread mDismissThread;

        /**
         * Method for counting number of taps
         */

        public synchronized void tapped() {
            if (mDismissThread == null) {
                mDismissThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(TIME_TO_DISMISS_MILLIS);
                            reset();
                        } catch (InterruptedException ie) {
                            reset();
                        }
                    }
                });

                mDismissThread.start();
            }

            mTapCount++;
            if (mTapCount == MAX_TAP_COUNT) {
                reset();

                Intent intent = new Intent(SaleActivity.this, SettingActivity.class);
                SaleActivity.this.startActivity(intent);
            }
        }

        /**
         * Method for resetting number of taps
         */
        private synchronized void reset() {
            mTapCount = 0;
            mDismissThread = null;
        }
    }

    @Subscribe(priority = 1)
    public void OnScanResultEvent(final ScanResultEvent event){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MUtils.hideSoftInput(SaleActivity.this);
                etSearch.setText(event.getResult());
            }
        });
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                upgradeManager.startUpgradeVersion();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("系统升级需访问 “网络” 和 “存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
