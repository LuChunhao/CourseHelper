package com.bs.coursehelper.activity;

import android.os.Bundle;

import com.bs.coursehelper.R;
import com.bs.coursehelper.base.BaseActivity;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxui.view.RxTitle;

import butterknife.BindView;

public class PublishCourseActivity extends BaseActivity {

    @BindView(R.id.id_rt_title)
    RxTitle idRtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        idRtTitle.setLeftFinish(mActivity);
        idRtTitle.setRightOnClickListener(view -> RxActivityTool.skipActivity(mContext, SelectedCourseListActivity.class));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_publish_course;
    }
}
