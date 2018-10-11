package com.joe.app.outbound.ui.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.joe.app.outbound.data.model.PackageBean;
import com.joe.app.outbound.data.model.PackageResponseBean;
import com.joe.app.outbound.data.model.ResponseBean;
import com.joe.app.outbound.data.model.RetailBean;
import com.joe.app.outbound.ui.dialog.InputPackageNumDialog;
import com.joe.app.outbound.ui.widget.ClearEditText;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RetailPackActivity extends BaseActivity {

    private RetailBean retailBean;
    private InputPackageNumDialog inputDialog;
    private PackListAdapter adapter;
    private String addId;

    private int isreel = 1;

    @Bind(R.id.retail_customcode)
    TextView retail_customcode;
    @Bind(R.id.retail_billdate)
    TextView retail_billdate;
    @Bind(R.id.retail_company)
    TextView retail_company;
    @Bind(R.id.retail_employee)
    TextView retail_employee;

    @Bind(R.id.etScanCode)
    ClearEditText etScanCode;

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.dye_check)
    CheckBox dye_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_pack);
        ButterKnife.bind(this);
        initview();
        addPackageItem();
    }

    public void initview() {
        adapter = new PackListAdapter();
        listView.setAdapter(adapter);
        if (getIntent() != null) {
            retailBean = (RetailBean) getIntent().getSerializableExtra(RetailBean.class.getSimpleName());
        }
        if (retailBean != null) {
            retail_customcode.setText(retailBean.code);
            retail_billdate.setText(retailBean.billdate);
            retail_company.setText(retailBean.customer_name);
            addId = retailBean.id;
        }

        etScanCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            //发送请求
                            Log.d("addPackage", "onEditorAction:" + actionId);
                            MUtils.hideSoftInput(RetailPackActivity.this);

                            addPackage("");
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

        //是否整卷
        dye_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isreel = 2;
                } else {
                    isreel = 1;
                }

            }
        });
    }
    //添加出库单
    public void addPackage(String value){

        String barcode = etScanCode.getText().toString().trim();
        if(TextUtils.isEmpty(barcode)){
            UIHelper.showLongToast(this,"请输入条码");
            return;
        }
        if (isreel==1) {
            inputDialog = new InputPackageNumDialog(RetailPackActivity.this, "");
            inputDialog.show();
            inputDialog.setOnInputListener(new InputPackageNumDialog.OnInputListener() {
                @Override
                public void input(String value) {
                    addPackage(value);
                }

                @Override
                public void dismiss() {
                    requestScanFocus();
                }
            });
            return;
        }

        Api api = new Api(this, new OnNetRequest(this,true,"请稍等...") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    UIHelper.showShortToast(RetailPackActivity.this, "新增成功");
                    adapter.refresh(responseBean.result);
                } else {
//                    adapter.refresh(responseBean.result);
                }
//                txtvCount.setText(adapter.getTotalCount());
                addPackageItem();
                etScanCode.setText("");
            }

            @Override
            public void onFail() {
//                txtvCount.setText(adapter.getTotalCount());
                etScanCode.setText("");
            }
        });
        api.addPack(addId,barcode);
    }

    //添加出库单
    public void addPackageItem(){

        Api api = new Api(this, new OnNetRequest(this,true,"请稍等...") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    adapter.refresh(responseBean.result);

                } else {

                }

                etScanCode.setText("");
            }

            @Override
            public void onFail() {
//                txtvCount.setText(adapter.getTotalCount());
                etScanCode.setText("");
            }
        });
        api.addPackageItem(addId);
    }

    class PackListAdapter extends BaseAdapter {
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retail_add, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final PackageBean packageBean = packageBeanList.get(position);
            viewHolder.txtvBarcode.setText(packageBean.barcode);
            viewHolder.txtv_material.setText(packageBean.material);
            viewHolder.txtvVolume.setText(packageBean.volume);
            viewHolder.txtvQuantity.setText(packageBean.quantity);
            viewHolder.txtv_color.setText(packageBean.color);

            viewHolder.txtv_craft.setText(packageBean.craft);
            viewHolder.txtvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String barCode = packageBean.barcode;
                    AlertDialog.Builder builder = new AlertDialog.Builder(RetailPackActivity.this);
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
        @Bind(R.id.txtv_material)
        TextView txtv_material;
        @Bind(R.id.txtv_volume)
        TextView txtvVolume;
        @Bind(R.id.txtv_quantity)
        TextView txtvQuantity;
        @Bind(R.id.txtv_color)
        TextView txtv_color;
        @Bind(R.id.txtv_craft)
        TextView txtv_craft;
        @Bind(R.id.txtvDelete)
        TextView txtvDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    //删除出库单
    public void deletePackage(String id){
        Api api = new Api(this, new OnNetRequest(this,true,"请稍等") {
            @Override
            public void onSuccess(String msg) {
                PackageResponseBean responseBean = JSONUtils.fromJson(msg, PackageResponseBean.class);
                if (responseBean != null && responseBean.result != null) {
                    if (responseBean.result.size() == 0) {
                        UIHelper.showShortToast(RetailPackActivity.this, "该订单下无发货码单");
                    }
                    UIHelper.showShortToast(RetailPackActivity.this, "删除成功");
                    adapter.refresh(responseBean.result);
                } else {
                    UIHelper.showShortToast(RetailPackActivity.this, "该订单下无发货码单");
                }
//                txtvCount.setText(adapter.getTotalCount());
            }

            @Override
            public void onFail() {
//                txtvCount.setText(adapter.getTotalCount());
            }
        });
        api.deletePackage(id);
    }
    public void requestScanFocus() {
        etScanCode.requestFocus();
        etScanCode.setFocusable(true);
    }
    @Subscribe(priority = 2)
    public void OnScanResultEvent(final ScanResultEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (inputDialog == null || !inputDialog.isShowing()) {
                    etScanCode.setText(event.getResult());
                    addPackage("");
                }
            }
        });
    }
}
