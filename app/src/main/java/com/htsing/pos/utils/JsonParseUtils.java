package com.htsing.pos.utils;

import android.content.Context;
import android.text.TextUtils;


import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.htsing.pos.BaseAct;
import com.htsing.pos.constant.Constant;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.bean.msg.TokenEventBean;
import com.htsing.pos.mvp.callback.OnRequestSuccess;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.request.GetRequest;
import com.zhouyou.http.request.PostRequest;
import com.pgyer.pgyersdk.PgyerSDKManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 曹荣冠
 * on 2020/1/10.
 */
public class JsonParseUtils {
    public static Gson gson = new Gson();


    public JsonParseUtils() {

    }

    private static JsonParseUtils singleton;

    public static JsonParseUtils getInstance() {
        if (singleton == null) {
            synchronized (JsonParseUtils.class) {
                if (singleton == null) {
                    singleton = new JsonParseUtils();
                }
            }
        }
        return singleton;
    }


    /**
     * 如果对方还是要求我们分部解析，则使用这个解析工具，data统一改为String,然后在返回值里面这样处理。
     */
    public static <T> T parse(String result, Class<T> cls) {
        try {
            T bean = new Gson().fromJson(result, cls);
            return bean;
        } catch (Exception e) {
            CommonUtils.setLog("JsonParseUtils", e.getMessage());
            return parseFastJson(result, cls);
        }
    }

    public static <T> T parseFastJson(String result, Class<T> cls) {
        try {
            return com.alibaba.fastjson.JSONObject.parseObject(result, cls);
        } catch (Exception e) {
            CommonUtils.setLog("JsonParseFastJson", e.getMessage());
            CommonViewUtils.showToast("解析错误");
            return null;
        }
    }


    /**
     * 如果对方还是要求我们分部解析，则使用这个解析工具，data统一改为String,然后在返回值里面这样处理。
     */
    public static <T> T parse(Context mContext, JsonElement result, Class<T> cls) {
        try {
            T bean = new Gson().fromJson(result, cls);
            return bean;
        } catch (Exception e) {
            CommonUtils.setLog("JsonParseUtils", e.getMessage());
            CommonUtils.showToast(mContext, "解析错误");
            return null;
        }
    }

    public static <T> void easyPost(BaseAct act, JSONObject requestParams, String url, Class<T> cls, boolean needToken, OnRequestSuccess<T> listener) {
        if (act == null) return;
        PostRequest postRequest = EasyHttp.post(url);

        if (needToken) {
            postRequest.headers(Constant.TOKEN_KEY, Constant.getToken());
            postRequest.headers("Content-Type", "application/json;charset=UTF-8");
        }
        else{
            postRequest.headers("isToken", "false");
            postRequest.headers(Constant.TOKEN_KEY, "Basic YWRtaW46YWRtaW4=");
            postRequest.headers("Content-Type", "application/json;charset=UTF-8");
        }
        postRequest.upJson(requestParams.toString())
        .execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                XLog.e("ApiException e   " + e.getDisplayMessage());
                CommonUtils.showToast(act, "网络错误");
                act.showLoading(false);
                PgyerSDKManager.reportException(e);
            }
            @Override
            public void onSuccess(String s) {
                act.showLoading(false);
                XLog.json(s);
                commonResult(s, cls, listener);
            }
        });
    }
    public static <T> void easyPostLogin(BaseAct act, JSONObject requestParams, String url, Class<T> cls, OnRequestSuccess<T> listener) {
        if (act == null) return;
        PostRequest postRequest = EasyHttp.post(url);
        postRequest.headers("isToken", "false");
        postRequest.headers(Constant.TOKEN_KEY, "Basic YWRtaW46YWRtaW4=");
        postRequest.params(getMap(requestParams))
        .execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                XLog.e("ApiException e   " + e.getDisplayMessage());
                CommonUtils.showToast(act, "网络错误");
                act.showLoading(false);
                PgyerSDKManager.reportException(e);
            }
            @Override
            public void onSuccess(String s) {
                act.showLoading(false);
//                XLog.json(s);
                commonResultLogin(s, cls, listener);
            }
        });
    }
    public static Map<String, String> getMap(JSONObject jsonObject)
    {
        try
        {
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            String value;
            Map<String, String> valueMap = new HashMap<String, String>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = (String) jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;

    }
    /**
     * get请求方法
     *
     * @param act
     * @param requestParams
     * @param url
     * @param cls
     * @param needToken
     * @param listener
     * @param <T>
     */
    public static <T> void easyGet(BaseAct act,  JSONObject requestParams, String url, Class<T> cls, boolean needToken, OnRequestSuccess<T> listener) {
        if (act == null) return;
        GetRequest getRequest = EasyHttp.get(url);

        if (needToken) {
            getRequest.headers(Constant.TOKEN_KEY, Constant.getToken());
        }
        else{
            getRequest.headers("isToken", "false");
            getRequest.headers(Constant.TOKEN_KEY, "Basic YWRtaW46YWRtaW4=");
            getRequest.headers("Content-Type", "application/json");
        }
        getRequest.params(getMap(requestParams))
        .execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                XLog.e("ApiException e   " + e.getDisplayMessage());
                CommonUtils.showToast(act, "网络错误");
                act.showLoading(false);
                PgyerSDKManager.reportException(e);
            }
            @Override
            public void onSuccess(String s) {
                act.showLoading(false);
                XLog.json(s);
                commonResult(s, cls, listener);
            }
        });
    }

    public static <T> void commonResult(String s, Class<T> cls, OnRequestSuccess<T> listener) {
        if (TextUtils.isEmpty(s)) listener.onResult(null);  //当s为空，则回调空，因为图片不一定必须加载成功。
        CommonResult baseBean = parse(s, CommonResult.class);
        if (baseBean == null) return;
        if (baseBean.getCode() == 0) {
            T bean = parse(s, cls);
            if (listener != null) listener.onResult(bean);
        } else if (baseBean.getCode() == 200) {
            T bean = parse(s, cls);
            if (listener != null) listener.onResult(bean);
        } else {
            CommonViewUtils.showToast(baseBean.getMessage());
        }
    }

    public static <T> void commonResultLogin(String s, Class<T> cls, OnRequestSuccess<T> listener) {
        if (TextUtils.isEmpty(s)) listener.onResult(null);  //当s为空，则回调空，因为图片不一定必须加载成功。
        T bean = parse(s, cls);
        if (listener != null) listener.onResult(bean);
    }


    public static void parseTokenBean(Context act, String resultBean) {
        TokenEventBean tokenEventBean = parse(resultBean, TokenEventBean.class);
        if (tokenEventBean != null) {
            String accessToken = tokenEventBean.getToken();
            String appToken = "bearer" + accessToken;
            PreferencesUtil.save(act, Constant.SP_TOKEN, appToken);
        }
    }
}
