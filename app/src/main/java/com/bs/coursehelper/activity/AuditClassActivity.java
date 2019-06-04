package com.bs.coursehelper.activity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.bean.CourseTeacherBean;
import com.bs.coursehelper.bean.MySubject;
import com.bs.coursehelper.bean.User;
import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.google.gson.Gson;
import com.vondear.rxtool.RxConstTool;
import com.vondear.rxtool.RxDataTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxTitle;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleSupport;
import com.zhuangfei.timetable.view.WeekView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuditClassActivity extends BaseActivity {
    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;
    @BindView(R.id.id_wv_course_list)
    WeekView idWvCourseList;
    @BindView(R.id.id_tv_course_list)
    TimetableView idTvCourseList;
    @BindView(R.id.et_search)
    EditText et_search;

    private DbHelper mDbHelper;
    private SweetAlertDialog mSweetAlertDialog;

    //记录切换的周次，不一定是当前周
    private int targetWeek = -1;

    private User user;


    private List<CourseTeacherBean> mCourseTeacherBeanList;
    private String[] courseTitles;
    private int selectedCourseIndex = -1;
    private int selectScheduleIndex = -1;   // 选择要查看的课程详情的index
    private List<Schedule> selectScheduleList = new ArrayList<>();    // 本周选中表格中某一项的内容集合
    private List<MySubject> subjects = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initData() {
        String userInfoStr = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
        user = new Gson().fromJson(userInfoStr, User.class);
        mCourseTeacherBeanList = new ArrayList<>();
        mDbHelper = DbHelper.getInstance();
        mSweetAlertDialog.show();
        getCourseList("");
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = idWvCourseList.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        targetWeek = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, idTvCourseList.curWeek() - 1,
                (dialogInterface, i) -> targetWeek = i);
        builder.setPositiveButton("设置为当前周", (dialog, which) -> {
            if (targetWeek != -1) {
                idWvCourseList.curWeek(targetWeek + 1).updateView();
                idTvCourseList.changeWeekForce(targetWeek + 1);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    protected void initListener() {
        et_search.setOnEditorActionListener((view, actionId, keyEvent) -> {
            String input = view.getText().toString();

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getCourseList(input);
                return true;
            }
            return false;
        });
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    protected void onStart() {
        super.onStart();
        idTvCourseList.onDateBuildListener().onHighLight();
    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setLeftFinish(mActivity);
        idRtTitle.setRightOnClickListener(view -> showPopmenu());

        mSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText("Loading");
        mSweetAlertDialog.setCancelable(false);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_course_list;
    }


    /**
     * 显示弹出菜单
     */
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(mContext, idRtTitle.getIvRight());
        popup.getMenuInflater().inflate(R.menu.popup_course_list_more, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_01:
                    //显示周次选择
                    showWeekView();
                    break;
                case R.id.item_02:
                    //隐藏周次选择
                    hideWeekView();
                    break;
                case R.id.item_03:
                    //显示节次时间
                    showTime();
                    break;
                case R.id.item_04:
                    //隐藏节次时间
                    hideTime();
                    break;
                case R.id.item_05:
                    //显示非本周课程
                    showNonThisWeek();
                    break;
                case R.id.item_06:
                    //隐藏非本周课程
                    hideNonThisWeek();
                    break;
                default:
                    break;
            }
            return true;
        });

        popup.show();
    }


    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    protected void showTime() {
        String[] times = new String[]{
                "8:00", "10:00", "14:00", "16:00"
        };
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) idTvCourseList.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        idTvCourseList.updateSlideView();
    }

    /**
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        idTvCourseList.callback((ISchedule.OnSlideBuildListener) null);
        idTvCourseList.updateSlideView();
    }

    /**
     * 显示WeekView
     */
    protected void showWeekView() {
        idWvCourseList.isShow(true);
    }

    /**
     * 隐藏WeekView
     */
    protected void hideWeekView() {
        idWvCourseList.isShow(false);
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        idTvCourseList.isShowNotCurWeek(false).updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        idTvCourseList.isShowNotCurWeek(true).updateView();
    }


    /**
     * 排课新的课程信息
     *
     * @param weekDiff
     * @param day
     * @param start
     * @param courseTeacherBean
     */
    private void showAddCourse(int weekDiff, int day, int start, CourseTeacherBean courseTeacherBean) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_course, null, false);
        EditText idEtCourseName = dialogView.findViewById(R.id.id_et_course_name);
        EditText idEtCourseTeacherName = dialogView.findViewById(R.id.id_et_course_teacher_name);
        EditText idEtCourseAddr = dialogView.findViewById(R.id.id_et_course_addr);
        EditText idEtCourseStuNum = dialogView.findViewById(R.id.id_et_course_stu_num);
        EditText idEtCourseNum = dialogView.findViewById(R.id.id_et_course_num);
        EditText idEtCourseScore = dialogView.findViewById(R.id.id_et_course_score);

        idEtCourseName.setEnabled(false);
        idEtCourseTeacherName.setEnabled(false);
        idEtCourseStuNum.setEnabled(false);
        idEtCourseNum.setEnabled(false);
        idEtCourseScore.setEnabled(false);

        idEtCourseName.setText(courseTeacherBean.getCourseName());
        idEtCourseTeacherName.setText(courseTeacherBean.getTeacher().getUserName());
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
                String courseTeacherName = idEtCourseTeacherName.getText().toString().trim();
                String courseAddr = idEtCourseAddr.getText().toString().trim();
                String courseStuNum = idEtCourseStuNum.getText().toString().trim();
                String courseNum = idEtCourseNum.getText().toString().trim();
                String courseScore = idEtCourseScore.getText().toString().trim();
                if (RxDataTool.isEmpty(courseName)) {
                    RxToast.normal("课程名称不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseName);
                    return;
                }
                if (RxDataTool.isEmpty(courseTeacherName)) {
                    RxToast.normal("授课教师不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseTeacherName);
                    return;
                }
                if (RxDataTool.isEmpty(courseAddr)) {
                    RxToast.normal("授课地点不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseAddr);
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

                int step = Integer.parseInt(courseNum);
                if (RxDataTool.isEmpty(courseNum)) {
                    RxToast.normal("课程节数不能为空！！！");
                    mActivity.handleEtEmpty(idEtCourseNum);
                    return;
                } else if (!RxDataTool.isInteger(courseNum) || courseNum.startsWith("0")) {
                    RxToast.normal("课程节数输入不合法！！！");
                    mActivity.handleEtEmpty(idEtCourseNum);
                    return;
                } else if (start + step > 10) {
                    RxToast.normal("当天的课程可用节数不够设置！！！");
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

                MySubject mySubject = new MySubject();
                mySubject.setName(courseName);
                mySubject.setTeacher(courseTeacherName);
                mySubject.setRoom(courseAddr);
                mySubject.setCourseStuNum(Integer.parseInt(courseStuNum));
                mySubject.setStart(start);
                mySubject.setDay(day);
                mySubject.setStep(step);
                mySubject.setCourseId(courseTeacherBean.getId());
                mySubject.setCourseScore(Float.parseFloat(courseScore));
                List<Integer> weekList = mySubject.getWeekList();
                if (weekList == null) {
                    weekList = new ArrayList<>();
                }
                //添加新的周数
                weekList.add(weekDiff);
                //排序下，以防我们滑动或者选择添加后面的周数
                Collections.sort(weekList);
                mySubject.setWeekList(weekList);

                Observable.just(mDbHelper.insertCourse(mySubject))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (aLong >= 0) {
                                RxToast.normal("新增课程成功！！！");
                                getCourseList("");
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
     * 获取课程的列表
     */
    private void getCourseList(String key) {
        Observable.just(mDbHelper.queryCoursesByKey(key))
                .map(mySubjectList -> {
                    Log.d(TAG, "mySubjectList: " + new Gson().toJson(mySubjectList));
                    if (mCourseTeacherBeanList.size() == 0) {
                        // 教师新建的课程
                        mCourseTeacherBeanList.addAll(mDbHelper.queryAllCourseTeachersAllowed());
                        Log.d(TAG, "mCourseTeacherBeanList: " + new Gson().toJson(mCourseTeacherBeanList));
                        courseTitles = new String[mCourseTeacherBeanList.size()];
                        for (int i = 0; i < mCourseTeacherBeanList.size(); i++) {
                            CourseTeacherBean courseTeacherBean = mCourseTeacherBeanList.get(i);
                            courseTitles[i] = courseTeacherBean.getCourseName() + "(" + courseTeacherBean.getTeacher().getUserName() + ")";
                        }

                        subjects.clear();
                        for (MySubject subject : mySubjectList) {
                            for (CourseTeacherBean bean : mCourseTeacherBeanList) {
                                // 教师名相同，课程名相同，且允许蹭课
                                if (subject.getName().equals(bean.getCourseName()) && bean.getIsAllowCengKe() == 1 && subject.getTeacher().equals(bean.getTeacher().getUserName())) {
                                    subjects.add(subject);
                                    break;
                                }
                            }
                        }
                        mySubjectList.clear();
                        mySubjectList.addAll(subjects);
                    }
                    return mySubjectList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubjectList -> {
                    //第一周我们为9月3号,所以从9月3号 开始计算
                    long timeDiff = RxTimeTool.getIntervalByNow(Constants.FIRST_WEEK_DATE, RxConstTool.TimeUnit.MSEC,
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
                    int weekDiff = (int) (timeDiff / (1000 * 60 * 60 * 24 * 7)) + 1;
                    //设置周次选择属性
                    idWvCourseList.source(mySubjectList)
                            .curWeek(weekDiff)
                            .callback(week -> {
                                int cur = idTvCourseList.curWeek();
                                //更新切换后的日期，从当前周cur->切换的周week
                                idTvCourseList.onDateBuildListener().onUpdateDate(cur, week);
                                idTvCourseList.changeWeekOnly(week);
                            })
                            .callback(() -> onWeekLeftLayoutClicked())
                            .showView();

                    idTvCourseList.source(mySubjectList)
                            .curWeek(weekDiff)
                            .curTerm("大一下学期")
                            .itemHeight(120)
                            .isShowNotCurWeek(true)
                            .maxSlideItem(4)//按照一天10节课来计算，选修课 会根据其他的课程来安排的
                            .monthWidthDp(50)
                            //透明度
                            //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                            //透明度范围为0->1，0为全透明，1为不透明
//                          .alpha(0.1f, 0.1f, 0.6f)
                            .callback((v, scheduleList, day, start) -> {
                                Log.i(TAG, "getCourseList: day===" + day + "===start===" + start);
                                int week = getCurWeek();
                                Log.i(TAG, "getCourseList: week==" + week + "===schedule的个数==" + scheduleList.size());
                                selectScheduleList.clear();
                                for (Schedule schedule : scheduleList) {
                                    if (null != schedule.getWeekList() && schedule.getWeekList().size() > 0 && schedule.getWeekList().get(0) == week) {
                                        selectScheduleList.add(schedule);
                                    }
                                }
                                //this.selectScheduleList = scheduleList;
                                if (scheduleList.size() > 0) {
                                    showScheduleList(scheduleList, day, start);
                                }


                            })
                            .callback(curWeek -> {
                                idRtTitle.setTitle("第" + curWeek + "周");
                            })
                            //旗标布局点击监听
                            .callback((day, start) -> {
                                idTvCourseList.hideFlaglayout();
//                                Log.i(TAG, "点击了旗标:周" + (day + 1) + ",第" + start + "节");
//                                //添加新课程
//                                showSelectCourse(day + 1, start);
                                new SweetAlertDialog(mContext)
                                        .setTitleText("提醒")
                                        .setContentText("您没有权限编辑课程表")
                                        .show();

                            })
                            .showView();

                    mSweetAlertDialog.dismissWithAnimation();

                });
    }


    /**
     * 选择要查看的课程
     */
    private void showScheduleList(List<Schedule> scheduleList, int day, int start) {
        List<Schedule> curweekScheduleList = new ArrayList<>();
        List<String> ScheduleNameList = new ArrayList<>();
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule schedule = scheduleList.get(i);
            // 只查看本周
//            if (schedule.getWeekList() != null && schedule.getWeekList().size() > 0 && schedule.getWeekList().get(0) == getCurWeek()) { // 排除不是本周的课程
//                //ScheduleNameList[i] = schedule.getName() + "（" + schedule.getTeacher() + "）";
//                ScheduleNameList.add(schedule.getName() + "（" + schedule.getTeacher() + "）");
//                curweekScheduleList.add(schedule);
//            }

            // 查看所有
            ScheduleNameList.add(schedule.getName() + "（第" + schedule.getWeekList().get(0) + "周）");
            curweekScheduleList.add(schedule);
            Log.i(TAG, "getCourseList: schedule===" + schedule.toString());
        }
        //ScheduleNameList[scheduleList.size()] = "发布新课程";
        String[] scheduleNames = new String[ScheduleNameList.size()];
        ScheduleNameList.toArray(scheduleNames);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请选择要查看的课程").setSingleChoiceItems(scheduleNames, -1,
                (dialogInterface, i) -> selectScheduleIndex = i)
                .setPositiveButton("确定", (dialog, which) -> {
                    Log.i(TAG, "selectScheduleIndex==" + selectScheduleIndex);
                    if (selectScheduleIndex == -1) {
                        RxToast.normal("请先选择要发布的课程");
                        return;
                    }
                    showCourseDetail(curweekScheduleList.get(selectScheduleIndex));
                })
                .setOnDismissListener(dialogInterface -> selectScheduleIndex = -1)
                .setNegativeButton("取消", null).create().show();
    }

    private int getCurWeek() {
        String title = idRtTitle.getTitle();
        return Integer.parseInt(idRtTitle.getTitle().substring(title.indexOf("第") + 1, title.indexOf("周")));
    }

    /**
     * 课程是否已经报过
     *
     * @param userId
     * @param courseId
     * @return
     */
    private int isApplyCoures(int userId, int courseId) {
        int isApply = DbHelper.getInstance().queryCourseIsApply(userId, courseId);
        return isApply;
    }

    /**
     * 显示课程的详情
     *
     * @param schedule
     */
    private void showCourseDetail(Schedule schedule) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_course_detail, null, false);
        TextView idTvCourseName = dialogView.findViewById(R.id.id_tv_course_desc);
        TextView idTvCourseTeacher = dialogView.findViewById(R.id.id_tv_course_teacher_desc);
        TextView idTvCourseRoom = dialogView.findViewById(R.id.id_tv_course_addr_desc);
        TextView idTvCourseDate = dialogView.findViewById(R.id.id_tv_course_date_desc);
        TextView idTvCourseStuNum = dialogView.findViewById(R.id.id_tv_course_stu_num_desc);
        TextView idTvCourseNum = dialogView.findViewById(R.id.id_tv_course_num_desc);
        TextView idTvCourseScore = dialogView.findViewById(R.id.id_tv_course_score_desc);
        Map<String, Object> extras = schedule.getExtras();
        int bgColor = mContext.getResources().getColor(R.color.tb_blue3);
        RxTextTool.getBuilder("课程名称：").append(schedule.getName()).setForegroundColor(bgColor).into(idTvCourseName);
        RxTextTool.getBuilder("授课教师：").append(schedule.getTeacher()).setForegroundColor(bgColor).into(idTvCourseTeacher);
        RxTextTool.getBuilder("授课地点：").append(schedule.getRoom()).setForegroundColor(bgColor).into(idTvCourseRoom);
        RxTextTool.getBuilder("蹭课人数：").append(extras.get(MySubject.COURSE_STU_APPLICATIONS) + " (" + extras.get(MySubject.COURSE_STU_NUM) + ")").setForegroundColor(bgColor).into(idTvCourseStuNum);
        RxTextTool.getBuilder("授课时间：").append("第" + schedule.getWeekList().get(0) + "周，周" + schedule.getDay() + ", 第" + schedule.getStart() + "节开始").setForegroundColor(bgColor).into(idTvCourseDate);
        RxTextTool.getBuilder("课程节数：").append(schedule.getStep() + "节").setForegroundColor(bgColor).into(idTvCourseNum);
        RxTextTool.getBuilder("课程学分：").append(String.valueOf(extras.get(MySubject.COURSE_SCORE))).setForegroundColor(bgColor).into(idTvCourseScore);

        new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("蹭课", (dialog, i) -> {
                    // TODO
                    //mSweetAlertDialog.show();

                    int courseId = (int) schedule.getExtras().get(MySubject.ID);
                    String teacher = schedule.getTeacher();
                    //Toast.makeText(this, "id==" + courseId, Toast.LENGTH_SHORT).show();

//                    courseId = 0;
//                    for (MySubject subject : subjects) {
//                        if (subject.getTeacher().equals(schedule.getTeacher()) && subject.getName().equals(schedule.getName())
//                                && subject.getWeekList().get(0) == schedule.getWeekList().get(0) && subject.getDay() == schedule.getDay()
//                                && subject.getStart() == schedule.getStart()) {
//                            courseId = subject.getCourseId();
//                            break;
//                        }
//                    }

                    Log.d(TAG, "showCourseDetail: " + isApplyCoures(user.getUserId(), courseId));

                    Observable.just(isApplyCoures(user.getUserId(), courseId))
                            .map(aLong -> {
                                long isSucess = -1;
                                if (aLong == 0) {
                                    try {
                                        isSucess = mDbHelper.insertCourseUser(courseId, user.getUserId(), teacher);
                                    } catch (SQLiteException sqLiteException) {
                                        isSucess = -1;
                                        sqLiteException.printStackTrace();
                                    }
                                } else if (aLong == 1) {
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
                                                    .setTitleText("蹭课成功!")
                                                    .show();
                                            //getCourseList();
                                        } else {
                                            if (aLong == -2) {
                                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("亲，不能重复蹭课！!")
                                                        .show();
                                            } else {
                                                new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("报名失败!")
                                                        .show();
                                            }

                                        }
                                    }
                            );
                })
                .create().show();
    }
}
