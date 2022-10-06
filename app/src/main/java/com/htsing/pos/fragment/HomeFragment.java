package com.htsing.pos.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.adapter.CardListAdapter;
import com.htsing.pos.adapter.CategoryListAdapter;
import com.htsing.pos.adapter.GoodsAdapter;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.base.fragment.HomeBaseFragment;
import com.htsing.pos.bean.Category;
import com.htsing.pos.bean.OrderSusses;
import com.htsing.pos.bean.ProductList;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.ui.login.PosMainActivity;
import com.htsing.pos.utils.CommonViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragment extends HomeBaseFragment {
    //监听 商米收银设备 扫码枪的广播
    private static final String ACTION_DATA_CODE_RECEIVED =
            "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
    private static final String ACTION_GETUI_PAY_MSG = "com.xeld.getui.pay.msg";
    //监听 商米收银设备 扫码枪的广播 key
    private static final String DATA = "data";
    //监听 商米收银设备 扫码枪的广播 key
    private static final String SOURCE = "source_byte";
    //首次获取商品列表的GridView的适配器
    private GoodsAdapter goodsAdapter;
    //商品分类的listView适配器
    private CategoryListAdapter cataListAdapter;
    //购物车列表里面的listView适配器
    private CardListAdapter cardListAdapter;
    //首次加载获取的商品列表
    private List<ProductList.DataBean> cardList;
    //首次加载获取的商品分类
    private List<Category.DataBean> categoryList;

    //首次获取商品列表的GridView
    @BindView(R.id.gv_shop_list)
    GridView goodGridView;
    //购物车列表
    @BindView(R.id.lv_cart_list)
    ListView cartListView;
    //商品类目列表
    @BindView(R.id.lv_product_cata_title)
    ListView cataListView;
    //商品搜索的输入框
    @BindView(R.id.input_shop_name)
    EditText evTextProdectName;
    //结算 按钮的父布局
    @BindView(R.id.layout_order_confrim)
    LinearLayout layoutConfirm;

    @BindView(R.id.layout_home_member_info)
    LinearLayout layoutMemberInfo;

    @BindView(R.id.layout_cart_tips_info)
    LinearLayout layoutCartTipsInfo;

    //会员姓名
    @BindView(R.id.tv_member_name)
    TextView tv_member_name;

    //会员积分
    @BindView(R.id.tv_member_integral)
    TextView tv_member_integral;

    //会员余额
    @BindView(R.id.tv_member_balance)
    TextView tv_member_balance;

    //充值 会员
    @BindView(R.id.tv_member_recharge)
    TextView tv_member_recharge;

    //会员 退出
    @BindView(R.id.tv_member_exit)
    TextView tv_member_exit;


    private BaseAct mBact;
    private PosMainActivity posMainActivity;

    @Override
    public int getLayout() {
        return R.layout.activity_mian_home_layout;
    }

    @Override
    public void initView() {
        mBact = getAct();
        posMainActivity = (PosMainActivity) getAct();

        cardList = new ArrayList<>();
        categoryList = new ArrayList<>();

        CommonViewUtils.setOnClick(layoutConfirm, view -> {
//            confirmOrder();

//            BaseEventBean eventBean = new BaseEventBean(BaseEventBean.HOME_PAY_FRAGMENT);
//            EventBus.getDefault().post(eventBean);
// modify 2022.05.28
            posMainActivity.replaceFragment(posMainActivity.homePayFragment);

            XLog.d("send msg");

        });

        CommonViewUtils.setOnClick(tv_member_exit, view -> {
            layoutCartTipsInfo.setVisibility(View.VISIBLE);
            layoutMemberInfo.setVisibility(View.GONE);
            XLog.d("send msg");

        });

        layoutCartTipsInfo.setVisibility(View.GONE);
        layoutMemberInfo.setVisibility(View.VISIBLE);

        initData();
    }


    private void initData() {
        getProducts();
        initCataData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void handleEvent(BaseEventBean eventBean) {

    }

    /**
     * 首次进入收银主页，获取推荐商品列表
     */
    private void getProducts() {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", Constant.getShopId());
            mBact.showLoading();
            easyGet(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETPRODUCTS, ProductList.class, stringResult -> {
                onProductsResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    /**
     * 获取商品列表的接口回调处理方法
     *
     * @param result
     */
    private void onProductsResult(ProductList result) {
        XLog.d("onProductsResult    = ");
        mBact.showLoading(false);
        if (result != null) {
            List<ProductList.DataBean> data = result.getRecords();
            if (data != null) {
                ProductList.DataBean bean = data.get(0);
                String shopName = bean.getShopName();
                XLog.d("shopName   = " + shopName);

                goodsAdapter = new GoodsAdapter(mBact, data, 1);
                goodGridView.setAdapter(goodsAdapter);
//                cataListView.setAdapter(goodsAdapter);

                goodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ProductList.DataBean bean1 = data.get(position);
                        cardList.add(bean1);
                        cardListAdapter = new CardListAdapter(mBact, cardList, 1);
                        cartListView.setAdapter(cardListAdapter);

                    }
                });

            }
        }

    }

    /**
     * 获取商品类目列表
     */
    private void initCataData() {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", "1");
//            showLoading();
            easyGet(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETCATEGORY, Category.class, result -> {
                onCateGoryResult(result);
            });
        } catch (Exception e) {
            XLog.e(e);
        }

    }

    /**
     * 根据店铺ID 获取 商品分类列表 的回调处方法
     * * @param result
     */
    private void onCateGoryResult(Category result) {
//        showLoading(false);
        if (result != null) {
            categoryList = result.getData();
            cataListAdapter = new CategoryListAdapter(mBact, categoryList, 1);
            cataListView.setAdapter(cataListAdapter);
            cataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //获取用户点击item对应的category ID；
                    getProductByShopId(categoryList.get(position).getCategoryId());
                }
            });
        }
    }

    static String getProductByShopIdUrl = "http://192.168.10.193:8088/app/homeData/getProductByShopId?categoryId=106";

    /**
     * 首次进入收银主页，获取推荐商品列表
     */
    private void getProductByShopId(int categoryId) {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", Constant.getShopId());
            json.put("categoryId", categoryId);
            mBact.showLoading();
            easyGet(json, getProductByShopIdUrl, ProductList.class, stringResult -> {
                onProductsResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void confirmOrder() {

        List<ProductList.DataBean> list = cardListAdapter.getList();

        try {

            JSONObject json = new JSONObject();
            json.put("phoneNumber", "18611128418");
            JSONArray jsonArray = new JSONArray();

            if (list != null) {

                for (ProductList.DataBean bean : list) {
//                    ConfirmOrderBean.OrderEntity orderEntity = comfirmOrderList.get(i);
                    XLog.d("getOrderNum = " + bean.getOrderNum());

                    JSONObject orderBean = new JSONObject();
                    orderBean.put("count", bean.getOrderNum());
                    orderBean.put("prodId", bean.getProdId());
                    jsonArray.put(orderBean);

                }

            }
            json.put("productList", jsonArray);
            mBact.showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.CONFIRMORDERURL, OrderSusses.class, orderResult -> {
                onconfirmOrderResult(orderResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }

    }

    /**
     * 订单提交到服务器，生成订单号
     *
     * @param result
     */
    private void onconfirmOrderResult(OrderSusses result) {
        mBact.showLoading(false);
        if (result.getData() != null) {
            String orderNum = result.getData().getOrderNumber();
            orderSussesBean = result;
            double actualTotal = result.getData().getActualTotal();
            XLog.d("orderNum " + orderNum);
            XLog.d("actualTotal " + actualTotal);
            showToast("解析成功，可以支付");
        }
    }

    static String getProductByBarCode = "http://192.168.10.193:8088/app/homeData/getProductByBarCode";
    static String getProductByBarCodeUrl = "http://192.168.10.193:8088/app/homeData/getProductByBarCode?barCode=400";

    private void getProductByBarCode(String barCode) {
        try {
            mBact.showLoading(false);
            XLog.e("barCode =  " + barCode);
            JSONObject json = new JSONObject();
            json.put("barCode", "400");
            mBact.showLoading();
            easyGet(json, getProductByBarCodeUrl, ProductList.class, result -> {
                onProductResultBybarCode(result);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void onProductResultBybarCode(ProductList result) {
        mBact.showLoading(false);
        XLog.d("onProductsResult    = ");
        if (result != null) {
            List<ProductList.DataBean> data = result.getRecords();
            if (data != null) {
                ProductList.DataBean bean = data.get(0);
                String shopName = bean.getShopName();
                XLog.d("shopName   = " + shopName);
            }
        }
    }

    private OrderSusses orderSussesBean;
    static String unionPayUrl = "http://192.168.10.193:8088/app/pay/payment?";

    private void orderUnionPay(String payCode) {
        try {

            StringBuilder builder = new StringBuilder(unionPayUrl);
            builder.append("orderNumber=" + orderSussesBean.getData().getOrderNumber());
            builder.append("&payMoney=" + 1.11);
            builder.append("&payCode=" + payCode);

            String unionPayUrl = builder.toString();
            XLog.d("unionPayUrl = " + unionPayUrl);

            JSONObject json = new JSONObject();
//            json.put("orderNumber", "1354250603961913344");
//            json.put("orderNumber", orderSussesBean.getData().getOrderNumber());
//            json.put("payMoney", 1.11);
//            json.put("payCode", payCode);

            mBact.showLoading();
            easyPost(json, unionPayUrl, String.class, stringResult -> {
                onUnionPayResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void onUnionPayResult(String string) {
        mBact.showLoading(false);
    }

//    private static IntentFilter filter() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_DATA_CODE_RECEIVED);
//        intentFilter.addAction(ACTION_GETUI_PAY_MSG);
//        return intentFilter;
//    }

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getAct(), "接收广播成功", Toast.LENGTH_SHORT).show();
//            String code = intent.getStringExtra(DATA);
//            byte[] arr = intent.getByteArrayExtra(SOURCE);
//            if (code != null && !code.isEmpty()) {
//
//                evTextProdectName.setText(code);
//                //orderUnionPay(code);
//                // showToast(code);
//            }
//
//            if (ACTION_GETUI_PAY_MSG.equals(intent.getAction())) {
//
//                XLog.d("收到广播");
//            }
//        }
//    };

}
