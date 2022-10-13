package com.htsing.pos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ListView;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.htsing.pos.adapter.CardListAdapter;
import com.htsing.pos.adapter.CategoryListAdapter;
import com.htsing.pos.adapter.GoodsAdapter;
import com.htsing.pos.bean.Category;
import com.htsing.pos.bean.OrderSusses;
import com.htsing.pos.bean.ProductList;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.fragment.OrderListFragment;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.utils.JsonParseUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONUtil;

public class MainActivity extends BaseAct  {
    //监听 商米收银设备 扫码枪的广播
    private static final String ACTION_DATA_CODE_RECEIVED =
            "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
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
    private List<Category> categoryList;


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
    @BindView(R.id.layout_main_member)
    LinearLayout layoutMember;
    @BindView(R.id.layout_main_add_member)
    LinearLayout layoutTitleAddMember;
    @BindView(R.id.ly_main_right_parent)
    LinearLayout layoutMainRightParent;
    @BindView(R.id.ly_main_right_product)
    LinearLayout layoutMainRightProduct;
    LinearLayout layoutMemberSelect;
    LinearLayout layoutParentAddMember;

    @BindView(R.id.layout_title_order)
    LinearLayout layoutTitleOrder;
    @BindView(R.id.layout_main_top_paient)
    RelativeLayout layoutMainTopPaient;

    private OrderListFragment orderListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);//一定要在layout初始化之后再绑定。

        cardList = new ArrayList<>();
        categoryList = new ArrayList<>();
        getProducts();
        initCataData();
//        getProductByBarCode("400");
        layoutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirmOrder();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册
