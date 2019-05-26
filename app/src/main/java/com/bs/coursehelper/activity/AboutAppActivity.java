package com.bs.coursehelper.activity;

import android.os.Bundle;

import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.vondear.rxui.view.RxTitle;

import butterknife.BindView;

public class AboutAppActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setLeftFinish(mActivity);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about_app;
    }
}
