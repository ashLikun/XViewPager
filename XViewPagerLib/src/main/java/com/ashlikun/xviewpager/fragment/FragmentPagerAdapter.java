package com.ashlikun.xviewpager.fragment;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
 */

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    public static final String POSITION = "POSITION";
    /**
     * 当前显示的Fragment
     */
    Fragment currentFragment;

    private List<FragmentPagerItem> pagerItems;

    private FragmentPagerAdapter(FragmentManager fm, List<FragmentPagerItem> items) {
        super(fm);
        this.pagerItems = items;
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
        FragmentPagerItem item = pagerItems.get(position);
        item.addParam(POSITION, position);
        return (Fragment) ARouter.getInstance()
                .build(item.path)
                .with(item.param)
                .navigation();
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
     * @author　　: 李坤
     * 创建时间: 2018/6/15 0015 下午 2:01
     * 邮箱　　：496546144@qq.com
     * <p>
     * 功能介绍：构建者
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
            return new FragmentPagerAdapter(fm, items);
        }
    }
}