package com.htsing.pos.fragment;

import android.widget.TextView;

import com.google.gson.Gson;
import com.htsing.pos.bean.ProductList;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.ui.login.PosActivity;
import com.htsing.pos.utils.JsonParseUtils;
import com.pgyer.pgyersdk.PgyerSDKManager;

import com.elvishew.xlog.XLog;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.adapter.holder.BaseListVH;
import com.htsing.pos.adapter.holder.TempOrderListHolder;
import com.htsing.pos.bean.ShopOrderDetail.RecordsBean;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.bean.ShopOrderDetail;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.utils.CommonViewUtils;

import com.htsing.pos.bean.msg.PayEventBean;
import com.htsing.pos.base.IBaseFragmentList;

import org.json.JSONObject;

import butterknife.BindView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class TempOrderFragment extends IBaseFragmentList implements TempOrderListHolder.ListViewItemClickInterface {


    private BaseAct mBact;
    private PosActivity posActivity;
    //会员充值关闭
    @BindView(R.id.tv_close_temp_order_fragment)
    TextView tv_close_temp_order_fragment;


    private TempOrderListHolder holder;
    private int current = 1;

    private ShopOrderDetail.RecordsBean relustOrder;  //listView的点击以后的订单对象

    @Override
    public int getLayout() {
        return R.layout.act_home_temp_order_layout;
    }

    @Override
    public void initView() {
        super.initView();//初始化 下拉刷新的ListView

        mBact = getAct();
        posActivity = (PosActivity) getAct();

        CommonViewUtils.setOnClick(tv_close_temp_order_fragment, view -> {
            posActivity.showProductFragment();

        });

    }

    @Override
    public void Refresh() {
//        post();
//        getToPaidOrder(0, 1);
    }


    public void updateTempOrderData() {
        getToPaidOrder(0, 1);
    }

    @Override
    public void onLoadMore() {
//        post();
        if (current > 1)
            current++;
        getToPaidOrder(0, current);
    }

    /**
     * 重写父类的方法
     *
     * @return
     */
    @Override
    protected BaseListVH getAdapterConvertVH() {
        holder = new TempOrderListHolder();
        holder.setContext(getContext());
        holder.setItemClickInterface(this);
        return holder;
    }


    private void getToPaidOrder(int orderStatus, int current) {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", Constant.getShopId());
            json.put("size", 10);
            json.put("current", current);
            if (orderStatus == 0) {
                json.put("status", "");
            } else {
                json.put("status", orderStatus);
            }

            mBact.showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETTOPAIDORDER, CommonResult.class, stringResult -> {
                onOrderResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void onOrderResult(CommonResult commonResult) {
        mBact.showLoading(false);

        try {
            if (commonResult == null) {
                setAdapterEmptyView();
                return;
            }
        } catch (Exception e) {
            PgyerSDKManager.reportException(e);
        }

        if (commonResult.getResult() == null) {
            showToast("没有挂单数据 ");
            return;
        } else {

            ShopOrderDetail shopOrderDetail = JsonParseUtils.parse(new Gson().toJson(commonResult.getResult(), Map.class), ShopOrderDetail.class);

            if (shopOrderDetail.getRecords() != null && shopOrderDetail.getRecords().size() > 0) {

                if (shopOrderDetail.getRecords().size() > 0) {

                    this.relustOrder = shopOrderDetail.getRecords().get(0);

                    current = shopOrderDetail.getCurrent();

                    setData(shopOrderDetail.getRecords());

                }

            }
        }

    }

    @Override
    public void onHandleItemClick(ShopOrderDetail.RecordsBean item) {
        if (item == null) {
            return;
        }

        this.relustOrder = item;
        posActivity.showProductFragment();

        PayEventBean payEventBean = new PayEventBean(BaseEventBean.TEMP_ORDER_DATA);
        payEventBean.setValue(relustOrder);
        payEventBean.setMobile(item.getUserMobile());
        EventBus.getDefault().post(payEventBean);
    }
}
