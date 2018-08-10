package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.listener.OnItemClickListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 17:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：一个普通的没有指示器的banner,
 * 1:可以用作广告栏
 * 2:可以用作启动页
 */

public class BannerViewPager extends ViewPager {
    //判断点击的最大移动距离
    private static final float SENS = 5;
    public static final long DEFAULT_TURNING_TIME = 5000;
    public static final float DEFAULT_RATIO = 16 / 9.0f;
    OnPageChangeListener mOuterPageChangeListener;
    private OnItemClickListener onItemClickListener;
    private CusPageAdapter mAdapter;
    //是否可以触摸滚动
    private boolean isCanTouchScroll = true;
    //是否可以循环
    private boolean canLoop = true;
    //自动轮播的间隔
    private long turningTime = DEFAULT_TURNING_TIME;
    //是否可以自动滚动
    private boolean turning = false;
    //是否只有一条数据的时候禁用翻页
    private boolean isOneDataOffLoopAndTurning = true;
    //是否可以自动滚动，内部用于判断触摸屏幕，与view进入焦点
    private boolean isAutoTurning = false;
    //大小比例，按照宽度
    private float ratio = DEFAULT_RATIO;
    private float downX = 0, downY = 0;

    private AdSwitchTask adSwitchTask;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager);
        canLoop = a.getBoolean(R.styleable.BannerViewPager_banner_canLoop, canLoop);
        isOneDataOffLoopAndTurning = a.getBoolean(R.styleable.BannerViewPager_banner_isOneDataOffLoopAndTurning, isOneDataOffLoopAndTurning);
        turningTime = a.getInteger(R.styleable.BannerViewPager_banner_turningTime, (int) turningTime);
        ratio = a.getFloat(R.styleable.BannerViewPager_banner_ratio, ratio);
        isCanTouchScroll = a.getBoolean(R.styleable.BannerViewPager_banner_isCanTouchScroll, isCanTouchScroll);
        a.recycle();
        setTurningTime(turningTime);
        addOnPageChangeListener(onPageChangeListener);
        adSwitchTask = new AdSwitchTask(this);

    }

    public void setTurningTime(long turningTime) {
        this.turningTime = turningTime;
        if (turningTime > 0) {
            turning = true;
            isAutoTurning = true;
        }
    }

    public void setRatio(float ratio) {
        if (this.ratio != ratio) {
            this.ratio = ratio;
            requestLayout();
        }
    }


    public int getFristItem() {
        return canLoop ? mAdapter.getRealCount() : 0;
    }

    public int getLastItem() {
        return mAdapter.getRealCount() - 1;
    }

    public boolean isCanTouchScroll() {
        return isCanTouchScroll;
    }

    public void setCanTouchScroll(boolean isCanScroll) {
        this.isCanTouchScroll = isCanScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onItemClickListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = ev.getX();
                    downY = ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(downX - ev.getX()) < SENS
                            && Math.abs(downY - ev.getY()) < SENS) {
                        if (getDatas() != null && getRealPosition() < getDatas().size()) {
                            onItemClickListener.onItemClick(this, getDatas().get(getRealPosition()), getRealPosition());
                        }
                    }
                    break;
            }
        }
        if (isCanTouchScroll && !isOneDataOffLoopAndTurning()) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanTouchScroll && !isOneDataOffLoopAndTurning()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    /**
     * 触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanTouchScroll && !isOneDataOffLoopAndTurning()) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                // 开始翻页
                if (isAutoTurning) {
                    startTurning(turningTime);
                }
            } else if (action == MotionEvent.ACTION_DOWN) {
                // 停止翻页
                if (isAutoTurning) {
                    stopTurning();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public CusPageAdapter getAdapter() {
        return mAdapter;
    }

    public int getRealPosition() {
        return ViewPagerUtils.getRealPosition(getCurrentItem(), mAdapter != null ? mAdapter.getRealCount() : 0);
    }

    /**
     * adapter真实的个数
     *
     * @return
     */
    public int getRealItemCount() {
        return mAdapter != null ? mAdapter.getRealCount() : 0;
    }

    /**
     * adapter的最大item个数，是假的
     *
     * @return
     */
    public int getItemCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }


    public void setOnBannerChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }


    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
            int realPosition = ViewPagerUtils.getRealPosition(position, mAdapter.getRealCount());
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int realPosition = ViewPagerUtils.getRealPosition(position, mAdapter.getRealCount());

            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrolled(realPosition,
                        positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public boolean isCanLoop() {
        return canLoop;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        if (canLoop == false) {
            setCurrentItem(getRealPosition(), false);
        }
        if (mAdapter == null) {
            return;
        }
        mAdapter.setCanLoop(canLoop);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 手动停止
     */
    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 手动开始
     */
    public BannerViewPager startTurning() {
        if (turningTime > 0) {
            return startTurning(turningTime);
        }
        return this;
    }

    /**
     * 手动开始
     */
    public BannerViewPager startTurning(long turningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        isAutoTurning = true;
        this.turningTime = turningTime;
        turning = true;
        if (!isOneDataOffLoopAndTurning()) {
            postDelayed(adSwitchTask, turningTime);
        }
        return this;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (widthSize / ratio);
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置banner的数据
     * 第二次后调用的
     *
     * @param datas
     */
    public BannerViewPager setPages(final List<Object> datas) {
        if (mAdapter == null) {
            throw new RuntimeException("没有发现holderCreator，请调用双参数的setPages");
        }
        setPages(null, datas);
        return this;
    }

    /**
     * 设置banner的数据
     * 第一次调用的，必须要有ViewPageHelperListener
     *
     * @param datas
     */
    public BannerViewPager setPages(final ViewPageHelperListener holderCreator, final List<Object> datas) {
        if (datas == null) {
            return this;
        }
        if (mAdapter == null) {
            mAdapter = new CusPageAdapter<>(this, holderCreator, datas);
            mAdapter.setCanLoop(canLoop);
            setAdapter(mAdapter);
        } else {
            mAdapter.setCanLoop(canLoop);
            mAdapter.setDatas(datas);
            mAdapter.notifyDataSetChanged();
        }
        setCurrentItem(getFristItem(), false);

        if (turning) {
            startTurning();
        }
        if (datas.size() == 1) {

        }
        return this;
    }

    public List<Object> getDatas() {
        return mAdapter.datas;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /**
     * 如果退出了，自动停止，进来则自动开始
     *
     * @param visibility
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (canLoop && isAutoTurning) {
            if (visibility == View.VISIBLE) {
                startTurning();
            } else {
                stopTurning();
            }
        }
    }


    /**
     * 是否只有一条数据的时候禁用翻页
     *
     * @return
     */
    public boolean isOneDataOffLoopAndTurning() {
        return isOneDataOffLoopAndTurning && getDatas() != null && getDatas().size() == 1;
    }

    /**
     * 设置是否只有一条数据的时候禁用翻页
     *
     * @param oneDataOffLoopAndTurning
     */
    public void setOneDataOffLoopAndTurning(boolean oneDataOffLoopAndTurning) {
        this.isOneDataOffLoopAndTurning = oneDataOffLoopAndTurning;
    }

    /**
     * 自动滚动的倒计时
     */
    static class AdSwitchTask implements Runnable {

        private final WeakReference<BannerViewPager> reference;

        AdSwitchTask(BannerViewPager bannerViewPager) {
            this.reference = new WeakReference(bannerViewPager);
        }

        @Override
        public void run() {
            BannerViewPager bannerViewPager = reference.get();
            if (bannerViewPager != null) {
                if (bannerViewPager.turning) {
                    int page = bannerViewPager.getCurrentItem() + 1;
                    if (page >= bannerViewPager.getItemCount()) {
                        page = 0;
                    }
                    if (page < bannerViewPager.getItemCount()) {
                        bannerViewPager.setCurrentItem(page);
                    }
                    bannerViewPager.postDelayed(bannerViewPager.adSwitchTask, bannerViewPager.turningTime);
                }
            }
        }
    }
}
