package com.bs.coursehelper.bean;

/**
 */

public class CourseUserBean {

    /**
     * 课程的id 或者 用户的id
     *
     */
    private int id;
    /**
     * 课程的名称 或者 用户的名字
     *
     */
    private String name;

    /**
     * 课程对应的教师 或者 用户的名字
     *
     */
    private String teacher;

    /**
     * 课程的数量 或者 用户的数量
     *
     */
    private int num;

    /**
     * 中间表的id
     *
     */
    private int midId;

    /**
     * 教师在本门课程的评分
     *
     */
    private float courseMark;

    /**
     * 课程学分
     *
     */
    private float courseScore;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getCourseMark() {
        return courseMark;
    }

    public void setCourseMark(float courseMark) {
        this.courseMark = courseMark;
    }

    public int getMidId() {
        return midId;
    }

    public void setMidId(int midId) {
        this.midId = midId;
    }

    public float getCourseScore() {
        return courseScore;
    }

    public void setCourseScore(float courseScore) {
        this.courseScore = courseScore;
    }

    @Override
    public String toString() {
        return "CourseUserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", num=" + num +
                ", midId=" + midId +
                ", courseMark=" + courseMark +
                ", courseScore=" + courseScore +
                '}';
    }
}
