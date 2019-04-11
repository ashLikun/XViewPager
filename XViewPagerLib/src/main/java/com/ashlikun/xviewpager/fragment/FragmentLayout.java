package com.ashlikun.xviewpager.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/10　17:02
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：用于fragment一次加载一个的，加载过了不用重新加载
 * 利用hind和show
 * 生命周期和ViewPager使用的一样
 * 必须依赖 {@link FragmentPagerAdapter}
 */
public class FragmentLayout extends FrameLayout {
    FragmentPagerAdapter adapter;
    int currentPosition = 0;

    public FragmentLayout(@NonNull Context context) {
        this(context, null);
    }

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

    }

    public int getItemCount() {
        return adapter != null ? adapter.getCacheSize() : 0;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(FragmentPagerAdapter adapter) {
        //清空已有的
        if (this.adapter != null) {
            for (int i = 0; i < getItemCount(); i++) {
                Fragment f = adapter.getCacheFragment(i);
                if (f != null) {
                    this.adapter.destroyItem(this, i, f);
                    this.adapter.finishUpdate(this);
                }
            }
        }
        //必须使用缓存
        adapter.isCache = true;
        this.adapter = adapter;
        showFragment(currentPosition);

    }

    /**
     * 显示一个fragment
     *
     * @param position
     */
    private void showFragment(int position) {
        Fragment f = adapter.getCacheFragment(position);
        FragmentTransaction ft = adapter.getFragmentManager().beginTransaction();
        //隐藏其他的
        for (int i = 0; i < getItemCount(); i++) {
            Fragment cacheFragment = adapter.getCacheFragment(i);
            if (cacheFragment != null) {
                ft.hide(cacheFragment);
            }
        }
        //是否已经添加了
        if (f != null) {
            adapter.setPrimaryItem(this, position, f);
            ft.show(f).commitNowAllowingStateLoss();
        } else {
            if (getItemCount() > 0) {
                ft.commitNowAllowingStateLoss();
            }
            adapter.instantiateItem(this, position);
            f = adapter.getCacheFragment(position);
            adapter.setPrimaryItem(this, position, f);
            adapter.finishUpdate(this);
        }
    }

    /**
     * 隐藏一个fragment
     *
     * @param position
     */
    private void hindFragment(int position) {
        Fragment f = adapter.getCacheFragment(position);
        if (f != null) {
            //已经添加了
            f.setUserVisibleHint(false);
            adapter.getFragmentManager().beginTransaction().hide(f).commitNowAllowingStateLoss();
        }
    }

    /**
     * 设置当前显示的fragment
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (currentPosition != position) {
            currentPosition = position;
            showFragment(currentPosition);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
