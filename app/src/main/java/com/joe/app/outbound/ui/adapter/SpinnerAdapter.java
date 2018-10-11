package com.joe.app.outbound.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.app.outbound.R;
import com.joe.app.outbound.data.model.EmployeeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Joe on 2016/6/8.
 * Email-joe.zong@xiaoniubang.com
 */
public class SpinnerAdapter extends BaseAdapter {
    List<EmployeeBean> listData = new ArrayList<>();
    public void refresh(List<EmployeeBean> ls){
        if(ls == null){
            ls = new ArrayList<>();
        }
        listData = ls;
        listData.add(0,new EmployeeBean("-1","请选择"));
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
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        EmployeeBean managerBean = listData.get(position);
        viewHolder.txtvName.setText(managerBean.name);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.txtvName)
        TextView txtvName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
