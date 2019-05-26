package com.bs.coursehelper.activity;

import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.CourseTeacherBean;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void initData() {

        //initAdmin();
        initUser();


    }

    private void gotoNextPage() {
        new Handler().postDelayed(() -> {
            String userInfo = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
            if (!TextUtils.isEmpty(userInfo)) {
                RxActivityTool.skipActivity(mContext, MainActivity.class);
            } else {
                //进入首次登录引导页面
                RxActivityTool.skipActivity(mContext, WelcomActivity.class);
            }
            mActivity.finish();
        }, 1000);
    }

    private void initAdmin() {
        final User[] user = {new User(), new User()};
        user[0].setUserName("管理员");
        user[0].setUserNumber("000000");
        user[0].setUserPwd("123456");
        user[0].setUserType(1);

        user[1].setUserName("admin");
        user[1].setUserNumber("000001");
        user[1].setUserPwd("123456");
        user[1].setUserType(1);
        DbHelper dbHelper = DbHelper.getInstance();
        Observable.just(0)
                .map(integer -> {
                    long isSucess = -1;
                    Log.i(TAG, "onClick: integer==" + integer);
                    if (integer == 0) {
                        try {
                            dbHelper.insertUser(user[0]);
                            isSucess = dbHelper.insertUser(user[1]);
                        } catch (SQLiteException sqLiteException) {
                            isSucess = -1;
                            sqLiteException.printStackTrace();
                        }
                    } else if (integer == 1) {
                        isSucess = -2;
                    }
                    return isSucess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o >= 0) {
                        //RxToast.normal("注册成功！");
                        Log.d(TAG, "注册成功！");
                        finish();
                    } else {
                        if (o == -2) {
                            //RxToast.normal("该用户已存在！！！");
                            Log.d(TAG, "该用户已存在！！！");
                            return;
                        }
                        //RxToast.normal("注册失败！");
                    }
                    gotoNextPage();
                });


    }

    /**
     * 构造数据
     */
    private void initUser() {
        List<User> userList = new ArrayList<>();
        // 学生
        User student1 = new User("学生001", "123456", "223456", 0);
        User student2 = new User("学生002", "123456", "223798", 0);
        User student3 = new User("学生003", "123456", "223741", 0);
        // 老师
        User teacher1 = new User("高数老师", "123456", "663456", 2);
        User teacher2 = new User("英语老师", "123456", "663798", 2);
        User teacher3 = new User("物理老师", "123456", "663741", 2);
        User teacher4 = new User("化学老师", "123456", "667456", 2);
        // 管理员
        User admin1 = new User("管理员", "123456", "993456", 1);
        User admin2 = new User("admin", "123456", "994698", 1);

        userList.add(student1);
        userList.add(student2);
        userList.add(student3);
        userList.add(teacher1);
        userList.add(teacher2);
        userList.add(teacher3);
        userList.add(teacher4);
        userList.add(admin1);
        userList.add(admin2);

        DbHelper dbHelper = DbHelper.getInstance();
        Observable.just(0)
                .map(integer -> {
                    long isSucess = -1;
                    Log.i(TAG, "onClick: integer==" + integer);
                    if (integer == 0) {
                        try {
                            for (User user : userList) {
                                isSucess = dbHelper.insertUser(user);
                            }
                        } catch (SQLiteException sqLiteException) {
                            isSucess = -1;
                            sqLiteException.printStackTrace();
                        }
                    } else if (integer == 1) {
                        isSucess = -2;
                    }
                    return isSucess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o >= 0) {
                        //RxToast.normal("注册成功！");
                        Log.d(TAG, "注册成功！");
                        finish();
                    } else {
                        if (o == -2) {
                            //RxToast.normal("该用户已存在！！！");
                            Log.d(TAG, "该用户已存在！！！");
                        }
                        //RxToast.normal("注册失败！");
                    }
                    insertCourse(dbHelper);
                });
    }

    private void insertCourse(DbHelper dbHelper) {

        Observable.just(dbHelper.queryUsers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userList -> {
                    Log.i(TAG, "getUserList: userList.size()===" + userList.size());
                    if (userList.size() > 0) {
                        for (User user : userList) {
                            CourseTeacherBean courseTeacherBean = new CourseTeacherBean();
                            courseTeacherBean.setTeacher(user);
                            courseTeacherBean.setCourseName("大学" + user.getUserName().replace("老师", ""));    // 课程名称
                            courseTeacherBean.setCourseMax(70); // 上课人数
                            courseTeacherBean.setCourseStep(1); // 课时
                            courseTeacherBean.setCourseScore(1);    // 学分
                            if (Integer.parseInt(user.getUserNumber()) % 2 == 0) {
                                courseTeacherBean.setIsAllowCengKe(1);
                            } else {
                                courseTeacherBean.setIsAllowCengKe(0);
                            }

                            dbHelper.insertCourseTeacher(courseTeacherBean);
                        }
                        gotoNextPage();
                    }
                });


    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }
}
