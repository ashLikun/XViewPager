package com.ashlikun.xviewpager.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.listener.PageWidthListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 14:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：banner的适配器
 */
public class BasePageAdapter<T> extends PagerAdapter {
    public static final int MULTIPLE_COUNT = Integer.MAX_VALUE;
    protected List<T> datas;
    protected ViewPageHelperListener holderCreator;
    protected PageWidthListener pageWidthListener;
    private boolean canLoop = true;
    private BannerViewPager viewPager;

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return canLoop ? MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 获取真实的Position
     */
    public int getRealPosition(int position) {
        if (canLoop) {
            return ViewPagerUtils.getRealPosition(position, getRealCount());
        }
        return position;
    }


    public T getItemData(int position) {
        if (position >= 0 && position < getRealCount() && datas != null) {
            return datas.get(position);
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = getRealPosition(position);
        Object data = getItemData(realPosition);
        View view = holderCreator.createView(viewPager.getContext(), viewPager, data, realPosition);
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


    public BasePageAdapter(BannerViewPager bannerViewPager, ViewPageHelperListener holderCreator, List<T> datas) {
        this.viewPager = bannerViewPager;
        this.holderCreator = holderCreator;
        this.datas = datas;
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

    public void setDatas(List datas) {
        this.datas = datas;
    }

    public List<T> getDatas() {
        return datas;
    }
}
