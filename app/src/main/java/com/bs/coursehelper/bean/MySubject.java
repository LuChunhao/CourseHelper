package com.bs.coursehelper.bean;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.List;

/**
 * 自定义实体类需要实现ScheduleEnable接口并实现getSchedule()
 *
 * @see ScheduleEnable#getSchedule()
 */
public class MySubject implements ScheduleEnable {

	public static final String COURSE_ID = "courseId";
	public static final String COURSE_STU_NUM="stuNum";
	public static final String COURSE_STU_APPLICATIONS="stuApplications";
	public static final String COURSE_NUM="num";
	public static final String COURSE_SCORE="score";
	public static final String COURSE_TERM="term";
	public static final String ID="id";

	private int id;


	/**
	 * 课程名
	 */
	private String name;

	/**
	 * 上课的时间  就是周几
	 *
	 */
	private String time;
	
	/**
	 * 教室
	 */
	private String room;
	
	/**
	 * 教师
	 */
	private String teacher;
	
	/**
	 * 第几周至第几周上
	 */
	private List<Integer> weekList;
	
	/**
	 * 开始上课的节次
	 */
	private int start;
	
	/**
	 * 上课节数
	 */
	private int step;
	
	/**
	 * 周几上
	 */
	private int day;
	
	private String term;

	/**
	 *  一个随机数，用于对应课程的颜色
	 */
	private int colorRandom = 0;

	/**
	 * 上课的最大人数
	 *
	 */
	private int courseStuNum;

	/**
	 * 学生的申请人数
	 *
	 */
	private int courseStuApplications;


	/**
	 * 课程的学时 也就是多少个课时，时间对应的都是每周几
	 *
	 */
	private int courseNum;

	/**
	 * 课程的学分
	 *
	 */
	private float courseScore;

	private int courseId;


	public MySubject() {
		// TODO Auto-generated constructor stub
	}


	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}

	public MySubject(int id, String name, String time, String room, String teacher, List<Integer> weekList, int start, int step, int day,
					 String term, int colorRandom, int courseStuNum, int courseStuApplications, int courseNum, float courseScore) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.room = room;
		this.teacher = teacher;
		this.weekList = weekList;
		this.start = start;
		this.step = step;
		this.day = day;
		this.term = term;
		this.colorRandom = colorRandom;
		this.courseStuNum = courseStuNum;
		this.courseStuApplications = courseStuApplications;
		this.courseNum = courseNum;
		this.courseScore = courseScore;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public void setWeekList(List<Integer> weekList) {
		this.weekList = weekList;
	}
	
	public List<Integer> getWeekList() {
		return weekList;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getColorRandom() {
		return colorRandom;
	}

	public void setColorRandom(int colorRandom) {
		this.colorRandom = colorRandom;
	}

	public int getCourseStuApplications() {
		return courseStuApplications;
	}

	public void setCourseStuApplications(int courseStuApplications) {
		this.courseStuApplications = courseStuApplications;
	}

	@Override
	public Schedule getSchedule() {
		Schedule schedule=new Schedule();
		schedule.setDay(getDay());
		schedule.setName(getName());
		schedule.setRoom(getRoom());
		schedule.setStart(getStart());
		schedule.setStep(getStep());
		schedule.setTeacher(getTeacher());
		schedule.setWeekList(getWeekList());
		schedule.setColorRandom(2);
		schedule.putExtras(ID, getId());
		schedule.putExtras(COURSE_ID, getCourseId());
		schedule.putExtras(COURSE_STU_NUM, getCourseStuNum());
		schedule.putExtras(COURSE_STU_APPLICATIONS, getCourseStuApplications());
		schedule.putExtras(COURSE_NUM, getCourseNum());
		schedule.putExtras(COURSE_SCORE, getCourseScore());
		schedule.putExtras(COURSE_TERM, getTerm());
		return schedule;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getCourseStuNum() {
		return courseStuNum;
	}

	public void setCourseStuNum(int courseStuNum) {
		this.courseStuNum = courseStuNum;
	}

	public int getCourseNum() {
		return courseNum;
	}

	public void setCourseNum(int courseNum) {
		this.courseNum = courseNum;
	}

	public float getCourseScore() {
		return courseScore;
	}

	public void setCourseScore(float courseScore) {
		this.courseScore = courseScore;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	@Override
	public String toString() {
		return "MySubject{" +
				"id=" + id +
				", name='" + name + '\'' +
				", time='" + time + '\'' +
				", room='" + room + '\'' +
				", teacher='" + teacher + '\'' +
				", weekList=" + weekList +
				", start=" + start +
				", step=" + step +
				", day=" + day +
				", term='" + term + '\'' +
				", colorRandom=" + colorRandom +
				", courseStuNum=" + courseStuNum +
				", courseStuApplications=" + courseStuApplications +
				", courseNum=" + courseNum +
				", courseScore=" + courseScore +
				", courseId=" + courseId +
				'}';
	}
}
