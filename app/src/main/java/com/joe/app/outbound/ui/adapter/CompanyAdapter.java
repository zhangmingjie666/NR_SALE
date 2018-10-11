package com.joe.app.outbound.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.app.outbound.R;
import com.joe.app.outbound.data.model.CompanyBean;

import java.util.ArrayList;
import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author MJ@ZHANG
 * @package: com.example.nrbzms17.ui.adapter
 * @filename SpinnerDateAdapter
 * @date on 2018/8/10 17:20
 * @descibe TODO
 * @email zhangmingjie@huansi.net
 */
public class CompanyAdapter extends BaseAdapter {
        List<CompanyBean> listData = new ArrayList<>();

    public void refresh(List<CompanyBean> ls) {
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

            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_company, null);


            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        CompanyBean managerBean = listData.get(position);


        viewHolder.link_fullname.setText(managerBean.fullname);


        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.link_fullname)
        TextView link_fullname;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
