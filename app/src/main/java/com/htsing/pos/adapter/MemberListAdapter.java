package com.htsing.pos.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htsing.pos.R;
import com.htsing.pos.bean.HomeMemberInfo;

import java.util.List;

public class MemberListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeMemberInfo> list;
    private int mFlag;
    private int selectPosition = -1;

    public List<HomeMemberInfo> getList() {
        return list;
    }

    public MemberListAdapter(Context context, List<HomeMemberInfo> lvBeans) {
        this.mContext = context;
        this.list = lvBeans;
    }

    public MemberListAdapter(List<HomeMemberInfo> var1) {
        this.list = var1;
    }

    public void setDataList(List<HomeMemberInfo> dataList) {
        this.list = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();

    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold hold = null;
        if (null == convertView) {
            hold = new ViewHold();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.member_list_layout, null);
            hold.tv_member_tel = (TextView) convertView.findViewById(R.id.tv_member_tel);
            hold.tv_member_tel.setText(list.get(position).getUserMobile() + "  " + list.get(position).getNickName());

        } else {
            hold = (ViewHold) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHold {
        TextView tv_member_tel;
    }


}
