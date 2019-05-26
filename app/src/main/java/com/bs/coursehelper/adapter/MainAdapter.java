package com.bs.coursehelper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bs.coursehelper.base.BaseFragment;

import java.util.List;

/**
 * 首页的fragment适配器
 *
 */

public class MainAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments;

    public MainAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
