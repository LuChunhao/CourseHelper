package com.bs.coursehelper;

import android.app.Application;

import com.bs.coursehelper.db.DbHelper;
import com.bs.coursehelper.utils.SPUtil;
import com.vondear.rxtool.RxTool;

/**
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxTool.init(this);
        SPUtil.getInstanse().init(this);
        DbHelper.getInstance().init(this);
    }
}
