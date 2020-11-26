package com.ashlikun.xviewpager;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import com.ashlikun.xviewpager.adapter.BasePageAdapter;

import java.lang.reflect.Field;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/3 17:38
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager的工具
 */

public class ViewPagerUtils {


    /**
     * 设置viewpager 之间的切换速度
     */
    public static void initSwitchTime(Context context, ViewPager viewPager, int time) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(context);
            scroller.setScrollDuration(time);
            field.set(viewPager, scroller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    /**
     * 无线循环的时候根据position返回真实的position
     *
     * @param position
     * @param allNumber 全部的数量，假的
     * @return
     */

    public static int getRealPosition(int position, int allNumber) {
        if (allNumber <= BasePageAdapter.MULTIPLE_COUNT * 2 + 1) {
            return 0;
        }
        if (position < BasePageAdapter.MULTIPLE_COUNT) {
            int diff = BasePageAdapter.MULTIPLE_COUNT - position;
            //最后一个
            return allNumber - BasePageAdapter.MULTIPLE_COUNT * 2 - diff;
        } else if (position >= allNumber - BasePageAdapter.MULTIPLE_COUNT) {
            //第一个
            return position - (allNumber - BasePageAdapter.MULTIPLE_COUNT);
        }
        return position - BasePageAdapter.MULTIPLE_COUNT;
    }
}
