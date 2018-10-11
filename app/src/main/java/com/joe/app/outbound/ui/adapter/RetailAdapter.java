package com.joe.app.outbound.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.app.outbound.R;
import com.joe.app.outbound.data.model.EmployeeBean;
import com.joe.app.outbound.data.model.RetailBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author MJ@ZHANG
 * @package: com.joe.app.outbound.ui.adapter
 * @filename RetailAdapter
 * @date on 2018/10/10 15:32
 * @descibe TODO
 * @email zhangmingjie@huansi.net
 */
public class RetailAdapter extends BaseAdapter {
    List<RetailBean> listData = new ArrayList<>();
    public void refresh(List<RetailBean> ls){
        if(ls == null){
            ls = new ArrayList<>();
        }else {
            listData = ls;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_retail_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        RetailBean managerBean = listData.get(position);
        viewHolder.retail_customcode.setText(managerBean.code);
        viewHolder.retail_billdate.setText(managerBean.billdate);
        viewHolder.retail_company.setText(managerBean.customer_name);
        viewHolder.retail_employee.setText(managerBean.salesman);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.retail_customcode)
        TextView retail_customcode;

        @Bind(R.id.retail_billdate)
        TextView retail_billdate;

        @Bind(R.id.retail_company)
        TextView retail_company;

        @Bind(R.id.retail_employee)
        TextView retail_employee;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}