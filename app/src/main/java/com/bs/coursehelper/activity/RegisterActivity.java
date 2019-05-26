package com.bs.coursehelper.activity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.http.OkGoUtil;
import com.bs.coursehelper.http.callbck.JsonCallback;
import com.bs.coursehelper.http.response.ResponseBean;
import com.bs.coursehelper.utils.ProgressDialogHelper;
import com.bs.coursehelper.utils.ShakeHelper;
import com.lzy.okgo.model.Response;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.RxRegTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxTitle;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.id_iv_register_logo)
    ImageView idIvRegisterLogo;
    @BindView(R.id.id_et_user_account)
    EditText idEtUserAccount;
    @BindView(R.id.id_iv_clean_account)
    ImageView idIvCleanAccount;
    @BindView(R.id.id_et_user_number)
    EditText idEtUsernumber;
    @BindView(R.id.id_iv_clean_number)
    ImageView idIvCleannumber;
    @BindView(R.id.id_et_user_pwd)
    EditText idEtUserPwd;
    @BindView(R.id.id_iv_clean_pwd)
    ImageView idIvCleanPwd;
    @BindView(R.id.id_et_user_pwd_sure)
    EditText idEtUserPwdSure;
    @BindView(R.id.id_iv_clean_pwd_sure)
    ImageView idIvCleanPwdSure;
    @BindView(R.id.id_cl_content_register)
    ConstraintLayout idClContentRegister;
    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rg_role)
    RadioGroup idRgRole;

    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initParam(Bundle savedInstanceState) {
        super.initParam(savedInstanceState);
    }

    @Override
    protected void initView() {
        idRgRole.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.id_rb_role_student:
                    userType = 0 ;
                    break;
                case R.id.id_rb_role_teacher:
                    userType = 1 ;
                    break;
            }
        });

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
        return R.layout.activity_register;
    }

    @OnClick({R.id.id_cl_root_register, R.id.id_iv_clean_account, R.id.id_iv_clean_number, R.id.id_iv_clean_pwd, R.id.id_iv_clean_pwd_sure, R.id.id_btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_cl_root_register:
                RxKeyboardTool.hideSoftInput(mActivity);
                break;
            case R.id.id_iv_clean_account:
                idEtUserAccount.setText("");
                break;
            case R.id.id_iv_clean_number:
                idEtUsernumber.setText("");
                break;
            case R.id.id_iv_clean_pwd:
                idEtUserPwd.setText("");
                break;
            case R.id.id_iv_clean_pwd_sure:
                idEtUserPwdSure.setText("");
                break;
            case R.id.id_btn_register:
                String userName = idEtUserAccount.getText().toString().trim();
                if (isEtEmpty(userName, "用户名不能为空...", idEtUserAccount)) return;

                String usernumber = idEtUsernumber.getText().toString().trim();
                if (isEtEmpty(usernumber, "用户学号或者工号不能为空...", idEtUsernumber)) return;
                if (!RxRegTool.isMatch("[0-9]*", usernumber)) {
                    RxToast.normal("学号或者工号不符合规则...");
                    ShakeHelper.shake(idEtUsernumber);
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
                user[0].setUserName(userName);
                user[0].setUserNumber(usernumber);
                user[0].setUserPwd(userPwd);


                //55 是管理员的工号开头  学号不是的,  66是教师的
                if (userType == 1 && !usernumber.startsWith("55") && !usernumber.startsWith("66")){
                    RxToast.normal("学生不能注册管理员...");
                    return;
                }else if ((usernumber.startsWith("55")  || usernumber.startsWith("66")) && userType == 0){
                    RxToast.normal("学号有误，请重新输入...");
                    return;
                }
                if (usernumber.startsWith("55")){
                    userType = 1;
                }else if (usernumber.startsWith("66")){
                    userType = 2;
                }
                Log.i(TAG, "onClick: 用户的类型  userType==" + userType);
                user[0].setUserType(userType);

                DbHelper dbHelper = DbHelper.getInstance();
                ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(mContext);
                progressDialogHelper.show("注册", "正在注册中...");
                RxKeyboardTool.hideSoftInput(mActivity);
                Map<String, String> params = new HashMap<>();
                params.put("userName", userName);
                params.put("userNumber", usernumber);
                OkGoUtil.getRequets(Constants.IS_EXIST_USER_URL, mActivity, params, new JsonCallback<ResponseBean<User>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<User>> response) {
                        Log.i(TAG, "onSuccess: response.body().Result==" + response.body().data);
                        int code = response.body().code;
                        if (code == 200){
                            //查询成功，该用户存在
                            RxToast.normal(response.body().msg);
                        }else if (code == 220){
                            //该账户不存在，可以注册
                            params.put("userNumber", usernumber);
                            params.put("userNumber", usernumber);
                            OkGoUtil.getRequets(Constants.INSERT_USER_URL, mActivity, params, new JsonCallback<ResponseBean<User>>() {
                                @Override
                                public void onSuccess(Response<ResponseBean<User>> response) {
                                    Log.i(TAG, "onSuccess: response.body().Result==" + response.body().data);
                                    int code = response.body().code;
                                    if (code == 200){
                                        //查询成功，该用户存在
                                        RxToast.normal(response.body().msg);
                                    }else if (code == 220){
                                        //该账户不存在，可以注册
                                    }

                                }
                            });
                        }

                    }
                });
                Observable.just(0)
                        .map(integer -> {
                            long isSucess = -1;
                            Log.i(TAG, "onClick: integer==" + integer);
                            if (integer==0){
                                try {
                                    isSucess = dbHelper.insertUser(user[0]);
                                } catch (SQLiteException sqLiteException) {
                                    isSucess = -1;
                                    sqLiteException.printStackTrace();
                                }
                            }else if (integer==1){
                                isSucess = -2;
                            }
                            return isSucess;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            progressDialogHelper.dismiss();
                            if (o >= 0) {
                                RxToast.normal("注册成功！");
                                finish();
                            }else{
                                if (o==-2){
                                    RxToast.normal("该用户已存在！！！");
                                    return;
                                }
                                RxToast.normal("注册失败！");
                            }
                        });
                break;
        }
    }

    /**
     * 用户是否存在
     *
     * @param name
     * @return
     */
    private int isExistName(String name, String number){
        User user = DbHelper.getInstance().queryUserIsExist(name, number);
        int result = 0;
        try {
            if (user == null) {
                result = 0;
            } else {
                result = 1;
            }
        } catch (SQLiteException sqLiteException) {
            result = -1;
            sqLiteException.printStackTrace();
        }
        return result;
    }

    /**
     * 用户名的监听
     *
     * @param editable
     */
    @OnTextChanged(value = R.id.id_et_user_account, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangedAccount(Editable editable) {
        if (editable.length() > 0) {
            idIvCleanAccount.setVisibility(View.VISIBLE);
        } else {
            idIvCleanAccount.setVisibility(View.GONE);
        }
    }

    /**
     * 用户手机号的监听
     *
     * @param editable
     */
    @OnTextChanged(value = R.id.id_et_user_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangednumber(Editable editable) {
        if (editable.length() > 0) {
            idIvCleannumber.setVisibility(View.VISIBLE);
        } else {
            idIvCleannumber.setVisibility(View.GONE);
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

    @OnFocusChange(R.id.id_et_user_number)
    public void onFocusChangenumber(EditText editText, boolean isFouces) {
        if (!isFouces) {
            String usernumber = idEtUsernumber.getText().toString().trim();
            if (TextUtils.isEmpty(usernumber)) {
                RxToast.normal("工号或者学号不能为空...");
                return;
            }
            if (!RxRegTool.isMatch("[0-9]*", usernumber)) {
                RxToast.normal("工号或者学号格式不正确...");
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
