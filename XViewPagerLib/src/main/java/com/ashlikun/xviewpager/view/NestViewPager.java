package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ashlikun.xviewpager.R;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 作者　　: 李坤
 * 创建时间:2017/8/24 0024　23:55
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager嵌套滑动处理
 * 1：对于指定的控件嵌套滑动,百度地图，高德地图，RecyclerView
 * 2:ViewPager是否可以左右滑动{@link #setCanSlide}
 * 3:外层是下拉刷新控件可以处理嵌套滑动问题
 */

public class NestViewPager extends ViewPager {
    private static final String BAIDU_MAP1 = "com.baidu.mapapi.map.MapView";
    private static final String BAIDU_MAP2 = "com.baidu.mapapi.map.TextureMapView";
    private static final String GAODE_MAP1 = "com.amap.api.maps.MapView";
    private static final String GAODE_MAP2 = "com.amap.api.maps.TextureMapView";
    private float startX, startY;
    private ArrayList<Class> classes;
    /**
     * ViewPager是否可以滑动
     */
    private boolean isCanSlide = true;
    private View refreshLayout;
    private int touchSlop;
    /**
     * 缩放比例
     */
    protected float ratio = 0;
    /**
     * 按照那个值为基础
     * 0:宽度
     * 1：高度
     */
    protected int orientation = 0;

    public NestViewPager(Context context) {
        this(context, null);
    }

    public NestViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NestViewPager);
        ratio = a.getFloat(R.styleable.NestViewPager_nvp_ratio, 0);
        orientation = a.getInt(R.styleable.NestViewPager_nvp_orientation, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (ratio > 0) {
            if (orientation == 0) {
                //宽度不变
                heightSize = (int) (widthSize / ratio);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                        MeasureSpec.EXACTLY);
            } else {
                //高度不变
                widthSize = (int) (heightSize / ratio);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
                        MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        String className = v.getClass().getName();
        return super.canScroll(v, checkV, dx, x, y)
                || BAIDU_MAP1.equals(className)
                || BAIDU_MAP2.equals(className)
                || GAODE_MAP1.equals(className)
                || GAODE_MAP2.equals(className)
                || (classes != null && classes.contains(className));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                if (distanceX > touchSlop && distanceX > distanceY) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isCanSlide) {
            return false;
        } else {
            if (refreshLayout != null) {

            }
            return super.onTouchEvent(ev);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isCanSlide) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    /**
     * 外部添加一个嵌套滑动的控件
     *
     * @param cls
     */
    public void addNestClass(Class... cls) {
        if (classes == null) {
            classes = new ArrayList<>();
        }
        classes.addAll(Arrays.asList(cls));
    }

    /**
     * ViewPager是否可以滑动
     */
    public void setCanSlide(boolean canSlide) {
        isCanSlide = canSlide;
    }

    /**
     * 设置下拉刷新控件
     * 在滑动的时候会判断是否禁用下拉刷新
     *
     * @param refreshLayout
     */
    public void setRefreshLayout(View refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    /**
     * 设置比例
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        if (this.ratio != ratio) {
            this.ratio = ratio;
            requestLayout();
        }
    }

    /**
     * 设置方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            requestLayout();
        }
    }
}