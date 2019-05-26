package com.bs.coursehelper.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 用户的信息
 *
 */

public class User implements Serializable {

    /**
     * 用户的姓名、密码、学号（教师工号）、性别、类型
     *
     */
    @SerializedName("id")
    private int userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_pwd")
    private String userPwd;
    @SerializedName("user_number")
    private String userNumber;
    @SerializedName("user_sex")
    private int userSex;  // 0 男  1  女
    /**
     * 用户的类型  0  学生  、1  管理员  2、教师
     *
     */
    @SerializedName("user_type")
    private int userType;

    /**
     * 用户的头像
     *
     */
    private String userHeadUrl;

    /**
     * 用户已经选修的课程数量
     *
     */
    private List<CourseUserBean> userCourses;
    /**
     * 已经选修的课程学分，  最大值为16
     */
    private float userCourseScore;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public List<CourseUserBean> getUserCourses() {
        return userCourses;
    }

    public void setUserCourses(List<CourseUserBean> userCourses) {
        this.userCourses = userCourses;
    }

    public float getUserCourseScore() {
        return userCourseScore;
    }

    public void setUserCourseScore(float userCourseScore) {
        this.userCourseScore = userCourseScore;
    }

    public String getUserHeadUrl() {
        return userHeadUrl;
    }

    public void setUserHeadUrl(String userHeadUrl) {
        this.userHeadUrl = userHeadUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userNumber='" + userNumber + '\'' +
                ", userSex=" + userSex +
                ", userType=" + userType +
                ", userHeadUrl='" + userHeadUrl + '\'' +
                ", userCourses=" + userCourses +
                ", userCourseScore=" + userCourseScore +
                '}';
    }
}
