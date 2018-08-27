package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

;

/**
 * 作者　　: 李坤
 * 创建时间:2017/8/24 0024　23:55
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager嵌套滑动处理
 * 1：对于指定的控件嵌套滑动,百度地图，高德地图，RecyclerView
 * 2:ViewPager是否可以左右滑动{@link #setCanSlide}
 */

public class NestViewPager extends ViewPager {
    private static final String BAIDU_MAP1 = "com.baidu.mapapi.map.MapView";
    private static final String BAIDU_MAP2 = "com.baidu.mapapi.map.TextureMapView";
    private static final String GAODE_MAP1 = "com.amap.api.maps.MapView";
    private static final String GAODE_MAP2 = "com.amap.api.maps.TextureMapView";

    private ArrayList<Class> classes;
    /**
     * ViewPager是否可以滑动
     */
    private boolean isCanSlide = true;

    public NestViewPager(Context context) {
        super(context);
    }

    public NestViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        String className = v.getClass().getName();
        return v instanceof RecyclerView
                || super.canScroll(v, checkV, dx, x, y)
                || BAIDU_MAP1.equals(className)
                || BAIDU_MAP2.equals(className)
                || GAODE_MAP1.equals(className)
                || GAODE_MAP2.equals(className)
                || (classes != null && classes.contains(className));
    }

    protected boolean isNeastClass(Class cls) {
        if (classes == null) {
            return false;
        }
        for (Class c : classes) {
            if (c.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isCanSlide) {
            return false;
        } else {
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanSlide) {
            return false;
        } else {
            return super.onInterceptTouchEvent(arg0);
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
}
