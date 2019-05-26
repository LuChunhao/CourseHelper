package com.bs.coursehelper.http.exception;

import com.bs.coursehelper.http.response.BaseResponseBean;
import com.google.gson.Gson;

/**
 * 异常处理
 *
 */

public class MyException extends IllegalStateException {

    private BaseResponseBean errorBean;

    public MyException(String s) {
        super(s);
        errorBean = new Gson().fromJson(s, BaseResponseBean.class);
    }

    public BaseResponseBean getErrorBean() {
        return errorBean;
    }
}
