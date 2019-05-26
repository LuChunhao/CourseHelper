package com.bs.coursehelper.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.coursehelper.R;
import com.bs.coursehelper.activity.CourseListActivity;
import com.bs.coursehelper.activity.RegisterActivity;
import com.bs.coursehelper.adapter.StudentListAdapter;
import com.bs.coursehelper.base.BaseFragment;
import com.bs.coursehelper.bean.CourseTeacherBean;
import com.bs.coursehelper.bean.CourseUserBean;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxDataTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxui.view.RxTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class AdminStuFragment extends BaseFragment {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rv_stu_list)
    RecyclerView idRvStuList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private StudentListAdapter studentListAdapter;
    private List<User> mUserList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_admin_stu;
    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setPadding(0, RxBarTool.getStatusBarHeight(mContext), 0, 0);
        idRtTitle.setRightOnClickListener(view -> {
            //添加人员：教师、学生
            startActivityForResult(new Intent(mActivity, RegisterActivity.class), 123);
        });
        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected void initData() {
        super.initData();
        mDbHelper = DbHelper.getInstance();
        mUserList = new ArrayList<>();
        studentListAdapter = new StudentListAdapter(mUserList, mContext);
        idRvStuList.setLayoutManager(new LinearLayoutManager(mContext));
        studentListAdapter.setIRVOnItemListener((user, position) -> {
            if (user.getUserType() == 2) {
                showCourseDetail(user.getCourseTeacherBean());
            } else {
                showCourseDetail(user);
            }
        });
        studentListAdapter.setIRVOnLongListener((user, position) -> new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否确定删除该用户？")
                .setCancelText("放弃")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setCancelClickListener(sDialog -> sDialog.setTitleText("已经放弃本次操作！！")
                        .setConfirmText("OK")
                        .showCancelButton(false)
                        .setCancelClickListener(null)
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE))
                .setConfirmClickListener(sDialog -> Observable.just(mDbHelper.deleteUser(user.getUserId()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (aLong >= 0) {
                                sDialog.setTitleText("用户删除成功！！！")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(sweetAlertDialog -> {
                                            sweetAlertDialog.dismissWithAnimation();
                                            getUserList();
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            } else {
                                sDialog.setTitleText("用户删除失败！！！")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }

                        })).show());

        idRvStuList.setAdapter(studentListAdapter);
        mSweetAlertDialog.show();
        getUserList();
    }

    /**
     * 获取用户的列表
     */
    private void getUserList() {
        // 获取
        Observable.just(mDbHelper.queryUsers())
                .map(userList -> {
                    List<CourseTeacherBean> courseTeacherBeans = mDbHelper.queryAllCourseTeachers();
                    Log.d(TAG, "userSize====" + userList.size());
                    Log.d(TAG, "courseTeacherBeans====" + courseTeacherBeans.size());
                    if (courseTeacherBeans.size() > 0) {
                        for (User user : userList) {
                            for (CourseTeacherBean bean : courseTeacherBeans) {
                                if (user.getUserNumber().equals(bean.getTeacher().getUserNumber())) {
                                    user.setCourseTeacherBean(bean);
                                    break;
                                }
                            }
                        }
                    }
                    return userList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userList -> {
                    Log.i(TAG, "getUserList: userList.size()===" + userList.size());
                    if (userList.size() == 0) {
                        idRvStuList.setVisibility(View.GONE);
                        idTvNoData.setText("暂无人员信息！！！");
                        idTvNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (mUserList.size() > 0) {
                            mUserList.clear();
                        }
                        Log.i(TAG, "getUserList: usr==" + userList.toString());
                        mUserList.addAll(userList);
                        studentListAdapter.notifyDataSetChanged();
                        idTvNoData.setVisibility(View.GONE);
                        idRvStuList.setVisibility(View.VISIBLE);
                    }
                    mSweetAlertDialog.dismissWithAnimation();
                });
        // 获取用户表
//        Observable.just(mDbHelper.queryUsers())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(userList -> {
//                    Log.i(TAG, "getUserList: userList.size()===" + userList.size());
//                    if (userList.size() == 0) {
//                        idRvStuList.setVisibility(View.GONE);
//                        idTvNoData.setText("暂无人员信息！！！");
//                        idTvNoData.setVisibility(View.VISIBLE);
//                    } else {
//                        if (mUserList.size() > 0) {
//                            mUserList.clear();
//                        }
//                        Log.i(TAG, "getUserList: usr==" + userList.toString());
//                        mUserList.addAll(userList);
//                        studentListAdapter.notifyDataSetChanged();
//                        idTvNoData.setVisibility(View.GONE);
//                        idRvStuList.setVisibility(View.VISIBLE);
//                    }
//                    mSweetAlertDialog.dismissWithAnimation();
//                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            getUserList();
        }
    }

    /**
     * 显示课程的详情
     *
     * @param courseTeacherBean
     */
    private void showCourseDetail(CourseTeacherBean courseTeacherBean) {
        if (null == courseTeacherBean) {
            Toast.makeText(mActivity, "该教师尚未新建课程", Toast.LENGTH_SHORT).show();
            return;
        }
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_course_detail_home, null, false);
        TextView idTvCourseName = dialogView.findViewById(R.id.id_tv_course_desc);
        TextView idTvCourseTeacher = dialogView.findViewById(R.id.id_tv_course_teacher_desc);
        ImageView idIvCourseAddr = dialogView.findViewById(R.id.id_iv_course_addr);
        TextView idTvCourseRoom = dialogView.findViewById(R.id.id_tv_course_addr_desc);
        View idVLineCourseAddr = dialogView.findViewById(R.id.id_v_line_course_addr);
        idIvCourseAddr.setVisibility(View.GONE);
        idTvCourseRoom.setVisibility(View.GONE);
        idVLineCourseAddr.setVisibility(View.GONE);
        TextView idTvCourseStuNum = dialogView.findViewById(R.id.id_tv_course_stu_num_desc);
        TextView idTvCourseScore = dialogView.findViewById(R.id.id_tv_course_score_desc);
        int bgColor = mContext.getResources().getColor(R.color.tb_blue3);
        RxTextTool.getBuilder("课程名称：").append(courseTeacherBean.getCourseName()).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("授课教师：").append(courseTeacherBean.getTeacher().getUserName()).setForegroundColor(bgColor).into(idTvCourseTeacher);
        RxTextTool.getBuilder("报名人数：").append(courseTeacherBean.getCourseStuApplications() + " (" + courseTeacherBean.getCourseMax() + ")").setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("课程学分：").append(String.valueOf(courseTeacherBean.getCourseScore())).setForegroundColor(bgColor).into(idTvCourseScore);

        new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }

    /**
     * 显示课程的详情
     *
     * @param user
     */
    private void showCourseDetail(User user) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_user_detail, null, false);
        TextView idTvCourseName = dialogView.findViewById(R.id.id_tv_course_desc);
        TextView idTvCourseStuNum = dialogView.findViewById(R.id.id_tv_course_stu_num_desc);
        TextView idTvCourseScore = dialogView.findViewById(R.id.id_tv_course_score_desc);
        int bgColor = mContext.getResources().getColor(R.color.tb_blue3);
        StringBuilder sbName = new StringBuilder();
        List<CourseUserBean> courseUserBeanList = user.getUserCourses();
        for (int i = 0; i < courseUserBeanList.size(); i++) {
            CourseUserBean courseUserBean = courseUserBeanList.get(i);
            sbName.append(courseUserBean.getName());
            sbName.append("(");
            sbName.append(courseUserBean.getTeacher());
            sbName.append(") ");
            if (i > 0 && i != courseUserBeanList.size() - 1) {
                sbName.append("\n");
            }
        }
        String name = sbName.toString();
        RxTextTool.getBuilder("已选课程：").append(RxDataTool.isEmpty(name) ? "暂未选课程" : name).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("已选课数：").append(String.valueOf(courseUserBeanList.size())).setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("已选学分：").append(String.valueOf(user.getUserCourseScore())).setForegroundColor(bgColor).into(idTvCourseScore);

        new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }
}
