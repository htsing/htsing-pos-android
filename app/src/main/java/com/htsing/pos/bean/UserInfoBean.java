package com.htsing.pos.bean;

import java.io.Serializable;
import java.util.List;

public class UserInfoBean implements Serializable {
    private int id;
    private String username;
    private String phone;
    private String avatar;
    private String password;
    private int shopId;
    private int tenantId;
    private String shopName;
    private String shopIds;
    private String tenantIds;
    private String tenantName;
    private String nickName;

    private List<Authority> authorities;

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getShopIds() {
        return shopIds;
    }

    public void setShopIds(String shopIds) {
        this.shopIds = shopIds;
    }
    public String getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(String tenantIds) {
        this.tenantIds = tenantIds;
    }

    @Override
    public String toString() {
        return "nickName='" + nickName + '\'' +
            ", shopId='" + shopId + '\'' +
            ", tenantId='" + tenantId + '\'' +
            ", shopIds='" + shopIds + '\'' +
            ", tenantIds='" + tenantIds + '\'' +
            ", shopName='" + shopName + '\'' +
            ", tenantName='" + tenantName + '\'' +
            ", phone='" + phone + '\''+
            ", avatar='" + avatar + '\''+
            ", username='" + username + '\'';
    }
}
