package com.bs.coursehelper.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * fragment的基类
 */

public abstract class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 上下文生命周期
     */
    protected BaseFragment mFragment;
    protected BaseActivity mActivity;
    protected Context mContext;

    /**
     * 内容view
     */
    protected View mContentView;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragment = this;
        mActivity = (BaseActivity) mFragment.getActivity();
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutResId(), container, false);
        //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
        unbinder = ButterKnife.bind(this, mContentView);
        //初始化view
        initView();
        initData();
        initListener();
        return mContentView;
    }


    /**
     * 获取要绑定的layoutId
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * 初始化一些view操作
     */
    protected void initView() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化监听
     */
    protected void initListener() {

    }

    /**
     * onDestroyView中进行解绑操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
