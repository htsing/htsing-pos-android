package com.htsing.pos.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.text.Editable;

import com.elvishew.xlog.XLog;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.adapter.CategoryListAdapter;
import com.htsing.pos.adapter.GoodsAdapter;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.base.fragment.HomeBaseFragment;
import com.htsing.pos.bean.Category;
import com.htsing.pos.bean.ProductList;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.utils.CommonViewUtils;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.xw.repo.XEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeProductFragment extends HomeBaseFragment {

    //首次获取商品列表的GridView
    @BindView(R.id.gv_shop_list)
    GridView goodGridView;
    //商品类目列表
    @BindView(R.id.lv_product_cata_title)
    ListView cataListView;
    //商品搜索的输入框
    @BindView(R.id.input_shop_name)
    XEditText productSearchView;

    //首次获取商品列表的GridView的适配器
    private GoodsAdapter goodsAdapter;
    //商品分类的listView适配器
    private CategoryListAdapter cataListAdapter;
    //首次加载获取的 商品列表
    private List<ProductList.DataBean> cardList;
    //首次加载获取的 商品分类列表
    private List<Category.DataBean> categoryList;

    private BaseAct mBact;

    @Override
    public int getLayout() {
        return R.layout.activity_home_product_layout;
    }

    @Override
    public void initView() {
        mBact = getAct();
        //初始化 集合对象
        cardList = new ArrayList<>();
        categoryList = new ArrayList<>();

        goodsAdapter = new GoodsAdapter(mBact, cardList, 1);
        goodGridView.setAdapter(goodsAdapter);

        CommonViewUtils.getInputEnterResult(mBact, productSearchView);

        productSearchView.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = s.toString();

                Thread loginThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(100);//使程序休眠五秒
                            getProducts(code);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                loginThread.start();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        initData();
    }

    private void initData() {
        getProducts();
        initCataData();
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
     * 通过输入商品拼音缩写，检索对应的产品
     */
    private void getProducts(String prodName) {
        try {
            JSONObject json = new JSONObject();
            json.put("shopId", Constant.getShopId());
            json.put("prodName", prodName);
//            mBact.showLoading();
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
     * @param commonResult
     */
    private void onProductsResult(CommonResult commonResult) {
//        mBact.showLoading(false);
        if (commonResult != null) {
//            List<ProductList.DataBean> data = result.getResult();
            ProductList productList = (ProductList) commonResult.getResult();
            if (productList != null && productList.getRecords().size() > 0) {
                ProductList.DataBean bean = productList.getRecords().get(0);
                String shopName = bean.getShopName();

//                goodsAdapter = new GoodsAdapter(mBact, data, 1);
//                goodGridView.setAdapter(goodsAdapter);

//                cardList.addAll(data);
                goodsAdapter.setNewData(productList.getRecords());

                goodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //加入购物车，由 homeCartFragment 处理
                        ProductList.DataBean bean1 = productList.getRecords().get(position);

                        BaseEventBean eventBean = new BaseEventBean(BaseEventBean.TYPE_GRIDVIEW_ITEM_CLICK);
                        eventBean.setValue(bean1);
                        EventBus.getDefault().post(eventBean);

                        productSearchView.setText("");
                    }
                });

            } else {
                showToast("无更新数据");
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

//    static String getProductByShopIdUrl = "http://192.168.10.193:8088/app/homeData/getProductByShopId?categoryId=106";

    /**
     * 首次进入收银主页，获取推荐商品列表
     */
    private void getProductByShopId(int categoryId) {
        try {
            StringBuffer urlBuffer = new StringBuffer(GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETPRODUCTBYSHOPID);
            urlBuffer.append("?categoryId=" + categoryId);
            String url = urlBuffer.toString();

            JSONObject json = new JSONObject();
//            json.put("shopId", Constant.getShopId());
//            json.put("categoryId", categoryId);
//            mBact.showLoading();
            easyGet(json, url, ProductList.class, stringResult -> {
                onProductsResult(stringResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public void getProductByBarCode(String barCode) {
        try {

            StringBuffer bufUrl = new StringBuffer(GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETPRODUCTBYBARCODE);
            bufUrl.append("?barCode=" + barCode);
            String url = bufUrl.toString();

            mBact.showLoading(false);
            XLog.e("barCode =  " + barCode);

            JSONObject json = new JSONObject();
            mBact.showLoading();
            easyGet(json, url, ProductList.class, result -> {
                onProductResultBybarCode(result);
//                onProductsResult(result);
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    private void onProductResultBybarCode(ProductList result) {
        mBact.showLoading(false);
        XLog.d("onProductsResult    = ");
        if (result != null) {

            if (result.getRecords() == null) {
                showToast("没有查询该产品");
                return;
            }

            List<ProductList.DataBean> data = result.getRecords();
            ProductList.DataBean bean = data.get(0);

            if (bean != null) {
                String shopName = bean.getShopName();
                XLog.d("shopName   = " + shopName);
                //扫码枪识别商品 直接加入购物车，由 homeCartFragment 处理
                BaseEventBean eventBean = new BaseEventBean(BaseEventBean.TYPE_GRIDVIEW_ITEM_CLICK);
                eventBean.setValue(bean);
                EventBus.getDefault().post(eventBean);

            }
        }
    }

    /**
     * 扫码识别的处理方法
     *
     * @param code
     */
    public void setProductData(String code) {
        productSearchView.setText(code);
        getProductByBarCode(code);
    }
}
