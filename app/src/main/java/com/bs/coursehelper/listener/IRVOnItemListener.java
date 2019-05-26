package com.bs.coursehelper.listener;

/**
 * RecycleView的item点击事件
 */

public interface IRVOnItemListener<T> {

    /***
     * 返回点击的索引和对象
     *
     * @param t
     * @param position
     */
    void onItemClick(T t, int position);
}
