package com.ashlikun.xviewpager.fragment;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

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

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * 是否缓存Fragment
     */
    protected boolean isCache = false;
    /**
     * 传递给fragment的参数
     */
    public static final String POSITION = "fpa_POSITION";
    /**
     * 当前显示的Fragment
     */
    Fragment currentFragment;

    private List<FragmentPagerItem> pagerItems;
    /**
     * 缓存时候的Fragment
     */
    private SparseArray<Fragment> chcheFragment;

    private FragmentPagerAdapter(Builder builder) {
        super(builder.fm);
        this.pagerItems = builder.items;
        this.isCache = builder.isCache;
    }

    public List<FragmentPagerItem> getPagerItems() {
        return pagerItems;
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
            if (chcheFragment == null) {
                chcheFragment = new SparseArray<>();
            }
            if (chcheFragment.get(position) != null) {
                fragment = chcheFragment.get(position);
            }
        }
        if (fragment == null) {
            FragmentPagerItem item = pagerItems.get(position);
            //添加一个告诉fragment当前是第几页
            item.addParam(POSITION, position);
            fragment = (Fragment) ARouter.getInstance()
                    .build(item.path)
                    .with(item.param)
                    .navigation();
            if (isCache) {
                chcheFragment.append(position, fragment);
            }
        }
        return fragment;
    }


    @Override
    public int getCount() {
        return pagerItems == null || pagerItems.isEmpty() ? 0 : pagerItems.size();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object != null && object instanceof Fragment) {
            currentFragment = (Fragment) object;
        } else {
            currentFragment = null;
        }
    }

    public <T extends Fragment> T getCurrentFragment() {
        if (currentFragment == null) {
            return null;
        }
        return (T) currentFragment;
    }


    /**
     * 获取缓存的fragment
     * 前提是开启缓存
     *
     * @param position
     * @return
     */
    public Fragment getCacheFragment(int position) {
        return chcheFragment.get(position);
    }

    /**
     * @author　　: 李坤
     * 创建时间: 2018/6/15 0015 下午 2:01
     * 邮箱　　：496546144@qq.com
     * <p>
     * 功能介绍：构建者
     */
    public static class Builder {
        FragmentManager fm;
        List<FragmentPagerItem> items = new ArrayList<>();
        boolean isCache = false;

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

        /**
         * 是否缓存Fragment
         */
        public Builder setCache(boolean cache) {
            isCache = cache;
            return this;
        }

        public FragmentPagerAdapter build() {
            return new FragmentPagerAdapter(this);
        }
    }
}