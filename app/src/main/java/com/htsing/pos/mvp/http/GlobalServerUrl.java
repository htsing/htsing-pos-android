package com.htsing.pos.mvp.http;


import com.htsing.pos.App;

/**
     * Created by caorongguan on 2019/5/5.
 */

public class GlobalServerUrl {
    public static final String DEBUG_URL = "http://192.168.31.89:9999";  //开发
    public static final String UPDATE_VERSION_URL = "https://wise.hbyjtech.com/";  //自动更新
    private static final String RELEASE_URL = "http://gateway.huapustore.com";  //正式
    public static final String BASE_URL = App.debug ? DEBUG_URL : RELEASE_URL;

    public static final String GET_OPENED_CITY_LIST = "/area/getOpenedCityList";
    public static final String APP_VERSION_UPDATE = "/system_service/appVersion/checkAndroidNeedUpdate";//APP版本更新

    public static String CHECK_VERSION = "/app/homeData/getAppVersion";

    //e 上传类型, resident:居民, black: 黑名单, search: 搜索
    public static final String UPLOAD_FACE1 = "/api/business/upload/face/resident";        //上传头像 4.1.1
    public static final String UPLOAD_FACE2 = "/api/business/upload/face/black";        //上传头像 4.1.1
    public static final String UPLOAD_FACE3 = "/api/business/upload/face/search";        //上传头像4.1.1

    //登录
    public static final String LOGIN = "/auth/oauth2/token";
    //获取产品信息
    public static final String GETPRODUCTS = "/mall-admin/goodsspu/page";
    public static final String GETPRODUCTBYBARCODE = "/app/homeData/getProductByBarCode";
    //根据条形码获取某一个商品
    public static final String GETPRODUCTBYSHOPID = "/app/homeData/getProductByShopId";
    //获取产品分类
    public static final String GETCATEGORY = "/mall-admin/goodscategory/getAllList";

    public static final String CONFIRMORDERURL = "/app/order/confirmOrder";
    //提交订单
    public static final String UNIONPAYURL = "/app/order/confirmOrder";

    //添加会员信息
    public static final String ADDNEWMEMBER = "/app/homeData/addNewMember";
    //获取会员信息
    public static final String GETMEMBER = "/app/homeData/getMember";

    public static final String GETALLORDERBYSHOPID = "/app/order/getAllOrderByshopId";
    public static final String CASH_PAYMENT = "/app/pay/payment";
    public static final String DEPOSITMONEY = "/app/depositMoney/insertDepositMoneyRecord";
    public static final String DEPOSITMONEYBYCASH = "/app/depositMoney/depositMoneyByCash";
    public static final String DEPOSITMONEYBYUMS = "/app/depositMoney/depositMoneyByUms";
    //退款
    public static final String REFUND = "/app/payOrder/refund";
    //撤销
    public static final String BACKOUT = "/app/payOrder/backOut";
    //发送验证码
    public static final String LOGINBYSENDSMS = "/p/sms/loginbySendSms";
    //获取店铺信息
    public static final String APPSHOPDETAIL = "/upms/organ";
    public static final String USERSHIFTSTATISTICS = "/app/appCashRegisterSystemUser/userShiftStatistics";
    public static final String ADDPRODUCT = "/app/appProduct/addProduct";
    public static final String GETUSERBYUSERID = "/upms/user";

    public static final String ORDERBUCHA = "/app/pay/bucha";
    public static final String SELECTDEPOSITMONEYRECORD = "/app/depositMoney/selectDepositMoneyRecord";

    public static final String PRODUCEQRCODE = "/app/appProduct/produceQRCode";
    public static final String GETTOPAIDORDER = "/app/order/getToPaidOrder";
}