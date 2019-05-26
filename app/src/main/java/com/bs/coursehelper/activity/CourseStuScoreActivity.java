package com.bs.coursehelper.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.adapter.CourseStuScoreAdapter;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.CourseUserBean;
import com.bs.coursehelper.bean.MySubject;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxui.view.RxTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseStuScoreActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rv_course_list)
    RecyclerView idRvCourseList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private CourseStuScoreAdapter courseStuScoreAdapter;
    private List<CourseUserBean> courseUserBeanList;
    private User user;

    private float stuCountMark;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_course_stu_score;
    }

    @Override
    protected void initView() {
        super.initView();

        idRtTitle.setLeftFinish(mActivity);
        idRtTitle.setRightTextOnClickListener(view ->
                Observable.just(mDbHelper.querySelectedScore(user.getUserId()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(score ->
                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("  总成绩 / 总学分")
                                        .setContentText(stuCountMark + " / " + score)
                                        .show()));

        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected void initData() {
        mDbHelper = DbHelper.getInstance();
        String userInfoStr = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        Log.i(TAG, "getCourseList: userInfoStr==" + userInfoStr);
        user = new Gson().fromJson(userInfoStr, User.class);
        courseUserBeanList = new ArrayList<>();
        courseStuScoreAdapter = new CourseStuScoreAdapter(courseUserBeanList, mContext);
        idRvCourseList.setLayoutManager(new LinearLayoutManager(mContext));
        courseStuScoreAdapter.setIRVOnItemListener((mySubject, position) -> {
        });

        idRvCourseList.setAdapter(courseStuScoreAdapter);

        getCourseList();
    }

    @Override
    protected void initListener() {

    }

    /**
     * 获取课程的列表
     */
    private void getCourseList() {
        mSweetAlertDialog.show();
        Observable.just(mDbHelper.queryUserById(user.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    List<CourseUserBean> courseUserBeanListTmp = user.getUserCourses();
                    if (courseUserBeanListTmp.size() == 0) {
                        idRvCourseList.setVisibility(View.GONE);
                        idTvNoData.setText("亲，你还未选课程,请先选择课程！！！");
                        idTvNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (courseUserBeanList.size() > 0) {
                            courseUserBeanList.clear();
                        }
                        Log.i(TAG, "getCourseList: courseUserBeanListTmp==" + courseUserBeanListTmp.toString());
                        courseUserBeanList.addAll(courseUserBeanListTmp);
                        for (CourseUserBean courseUserBean : courseUserBeanList) {
                            stuCountMark += courseUserBean.getCourseMark();
                        }
                        courseStuScoreAdapter.notifyDataSetChanged();
                        idTvNoData.setVisibility(View.GONE);
                        idRvCourseList.setVisibility(View.VISIBLE);
                    }
                    mSweetAlertDialog.dismiss();
                });
    }


    /**
     * 显示课程的详情
     *
     * @param mySubject
     */
    private void showCourseDetail(MySubject mySubject) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_course_detail_home, null, false);
        TextView idTvCourseName = dialogView.findViewById(R.id.id_tv_course_desc);
        TextView idTvCourseTeacher = dialogView.findViewById(R.id.id_tv_course_teacher_desc);
        TextView idTvCourseRoom = dialogView.findViewById(R.id.id_tv_course_addr_desc);
        TextView idTvCourseStuNum = dialogView.findViewById(R.id.id_tv_course_stu_num_desc);
        TextView idTvCourseScore = dialogView.findViewById(R.id.id_tv_course_score_desc);
        int bgColor = mContext.getResources().getColor(R.color.tb_blue3);
        RxTextTool.getBuilder("课程名称：").append(mySubject.getName()).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("授课教师：").append(mySubject.getTeacher()).setForegroundColor(bgColor).into(idTvCourseTeacher);
        RxTextTool.getBuilder("授课地点：").append(mySubject.getRoom()).setForegroundColor(bgColor).into(idTvCourseRoom);
        RxTextTool.getBuilder("报名人数：").append(mySubject.getCourseStuApplications() + " (" + mySubject.getCourseStuNum() + ")").setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("课程学分：").append(String.valueOf(mySubject.getCourseScore())).setForegroundColor(bgColor).into(idTvCourseScore);

        new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }
}
