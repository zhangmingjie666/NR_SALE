package com.joe.app.outbound.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.app.baseutil.Event;
import com.joe.app.baseutil.ui.BaseActivity;
import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.baseutil.util.MUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.R;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.event.ScanResultEvent;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.EmployeeBean;
import com.joe.app.outbound.data.model.PackageBean;
import com.joe.app.outbound.data.model.PackageResponseBean;
import com.joe.app.outbound.data.model.SaleSendOrderBean;
import com.joe.app.outbound.ui.dialog.InputPackageNumDialog;
import com.joe.app.outbound.ui.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaleSendDetailActivity extends BaseActivity {

    @Bind(R.id.txtvLeft)
    TextView txtvLeft;
    @Bind(R.id.txtvActionbarTitle)
    TextView txtvActionbarTitle;
    @Bind(R.id.txtvRight)
    TextView txtvRight;
    @Bind(R.id.txtv_material)
    TextView txtvMaterial;
    @Bind(R.id.txtvCompanyName)
    TextView txtvCompanyName;
    @Bind(R.id.txtv_color)
    TextView txtvColor;
    @Bind(R.id.txtvCount)
    TextView txtvCount;
    @Bind(R.id.txtv_plan_quantity)
    TextView txtvPlanQuantity;
    @Bind(R.id.txtvPackageNum)
    TextView txtvPackageNum;
    @Bind(R.id.etScanCode)
    ClearEditText etScanCode;
    @Bind(R.id.listView)
    ListView listView;
    private EmployeeBean mEmployee;
    private SaleSendOrderBean saleSendOrder;
    private InputPackageNumDialog inputDialog;
    private PackageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_send_detail);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            mEmployee = (EmployeeBean) getIntent().getSerializableExtra(EmployeeBean.class.getSimpleName());
            saleSendOrder = (SaleSendOrderBean) getIntent().getSerializableExtra(SaleSendOrderBean.class.getSimpleName());
        }
        setViews();
    }

    private void setViews() {
        adapter = new PackageListAdapter();
        listView.setAdapter(adapter);
//        listView.setEnabled(false);
        listView.setSelected(false);
        listView.setFocusable(false);
        if (mEmployee != null) {
            txtvRight.setText(mEmployee.name);
        }
        if (saleSendOrder != null) {
            txtvActionbarTitle.setText(saleSendOrder.customcode);
            txtvCompanyName.setText(saleSendOrder.customer_name);
            txtvMaterial.setText(saleSendOrder.material);
            txtvColor.setText(saleSendOrder.color);
            txtvPlanQuantity.setText(saleSendOrder.plan_quantity);
            txtvCount.setText(adapter.getTotalCount());
            getPackageList(saleSendOrder.id);
        }
        etScanCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            //发送请求
                            Log.i("addPackage","onEditorAction:"+actionId);
                            MUtils.hideSoftInput(SaleSendDetailActivity.this);
                            addPackage();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

    }




    @OnClick(R.id.txtvLeft)
    public void onBackClickListener() {
        SaleSendDetailActivity.this.finish();
    }

    @OnClick(R.id.txtvConfirm)
    public void onConfirmClickListener() {
        MUtils.hideSoftInput(SaleSendDetailActivity.this);
        addPackage();
    }

    @OnClick(R.id.txtvPackageNum)
    public void onPackageNumClick() {
        String value = txtvPackageNum.getText().toString().trim();
        inputDialog = new InputPackageNumDialog(SaleSendDetailActivity.this, value);
        inputDialog.show();
        inputDialog.setOnInputListener(new InputPackageNumDialog.OnInputListener() {
            @Override
            public void input(String value) {
                txtvPackageNum.setText(value);
            }

            @Override
            public void dismiss() {
                requestScanFocus();
            }
        });
    }

    //添加出库单
    public void addPackage(){
        String bale = txtvPackageNum.getText().toString().trim();
//        if(TextUtils.isEmpty(bale)){
//            UIHelper.showLongToast(this,"请输入包号");
//            return;
//        }
        String barcode = etScanCode.getText().toString().trim();
        if(TextUtils.isEmpty(barcode)){
            UIHelper.showLongToast(this,"请输入条码");
            return;
        }
        Api api = new Api(this, new OnNetRequest(this,true,"请稍等...") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    if (responseBean.result.size() == 0) {
                        UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                    }
                    UIHelper.showShortToast(SaleSendDetailActivity.this, "出库成功");
                    adapter.refresh(responseBean.result);
                } else {
                    UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                }
                txtvCount.setText(adapter.getTotalCount());
                etScanCode.setText("");
            }

            @Override
            public void onFail() {
                txtvCount.setText(adapter.getTotalCount());
                etScanCode.setText("");
            }
        });
        api.addPackage(saleSendOrder.id, mEmployee.id, barcode, bale);
    }

    //删除出库单
    public void deletePackage(String id){
        Api api = new Api(this, new OnNetRequest(this,true,"请稍等") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    if (responseBean.result.size() == 0) {
                        UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                    }
                    UIHelper.showShortToast(SaleSendDetailActivity.this, "删除成功");
                    adapter.refresh(responseBean.result);
                } else {
                    UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                }
                txtvCount.setText(adapter.getTotalCount());
            }

            @Override
            public void onFail() {
                txtvCount.setText(adapter.getTotalCount());
            }
        });
        api.deletePackage(id);
    }


    //获取出库单
    public void getPackageList(String orderId) {
        Api api = new Api(this, new OnNetRequest(this, true, "正在加载...") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    if (responseBean.result.size() == 0) {
                        UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                    }
                    adapter.refresh(responseBean.result);
                } else {
                    UIHelper.showShortToast(SaleSendDetailActivity.this, "该订单下无发货码单");
                }
                txtvCount.setText(adapter.getTotalCount());
            }

            @Override
            public void onFail() {
                txtvCount.setText(adapter.getTotalCount());
            }
        });
        api.getPackageList(orderId);
    }

    public void requestScanFocus() {
        etScanCode.requestFocus();
        etScanCode.setFocusable(true);
    }


    class PackageListAdapter extends BaseAdapter {
        List<PackageBean> packageBeanList = new ArrayList<>();

        public String getTotalCount(){
            double volumeCount = 0;//匹数
            double quantityCount = 0;//数量
            for(PackageBean packageBean:packageBeanList){
                if(!TextUtils.isEmpty(packageBean.volume)){
                    volumeCount += Double.parseDouble(packageBean.volume);
                }
                if(!TextUtils.isEmpty(packageBean.quantity)){
                    quantityCount += Double.parseDouble(packageBean.quantity);
                }
            }
//            DecimalFormat formater = new DecimalFormat("#.00");
//            String vc = formater.format(volumeCount);
//            String qc = formater.format(quantityCount);
//            if(vc.contains(".")){
//                String[] value1 = vc.split(".");
//                if(value1[1].equals("00")){
//                    vc = value1[0];
//                }
//            }
//            if(qc.contains(".")){
//                String[] value1 = qc.split(".");
//                if(value1[1].equals("00")){
//                    qc = value1[0];
//                }
//            }
//            BigDecimal vc = new BigDecimal(volumeCount);
//            vc = vc.setScale(2,BigDecimal.ROUND_HALF_UP);
//            BigDecimal qc = new BigDecimal(quantityCount);
//            qc = vc.setScale(2,BigDecimal.ROUND_HALF_UP);
            volumeCount=((int)(volumeCount*100))/100.0;
            quantityCount=((int)(quantityCount*100))/100.0;
            String vcText = "";
            String qcText = "";
            if(volumeCount == 0){
                vcText = "0";
            }else{
                vcText = volumeCount+"";
            }
            if(quantityCount == 0){
                qcText = "0";
            }else{
                qcText = quantityCount+"";
            }
            return "[ " + vcText + " : "+ qcText + " ]";
        }


        public void refresh(List<PackageBean> ls) {
            if (ls == null) {
                ls = new ArrayList<>();
            }
            packageBeanList = ls;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return packageBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return packageBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_package, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final PackageBean packageBean = packageBeanList.get(position);
            viewHolder.txtvBarcode.setText(packageBean.barcode);
            viewHolder.txtvBale.setText(packageBean.bale);
            viewHolder.txtvVolume.setText(packageBean.volume);
            viewHolder.txtvQuantity.setText(packageBean.quantity);
            viewHolder.txtvReel.setText(packageBean.reel);
            viewHolder.txtvLot.setText(packageBean.lot);
            viewHolder.txtvGrade.setText(packageBean.grade);
            viewHolder.txtvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String barCode = packageBean.barcode;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SaleSendDetailActivity.this);
                    builder.setMessage("是否确认删除此发货码单\n"+ barCode +"?");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String id = packageBean.id;
                            deletePackage(id);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
//                    UIHelper.showShortToast(SaleSendDetailActivity.this,position+"");
                }
            });
            return convertView;
        }
    }
    static class ViewHolder {
        @Bind(R.id.txtv_barcode)
        TextView txtvBarcode;
        @Bind(R.id.txtv_bale)
        TextView txtvBale;
        @Bind(R.id.txtv_volume)
        TextView txtvVolume;
        @Bind(R.id.txtv_quantity)
        TextView txtvQuantity;
        @Bind(R.id.txtv_reel)
        TextView txtvReel;
        @Bind(R.id.txtv_lot)
        TextView txtvLot;
        @Bind(R.id.txtv_grade)
        TextView txtvGrade;
        @Bind(R.id.txtvDelete)
        TextView txtvDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Subscribe(priority = 2)
    public void OnScanResultEvent(final ScanResultEvent event){
        EventBus.getDefault().cancelEventDelivery(event);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(inputDialog==null||!inputDialog.isShowing()){
                    etScanCode.setText(event.getResult());
                    addPackage();
                }
            }
        });
    }
}
