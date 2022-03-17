package com.ashlikun.xviewpager.adapter;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.fragment.FragmentLayout;
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
    //数据前后各加多少个假数据(前后加上2)，如果循环的时候
    public static final int MULTIPLE_COUNT = 1;
    public static final int TAG_KEY = 88888666;
    protected List<T> mDatas;
    protected ViewPageHelperListener holderCreator;
    protected PageWidthListener pageWidthListener;
    private boolean canLoop = true;
    private boolean isCache = true;
    private BannerViewPager viewPager;
    private SparseArray<View> views;

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
        if (isCache) {
            if (views == null) {
                views = new SparseArray<>();
            }
        }
        int pp = getRealPosition(position);
        T data = getItemData(pp);
        View view = null;
        if (isCache) {
            view = views.get(position);
            if (view != null) {
                Object cache = view.getTag(TAG_KEY);
                //判断缓存与data是否相等
                if ((cache == null && data == null) || (cache != null && cache.equals(data)) || (data != null && data.equals(cache))) {
                } else {
                    view = null;
                }
            }
        }
        if (view == null) {
            view = holderCreator.createView(viewPager.getContext(), viewPager, data, pp);
            if (viewPager.getOnItemClickListener() != null) {
                if (view.hasOnClickListeners()) {
                    FragmentLayout fragmentLayout = new FragmentLayout(container.getContext());
                    fragmentLayout.addView(view);
                    view = fragmentLayout;
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getDatas() != null && viewPager.getRealPosition() < getDatas().size()) {
                            viewPager.getOnItemClickListener().onItemClick(viewPager, getDatas().get(viewPager.getRealPosition()), viewPager.getRealPosition());
                        }
                    }
                });
            }
            if (isCache) {
                view.setTag(TAG_KEY, data);
                views.put(position, view);
            }
        } else {
            Log.e("aaa", position + "使用缓存" + pp);
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
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public boolean isCache() {
        return isCache;
    }
}
