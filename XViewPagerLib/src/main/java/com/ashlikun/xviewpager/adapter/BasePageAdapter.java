package com.ashlikun.xviewpager.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.listener.PageWidthListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 14:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：banner的适配器
 */
public class BasePageAdapter<T> extends PagerAdapter {
    //数据前后各加多少个假数据(前后加上2)，如果循环的时候
    public static final int MULTIPLE_COUNT = 1;
    protected List<T> mDatas;
    protected ViewPageHelperListener holderCreator;
    protected PageWidthListener pageWidthListener;
    private boolean canLoop = true;
    private BannerViewPager viewPager;
    private List<View> views;

    public BasePageAdapter(BannerViewPager bannerViewPager, ViewPageHelperListener holderCreator, List<T> mDatas) {
        this.viewPager = bannerViewPager;
        this.holderCreator = holderCreator;
        this.mDatas = mDatas;
    }


    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return canLoop ? getRealCount() + MULTIPLE_COUNT * 2 : getRealCount();
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public int getFristItem() {
        return canLoop ? MULTIPLE_COUNT : 0;
    }

    /**
     * 获取真实的Position
     */
    public int getRealPosition(int position) {
        return canLoop ? ViewPagerUtils.getRealPosition(position, getCount()) : position;
    }

    /**
     * 反转换
     */
    public int getRealFanPosition(int position) {
        return canLoop ? position + MULTIPLE_COUNT : position;
    }

    public T getItemData(int position) {
        if (position >= 0 && position < getRealCount() && mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (views == null) {
            views = new ArrayList<>();
        }
        View view = null;
        if (position >= 0 && position < views.size()) {
            views.get(position);
        }
        if (view == null) {
            int pp = getRealPosition(position);
            view = holderCreator.createView(viewPager.getContext(), viewPager, getItemData(pp), pp);
            views.add(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }


    public void setPageWidthListener(PageWidthListener pageWidthListener) {
        this.pageWidthListener = pageWidthListener;
    }

    @Override
    public float getPageWidth(int position) {
        if (pageWidthListener != null) {
            return pageWidthListener.getPageWidth(position);
        }
        return super.getPageWidth(position);
    }

    /**
     * 意思是如果item的位置如果没有发生变化，则返回POSITION_UNCHANGED。
     * 如果返回了POSITION_NONE，表示该位置的item已经不存在了。
     * 默认的实现是假设item的位置永远不会发生变化，而返回POSITION_UNCHANGED
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setDatas(List mDatas) {
        this.mDatas = mDatas;
        views = null;
    }

    public List<T> getDatas() {
        return mDatas;
    }
}
