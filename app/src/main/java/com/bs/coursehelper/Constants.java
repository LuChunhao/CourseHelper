package com.bs.coursehelper;

/**
 * 常量类
 */

public class Constants {

    /**
     * 是否是登录进入的，我们登录过，默认呢 是要存储账户信息的，所以只要退出我们就认为是第一次登录
     *
     */
    public static final String IS_FIRST_LOGIN = "isFirstLogin";

    /**
     * 用户的本地缓存信息
     *
     */
    public static final String USER_LOCAL_INFO = "userLocalInfo";

    /**
     * 课程的id
     *
     */
    public static final String COURSE_ID = "courseId";

    public static final String BASE_URL = "http://www.ch.com";

    /**
     * 注册用户的地址
     *
     */
    public static final String INSERT_USER_URL = BASE_URL + "/api/v1_0/user/add";

    /**
     * 根据用户姓名和学号 查询是否存在该用户
     *
     */
    public static final String IS_EXIST_USER_URL = BASE_URL + "/api/v1_0/user/exist";

}
