package com.ashlikun.xviewpager.view;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ashlikun.xviewpager.listener.ViewPageHelperListener;

import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 14:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：banner的适配器
 */
class CusPageAdapter<T> extends PagerAdapter {
    private final int MULTIPLE_COUNT = 3000;
    protected List<T> datas;
    protected ViewPageHelperListener holderCreator;
    private boolean canLoop = true;
    private int POSITION_CHANG = POSITION_UNCHANGED;
    private BannerViewPager viewPager;

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0) {
            return 0;
        }
        int realPosition = position % realCount;
        return realPosition;
    }

    @Override
    public int getCount() {
        return canLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);
        Object data = null;
        if (datas != null && !datas.isEmpty()) {
            data = datas.get(realPosition);
        }
        View view = holderCreator.createView(viewPager.getContext(), viewPager, data);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFristItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }


    public CusPageAdapter(BannerViewPager bannerViewPager, ViewPageHelperListener holderCreator, List<T> datas) {
        this.viewPager = bannerViewPager;
        this.holderCreator = holderCreator;
        this.datas = datas;
    }

    @Override
    public void notifyDataSetChanged() {
        POSITION_CHANG = POSITION_NONE;
        super.notifyDataSetChanged();
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
        if (POSITION_CHANG == POSITION_NONE) {
            POSITION_CHANG = POSITION_UNCHANGED;
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

//    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
}