//        registerReceiver(receiver, filter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消
//        unregisterReceiver(receiver);

    }

    /**
     * 首次进入收银主页，获取推荐商品列表
     */
    private void getProducts() {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", Constant.getShopId());
            showLoading();
            easyGet(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETPRODUCTS, CommonResult.class, stringResult -> {
                onProductsResult(stringResult);
            });
        } catch (Exception e) {
            setLog("getProducts", e.getMessage());
            XLog.e(e);
        }
    }

    /**
     * 获取商品列表的接口回调处理方法
     *
     * @param commonResult
     */
    private void onProductsResult(CommonResult commonResult) {
        XLog.d("onProductsResult    = ");
        showLoading(false);
        if (commonResult.getResult() != null) {
            ProductList productList = JsonParseUtils.parse(new Gson().toJson(commonResult.getResult(), Map.class), ProductList.class);
            if (productList != null && productList.getRecords().size() > 0) {
                ProductList.DataBean bean = productList.getRecords().get(0);

                goodsAdapter = new GoodsAdapter(getApplicationContext(), productList.getRecords(), 1);
                goodGridView.setAdapter(goodsAdapter);
//                cataListView.setAdapter(goodsAdapter);

                goodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ProductList.DataBean bean1 = productList.getRecords().get(position);
                        cardList.add(bean1);
                        cardListAdapter = new CardListAdapter(getApplicationContext(), cardList, 1);
                        cartListView.setAdapter(cardListAdapter);

                    }
                });

            }
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
            showLoading();
            easyGet(json, getProductByShopIdUrl, CommonResult.class, stringResult -> {
                onProductsResult(stringResult);
            });
        } catch (Exception e) {
            setLog("getProductByShopId", e.getMessage());
            XLog.e(e);
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
            easyGet(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETCATEGORY, CommonResult.class, result -> {
                onCateGoryResult(result);
            });
        } catch (Exception e) {
            setLog("initCataData", e.getMessage());
            XLog.e(e);
        }

    }

    static String getProductByBarCode = "http://192.168.10.193:8088/app/homeData/getProductByBarCode";
    static String getProductByBarCodeUrl = "http://192.168.10.193:8088/app/homeData/getProductByBarCode?barCode=400";

    private void getProductByBarCode(String barCode) {
        try {
            showLoading(false);
            XLog.e("barCode =  " + barCode);
            JSONObject json = new JSONObject();
            json.put("barCode", "400");
            showLoading();
            easyGet(json, getProductByBarCodeUrl, CommonResult.class, result -> {
                onProductResultBybarCode(result);
            });
        } catch (Exception e) {
            setLog("getProductByBarCode", e.getMessage());
            XLog.e(e);
        }
    }

    private void onProductResultBybarCode(CommonResult commonResult) {
        showLoading(false);
        XLog.d("onProductsResult    = ");
        if (commonResult.getResult() != null) {
            ProductList productList = JsonParseUtils.parse(new Gson().toJson(commonResult.getResult(), Map.class), ProductList.class);
            if (productList.getRecords() != null && productList.getRecords().size() > 0) {
                ProductList.DataBean bean = productList.getRecords().get(0);
            }
        }
    }

    /**
     * 根据店铺ID 获取 商品分类列表 的回调处方法
     * * @param result
     */
    private void onCateGoryResult(CommonResult result) {
//        showLoading(false);
        if (result != null) {
//            List<Category> categoryList = JsonParseUtils.parse(new Gson().toJson(result.getResult(), Map.class), Category.class);
            Category[] array = new Gson().fromJson(new Gson().toJson(result.getResult()),Category[].class);
            List<Category> categoryList = Arrays.asList(array);
            cataListAdapter = new CategoryListAdapter(getApplicationContext(), categoryList, 1);
            cataListView.setAdapter(cataListAdapter);
            cataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //获取用户点击item对应的category ID；
                    getProductByShopId(categoryList.get(position).getId());
                }
            });
        }
    }

    private void confirmOrder() {
        try {

            JSONObject json = new JSONObject();
            json.put("phoneNumber", "18611128418");
            JSONArray jsonArray = new JSONArray();
            JSONObject orderBean = new JSONObject();
            orderBean.put("count", "2");
            orderBean.put("prodId", "1456");
            jsonArray.put(orderBean);
            json.put("productList", jsonArray);
            showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.CONFIRMORDERURL, CommonResult.class, orderResult -> {
                onconfirmOrderResult(orderResult);
            });
        } catch (Exception e) {
            setLog("confirmOrder", e.getMessage());
            XLog.e(e);
        }

    }

    /**
     * 订单提交到服务器，生成订单号
     *
     * @param result
     */
    private void onconfirmOrderResult(CommonResult result) {
        showLoading(false);
        if (result.getResult() != null) {
            OrderSusses orderSusses = new Gson().fromJson(new Gson().toJson(result.getResult()),OrderSusses.class);
            String orderNum = orderSusses.getOrderNumber();
            orderSussesBean = orderSusses;
            double actualTotal = orderSusses.getActualTotal();
            XLog.d("orderNum " + orderNum);
            XLog.d("actualTotal " + actualTotal);
            showToast("解析成功，可以支付");
        }
    }

    private OrderSusses orderSussesBean;
    static String unionPayUrl = "http://192.168.10.193:8088/app/pay/payment?";

    private void orderUnionPay(String payCode) {
        try {

            StringBuilder builder = new StringBuilder(unionPayUrl);
            builder.append("orderNumber=" + orderSussesBean.getOrderNumber());
            builder.append("&payMoney=" + 1.11);
            builder.append("&payCode=" + payCode);

            String unionPayUrl = builder.toString();
            XLog.d("unionPayUrl = " + unionPayUrl);

            JSONObject json = new JSONObject();
//            json.put("orderNumber", "1354250603961913344");
//            json.put("orderNumber", orderSussesBean.getData().getOrderNumber());
//            json.put("payMoney", 1.11);
//            json.put("payCode", payCode);

            showLoading();
            easyPost(json, unionPayUrl, String.class, stringResult -> {
                onUnionPayResult(stringResult);
            });
        } catch (Exception e) {
            setLog("orderUnionPay", e.getMessage());
            XLog.e(e);
        }
    }

    private void onUnionPayResult(String string) {
        showLoading(false);
    }


//    private static IntentFilter filter() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_DATA_CODE_RECEIVED);
//        return intentFilter;
//    }

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "接收广播成功", Toast.LENGTH_SHORT).show();
//            String code = intent.getStringExtra(DATA);
//            byte[] arr = intent.getByteArrayExtra(SOURCE);
//            if (code != null && !code.isEmpty()) {
//
//                evTextProdectName.setText(code);
////                orderUnionPay(code);
//                showToast(code);
//            }
//        }
//    };
}