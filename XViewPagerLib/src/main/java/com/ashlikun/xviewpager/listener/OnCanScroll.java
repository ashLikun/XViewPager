package com.ashlikun.xviewpager.listener;

import android.view.View;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/7/7　16:15
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager 的 canScroll
 */
public interface OnCanScroll {
    /**
     * ViewPager 的 canScroll
     *
     * @param v
     * @param checkV
     * @param dx
     * @param x
     * @param y
     * @return
     */
    public boolean canScroll(View v, boolean checkV, int dx, int x, int y);
}
