package com.bs.coursehelper.listener;

import android.view.View;

/**
 * RecycleView的item中的view对应的点击事件
 */

public interface IRVViewOnClickListener<T> {

    /***
     * 返回点击的索引和对象
     *
     * @param view
     * @param t
     * @param position
     */
    void onClick(View view, T t, int position);
}
