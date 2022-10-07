package com.htsing.pos.bean;

import com.google.gson.annotations.SerializedName;
import com.htsing.pos.easyhttp.CommonResult;

import java.io.Serializable;

public class ShopInfobean  {


    /**
     * data : {"shopId":1,"shopName":"小二啷当","userId":null,"shopType":null,"intro":"致力为社会提供健康、美味、营养的精品美食！","shopNotice":null,"shopIndustry":null,"shopOwner":"周月","mobile":"18721961968","tel":"","shopLat":"32.126229","shopLng":"112.241647","shopAddress":"佳海工业园金煌食品A43-1","province":"湖北省","city":"襄阳市","area":"高新区","pcaCode":"44/4401/440113","shopLogo":"2018/08/78a6a63cf02d4965912bc5047f49afa0.jpg","shopPhotos":"2018/08/c7a50f443a85462d8129d83cf0f7eb91.jpg,2018/08/79791fc749444ef1ab4d2ca56fe9363f.jpg","openTime":"07:00:00-23:59:00","shopStatus":1,"transportType":null,"fixedFreight":null,"fullFreeShipping":null,"createTime":"2018-08-30 11:10:05","updateTime":null,"isDistribution":1,"state":0,"account":15171.3,"distance":null,"agentId":null,"bussinessLicense":"2020/09/26af071c5ddf4f1d97cf1c18d3fffc2e.jpg","idCard":"2020/08/58095366e20542b88590910176ff19a7.png,2020/08/cd5e5a2f182c4424910100d106185c39.png","rebateMoney":2140.5,"hygienicLicense":"2020/09/2bbdccfd651a472292a4996d8fb9bf60.jpg","foodBusinessLicense":null,"cardOwner":"周月","cardNumber":"6217001180001604985","openBank":"中国建设银行","startPrice":0,"deliveryPrice":0,"meituanDistributionFlag":0,"mainStore":0,"loginName":null,"shopTypeId":null,"cashMoney":11849.18}
     * resultMsg : null
     */
    private int id;
    private String name;
    private int sort;
    private int code;
    private int type;
    private int tenantId;
    private String phone;
    private String fax;
    private String email;
    private String address;
    private String remarks;
    private String openHours;
    private String latitude;
    private String longitude;
    private String logo;
    private String homeUrl;
    private String wxMchId;
    private String qywxIdentifier;
    private String pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


}
