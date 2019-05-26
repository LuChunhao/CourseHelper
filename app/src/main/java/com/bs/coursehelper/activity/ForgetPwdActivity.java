package com.bs.coursehelper.activity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.ProgressDialogHelper;
import com.bs.coursehelper.utils.ShakeHelper;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.RxRegTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxTitle;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_iv_forget_logo)
    ImageView idIvForgetLogo;
    @BindView(R.id.id_iv_icon_number)
    ImageView idIvIconNumber;
    @BindView(R.id.id_et_user_number)
    EditText idEtUserNumber;
    @BindView(R.id.id_iv_clean_number)
    ImageView idIvCleanNumber;
    @BindView(R.id.id_v_line_number)
    View idVLineNumber;
    @BindView(R.id.id_iv_icon_pwd)
    ImageView idIvIconPwd;
    @BindView(R.id.id_et_user_pwd)
    EditText idEtUserPwd;
    @BindView(R.id.id_iv_clean_pwd)
    ImageView idIvCleanPwd;
    @BindView(R.id.id_cb_pwd)
    CheckBox idCbPwd;
    @BindView(R.id.id_v_line_pwd)
    View idVLinePwd;
    @BindView(R.id.id_iv_icon_pwd_sure)
    ImageView idIvIconPwdSure;
    @BindView(R.id.id_et_user_pwd_sure)
    EditText idEtUserPwdSure;
    @BindView(R.id.id_iv_clean_pwd_sure)
    ImageView idIvCleanPwdSure;
    @BindView(R.id.id_cb_pwd_sure)
    CheckBox idCbPwdSure;
    @BindView(R.id.id_v_line_pwd_sure)
    View idVLinePwdSure;
    @BindView(R.id.id_btn_save)
    Button idBtnSave;
    @BindView(R.id.id_cl_content_forget)
    ConstraintLayout idClContentForget;
    @BindView(R.id.id_cl_root_register)
    ConstraintLayout idClRootRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        idRtTitle.setLeftFinish(mActivity);

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_forget_pwd;
    }

    @OnClick({R.id.id_iv_clean_number, R.id.id_iv_clean_pwd, R.id.id_iv_clean_pwd_sure, R.id.id_btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_clean_number:
                idEtUserNumber.setText("");
                break;
            case R.id.id_iv_clean_pwd:
                idEtUserPwd.setText("");
                break;
            case R.id.id_iv_clean_pwd_sure:
                idEtUserPwdSure.setText("");
                break;
            case R.id.id_btn_save:

                String usernumber = idEtUserNumber.getText().toString().trim();
                if (isEtEmpty(usernumber, "用户学号或者工号不能为空...", idEtUserNumber)) return;
                if (!RxRegTool.isMatch("[0-9]*", usernumber)) {
                    RxToast.normal("学号或者工号不符合规则...");
                    ShakeHelper.shake(idEtUserNumber);
                    return;
                }

                String userPwd = idEtUserPwd.getText().toString().trim();
                if (isEtEmpty(userPwd, "用户密码不能为空...", idEtUserPwd)) return;

                String userPwdSure = idEtUserPwdSure.getText().toString().trim();
                if (isEtEmpty(userPwdSure, "用户再次输入密码不能为空...", idEtUserPwdSure)) return;

                if (!TextUtils.equals(userPwd, userPwdSure)) {
                    RxToast.normal("两次输入的密码不一致...");
                    ShakeHelper.shake(idEtUserPwdSure);
                    idEtUserPwdSure.setText("");
                    return;
                }

                final User[] user = {new User()};
                user[0].setUserNumber(usernumber);
                user[0].setUserPwd(userPwd);

                DbHelper dbHelper = DbHelper.getInstance();
                ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(mContext);
                progressDialogHelper.show("找回密码", "正在找回中...");
                RxKeyboardTool.hideSoftInput(mActivity);
                Observable.just(0)
                        .map(integer -> {
                            long isSucess = -1;
                            User userTmp = dbHelper.queryUserIsExist(usernumber, usernumber);
                            if (userTmp == null) {
                                isSucess = -2;
                            }else{
                                try {
                                    isSucess = dbHelper.updateUserPwd(user[0]);
                                } catch (SQLiteException sqLiteException) {
                                    isSucess = -1;
                                    sqLiteException.printStackTrace();
                                }
                            }
                            return isSucess;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            progressDialogHelper.dismiss();
                            if (o >= 0) {
                                RxToast.normal("找回密码成功！");
                                finish();
                            }else if (o == -2){
                                RxToast.normal("学生对应的学号或者管理员的工号不存在，请仔细填写！！！");
                                ShakeHelper.shake(idEtUserNumber);
                                idEtUserNumber.setText("");
                            }else if (0 == -1){
                                RxToast.normal("找回密码失败！！！");
                            }
                        });
                break;
        }
    }

    /**
     * 用户手机号(其实就是学号)的监听
     *
     * @param editable
     */
    @OnTextChanged(value = R.id.id_et_user_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangednumber(Editable editable) {
        if (editable.length() > 0) {
            idIvCleanNumber.setVisibility(View.VISIBLE);
        } else {
            idIvCleanNumber.setVisibility(View.GONE);
        }
    }

    /**
     * 用户密码的监听
     *
     * @param editable
     */
    @OnTextChanged(value = R.id.id_et_user_pwd, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangedPwd(Editable editable) {
        if (editable.length() > 0) {
            idIvCleanPwd.setVisibility(View.VISIBLE);
        } else {
            idIvCleanPwd.setVisibility(View.GONE);
        }
    }

    /**
     * 用户再次输入密码的监听
     *
     * @param editable
     */
    @OnTextChanged(value = R.id.id_et_user_pwd_sure, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangedPwdSure(Editable editable) {
        if (editable.length() > 0) {
            idIvCleanPwdSure.setVisibility(View.VISIBLE);
        } else {
            idIvCleanPwdSure.setVisibility(View.GONE);
        }
    }

    @OnFocusChange(R.id.id_et_user_pwd_sure)
    public void onFocusChangePwdSure(EditText editText, boolean isFouces) {
        if (!isFouces) {
            String userPwd = idEtUserPwd.getText().toString().trim();
            if (TextUtils.isEmpty(userPwd)) {
                idEtUserPwdSure.setText("");
                RxToast.normal("请先输入密码...");
                return;
            }
            if (!TextUtils.equals(editText.getText().toString().trim(), userPwd)) {
                RxToast.normal("两次输入的密码不一致...");
                ShakeHelper.shake(editText);
                editText.setText("");
            }
        }
    }


    @OnCheckedChanged({R.id.id_cb_pwd, R.id.id_cb_pwd_sure})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.id_cb_pwd:
                setEtInputType(idEtUserPwd, isChecked);
                break;
            case R.id.id_cb_pwd_sure:
                setEtInputType(idEtUserPwdSure, isChecked);
                break;
        }
    }

    /**
     * 设置edittext的inputtype
     *
     * @param et
     * @param isChecked
     */
    private void setEtInputType(EditText et, boolean isChecked) {
        if (isChecked) {
            et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String pwd = et.getText().toString();
        if (!TextUtils.isEmpty(pwd))
            et.setSelection(pwd.length());
    }
}
