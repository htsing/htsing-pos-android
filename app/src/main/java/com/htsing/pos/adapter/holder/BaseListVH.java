package com.htsing.pos.adapter.holder;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public abstract class BaseListVH<T> {

    private List<T> mData;

    public abstract int getAdapterLayoutId();

    public abstract void convert(@NonNull BaseViewHolder helper, T item);

    public void setData(List<T> data){
        mData = data;
    }

    public List<T> getData(){
        return mData;
    }
}
