package com.bs.coursehelper.activity;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.adapter.HomeListAdapter;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.MySubject;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxui.view.RxTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllCoursesActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rv_course_list)
    RecyclerView idRvCourseList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private HomeListAdapter homeListAdapter;
    private List<MySubject> mySubjectList;

    private User user;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_all_courses;
    }

    @Override
    protected void initView() {
        super.initView();
        //idRtTitle.setPadding(0, RxBarTool.getStatusBarHeight(mContext), 0, 0);
        idRtTitle.setLeftFinish(mActivity);

        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected void initData() {
        mDbHelper = DbHelper.getInstance();
        String userInfoStr = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        user = new Gson().fromJson(userInfoStr, User.class);
        mySubjectList = new ArrayList<>();
        homeListAdapter = new HomeListAdapter(mySubjectList, mContext);
        idRvCourseList.setLayoutManager(new LinearLayoutManager(mContext));
        homeListAdapter.setIRVOnItemListener((mySubject, position) -> {
            showCourseDetail(mySubject);
        });
        homeListAdapter.setIRVViewOnClickListener((view, mySubject, position) -> {
            if (view.getId() == R.id.id_iv_sign_up) {
                //报名操作
                mSweetAlertDialog.show();
                Observable.just(isApplyCoures(user.getUserId(), mySubject.getCourseId()))
                        .map(aLong -> {
                            long isSucess = -1;
                            if (aLong==0){
                                try {
                                    isSucess = mDbHelper.insertCourseUser(mySubject.getCourseId(), user.getUserId());
                                } catch (SQLiteException sqLiteException) {
                                    isSucess = -1;
                                    sqLiteException.printStackTrace();
                                }
                            }else if (aLong==1){
                                isSucess = -2;
                            }
                            return isSucess;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                                    mSweetAlertDialog.dismiss();
                                    if (aLong >= 0) {
                                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("报名成功!")
                                                .show();
                                    } else {
                                        if (aLong==-2){
                                            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("亲，不能重复报名！!")
                                                    .show();
                                        }else{
                                            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("报名失败!")
                                                    .show();
                                        }

                                    }
                                }
                        );
            }
        });
        idRvCourseList.setAdapter(homeListAdapter);

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourseList();
    }

    /**
     * 课程是否已经报过
     *
     * @param userId
     * @param courseId
     * @return
     */
    private int isApplyCoures(int userId, int courseId){
        int isApply = DbHelper.getInstance().queryCourseIsApply(userId, courseId);
        return isApply;
    }

    /**
     * 获取课程的列表
     */
    private void getCourseList() {
        mSweetAlertDialog.show();
        Observable.just(mDbHelper.queryCoursesGroupByCourseId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subjectList -> {
                    if (subjectList.size() == 0) {
                        idRvCourseList.setVisibility(View.GONE);
                        idTvNoData.setText("暂无课程信息，请点击右上角添加新课程！！！");
                        idTvNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (mySubjectList.size() > 0) {
                            mySubjectList.clear();
                        }
                        mySubjectList.addAll(subjectList);
                        homeListAdapter.notifyDataSetChanged();
                        idTvNoData.setVisibility(View.GONE);
                        idRvCourseList.setVisibility(View.VISIBLE);
                    }
                    mSweetAlertDialog.dismissWithAnimation();
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

        new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }

}
