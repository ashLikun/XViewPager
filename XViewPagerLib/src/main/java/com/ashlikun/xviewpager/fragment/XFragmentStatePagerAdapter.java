package com.ashlikun.xviewpager.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implementation of {@link PagerAdapter} that
 * uses a {@link Fragment} to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 * <p>This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * {@link FragmentPagerAdapter} at the cost of potentially more overhead when
 * switching between pages.
 *
 * <p>When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.</p>
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 *
 * <p>Here is an example implementation of a pager containing fragments of
 * lists:
 * <p>
 * {@sample frameworks/support/samples/Support13Demos/src/main/java/com/example/android/supportv13/app/FragmentStatePagerSupport.java
 * complete}
 *
 * <p>The <code>R.layout.fragment_pager</code> resource of the top-level fragment is:
 * <p>
 * {@sample frameworks/support/samples/Support13Demos/src/main/res/layout/fragment_pager.xml
 * complete}
 *
 * <p>The <code>R.layout.fragment_pager_list</code> resource containing each
 * individual fragment's layout is:
 * <p>
 * {@sample frameworks/support/samples/Support13Demos/src/main/res/layout/fragment_pager_list.xml
 * complete}
 */
public abstract class XFragmentStatePagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentStatePagerAdapt";

    protected final FragmentManager mFragmentManager;
    protected FragmentTransaction mCurTransaction = null;

    protected SparseArray<Fragment.SavedState> mSavedState = new SparseArray<Fragment.SavedState>();
    /**
     * 缓存时候的Fragment
     */
    protected SparseArray<Fragment> mCacheFragment = new SparseArray<Fragment>();
    /**
     * 默认的最大缓存
     */
    public static final int MAX_CACHE = Integer.MAX_VALUE;

    protected Fragment mCurrentPrimaryItem = null;
    protected int mCurrentPrimaryPosition = -1;

    public XFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }


    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f = mCacheFragment.get(position);
        if (f != null) {
            return f;
        }


        Fragment fragment = getItem(position);
        Fragment.SavedState fss = mSavedState.get(position);
        if (fss != null) {
            fragment.setInitialSavedState(fss);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mCacheFragment.put(position, fragment);

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.add(container.getId(), fragment);
        return fragment;
    }

    /**
     * 检查缓存是否超过最大值
     *
     * @param position 应该删除的 Position
     * @param fragment 应该删除的 fragment
     */
    protected void checkCacheMax(int position, Fragment fragment) {

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        //如果不使用缓存才销毁
        if (!isUserCache()) {
            removeCache(position, fragment);
        } else {
            //使用缓存
            checkCacheMax(position, fragment);
        }
    }

    /**
     * 移除缓存
     *
     * @param position
     */
    public void removeCache(int position, Fragment fragment) {
        if (fragment != null) {

            mSavedState.put(position, fragment.isAdded()
                    ? mFragmentManager.saveFragmentInstanceState(fragment) : null);
            mCacheFragment.remove(position);
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.remove(fragment);

        }
    }

    /**
     * 是否使用缓存
     *
     * @return
     */
    protected boolean isUserCache() {
        return false;
    }


    @Override
    @SuppressWarnings("ReferenceEquality")
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
            mCurrentPrimaryPosition = position;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            for (int i = 0; i < mSavedState.size(); i++) {
                fss[i] = mSavedState.get(mSavedState.keyAt(i));
            }
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mCacheFragment.size(); i++) {
            Fragment f = mCacheFragment.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mCacheFragment.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.put(i, (Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        f.setMenuVisibility(false);
                        mCacheFragment.put(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }


    /**
     * 获取缓存的fragment
     * 前提是开启缓存
     *
     * @param position
     * @return
     */
    public Fragment getCacheFragment(int position) {
        if (mCacheFragment == null) {
            return null;
        }
        return mCacheFragment.get(position);
    }

    public int getCacheSize() {
        return mCacheFragment != null ? mCacheFragment.size() : 0;
    }
}
