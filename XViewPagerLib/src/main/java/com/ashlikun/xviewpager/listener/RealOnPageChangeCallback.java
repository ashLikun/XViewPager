package com.ashlikun.xviewpager.listener;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.ashlikun.xviewpager.view.BannerViewPager;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/3　10:48
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：实现翻页时候的真实位置回调给用户
 */
public class RealOnPageChangeCallback implements ViewPager.OnPageChangeListener {
    OnPageChangeListener callback;
    BannerViewPager bannerViewPager;
    private int mPreviousPosition = -1;

    public RealOnPageChangeCallback(OnPageChangeListener callback, BannerViewPager bannerViewPager) {
        this.callback = callback;
        this.bannerViewPager = bannerViewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition = bannerViewPager.getRealPosition(position);
        callback.onPageScrolled(realPosition,
                positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        int realPosition = bannerViewPager.getRealPosition(position);
        if (mPreviousPosition != realPosition) {
            mPreviousPosition = realPosition;
            callback.onPageSelected(realPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        callback.onPageScrollStateChanged(state);
    }
}
