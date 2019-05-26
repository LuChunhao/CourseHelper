package com.bs.coursehelper.activity;

import android.animation.ObjectAnimator;
import android.database.sqlite.SQLiteException;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.ProgressDialogHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.bs.coursehelper.utils.SoftKeyBoardListener;
import com.google.gson.Gson;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxAnimationTool;
import com.vondear.rxtool.RxImageTool;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.view.RxToast;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.id_iv_login_logo)
    ImageView idIvLoginLogo;
    @BindView(R.id.id_et_user_account)
    EditText idEtUserAccount;
    @BindView(R.id.id_iv_clean_account)
    ImageView idIvCleanAccount;
    @BindView(R.id.id_et_user_pwd)
    EditText idEtUserPwd;
    @BindView(R.id.id_iv_clean_pwd)
    ImageView idIvCleanPwd;
    @BindView(R.id.id_cb_pwd)
    CheckBox idCbPwd;
    @BindView(R.id.id_tv_forget_pwd)
    TextView idTvForgetPwd;
    @BindView(R.id.id_btn_login)
    Button idBtnLogin;
    @BindView(R.id.id_btn_register)
    Button idBtnRegister;
    @BindView(R.id.id_nsv_login)
    NestedScrollView idNsvLogin;
    @BindView(R.id.id_cl_content_login)
    ConstraintLayout idClContentLogin;

    @Override
    protected void initView() {

        /**
         * 禁止键盘弹起的时候可以滚动
         */
        idNsvLogin.setOnTouchListener((v, event) -> true);
        idNsvLogin.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

            SoftKeyBoardListener.setListener(mActivity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                @Override
                public void keyBoardShow(int height) {
                    int dist = (int) (RxImageTool.dip2px(80) * 0.4f);
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(idClContentLogin, "translationY", 0.0f, -dist);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    RxAnimationTool.zoomIn(idIvLoginLogo, 0.6f, dist);
                }

                @Override
                public void keyBoardHide(int height) {
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(idClContentLogin, "translationY", idClContentLogin.getTranslationY(), 0);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                    RxAnimationTool.zoomOut(idIvLoginLogo, 0.6f);
                }
            });
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @OnClick({R.id.id_cl_root_login, R.id.id_iv_clean_account, R.id.id_iv_clean_pwd, R.id.id_tv_forget_pwd, R.id.id_btn_login, R.id.id_btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_cl_root_login:
                RxKeyboardTool.hideSoftInput(mActivity);
                break;
            case R.id.id_iv_clean_account:
                idEtUserAccount.setText("");
                break;
            case R.id.id_iv_clean_pwd:
                idEtUserPwd.setText("");
                break;
            case R.id.id_tv_forget_pwd:
                // TODO: 2018/10/14 跳转到忘记密码的界面
                idEtUserAccount.setText("");
                idEtUserPwd.setText("");
                RxActivityTool.skipActivity(mContext, ForgetPwdActivity.class);
                break;
            case R.id.id_btn_login:
                // TODO: 2018/10/14 跳转首页
                String userName = idEtUserAccount.getText().toString().trim();
                if (isEtEmpty(userName, "用户名不能为空...", idEtUserAccount)) return;

                String userPwd = idEtUserPwd.getText().toString().trim();
                if (isEtEmpty(userPwd, "用户密码不能为空...", idEtUserPwd)) return;
                DbHelper dbHelper = DbHelper.getInstance();
                ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(mContext);
                progressDialogHelper.show("登录", "正在登录中...");
                RxKeyboardTool.hideSoftInput(mActivity);
                Observable.just(0)
                        .map(integer -> {
                            User user = dbHelper.queryUserIsExist(userName, userName);
                            int result = 0;
                            try {
                                if (user == null) {
                                    result = 0;
                                } else {
                                    user = dbHelper.queryUser(userName, userPwd);
                                    if (user != null) {
                                        String userInfo = new Gson().toJson(user);
                                        SPUtil.getInstanse().setParam(Constants.USER_LOCAL_INFO, userInfo);
                                        result = 2;
                                    } else {
                                        result = 1;
                                    }
                                }
                            } catch (SQLiteException sqLiteException) {
                                result = -1;
                                sqLiteException.printStackTrace();
                            }
                            return result;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            progressDialogHelper.dismiss();
                            switch (o) {
                                case -1:
                                    RxToast.normal("数据库异常，请联系管理员！！！");
                                    break;
                                case 0:
                                    RxToast.normal("该用户不存在，请注册！！！");
                                    break;
                                case 1:
                                    RxToast.normal("用户名或者密码错误，请仔细检查！！！");
                                    break;
                                case 2:
                                    RxToast.normal("登录成功！");
                                    RxActivityTool.skipActivity(mContext, MainActivity.class);
                                    finish();
                                    break;
                            }
                        });

                break;
            case R.id.id_btn_register:
                idEtUserAccount.setText("");
                idEtUserPwd.setText("");
                RxActivityTool.skipActivity(mContext, RegisterActivity.class);
                break;
        }
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


    @OnCheckedChanged(R.id.id_cb_pwd)
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (isChecked) {
            idEtUserPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            idEtUserPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String pwd = idEtUserPwd.getText().toString();
        if (!TextUtils.isEmpty(pwd))
            idEtUserPwd.setSelection(pwd.length());
    }

}
