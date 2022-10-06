package com.htsing.pos.bean;

import java.util.List;

public class LoginInfoBean {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private UserInfoBean user_info;
    private List<Authority> authorities;

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public UserInfoBean getUserInfo() {
        return user_info;
    }

    public void setUserInfo(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        return user_info.toString();
    }

}
