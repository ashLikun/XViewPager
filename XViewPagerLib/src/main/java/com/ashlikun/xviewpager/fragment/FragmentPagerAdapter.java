package com.ashlikun.xviewpager.fragment;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/6/15 0015 下午 1:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：viewpager显示fragment的适配器,用路由模式去寻找fragment
 * 可以设置缓存fragment，第一次会使用Arouter去发现Fragment，后续就会缓存起来
 */

public class FragmentPagerAdapter extends XFragmentStatePagerAdapter {
    /**
     * 传递给fragment的参数
     */
    public static final String POSITION = "fpa_POSITION";

    protected List<FragmentPagerItem> pagerItems;
    /**
     * 是否缓存Fragment,ViewPager不要使用缓存
     */
    protected boolean isCache;
    /**
     * 最大缓存个数,ViewPager不要使用缓存
     */
    protected int maxCache = MAX_CACHE;

    private FragmentPagerAdapter(Builder builder) {
        super(builder.fm);
        this.pagerItems = builder.items;
    }

    @Override
    protected void checkCacheMax(int position, Fragment fragment) {
        super.checkCacheMax(position, fragment);
        if (getCacheSize() > maxCache) {
            //超过最大值，删除老的，并且检查是否存在
            //就是你了,删除了
            removeCache(position, fragment);
        }
    }


    public List<FragmentPagerItem> getPagerItems() {
        return pagerItems;
    }

    public FragmentPagerItem getPagerItem(int position) {
        return pagerItems.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pagerItems.get(position).title;
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (isCache) {
            fragment = getCacheFragment(position);
        }
        if (fragment == null) {
            FragmentPagerItem item = pagerItems.get(position);
            //添加一个告诉fragment当前是第几页
            item.addParam(POSITION, position);
            fragment = (Fragment) ARouter.getInstance()
                    .build(item.path)
                    .with(item.param)
                    .navigation();
        }
        return fragment;
    }

    /**
     * 查找这个id对应 的position
     *
     * @param id
     * @return
     */
    public int findIdPosition(String id) {
        try {
            for (int i = 0; i < getCount(); i++) {
                if (TextUtils.equals(id, getPagerItem(i).getId())) {
                    return i;
                }
            }
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public int getCount() {
        return pagerItems == null || pagerItems.isEmpty() ? 0 : pagerItems.size();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public <T extends Fragment> T getCurrentFragment() {
        if (mCurrentPrimaryItem == null) {
            return null;
        }
        return (T) mCurrentPrimaryItem;
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    /**
     * 构建者
     */
    public static class Builder {
        FragmentManager fm;
        List<FragmentPagerItem> items = new ArrayList<>();

        private Builder(FragmentManager fm) {
            this.fm = fm;
        }

        public static Builder get(FragmentManager fm) {
            return new Builder(fm);
        }

        public Builder setItems(List<FragmentPagerItem> items) {
            this.items = items;
            return this;
        }

        public Builder addItem(FragmentPagerItem item) {
            items.add(item);
            return this;
        }

        public FragmentPagerItem addItem(String itemPath) {
            FragmentPagerItem item = FragmentPagerItem.get(itemPath);
            item.builder = this;
            items.add(item);
            return item;
        }

        public FragmentPagerAdapter build() {
            return new FragmentPagerAdapter(this);
        }
    }
}