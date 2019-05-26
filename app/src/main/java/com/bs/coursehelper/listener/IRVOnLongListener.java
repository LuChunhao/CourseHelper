package com.bs.coursehelper.listener;

/**
 * RecycleView的长按事件
 */

public interface IRVOnLongListener<T> {

    /***
     * 返回点击的索引和对象
     *
     * @param t
     * @param position
     */
    void onLongClick(T t, int position);
}
