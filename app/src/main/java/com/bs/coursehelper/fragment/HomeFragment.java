package com.bs.coursehelper.fragment;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.activity.AllCoursesActivity;
import com.bs.coursehelper.activity.AuditClassActivity;
import com.bs.coursehelper.activity.CourseStuScoreActivity;
import com.bs.coursehelper.activity.SelectedCourseListActivity;
import com.bs.coursehelper.adapter.HomeClassfiyAdapter;
import com.bs.coursehelper.adapter.HomeListAdapter;
import com.bs.coursehelper.base.BaseFragment;
import com.bs.coursehelper.bean.HomeClassfiyBean;
import com.bs.coursehelper.bean.MySubject;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxui.view.RxTextViewVerticalMore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 首页
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.id_iv_top_home)
    ImageView idIvTopHome;
    @BindView(R.id.id_tl_home)
    Toolbar idTlHome;
    @BindView(R.id.id_ctl_home)
    CollapsingToolbarLayout idCtlHome;
    @BindView(R.id.id_rv_home)
    RecyclerView idRvHome;
    @BindView(R.id.id_rvvm_home)
    RxTextViewVerticalMore idRvvmHome;
    @BindView(R.id.id_rv_home_list)
    RecyclerView idRvHomeList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;
    @BindView(R.id.id_tv_all_course)
    TextView idTvAllCourse;

    private AnimationDrawable mAnimation;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private HomeListAdapter homeListAdapter;
    private List<MySubject> mySubjectList;

    private User user;

    @Override
    protected void initView() {
        super.initView();
//        idIvNotify.setImageResource(R.drawable.anim_notify);
//        mAnimation = (AnimationDrawable) idIvNotify.getDrawable();
//        mAnimation.start();
        idCtlHome.setTitle("学校名字");


        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();
        mDbHelper = DbHelper.getInstance();
        String userInfoStr = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        user = new Gson().fromJson(userInfoStr, User.class);
        mySubjectList = new ArrayList<>();
        String[] descArr = mContext.getResources().getStringArray(R.array.text_home_patient_classfiy);
        TypedArray ta = mContext.getResources().obtainTypedArray(R.array.int_home_patient_classfiy);
        List<HomeClassfiyBean> homeClassfiyBeanList = new ArrayList<>();
        for (int i = 0; i < descArr.length; i++) {
            HomeClassfiyBean homeClassfiyBean = new HomeClassfiyBean();
            homeClassfiyBean.setImgId(ta.getResourceId(i, 0));
            homeClassfiyBean.setClassfiyName(descArr[i]);
            homeClassfiyBean.setColorId(mContext.getResources().getColor(R.color.gray_8f));
            homeClassfiyBeanList.add(homeClassfiyBean);
        }
        HomeClassfiyAdapter homeClassfiyAdapter = new HomeClassfiyAdapter(homeClassfiyBeanList, mContext);
        homeClassfiyAdapter.setIRVOnItemListener((homeClassfiyBean, position) -> {
            switch (homeClassfiyBean.getClassfiyName()) {
                case "课程查询":
                    RxActivityTool.skipActivity(mContext, AllCoursesActivity.class);
                    break;
                case "我要蹭课":
                    //RxActivityTool.skipActivity(mContext, CourseStuScoreActivity.class);
                    RxActivityTool.skipActivity(mContext, AuditClassActivity.class);

//                    Observable.just(mDbHelper.querySelectedScore(user.getUserId()))
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(score ->
//                                    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("学分查询")
//                                    .setContentText("已选学分: " + score)
//                                    .show());
                    break;
                case "已蹭课程":
                    RxActivityTool.skipActivity(mContext, SelectedCourseListActivity.class);
                    break;
            }
        });
        idRvHome.setLayoutManager(new GridLayoutManager(mContext, 3));
        idRvHome.setAdapter(homeClassfiyAdapter);

        List<View> viewList = new ArrayList<>();
        setUPMarqueeView(viewList, 3);
        idRvvmHome.setViews(viewList);


        homeListAdapter = new HomeListAdapter(mySubjectList, mContext);
        idRvHomeList.setLayoutManager(new LinearLayoutManager(mContext));
        homeListAdapter.setIRVOnItemListener((mySubject, position) -> showCourseDetail(mySubject));
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
                                        getCourseList();
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
        idRvHomeList.setAdapter(homeListAdapter);

    }

    @Override
    public void onResume() {
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

    private void setUPMarqueeView(List<View> views, int size) {
        for (int i = 0; i < size; i++) {
            //设置滚动的单个布局
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_view, null);
            RelativeLayout rl1 = moreView.findViewById(R.id.rl);
            RelativeLayout rl2 = moreView.findViewById(R.id.rl2);
            //初始化布局的控件
            TextView titleTv1 = moreView.findViewById(R.id.title_tv1);
            TextView tv1 = moreView.findViewById(R.id.tv1);
            titleTv1.setText("公告");
            tv1.setText("新闻公告");

            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            //初始化布局的控件
            TextView titleTv2 = moreView.findViewById(R.id.title_tv2);
            TextView tv2 = moreView.findViewById(R.id.tv2);
            titleTv2.setText("咨询");
            tv2.setText(i + "==校园实用咨询");

            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            //添加到循环滚动数组里面去
            views.add(moreView);
        }
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
                    //只要是课程的名称和授课教师名字通过拼接(加上courseId)，相同我们就认为是同一门课程
                    if (subjectList.size() == 0) {
                        idRvHomeList.setVisibility(View.GONE);
                        idTvNoData.setText("暂无推荐信息，请联系管理员添加课程！！！");
                        idTvNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (mySubjectList.size() > 0) {
                            mySubjectList.clear();
                        }
                        mySubjectList.addAll(subjectList);
                        homeListAdapter.notifyDataSetChanged();
                        idTvNoData.setVisibility(View.GONE);
                        idRvHomeList.setVisibility(View.VISIBLE);
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
        ImageView id_iv_course_stu_num = dialogView.findViewById(R.id.id_iv_course_stu_num);  // 人数图标
        ImageView id_iv_course_addr = dialogView.findViewById(R.id.id_iv_course_addr);  // 授课地点图标
        View id_v_line_course_addr = dialogView.findViewById(R.id.id_v_line_course_addr);  // 授课地点图标
        View id_v_line_course_stu_num = dialogView.findViewById(R.id.id_v_line_course_stu_num);
        // 隐藏地点
        id_v_line_course_addr.setVisibility(View.GONE);
        id_iv_course_addr.setVisibility(View.GONE);
        idTvCourseRoom.setVisibility(View.GONE);

        // 隐藏人数
        idTvCourseStuNum.setVisibility(View.GONE);
        id_iv_course_stu_num.setVisibility(View.GONE);
        id_v_line_course_stu_num.setVisibility(View.GONE);
        int bgColor = mContext.getResources().getColor(R.color.tb_blue3);
        RxTextTool.getBuilder("课程名称：").append(mySubject.getName()).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("授课教师：").append(mySubject.getTeacher()).setForegroundColor(bgColor).into(idTvCourseTeacher);
        //RxTextTool.getBuilder("授课地点：").append(mySubject.getRoom()).setForegroundColor(bgColor).into(idTvCourseRoom);
        //RxTextTool.getBuilder("蹭课人数：").append(mySubject.getCourseStuApplications() + " (" + mySubject.getCourseStuNum() + ")").setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("课程学分：").append(String.valueOf(mySubject.getCourseScore())).setForegroundColor(bgColor).into(idTvCourseScore);

        new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }

    @OnClick(R.id.id_tv_all_course)
    public void onClick() {
        //查看更多
        RxActivityTool.skipActivity(mContext, AllCoursesActivity.class);
    }
}