package com.bs.coursehelper.bean;

/**
 * 教师对应的课程，简单来说就是 一个教师只能发布一门课程，然后管理员来排课， 切记 教师不能删除课程， 只要管理员可以
 *
 *      说白了，教师能修改自己发布的课程，但是呢不能删除，想删除的话，就让管理员不发布（复杂的话 就加上一个关闭的操作，教师自己关闭）
 *
 */

public class CourseTeacherBean {

    /**
     * 课程id
     *
     */
    private int id;

    /**
     * 对应的教师id  其实也就是用户id， 我们在发布课程信息的时候，可以转换为User
     *
     */
    private User teacher;

    /**
     * 课程的名称
     *
     */
    private String courseName;

    /**
     * 课程的最大人数
     *
     */
    private int courseMax;

    /**
     * 学生的申请人数
     *
     */
    private int courseStuApplications;

    /**
     * 课程的学时 也就是多少个课时
     *
     */
    private int courseNum;

    /**
     * 课程的学分
     *
     */
    private float courseScore;

    /**
     * 上课的节数
     *
     */
    private int courseStep;

    /**
     * 是否允许蹭课，1 允许 0 不允许
     */
    private int isAllowCengKe;

    public int getIsAllowCengKe() {
        return isAllowCengKe;
    }

    public void setIsAllowCengKe(int isAllowCengKe) {
        this.isAllowCengKe = isAllowCengKe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseMax() {
        return courseMax;
    }

    public void setCourseMax(int courseMax) {
        this.courseMax = courseMax;
    }

    public int getCourseStuApplications() {
        return courseStuApplications;
    }

    public void setCourseStuApplications(int courseStuApplications) {
        this.courseStuApplications = courseStuApplications;
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


    public int getCourseStep() {
        return courseStep;
    }

    public void setCourseStep(int courseStep) {
        this.courseStep = courseStep;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "CourseTeacherBean{" +
                "id=" + id +
                ", teacher=" + teacher +
                ", courseName='" + courseName + '\'' +
                ", courseMax=" + courseMax +
                ", courseStuApplications=" + courseStuApplications +
                ", courseNum=" + courseNum +
                ", courseScore=" + courseScore +
                ", courseStep=" + courseStep +
                '}';
    }
}
