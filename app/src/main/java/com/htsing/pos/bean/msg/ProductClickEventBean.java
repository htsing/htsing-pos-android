package com.htsing.pos.bean.msg;

import com.htsing.pos.base.fragment.BaseEventBean;

public class ProductClickEventBean extends BaseEventBean {

    public ProductClickEventBean(int type, int position) {
        super(type);
        this.position = position;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
