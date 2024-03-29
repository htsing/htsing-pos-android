package com.htsing.pos.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.adapter.CardListAdapter;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.base.fragment.HomeBaseFragment;
import com.htsing.pos.bean.HomeMemberInfo;
import com.htsing.pos.bean.MenusBean;
import com.htsing.pos.bean.ProductList;
import com.htsing.pos.bean.ShopInfobean;
import com.htsing.pos.bean.ShopOrderDetail;
import com.htsing.pos.bean.msg.PayEventBean;
import com.htsing.pos.callback.ModifyCountInterface;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.ui.login.PosActivity;
import com.htsing.pos.utils.BigDecimalUtils;
import com.htsing.pos.utils.CommonViewUtils;
import com.htsing.pos.utils.JsonParseUtils;
import com.htsing.pos.utils.PreferencesUtil;
import com.htsing.pos.utils.ResourcesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class HomeCartFragment extends HomeBaseFragment implements ModifyCountInterface {

    //购物车列表
    @BindView(R.id.lv_cart_list)
    ListView cartListView;
    //订单提交的布局
    @BindView(R.id.layout_order_confrim)
    LinearLayout layoutConfirm;
    //购物车列表里面的listView适配器
    private CardListAdapter cardListAdapter;
    //首次加载获取的商品列表
    private List<ProductList.DataBean> cardList;
    private BaseAct mBact;
    private PosActivity posActivity;
    //会员登录以后，显示会员自己余额
    @BindView(R.id.layout_home_member_info)
    LinearLayout layoutMemberInfo;
    //购物车列表的提示信息
    @BindView(R.id.layout_cart_tips_info)
    LinearLayout layoutCartTipsInfo;
    //会员姓名
    @BindView(R.id.tv_member_name)
    TextView tv_member_name;
    //会员手机号码
    @BindView(R.id.tv_member_phone)
    TextView tv_member_phone;
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

    //购物车计算商品的价格
    @BindView(R.id.tv_home_cart_money)
    TextView tv_home_cart_money;
    //购物车 显示商品的件数
    @BindView(R.id.tv_home_cart_num)
    TextView tv_home_cart_num;
    //挂单功能
    @BindView(R.id.tv_home_cart_temp_order)
    TextView tv_home_cart_temp_order;
    //核销功能
    @BindView(R.id.tv_home_cart_temp_special)
    TextView tv_home_cart_temp_special;

    //购物车title上面的 商品数量显示
    @BindView(R.id.tv_cart_title_shop_num)
    TextView tv_cart_title_shop_num;
    //挂单功能 核销
    @BindView(R.id.tv_cart_temp_get)
    TextView tv_cart_temp_get;

    private DecimalFormat decimalFormat = new DecimalFormat("#.00");

    //存储会员信息的对象
    private HomeMemberInfo memberInfoBean;
    //购物车商品商品数量
    int productNum = 0;
    //购物车商品总价
    double totalPrice = 0.00;
    //挂单功能，临时存储的购物车列表数据
    private Map<String, List<ProductList.DataBean>> limitCartDataMap;

    @Override
    public int getLayout() {
        return R.layout.activity_home_cart_layout;
    }

    @Override
    public void initView() {
        mBact = getAct();
        posActivity = (PosActivity) getAct();

        //结算按钮 订单提交
        CommonViewUtils.setOnClick(layoutConfirm, view -> {
            confirmOrder();
        });
        //用户登录 切换充值界面
        CommonViewUtils.setOnClick(tv_member_recharge, view -> {
            posActivity.showRechargeFragment();
            BaseEventBean eventBean = new BaseEventBean(BaseEventBean.RECHARGE_MEMBER_FRAGMENT);
            if (memberInfoBean != null) {
                eventBean.setValue(memberInfoBean);
            }
            EventBus.getDefault().post(eventBean);
        });
        //用户登录 退出
        CommonViewUtils.setOnClick(tv_member_exit, view -> {
            layoutCartTipsInfo.setVisibility(View.VISIBLE);
            layoutMemberInfo.setVisibility(View.GONE);
            XLog.d("send msg");
            memberInfoBean = null;
            cardListAdapter.setMember(null);
        });
        cardList = new ArrayList<>();
        cardListAdapter = new CardListAdapter(mBact, cardList, 1);
        cartListView.setAdapter(cardListAdapter);
        cardListAdapter.setModifyCountInterface(this);

        limitCartDataMap = new HashMap<String, List<ProductList.DataBean>>();
        //挂单按钮
        CommonViewUtils.setOnClick(tv_home_cart_temp_order, view -> {

            confirmTempOrder();

        });

        //取单功能
        CommonViewUtils.setOnClick(tv_cart_temp_get, view -> {

            posActivity.showTempFragment();
        });

        //清空购物车
        CommonViewUtils.setOnClick(tv_home_cart_temp_special, view -> {

            cardListAdapter.getList().removeAll(cardListAdapter.getList());
            cardListAdapter.notifyDataSetChanged();

            clearViewOldValue();

        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(BaseEventBean eventBean) {
        XLog.d("onHandleEventbusMsg  =  " + eventBean.getType());
        if (eventBean.getType() == BaseEventBean.TYPE_GOTO_MEMBER_FRAGMENT ||
                eventBean.getType() == BaseEventBean.TYPE_GOTO_ADD_MEMBER_FRAGMENT) {

            memberInfoBean = (HomeMemberInfo) eventBean.getValue();

            if (memberInfoBean != null) {
                //先隐藏其他的View
                layoutCartTipsInfo.setVisibility(View.GONE);
                layoutMemberInfo.setVisibility(View.VISIBLE);
                tv_member_name.setText(memberInfoBean.getNickName());
                tv_member_phone.setText(memberInfoBean.getUserMobile());
                tv_member_integral.setText(memberInfoBean.getPointAccount() + "");
            } else {
                //非会员
                layoutCartTipsInfo.setVisibility(View.VISIBLE);
                layoutMemberInfo.setVisibility(View.GONE);
            }

//            if (memberInfoBean.getVipLevel() == 1) {
//                cardListAdapter.setMember(memberInfoBean);
//            }

            //如果老账户的余额 等于0  才享有8.8折的优惠
            if (memberInfoBean.getOldAccount() > 0) {
                cardListAdapter.setMember(memberInfoBean);
            }

            if (memberInfoBean.getOldAccount() > 0) {
                if (memberInfoBean.getUserAccount() > 0) {
                    tv_member_balance.setText(memberInfoBean.getUserAccount() + "|| " + memberInfoBean.getOldAccount());
                } else {
                    tv_member_balance.setText("0" + "," + memberInfoBean.getOldAccount());
                }

            } else {

                if (memberInfoBean.getUserAccount() > 0) {


                    if (memberInfoBean.getOldAccount() > 0) {
                        tv_member_balance.setText(memberInfoBean.getUserAccount() + "|| " + memberInfoBean.getOldAccount());
                    } else {
                        tv_member_balance.setText(memberInfoBean.getUserAccount() + "");
                    }

                } else {
                    tv_member_balance.setText("0");
                }
            }


        } else if (eventBean.getType() == BaseEventBean.TYPE_GRIDVIEW_ITEM_CLICK) {

            ProductList.DataBean dataBean = (ProductList.DataBean) eventBean.getValue();
            if (dataBean == null) {
                return;
            }


            if (cardListAdapter.getList().size() == 0) {
                cardListAdapter.setDataBean(getNewDataBean(dataBean));
            } else {

                //先遍历一遍 如果有相同的 就修改商量
                for (int i = 0; i < cardListAdapter.getList().size(); i++) {

                    XLog.e(dataBean.getSpuCode() + "   " + cardListAdapter.getList().get(i).getSpuCode());
                    if (dataBean.getSpuCode() == cardListAdapter.getList().get(i).getSpuCode()) {
                        int num = cardListAdapter.getList().get(i).getOrderNum();
                        num++;
                        cardListAdapter.getList().get(i).setOrderNum(num);
                        //如果购物车 已经有了之前的产品，直接在数量上 加 1 并更新UI
                        cardListAdapter.notifyDataSetChanged();

                        return;
                    }
                }

                cardListAdapter.setDataBean(getNewDataBean(dataBean));

            }

        } else if (eventBean.getType() == BaseEventBean.PAY_SUSSESS_CLEAR_CRAT) {
            cardListAdapter.getList().removeAll(cardListAdapter.getList());
            cardListAdapter.notifyDataSetChanged();
            clearViewOldValue();

            if (memberInfoBean != null) {
                layoutCartTipsInfo.setVisibility(View.VISIBLE);
                layoutMemberInfo.setVisibility(View.GONE);
                memberInfoBean = null;
                cardListAdapter.setMember(null);
            }

        } else if (eventBean.getType() == BaseEventBean.UPDATE_MEMBER_FRAGMENT) {
            //会员扫码充值成功以后，收到成功消息后，更新会员信息
            getMemberData();
        } else if (eventBean.getType() == BaseEventBean.TEMP_ORDER_DATA) {
            //添加挂单中的产品到购物车
            ShopOrderDetail.RecordsBean item = (ShopOrderDetail.RecordsBean) eventBean.getValue();
            getTempOrderListData(item);

            //从EventBus消息中取出 订单和会员对象
            PayEventBean payEventBean = (PayEventBean) eventBean;
            payEventBean.getMobile();
            //查找用户信息
            String mobile = payEventBean.getMobile();
            getMemberData(mobile);

            XLog.d("mobile = " + mobile);


        } else if (eventBean.getType() == BaseEventBean.AGAIN_ORDER) {
            //扫码支付失败，重新支付请求
            confirmOrder();
        }

    }

    /**
     * 结算完成一单后，重置View的值
     * 当购物车 没有商品的时候
     */
    private void clearViewOldValue() {

        StringBuffer numBuffer = new StringBuffer();
        numBuffer.append("共：0 件");
        tv_home_cart_num.setText(numBuffer.toString());

        tv_home_cart_money.setText("0.0");

        tv_cart_title_shop_num.setText("购物车列表(0)");

    }

    /**
     * 统计操作
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作
     * 3.给底部的textView进行数据填充
     */
    private void getProductPrice() {
        productNum = 0;
        totalPrice = 0.0;
        double itemTotalPrice = 0.0;

        List<ProductList.DataBean> list = cardListAdapter.getList();

        for (ProductList.DataBean bean : list) {
//            totalPrice += BigDecimalUtils.mul(bean.getPrice(), bean.getOrderNum(), 1);
            productNum += bean.getOrderNum();

            if (memberInfoBean != null) {
//                itemTotalPrice = BigDecimalUtils.mul(bean.getVipPrice(), bean.getOrderNum(), 1);

                if (memberInfoBean.getOldAccount() > 0) {
                    //老账户有钱 就享受8.8折扣
                    totalPrice += BigDecimalUtils.mul(bean.getPriceVip(), bean.getOrderNum(), 2);
                } else {
                    totalPrice += BigDecimalUtils.mul(bean.getPriceRetail(), bean.getOrderNum(), 2);
                }

            } else {
//                itemTotalPrice = BigDecimalUtils.mul(bean.getPrice(), bean.getOrderNum(), 1);

                totalPrice += BigDecimalUtils.mul(bean.getPriceRetail(), bean.getOrderNum(), 2);
            }
        }

        StringBuffer numBuffer = new StringBuffer();
        numBuffer.append("共：");
        numBuffer.append(productNum);
        numBuffer.append("件");
        XLog.d("productNum = " + productNum);

        StringBuffer numBuf = new StringBuffer();
        numBuf.append("购物车列表 （ ");
        numBuf.append(productNum);
        numBuf.append("）");


        tv_home_cart_num.setText(numBuffer);
        tv_home_cart_money.setText(decimalFormat.format(totalPrice) + "");//格式化 金额
//        tv_home_cart_money.setText(totalPrice + "");//格式化 金额

        tv_cart_title_shop_num.setText(numBuf + "");
    }

    /**
     * 提交订单，在服务端生成订单号
     */
    private void confirmOrder() {
        List<ProductList.DataBean> list = cardListAdapter.getList();
        if (list.size() == 0) {
            showToast("购物车为空");
            return;
        }

        try {
            JSONObject json = new JSONObject();

            if (memberInfoBean == null) {
                //游客模式
                json.put("phoneNumber", 0);
            } else {
                json.put("phoneNumber", memberInfoBean.getUserMobile());
            }
            json.put("cid", PreferencesUtil.getString(mBact, Constant.SP_CID));//个推的cid
            XLog.d("cid = " + PreferencesUtil.getString(mBact, Constant.SP_CID));
            JSONArray jsonArray = new JSONArray();

            if (list != null) {

                for (ProductList.DataBean bean : list) {
                    XLog.d("getOrderNum = " + bean.getOrderNum());

                    JSONObject orderBean = new JSONObject();
                    orderBean.put("count", bean.getOrderNum());//产品数量
                    orderBean.put("spuCode", bean.getSpuCode());//产品ID
                    jsonArray.put(orderBean);
                }
            }
            json.put("productList", jsonArray);
            mBact.showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.CONFIRMORDERURL, CommonResult.class, orderResult -> {
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
    private void onconfirmOrderResult(CommonResult result) {
        mBact.showLoading(false);

        if (result.getResult() == null) {
            showToast("订单生成失败");
            return;
        }

        if (result.getCode() == 200) {
//            double actualTotal = result.getData().getRecords().get(0).getActualTotal();
//            String orderNum = result.getData().getRecords().get(0).getOrderNumber();
            ShopOrderDetail shopOrderDetail = new Gson().fromJson(new Gson().toJson(result.getResult()), ShopOrderDetail.class);
            posActivity.showPayFragment();
            PayEventBean payEventBean = new PayEventBean(BaseEventBean.TYPE_GOTO_PAY_FRAGMENT);
            payEventBean.setValue(result);
            if (memberInfoBean != null) {
                payEventBean.setMemberInfo(memberInfoBean);
            }
            EventBus.getDefault().post(payEventBean);
        }
    }

    /**
     * 提交订单，在服务端生成订单号
     */
    private void confirmTempOrder() {
        List<ProductList.DataBean> list = cardListAdapter.getList();
        if (list.size() == 0) {
            showToast("购物车为空");
            return;
        }

        try {
            JSONObject json = new JSONObject();

            if (memberInfoBean == null) {
                //游客模式
                json.put("phoneNumber", -1);
            } else {
                json.put("phoneNumber", memberInfoBean.getUserMobile());
            }
            json.put("cid", PreferencesUtil.getString(mBact, Constant.SP_CID));//个推的cid
            json.put("keepOrder", 1);

            JSONArray jsonArray = new JSONArray();

            if (list != null) {

                for (ProductList.DataBean bean : list) {
                    XLog.d("getOrderNum = " + bean.getOrderNum());

                    JSONObject orderBean = new JSONObject();
                    orderBean.put("count", bean.getOrderNum());//产品数量
                    orderBean.put("spuCode", bean.getSpuCode());//产品ID
                    jsonArray.put(orderBean);
                }
            }
            json.put("productList", jsonArray);
            mBact.showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.CONFIRMORDERURL, CommonResult.class, orderResult -> {
                onconfirmTempOrderResult(orderResult);
            });
        } catch (Exception e) {
            XLog.e(e);
        }

    }

    /**
     * 订单提交到服务器，生成订单号
     *
     * @param commonResult
     */
    private void onconfirmTempOrderResult(CommonResult commonResult) {
        mBact.showLoading(false);

        if (commonResult.getResult() == null) {
            showToast("订单生成失败");
            return;
        }

        if (commonResult.getCode() == 200) {
            ShopOrderDetail shopOrderDetail = JsonParseUtils.parse(new Gson().toJson(commonResult.getResult(), Map.class), ShopOrderDetail.class);
            //发消息给购物车，清空购物车
            BaseEventBean bean = new BaseEventBean(BaseEventBean.PAY_SUSSESS_CLEAR_CRAT);
            EventBus.getDefault().post(bean);

            showToast("挂单成功");
        }
    }


    /**
     * 会员充值成功以后，更新会员的余额信息
     */
    private void getMemberData() {
        try {
            JSONObject json = new JSONObject();

            String url = GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETMEMBER;
            StringBuffer buffer = new StringBuffer(url);
            buffer.append("?tel=" + memberInfoBean.getUserMobile());

            String stringUrl = buffer.toString();
            mBact.showLoading();
            easyPost(json, stringUrl, CommonResult.class, result -> {
                onMemberResult(result);
            });
        } catch (Exception e) {
            mBact.setLog("easyPostLogin", e.getMessage());
            XLog.e(e);
        }

    }

    /**
     * 会员充值成功以后，更新会员的余额信息
     */
    private void getMemberData(String mobile) {
        try {

            if (TextUtils.isEmpty(mobile) || "".equals(mobile)) {
                return;
            }
            JSONObject json = new JSONObject();

            String url = GlobalServerUrl.DEBUG_URL + GlobalServerUrl.GETMEMBER;
            StringBuffer buffer = new StringBuffer(url);
            buffer.append("?tel=" + mobile);

            String stringUrl = buffer.toString();
            mBact.showLoading();
            easyPost(json, stringUrl, CommonResult.class, result -> {
                onMemberResult(result);
            });
        } catch (Exception e) {
            mBact.setLog("easyPostLogin", e.getMessage());
            XLog.e(e);
        }

    }

    private void onMemberResult(CommonResult commonResult) {
        if (commonResult.getResult() == null) {
            showToast("会员信息为空");
            return;
        } else {
            HomeMemberInfo homeMemberInfo = new Gson().fromJson(new Gson().toJson(commonResult.getResult()),HomeMemberInfo.class);
            BaseEventBean eventBean = new BaseEventBean(BaseEventBean.TYPE_GOTO_MEMBER_FRAGMENT);
            eventBean.setValue(homeMemberInfo);
            EventBus.getDefault().post(eventBean);
        }

    }

    /**
     * GridView  每一次点击，产生一个新的对象，这样不影响 GridView的对象值
     *
     * @param dataBean
     * @return
     */
    private ProductList.DataBean getNewDataBean(ProductList.DataBean dataBean) {
        ProductList.DataBean bean = new ProductList.DataBean();
        bean.setSpuCode(dataBean.getSpuCode());
        bean.setOrderNum(1);
        bean.setPriceRetail(dataBean.getPriceRetail());
        bean.setName(dataBean.getName());
        bean.setPicUrls(dataBean.getPicUrls());
        bean.setPriceVip(dataBean.getPriceVip());

        double vipPrice = 0;

        if (bean.getPriceVip() == 0) {

            //日常价 活动商品 不享受折扣
            vipPrice = bean.getPriceRetail();
        } else {
            //如果折扣 大于0  就享受会员价
            vipPrice = BigDecimalUtils.mul(bean.getPriceRetail(), 0.88, 2);
        }

        XLog.d("vipPrice = " + vipPrice);

//        bean.setVipPrice(dataBean.getVipPrice());

        bean.setPriceVip(vipPrice);

        bean.setScanCode(dataBean.getScanCode());

        return bean;
    }

    private List<MenusBean> menus = new ArrayList<>();

    private void formatGoods(ProductList.DataBean gvBeans) {
        MenusBean bean = new MenusBean();
        bean.setId("" + (menus.size() + 1));
        bean.setMoney(gvBeans.getPriceRetail() + "");
        bean.setName(gvBeans.getName());
        bean.setType(0);
//        bean.setCode(gvBeans.getBarCode());
        menus.add(bean);
        float price = 0.00f;
        for (MenusBean bean1 : menus) {
            price = price + Float.parseFloat(bean1.getMoney().substring(1));
        }
        Log.e("@@@@", "spuCode==" + gvBeans.getSpuCode());
        buildMenuJson(menus, decimalFormat.format(price));
    }

    String goods_data = "";

    private void buildMenuJson(List<MenusBean> menus, String price) {
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Sunmi " + ResourcesUtils.getString(mBact, R.string.menus_title));
            JSONObject head = new JSONObject();
            head.put("param1", ResourcesUtils.getString(mBact, R.string.menus_number));
            head.put("param2", ResourcesUtils.getString(mBact, R.string.menus_goods_name));
            head.put("param3", ResourcesUtils.getString(mBact, R.string.menus_unit_price));
            data.put("head", head);
            data.put("flag", "true");
            JSONArray list = new JSONArray();
            for (int i = 0; i < menus.size(); i++) {
                JSONObject listItem = new JSONObject();
                listItem.put("param1", "" + (i + 1));
                listItem.put("param2", menus.get(i).getName());
                listItem.put("param3", menus.get(i).getMoney());
                listItem.put("type", menus.get(i).getType());
                listItem.put("code", menus.get(i).getCode());
                listItem.put("net", menus.get(i).getNet());
                list.put(listItem);
            }
            data.put("list", list);
            JSONArray KVPList = new JSONArray();
            JSONObject KVPListOne = new JSONObject();
            KVPListOne.put("name", ResourcesUtils.getString(mBact, R.string.shop_car_total) + " ");
            KVPListOne.put("value", price);
            JSONObject KVPListTwo = new JSONObject();
            KVPListTwo.put("name", ResourcesUtils.getString(mBact, R.string.shop_car_offer) + " ");
            KVPListTwo.put("value", "0.00");
            JSONObject KVPListThree = new JSONObject();
            KVPListThree.put("name", ResourcesUtils.getString(mBact, R.string.shop_car_number) + " ");
            KVPListThree.put("value", "" + menus.size());
            JSONObject KVPListFour = new JSONObject();
            KVPListFour.put("name", ResourcesUtils.getString(mBact, R.string.shop_car_receivable) + " ");
            KVPListFour.put("value", price);
            KVPList.put(0, KVPListOne);
            KVPList.put(1, KVPListTwo);
            KVPList.put(2, KVPListThree);
            KVPList.put(3, KVPListFour);
            data.put("KVPList", KVPList);
            Log.d("HHHH", "onClick: ---------->" + data.toString());
            goods_data = data.toString();
//            Log.d(TAG, "buildMenuJson: ------->" + (videoMenuDisplay != null));

            // 购物车有东西

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ListView购物车点击事件的回调函数
    @Override
    public void doIncrease(int position, View showCountView, boolean isChecked) {
        ProductList.DataBean bean = cardListAdapter.getList().get(position);
        int currentCount = bean.getOrderNum();
        currentCount++;
        bean.setOrderNum(currentCount);

        StringBuffer numBuffer = new StringBuffer();
        numBuffer.append("共：");
        numBuffer.append(currentCount);
        numBuffer.append("件");
        XLog.d("currentCount = " + currentCount);

        tv_home_cart_num.setText(numBuffer);
        ((TextView) showCountView).setText(currentCount + "");
        cardListAdapter.notifyDataSetChanged();
    }

    @Override
    public void doDecrease(int position, View showCountView, boolean isChecked) {
        ProductList.DataBean bean = cardListAdapter.getList().get(position);
        int currentCount = bean.getOrderNum();
        if (currentCount == 1) {
            return;
        }
        currentCount--;
        bean.setOrderNum(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        cardListAdapter.notifyDataSetChanged();
    }

    @Override
    public void childDelete(int position) {
        cardListAdapter.getList().remove(position);
        cardListAdapter.notifyDataSetChanged();

        //购物车为空的时候
        if (cardListAdapter.getList().size() == 0) {
            clearViewOldValue();
        }

    }

    @Override
    public void calcProductPrice() {
        getProductPrice();
    }

    /**
     * 把挂单的数据，恢复到购物车中
     * 从订单对象中，转化成商品列表对象
     *
     * @param item
     */
    private void getTempOrderListData(ShopOrderDetail.RecordsBean item) {

        for (ShopOrderDetail.RecordsBean.OrderItemsBean itemsBean : item.getOrderItems()) {
            cardListAdapter.setDataBean(getNewTempDataBean(itemsBean));
        }
    }

    private ProductList.DataBean getNewTempDataBean(ShopOrderDetail.RecordsBean.OrderItemsBean dataBean) {
        ProductList.DataBean bean = new ProductList.DataBean();
        bean.setSpuCode(String.valueOf(dataBean.getProdId()));
        bean.setOrderNum(1);
        bean.setPriceRetail(dataBean.getPrice());
        bean.setName(dataBean.getProdName());
        //要修改！！！
//        bean.setVipDiscount(dataBean.getVipDiscount());

        bean.setOrderNum(dataBean.getProdCount());//设置挂单数量

        double vipPrice = 0;

        if (bean.getPriceVip() == 0) {

            //日常价 活动商品 不享受折扣
            vipPrice = bean.getPriceRetail();
        } else {
            //如果折扣 大于0  就享受会员价
            vipPrice = BigDecimalUtils.mul(bean.getPriceRetail(), 0.88, 2);
        }

        XLog.d("vipPrice = " + vipPrice);

//        bean.setVipPrice(dataBean.getVipPrice());

        bean.setPriceVip(vipPrice);

//        bean.setBarCode(dataBean.getBarCode());

        return bean;
    }


    //hashMap的使用
    private void tempGetOrder() {

        List<ProductList.DataBean> tempList = Arrays.asList(new ProductList.DataBean[(cardListAdapter.getList().size())]);

        Collections.copy(tempList, cardListAdapter.getList());
        //这是由Arrays.asList() 返回的市Arrays的内部类ArrayList， 而不是java.util.ArrayList
        List<ProductList.DataBean> arrtempList = new ArrayList<>(tempList);
        limitCartDataMap.put("temp", arrtempList);

        cardListAdapter.getList().removeAll(cardListAdapter.getList());
        cardListAdapter.notifyDataSetChanged();

        List<ProductList.DataBean> list = limitCartDataMap.get("temp");


        if (list != null & list.size() > 0) {
            cardListAdapter.setNewData(list);
            cardListAdapter.notifyDataSetChanged();
        } else {
            showToast("没有挂单数据");
        }

    }


    private void updateToken() {
        String token = "11111";
        PreferencesUtil.save(mBact, Constant.SP_TOKEN, token);
        showToast("修改token");
    }

}
