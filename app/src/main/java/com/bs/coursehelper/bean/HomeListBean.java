package com.bs.coursehelper.bean;

/**
 */

public class HomeListBean {
    /**
     * 首页的课程推荐列表信息
     *
     */
    private String teacherHeadUrl;
    private String teacherName;
    private String courseName;
    private int studentNum;
    private int studentApplications;


    public String getTeacherHeadUrl() {
        return teacherHeadUrl;
    }

    public void setTeacherHeadUrl(String teacherHeadUrl) {
        this.teacherHeadUrl = teacherHeadUrl;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public int getStudentApplications() {
        return studentApplications;
    }

    public void setStudentApplications(int studentApplications) {
        this.studentApplications = studentApplications;
    }

    @Override
    public String toString() {
        return "HomeListBean{" +
                "teacherHeadUrl='" + teacherHeadUrl + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", studentNum='" + studentNum + '\'' +
                ", studentApplications='" + studentApplications + '\'' +
                '}';
    }
}
