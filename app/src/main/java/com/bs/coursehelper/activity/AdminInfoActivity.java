package com.bs.coursehelper.activity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxDataTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxTitle;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AdminInfoActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_cir_mine)
    CircleImageView idCirMine;
    @BindView(R.id.id_et_user_account)
    EditText idEtUserAccount;
    @BindView(R.id.id_et_user_number)
    EditText idEtUserNumber;
    @BindView(R.id.id_rg_role)
    RadioGroup idRgRole;
    @BindView(R.id.id_btn_save)
    Button idBtnSave;
    @BindView(R.id.id_rb_role_student)
    RadioButton idRbRoleStudent;
    @BindView(R.id.id_rb_role_teacher)
    RadioButton idRbRoleTeacher;
    @BindView(R.id.et_user_college)
    EditText etUserCollege;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        String userInfo = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        user = new Gson().fromJson(userInfo, User.class);
        idEtUserAccount.setText(user.getUserName());
        idEtUserNumber.setText(user.getUserNumber());
        etUserCollege.setText(user.getCollege());
        int sex = user.getUserSex();
        if (sex==0){
            idRbRoleStudent.setChecked(true);
        }else if (sex==1){
            idRbRoleTeacher.setChecked(true);
        }
        idRgRole.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.id_rb_role_student:
                    user.setUserSex(0);
                    break;
                case R.id.id_rb_role_teacher:
                    user.setUserSex(1);
                    break;
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setLeftFinish(mActivity);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_info;
    }

    @OnClick(R.id.id_btn_save)
    public void onClick() {

        String name = idEtUserAccount.getText().toString().trim();
        String college = etUserCollege.getText().toString().trim();
        if (RxDataTool.isEmpty(name)) {
            RxToast.normal("用户姓名不能为空！！！");
            mActivity.handleEtEmpty(idEtUserAccount);
            return;
        }
        user.setUserName(name);
        user.setCollege(college);
//        Observable.just(isExistName(name, user.getUserNumber()))
//                .map(integer -> {
//                    long isSucess = -1;
//                    if (integer==0){
//                        try {
//                            isSucess = DbHelper.getInstance().updateUserInfo(user);
//                        } catch (SQLiteException sqLiteException) {
//                            isSucess = -1;
//                            sqLiteException.printStackTrace();
//                        }
//                    }else if (integer==1){
//                        isSucess = -2;
//                    }
//                    return isSucess;
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aLong -> {
//                    if (aLong >= 0) {
//                        RxToast.normal("用户信息修改成功！");
//                        mActivity.finish();
//                    }else{
//                        if (aLong==-2){
//                            RxToast.normal("该用户已存在！！！");
//                            return;
//                        }
//                        RxToast.normal("用户信息修改失败！");
//                    }
//                });

        Observable.just(DbHelper.getInstance().updateUserInfo(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong >= 0) {
                        RxToast.normal("用户信息修改成功！");
                        String userInfo = new Gson().toJson(user);
                        SPUtil.getInstanse().setParam(Constants.USER_LOCAL_INFO, userInfo);
                        mActivity.finish();
                    }else{
                        RxToast.normal("用户信息修改失败！");
                    }
                });

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
}
