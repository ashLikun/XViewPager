package com.ashlikun.xviewpager.listener;

import androidx.viewpager.widget.ViewPager;

import com.ashlikun.xviewpager.adapter.BasePageAdapter;
import com.ashlikun.xviewpager.view.BannerViewPager;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/11/26　15:11
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：控制Banner不会滑动到第一个和最后一个
 */
public class ControlOnPageChangeCallback implements ViewPager.OnPageChangeListener {
    BannerViewPager bannerViewPager;

    public ControlOnPageChangeCallback(BannerViewPager bannerViewPager) {
        this.bannerViewPager = bannerViewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 0://No operation
            case 1://start Sliding
                if (bannerViewPager.isCanLoop()) {
                    int position = bannerViewPager.getCurrentItem();
                    if (position < BasePageAdapter.MULTIPLE_COUNT) {
                        //后面的
                        int pp = BasePageAdapter.MULTIPLE_COUNT - position - 1;
                        bannerViewPager.setCurrentItemReal(bannerViewPager.getItemCount() - BasePageAdapter.MULTIPLE_COUNT - 1 - pp, false);
                    } else if (position >= bannerViewPager.getItemCount() - BasePageAdapter.MULTIPLE_COUNT) {
                        //前面的
                        int pp = position - (bannerViewPager.getItemCount() - BasePageAdapter.MULTIPLE_COUNT);
                        bannerViewPager.setCurrentItemReal(BasePageAdapter.MULTIPLE_COUNT + pp, false);
                    }
                }
                break;
        }
    }
}
