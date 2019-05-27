package com.bs.coursehelper.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.bs.coursehelper.utils.ShakeHelper;
import com.jaeger.library.StatusBarUtil;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.view.RxToast;

import butterknife.ButterKnife;

/**
 * Created by boys on 2018/10/11.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 上下文生命周期
     */
    protected Context mContext;
    protected BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mActivity = this;
        if (isTransparent()){
            StatusBarUtil.setTransparent(mActivity);
        }
        // 设置页面布局并且绑定数据
        if (getLayoutResId() != -1) {
            setContentView(getLayoutResId());
            //绑定初始化ButterKnife
            ButterKnife.bind(this);
        }
        initParam(savedInstanceState);
        //初始化view
        initView();
        initData();
        initListener();


        RxActivityTool.addActivity(mActivity);
    }

    /**
     * 是否透明
     *
     * @return
     */
    protected boolean isTransparent() {
        return false;
    }

    /**
     * 初始化参数
     *
     * @param savedInstanceState
     */
    protected void /**/initParam(Bundle savedInstanceState) {

    }

    /**
     * 初始化view
     */
    protected void initView(){

    }

    /**
     * 初始化data 也就是初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 获取要绑定的layoutId
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * 处理edittext的空
     *
     * @param editText
     */
    public void handleEtEmpty(EditText editText) {
        ShakeHelper.shake(editText);
        editText.setText("");
        editText.requestFocus();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
    }

    /**
     * 判断edittext的值是否为空
     *
     * @param editText
     */
    public boolean isEtEmpty(String content, String msg, EditText editText) {
        if (TextUtils.isEmpty(content)){
            RxToast.normal(msg);
            ShakeHelper.shake(editText);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxActivityTool.finishActivity(mActivity);
    }

    /**
     * 是否全屏
     *
     * @param activity
     * @return
     */
    public boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
}
