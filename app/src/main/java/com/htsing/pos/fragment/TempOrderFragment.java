package com.htsing.pos.fragment;

import android.widget.TextView;

import com.pgyer.pgyersdk.PgyerSDKManager;

import com.elvishew.xlog.XLog;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.adapter.holder.BaseListVH;
import com.htsing.pos.adapter.holder.TempOrderListHolder;
import com.htsing.pos.bean.ShopOrderDetailBean.DataBean.RecordsBean;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.bean.ShopOrderDetailBean;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.ui.login.PosMainActivity;
import com.htsing.pos.utils.CommonViewUtils;

import com.htsing.pos.bean.msg.PayEventBean;
import com.htsing.pos.base.IBaseFragmentList;

import org.json.JSONObject;

import butterknife.BindView;

import org.greenrobot.eventbus.EventBus;

public class TempOrderFragment extends IBaseFragmentList implements TempOrderListHolder.ListViewItemClickInterface {


    private BaseAct mBact;
    private PosMainActivity posMainActivity;
    //会员充值关闭
    @BindView(R.id.tv_close_temp_order_fragment)
    TextView tv_close_temp_order_fragment;


    private TempOrderListHolder holder;
    private int current = 1;

    private RecordsBean relustOrder;  //listView的点击以后的订单对象

    @Override
    public int getLayout() {
        return R.layout.act_home_temp_order_layout;
    }

    @Override
    public void initView() {
        super.initView();//初始化 下拉刷新的ListView

        mBact = getAct();
        posMainActivity = (PosMainActivity) getAct();

        CommonViewUtils.setOnClick(tv_close_temp_order_fragment, view -> {
            posMainActivity.showProductFragment();

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
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETTOPAIDORDER, ShopOrderDetailBean.class, stringResult -> {
                onOrderResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void onOrderResult(ShopOrderDetailBean relust) {
        mBact.showLoading(false);

        try {
            if (relust == null) {
                setAdapterEmptyView();
                return;
            }
        } catch (Exception e) {
            PgyerSDKManager.reportException(e);
        }

        if (relust.getData() == null) {
            showToast("没有挂单数据 ");
            return;
        } else {

            if (relust.getData().getRecords() != null && relust.getData().getRecords().size() > 0) {

                if (relust.getData().getRecords().size() > 0) {

                    this.relustOrder = relust.getData().getRecords().get(0);

                    current = relust.getData().getCurrent();

                    setData(relust.getData().getRecords());

                }

            }
        }

    }

    @Override
    public void onHandleItemClick(RecordsBean item) {
        if (item == null) {
            return;
        }

        this.relustOrder = item;
        posMainActivity.showProductFragment();

        PayEventBean payEventBean = new PayEventBean(BaseEventBean.TEMP_ORDER_DATA);
        payEventBean.setValue(relustOrder);
        payEventBean.setMobile(item.getUserMobile());
        EventBus.getDefault().post(payEventBean);
    }
}