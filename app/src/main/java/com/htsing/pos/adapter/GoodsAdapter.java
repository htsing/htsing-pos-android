package com.htsing.pos.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.htsing.pos.R;
import com.htsing.pos.bean.ProductList;

import java.util.List;

public class GoodsAdapter extends BaseAdapter {

    private Context mContext;
    private List<ProductList.DataBean> list;
    private int mFlag;
    private int selectPosition = -1;

    public GoodsAdapter(Context context, List<ProductList.DataBean> gvBeans, int flag) {
        this.mContext = context;
        this.list = gvBeans;
        this.mFlag = flag;
    }

    public GoodsAdapter(List<ProductList.DataBean> var1) {
        this.list = var1;
    }

    public void setNewData(List<ProductList.DataBean> list) {
        this.list = list;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gv_menus_layout, null);
            hold.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
            hold.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            hold.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            hold.tvStock = (TextView) convertView.findViewById(R.id.tv_stock);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }
//        hold.ivPhoto.setImageResource(list.get(position).getImgId());

        Glide
                .with(mContext)
                .load(list.get(position).getPicUrls()[0])
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(hold.ivPhoto);


        hold.tvName.setText(list.get(position).getName());
        hold.tvPrice.setText(list.get(position).getPriceRetail()+ "/" +list.get(position).getUnit());
        hold.tvStock.setText(list.get(position).getPriceVip()+"");

//        switch (mFlag) {
//            case 1:
//                hold.tvUnit.setText("/" + ResourcesUtils.getString(mContext, R.string.units_tin));
//                break;
//            case 2:
//                hold.tvUnit.setText("/" + ResourcesUtils.getString(mContext, R.string.units_kg));
//                break;
//            case 3:
//                hold.tvUnit.setText("/" + ResourcesUtils.getString(mContext, R.string.units_bag));
//                break;
//        }

        return convertView;
    }

    public class ViewHold {
        private ImageView ivPhoto;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvStock;
    }


}
