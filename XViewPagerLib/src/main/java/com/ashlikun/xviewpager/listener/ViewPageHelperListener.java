package com.ashlikun.xviewpager.listener;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashlikun.xviewpager.view.BannerViewPager;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 15:49
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：viewpager创建item的回调,为了解耦
 */

public interface ViewPageHelperListener<T> {
    /**
     * 创建新的view,还要更新
     *
     * @param banner
     * @param context
     * @param data data可能是空的,loop模式的时候最少有
     * @return
     */
    View createView(@NonNull Context context, @NonNull BannerViewPager banner, @Nullable T data, int position);
}
