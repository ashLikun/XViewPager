package com.ogow.moduleguoji.view.activity.me;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/2/18　15:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class MyAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }


    public void setPrimaryItem2(@NonNull ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }
}
