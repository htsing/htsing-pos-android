package com.htsing.pos.fragment;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.htsing.pos.BaseAct;
import com.htsing.pos.R;
import com.htsing.pos.base.fragment.BaseEventBean;
import com.htsing.pos.base.fragment.HomeBaseFragment;
import com.htsing.pos.bean.HomeMemberInfo;
import com.htsing.pos.easyhttp.CommonResult;
import com.htsing.pos.mvp.http.GlobalServerUrl;
import com.htsing.pos.ui.login.PosActivity;
import com.htsing.pos.utils.CommonViewUtils;
import com.xw.repo.XEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class HomePayFragment extends HomeBaseFragment {
    //输入 会员手机号码
    @BindView(R.id.phone_et_main_add_member)
    XEditText etViewMemberPhone;
    //输入会员昵称
    @BindView(R.id.nickname_et_main_add_member)
    XEditText etViewMemberNickName;
    //确定
    @BindView(R.id.tx_member_add)
    TextView tvViewMemberAdd;
    //取消
    @BindView(R.id.tx_member_cancel)
    TextView tvViewMemberCancel;
    //会员角色
    @BindView(R.id.tv_menber_role)
    TextView tvViewMemberRole;
    //会员的性别 男
    @BindView(R.id.ck_menber_man)
    CheckBox tvViewMemberManSex;
    //会员的性别 女
    @BindView(R.id.ck_menber_woman)
    CheckBox tvViewMemberWoManSex;
    //会员生日
    @BindView(R.id.ev_selete_member_birthday)
    TextView ev_selete_member_birthday;
    //关闭新增会员fragment
    @BindView(R.id.tv_close_add_member_fragment)
    TextView tv_close_add_member_fragment;

    private BaseAct mBact;
    private PosActivity posActivity;

    @Override
    public int getLayout() {
        return R.layout.activity_main_add_member_layout;
    }

    @Override
    public void initView() {
        mBact = getAct();
        posActivity = (PosActivity) getAct();

        CommonViewUtils.getInputEnterResult(mBact, etViewMemberPhone);
        CommonViewUtils.getInputEnterResult(mBact, etViewMemberNickName);


        CommonViewUtils.setOnClick(tvViewMemberAdd, view -> {
            addNewMember();
        });

        //取消   新增会员
        CommonViewUtils.setOnClick(tvViewMemberCancel, view -> {
            posActivity.showProductFragment();
        });
        //关闭 新增会员
        CommonViewUtils.setOnClick(tv_close_add_member_fragment, view -> {
            posActivity.showProductFragment();
        });

        CommonViewUtils.setOnClick(ev_selete_member_birthday, view -> {
            seleteMemberDate();
        });

        tvViewMemberManSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvViewMemberWoManSex.isChecked()) {
                    tvViewMemberWoManSex.setChecked(false);
                }
            }
        });

        tvViewMemberWoManSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvViewMemberManSex.isChecked()) {
                    tvViewMemberManSex.setChecked(false);
                }
            }
        });


    }

    private void addNewMember() {
        String userMobile = etViewMemberPhone.getText().toString().trim();
        String nickName = etViewMemberNickName.getText().toString().trim();
        String birthDate = ev_selete_member_birthday.getText().toString();

        if (TextUtils.isEmpty(userMobile)) {
            showToast("请输入手机号码");
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            showToast("请输入昵称");
            return;
        }
        String sex = "MF";
        if (tvViewMemberManSex.isChecked())
            sex = "M";
        if (tvViewMemberWoManSex.isChecked())
            sex = "F";

        if (TextUtils.isEmpty(userMobile)) {
            showToast("请输入手机号码");
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("userMobile", userMobile);
            json.put("nickName", nickName);
            json.put("sex", sex);
            json.put("birthDate", birthDate);
            mBact.showLoading();
            easyPost(json, GlobalServerUrl.DEBUG_URL + GlobalServerUrl.ADDNEWMEMBER, CommonResult.class, result -> {
                onNewMemberResult(result);
            });
        } catch (Exception e) {
            mBact.setLog("easyPostLogin", e.getMessage());
            XLog.e(e);
        }
    }

    private void onNewMemberResult(CommonResult commonResult) {
        mBact.showLoading(false);
        XLog.d(commonResult);
        if (commonResult.getResult() == null) {
            showToast("增加会员信息失败");
            return;
        }
        HomeMemberInfo homeMemberInfo = new Gson().fromJson(new Gson().toJson(commonResult.getResult()),HomeMemberInfo.class);
        BaseEventBean eventBean = new BaseEventBean(BaseEventBean.TYPE_GOTO_ADD_MEMBER_FRAGMENT);
        eventBean.setValue(homeMemberInfo);
        EventBus.getDefault().post(eventBean);
        posActivity.showProductFragment();

        etViewMemberPhone.setText("");
        etViewMemberNickName.setText("");
        ev_selete_member_birthday.setText("请选择会员的生日");
        tvViewMemberManSex.setChecked(false);
        tvViewMemberWoManSex.setChecked(false);

    }

    private void seleteMemberDate() {
        TimePickerView pvTime = new TimePickerBuilder(mBact, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(mAct, getTime(date), Toast.LENGTH_SHORT).show();
                ev_selete_member_birthday.setText(getTime(date));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})//分别对应年月日时分秒，默认全部显示
                .isDialog(true)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
        pvTime.show();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(date);
        return str;
    }
}
