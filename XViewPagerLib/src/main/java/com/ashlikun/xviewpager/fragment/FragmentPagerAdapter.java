package com.ashlikun.xviewpager.fragment;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
     * 传递给fragment的参数
     */
    public static final String POSITION = "fpa_POSITION";

    protected List<FragmentPagerItem> pagerItems;
    /**
     * 是否缓存Fragment
     */
    private boolean isCache = false;
    protected final FragmentManager fragmentManager;
    /**
     * 缓存时候的Fragment
     */
    protected SparseArray<Fragment> mCacheFragment = new SparseArray<>();

    protected Fragment mCurrentPrimaryItem = null;

    private FragmentPagerAdapter(Builder builder) {
        super(builder.fm, builder.isBehavior ? BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT : BEHAVIOR_SET_USER_VISIBLE_HINT);
        fragmentManager = builder.fm;
        this.pagerItems = builder.items;
        setCache(builder.isCache);
    }


    public List<FragmentPagerItem> getPagerItems() {
        if (pagerItems == null) {
            pagerItems = new ArrayList<>();
        }
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

    public void setCache(boolean cache) {
        isCache = cache;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        //防止ViewPager内容概率性不显示
        if (isCache && getCacheFragment(position) != null) {
            container.requestLayout();
        }
        mCacheFragment.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        if (!isCache) {
            super.destroyItem(container, position, object);
            mCacheFragment.remove(position);
        }
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            mCurrentPrimaryItem = fragment;
        }
    }

    public <T extends Fragment> T getCurrentFragment() {
        if (mCurrentPrimaryItem == null) {
            return null;
        }
        return (T) mCurrentPrimaryItem;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    /**
     * 获取缓存的fragment
     * 前提是开启缓存
     *
     * @param position
     * @return
     */
    public <T extends Fragment> T getCacheFragment(int position) {
        if (mCacheFragment == null) {
            return null;
        }
        return (T) mCacheFragment.get(position);
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        if (isCache) {
            mCacheFragment.clear();
        }
    }

    /**
     * 清空缓存
     */
    public void removeCache(int position) {
        if (isCache) {
            mCacheFragment.remove(position);
        }
    }

    public int getCacheSize() {
        return mCacheFragment != null ? mCacheFragment.size() : 0;
    }

    @Override
    public Parcelable saveState() {
        //ViewPager又包裹了一层ViewPager，且外层的Adapter继承了FragmentPagerAdapter，但里层继承了FragmentStatePagerAdapter。
        //Fragment no longer exists for key f0: index 0
        try {
            return super.saveState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        //ViewPager又包裹了一层ViewPager，且外层的Adapter继承了FragmentPagerAdapter，但里层继承了FragmentStatePagerAdapter。
        //Fragment no longer exists for key f0: index 0
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建者
     */
    public static class Builder {
        FragmentManager fm;
        List<FragmentPagerItem> items = new ArrayList<>();
        private boolean isCache;
        private boolean isBehavior;

        private Builder(FragmentManager fm, boolean isBehavior) {
            this.fm = fm;
            this.isBehavior = isBehavior;
        }

        /**
         * 这个方法对应老的版本的Fragment
         * 会调用 setUserVisibleHint  可能没View没出来也会调用
         *
         * @param fm
         * @return
         * @deprecated 请使用 {@link #create(FragmentManager)}
         */
        @Deprecated
        public static Builder get(FragmentManager fm) {
            return new Builder(fm, false);
        }

        /**
         * 新版本Fragment
         * 获取焦点（包括滑动）会调用onResume, 失去焦点（包括滑动走了） 会调用 onPause
         *
         * @param fm
         * @return
         */
        public static Builder create(FragmentManager fm) {
            return new Builder(fm, true);
        }

        public Builder setItems(List<FragmentPagerItem> items) {
            this.items = items;
            return this;
        }

        public Builder setCache(boolean isCache) {
            this.isCache = isCache;
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