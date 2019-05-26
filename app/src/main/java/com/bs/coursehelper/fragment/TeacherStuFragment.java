package com.bs.coursehelper.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.adapter.TeacherStudentListAdapter;
import com.bs.coursehelper.base.BaseFragment;
import com.bs.coursehelper.bean.CourseUserBean;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.bs.coursehelper.widget.MSeekBar;
import com.google.gson.Gson;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxDataTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxui.view.RxTitle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 教师 只能查看和评分  不能删除学生的报名   管理员可以删除， 初次之外，教师评分需要最后一周才可以的，这里我们为了方便测试 ，每次都已评分，
 */
public class TeacherStuFragment extends BaseFragment {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_rv_stu_list)
    RecyclerView idRvStuList;
    @BindView(R.id.id_tv_no_data)
    TextView idTvNoData;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    private TeacherStudentListAdapter studentListAdapter;
    private List<User> mUserList;

    private User teacher;
    private int markIndex = -1;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_teacher_stu;
    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setPadding(0, RxBarTool.getStatusBarHeight(mContext), 0, 0);
        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected void initData() {
        super.initData();
        String userInfo = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        if (!TextUtils.isEmpty(userInfo)) {
            teacher = new Gson().fromJson(userInfo, User.class);
        }
        mDbHelper = DbHelper.getInstance();
        mUserList = new ArrayList<>();
        studentListAdapter = new TeacherStudentListAdapter(mUserList, mContext);
        idRvStuList.setLayoutManager(new LinearLayoutManager(mContext));
        studentListAdapter.setIRVOnItemListener((user, position) -> showCourseDetail(user));
        studentListAdapter.setIRVOnLongListener((user, position) -> {
            //长按弹出评分
            showStuCourseScore(user);
        });

        idRvStuList.setAdapter(studentListAdapter);
        mSweetAlertDialog.show();
        getUserList();
    }

    /**
     * 获取用户的列表
     */
    private void getUserList() {
        Observable.just(mDbHelper.queryUsersByCourseId(teacher.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userList -> {
                    Log.i(TAG, "getUserList: userList.size()===" + userList.size());
                    if (userList.size() == 0) {
                        idRvStuList.setVisibility(View.GONE);
                        idTvNoData.setText("暂无学生信息！！！");
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
        CourseUserBean courseUserBean = user.getUserCourses().get(0);
        sbName.append(courseUserBean.getName());
        sbName.append("(");
        sbName.append(teacher.getUserName());
        sbName.append(") ");
        String name = sbName.toString();
        RxTextTool.getBuilder("本门课程：").append(RxDataTool.isEmpty(name) ? "暂未选课程" : name).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("学生姓名：").append(user.getUserName()).setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("学生成绩：").append(courseUserBean.getCourseMark() + " (" + courseUserBean.getCourseScore() + " )").setForegroundColor(bgColor).into(idTvCourseScore);

        new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .create().show();
    }


    /**
     * 显示学生的评分
     *
     * @param user
     */
    private void showStuCourseScore(User user) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_stu_score, null, false);
        TextView idTvStuScoreDesc = dialogView.findViewById(R.id.id_tv_stu_score_desc);
        MSeekBar idMsbStuScore = dialogView.findViewById(R.id.id_msb_stu_score);
        float courseScore = user.getUserCourses().get(0).getCourseScore();
        float[] stuScore = new float[1];
        idMsbStuScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BigDecimal bigDecimal= new BigDecimal(courseScore * progress / 100f);
                String stuScoreStr = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                stuScore[0] = Float.parseFloat(stuScoreStr);
                RxTextTool.getBuilder(user.getUserName()).setForegroundColor(mContext.getResources().getColor(R.color.tb_blue3))
                        .append(" 分数：")
                        .append(stuScoreStr).setForegroundColor(mContext.getResources().getColor(R.color.tb_blue3))
                        .setProportion(1.8f)
                        .into(idTvStuScoreDesc);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setPositiveButton("评分", (dialogInterface, i) -> {
                    CourseUserBean courseUserBean = user.getUserCourses().get(0);
                    Log.i(TAG, "showStuCourseScore: courseUserBean==" + courseUserBean.toString() + "===" + stuScore[0]);
                    Observable.just(mDbHelper.updateCourseUserMark(courseUserBean.getMidId(), courseUserBean.getId(), user.getUserId(), stuScore[0]))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                dialogInterface.dismiss();
                                if (result >= 0) {
                                    getUserList();
                                    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("评分成功！!")
                                            .show();
                                } else {
                                    new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("评分失败!")
                                            .show();
                                }
                            });
                })
                .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss())
                .create().show();
    }
}
