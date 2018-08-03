package com.ashlikun.xviewpager.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/6/15 0015　下午 1:44
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：{@link FragmentPagerAdapter}
 * ViewPager的Fragment 适配器的item参数
 */
public class FragmentPagerItem implements Parcelable {
    /**
     * 必须参数,路由路径
     */
    protected String path;
    /**
     * fragment 的title,
     */
    protected String title;
    /**
     * fragment 的id,可以null
     */
    protected String id;

    /**
     * 可null，携带的参数
     */
    protected Bundle param;

    /**
     * 零时的
     */
    protected FragmentPagerAdapter.Builder builder;

    private FragmentPagerItem(String path) {
        this.path = path;
    }

    public FragmentPagerItem setPath(String path) {
        this.path = path;
        return this;
    }

    public FragmentPagerItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public FragmentPagerItem setId(String id) {
        this.id = id;
        return this;
    }

    public FragmentPagerItem setParam(Bundle param) {
        this.param = param;
        return this;
    }

    public FragmentPagerItem addParam(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return this;
        }
        if (param == null) {
            param = new Bundle();
        }
        if (value instanceof String) {
            param.putString(key, (String) value);
        } else if (value instanceof Integer) {
            param.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            param.putFloat(key, (Float) value);
        } else if (value instanceof Short) {
            param.putShort(key, (Short) value);
        } else if (value instanceof Boolean) {
            param.putBoolean(key, (Boolean) value);
        } else if (value instanceof Serializable) {
            param.putSerializable(key, (Serializable) value);
        } else if (value instanceof int[]) {
            param.putIntArray(key, (int[]) value);
        } else if (value instanceof String[]) {
            param.putStringArray(key, (String[]) value);
        } else if (value instanceof float[]) {
            param.putFloatArray(key, (float[]) value);
        } else if (value instanceof Parcelable) {
            param.putParcelable(key, (Parcelable) value);
        }
        return this;
    }

    /**
     * 回退到构建的地方
     *
     * @return
     */
    public FragmentPagerAdapter.Builder ok() {
        FragmentPagerAdapter.Builder res = builder;
        builder = null;
        return res;
    }

    public static FragmentPagerItem get(String path) {
        return new FragmentPagerItem(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeBundle(this.param);
    }

    public FragmentPagerItem() {
    }

    protected FragmentPagerItem(Parcel in) {
        this.path = in.readString();
        this.title = in.readString();
        this.id = in.readString();
        this.param = in.readBundle();
    }

    public static final Parcelable.Creator<FragmentPagerItem> CREATOR = new Parcelable.Creator<FragmentPagerItem>() {
        @Override
        public FragmentPagerItem createFromParcel(Parcel source) {
            return new FragmentPagerItem(source);
        }

        @Override
        public FragmentPagerItem[] newArray(int size) {
            return new FragmentPagerItem[size];
        }
    };
}
