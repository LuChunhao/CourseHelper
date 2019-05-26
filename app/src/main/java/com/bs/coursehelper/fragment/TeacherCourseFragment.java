package com.bs.coursehelper.fragment;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.activity.TeacherCourseListActivity;
import com.bs.coursehelper.adapter.TeacherHomeListAdapter;
import com.bs.coursehelper.base.BaseFragment;
import com.bs.coursehelper.bean.CourseTeacherBean;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxDataTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TeacherCourseFragment extends BaseFragment {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rv_course_list)
    RecyclerView idRvCourseList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private TeacherHomeListAdapter teacherHomeListAdapter;
    private List<CourseTeacherBean> mCourseTeacherBeanList;

    private User user;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_teacher_course;
    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setPadding(0, RxBarTool.getStatusBarHeight(mContext), 0, 0);
        idRtTitle.setRightOnClickListener(view -> {
            if (mCourseTeacherBeanList.size()>0){
                RxToast.normal("教师只能发布一门课程！！！");
            }else if (mCourseTeacherBeanList.size() == 0){
                showAddCourseTeacher();
            }
        });

        idRtTitle.setLeftTextOnClickListener(view -> RxActivityTool.skipActivity(mActivity, TeacherCourseListActivity.class));

        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected void initData() {
        super.initData();
        String userInfo = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        if (!TextUtils.isEmpty(userInfo)){
            user = new Gson().fromJson(userInfo, User.class);
        }
        mDbHelper = DbHelper.getInstance();
        mCourseTeacherBeanList = new ArrayList<>();
        teacherHomeListAdapter = new TeacherHomeListAdapter(mCourseTeacherBeanList, mContext);
        idRvCourseList.setLayoutManager(new LinearLayoutManager(mContext));
        teacherHomeListAdapter.setIRVOnItemListener((courseTeacherBean, position) -> {
            showCourseDetail(courseTeacherBean);
        });
        teacherHomeListAdapter.setIRVOnLongListener((courseTeacherBean, position) -> {
            editCourseTeacher(courseTeacherBean);

        });
        idRvCourseList.setAdapter(teacherHomeListAdapter);

        getCourseList();
    }

    /**
     * 获取课程的列表
     */
    private void getCourseList() {
        mSweetAlertDialog.show();
        Observable.just(1)
                .map(integer -> {
                    CourseTeacherBean courseTeacherBean = mDbHelper.queryCourseTeacherByTeacherId(user.getUserId());
                    List<CourseTeacherBean> courseTeacherBeanList = new ArrayList<>();
                    if (courseTeacherBean != null) {
                        courseTeacherBeanList.add(courseTeacherBean);
                    }
                    return courseTeacherBeanList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courseTeacherBeanList -> {
                    Log.i(TAG, "getCourseList: " + courseTeacherBeanList.size());
                    if (courseTeacherBeanList.size() == 0) {
                        idRvCourseList.setVisibility(View.GONE);
                        idTvNoData.setText("暂无课程信息，请点击右上角添加新课程！！！");
                        idTvNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (mCourseTeacherBeanList.size() > 0) {
                            mCourseTeacherBeanList.clear();
                        }
                        mCourseTeacherBeanList.addAll(courseTeacherBeanList);
                        teacherHomeListAdapter.notifyDataSetChanged();
                        idTvNoData.setVisibility(View.GONE);
                        idRvCourseList.setVisibility(View.VISIBLE);
                    }
                    mSweetAlertDialog.dismiss();
                });
    }

    /**
     * 新增课程
     *
     */
    private void showAddCourseTeacher() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_course_teacher, null, false);
        EditText idEtCourseName = dialogView.findViewById(R.id.id_et_course_name);
        EditText idEtCourseStuNum = dialogView.findViewById(R.id.id_et_course_stu_num);
        EditText idEtCourseNum = dialogView.findViewById(R.id.id_et_course_num);
        EditText idEtCourseScore = dialogView.findViewById(R.id.id_et_course_score);
        Switch switch_cengke = dialogView.findViewById(R.id.switch_cengke);
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setPositiveButton("保存", null).setNegativeButton("放弃", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> {
            Button positionButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
            positionButton.setOnClickListener(v -> {
                String courseName = idEtCourseName.getText().toString().trim();
                String courseStuNum = idEtCourseStuNum.getText().toString().trim();
                String courseNum = idEtCourseNum.getText().toString().trim();
                String courseScore = idEtCourseScore.getText().toString().trim();
                if (RxDataTool.isEmpty(courseName)) {
                    RxToast.normal("课程名称不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseName);
                    return;
                }
                if (RxDataTool.isEmpty(courseStuNum)) {
                    RxToast.normal("上课人数不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseStuNum);
                    return;
                } else if (!RxDataTool.isInteger(courseStuNum) || courseStuNum.startsWith("0")) {
                    RxToast.normal("上课人数输入不合法！！！");
                    mActivity.handleEtEmpty(idEtCourseStuNum);
                    return;
                }

                //int step = Integer.parseInt(courseNum);
                int step = 1;
                if (RxDataTool.isEmpty(courseNum)) {
                    RxToast.normal("课程节数不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseNum);
                    return;
                }

                if (RxDataTool.isEmpty(courseScore)) {
                    RxToast.normal("课程学分不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseScore);
                    return;
                } else if (!RxDataTool.isNumber(courseScore) || courseScore.equals("0.0")) {
                    RxToast.normal("课程学分输入不合法！！！");
                    mActivity.handleEtEmpty(idEtCourseScore);
                    return;
                }

                CourseTeacherBean courseTeacherBean = new CourseTeacherBean();
                courseTeacherBean.setTeacher(user);
                courseTeacherBean.setCourseName(courseName);
                courseTeacherBean.setCourseMax(Integer.parseInt(courseStuNum));
                courseTeacherBean.setCourseStep(step);
                courseTeacherBean.setCourseScore(Float.parseFloat(courseScore));
                courseTeacherBean.setIsAllowCengKe(switch_cengke.isChecked() ? 1 : 0);

                Observable.just(mDbHelper.insertCourseTeacher(courseTeacherBean))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (aLong >= 0) {
                                RxToast.normal("新增课程成功！！！");
                                getCourseList();
                                alertDialog.dismiss();
                            } else {
                                RxToast.normal("新增课程失败！！！");
                            }
                        });

            });
            negativeButton.setOnClickListener(v -> alertDialog.dismiss());
        });
        alertDialog.show();
    }


    /**
     * 编辑课程
     *
     * @param courseTeacherBean
     */
    private void editCourseTeacher(CourseTeacherBean courseTeacherBean) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_course_teacher, null, false);
        EditText idEtCourseName = dialogView.findViewById(R.id.id_et_course_name);
        EditText idEtCourseStuNum = dialogView.findViewById(R.id.id_et_course_stu_num);
        EditText idEtCourseNum = dialogView.findViewById(R.id.id_et_course_num);

        EditText idEtCourseScore = dialogView.findViewById(R.id.id_et_course_score);
        idEtCourseNum.setEnabled(false);

        idEtCourseName.setText(courseTeacherBean.getCourseName());
        idEtCourseStuNum.setText(String.valueOf(courseTeacherBean.getCourseMax()));
        idEtCourseNum.setText(String.valueOf(courseTeacherBean.getCourseStep()));
        idEtCourseScore.setText(String.valueOf(courseTeacherBean.getCourseScore()));

        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setPositiveButton("保存", null).setNegativeButton("放弃", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> {
            Button positionButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
            positionButton.setOnClickListener(v -> {
                String courseName = idEtCourseName.getText().toString().trim();
                String courseStuNum = idEtCourseStuNum.getText().toString().trim();
                String courseScore = idEtCourseScore.getText().toString().trim();

                if (RxDataTool.isEmpty(courseName)) {
                    RxToast.normal("课程名称不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseName);
                    return;
                }
                if (RxDataTool.isEmpty(courseStuNum)) {
                    RxToast.normal("上课人数不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseStuNum);
                    return;
                } else if (!RxDataTool.isInteger(courseStuNum) || courseStuNum.startsWith("0")) {
                    RxToast.normal("上课人数输入不合法！！！");
                    mActivity.handleEtEmpty(idEtCourseStuNum);
                    return;
                }

                if (RxDataTool.isEmpty(courseScore)) {
                    RxToast.normal("课程学分不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseScore);
                    return;
                } else if (!RxDataTool.isNumber(courseScore) || courseScore.equals("0.0")) {
                    RxToast.normal("课程学分输入不合法！！！");
                    mActivity.handleEtEmpty(idEtCourseScore);
                    return;
                }

                courseTeacherBean.setCourseName(courseName);
                courseTeacherBean.setCourseMax(Integer.parseInt(courseStuNum));
                courseTeacherBean.setCourseScore(Float.parseFloat(courseScore));

                Observable.just(mDbHelper.updateCourseTeacher(courseTeacherBean))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (aLong >= 0) {
                                RxToast.normal("课程更新成功！！！");
                                getCourseList();
                                alertDialog.dismiss();
                            } else {
                                RxToast.normal("课程更新失败！！！");
                            }
                        });

            });
            negativeButton.setOnClickListener(v -> alertDialog.dismiss());
        });
        alertDialog.show();
    }


    /**
     * 显示课程的详情
     *
     * @param courseTeacherBean
     */
    private void showCourseDetail(CourseTeacherBean courseTeacherBean) {
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


}
