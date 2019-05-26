package com.bs.coursehelper.activity;

import android.os.Handler;
import android.text.TextUtils;

import com.bs.coursehelper.Constants;
import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.bs.coursehelper.utils.SPUtil;
import com.vondear.rxtool.RxActivityTool;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void initData() {

        new Handler().postDelayed(() -> {
            String userInfo = (String) SPUtil.getInstanse().getParam(Constants.USER_LOCAL_INFO, "");
            if (!TextUtils.isEmpty(userInfo)) {
                RxActivityTool.skipActivity(mContext, MainActivity.class);
            } else {
                //进入首次登录引导页面
                RxActivityTool.skipActivity(mContext, WelcomActivity.class);
            }
            mActivity.finish();
        }, 3000);

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }
}
