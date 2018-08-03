package com.ashlikun.xviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Scroller;

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
            field.set(viewPager, new ViewPagerScroller(context, time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ViewPagerScroller extends Scroller {
        int time;

        public ViewPagerScroller(Context context, int time) {
            super(context);
            this.time = time;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, time);
        }
    }

}
