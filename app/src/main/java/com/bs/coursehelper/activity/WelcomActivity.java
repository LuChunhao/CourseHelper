package com.bs.coursehelper.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.coursehelper.R;
import com.bs.coursehelper.adapter.WelcomeAdapter;
import com.bs.coursehelper.base.BaseActivity;
import com.jaeger.library.StatusBarUtil;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.rd.draw.data.Orientation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 欢迎页面
 */
public class WelcomActivity extends BaseActivity {

    @BindView(R.id.id_vp)
    ViewPager idVp;
    @BindView(R.id.id_piv)
    PageIndicatorView idPiv;
    private List<View> guideViewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        idPiv.setAnimationType(AnimationType.DROP);
        idPiv.setOrientation(Orientation.HORIZONTAL);
    }

    @Override
    protected void initParam(Bundle savedInstanceState) {
        super.initParam(savedInstanceState);
        StatusBarUtil.hideFakeStatusBarView(mActivity);
    }

    @Override
    protected void initData() {
        guideViewList = new ArrayList<>();
        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.int_welcome);
        int len = typedArray.length();
        for (int i = 0; i < len; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_vp_welcome, null);
            ((ImageView) view.findViewById(R.id.id_iv_welecome)).setImageResource(typedArray.getResourceId(i, 0));
            TextView idTvEnter = view.findViewById(R.id.id_tv_enter);
            idTvEnter.setVisibility(View.GONE);
            if (i == len - 1) {
                idTvEnter.setVisibility(View.VISIBLE);
                idTvEnter.setOnClickListener(view1 -> {
                    mActivity.startActivity(new Intent(mContext, LoginActivity.class));
                    mActivity.finish();
                });
            }
            guideViewList.add(view);
        }
        idPiv.setCount(len);
        WelcomeAdapter welcomeAdapter = new WelcomeAdapter(guideViewList);
        idVp.setAdapter(welcomeAdapter);
        idVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == len - 1) {
                    idPiv.setVisibility(View.GONE);
                } else {
                    idPiv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        idVp.setCurrentItem(0);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcom;
    }
}
