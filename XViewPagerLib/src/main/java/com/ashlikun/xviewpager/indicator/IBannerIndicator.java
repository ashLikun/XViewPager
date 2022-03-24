package com.ashlikun.xviewpager.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.listener.RealOnPageChangeCallback;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/8/6　9:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Banner指示器的View接口，其他view只需实现这个接口就可以了
 * 继承自LinearLayout
 */
public abstract class IBannerIndicator extends LinearLayout {
    /**
     * 资源，必须要有大小
     */
    protected Drawable selectDraw;
    protected Drawable noSelectDraw;
    protected boolean oneDateIsShow;
    /**
     * 间距
     */
    protected int space = 3;
    protected List<Object> datas;
    protected List<View> pointViews = new ArrayList<>();

    public IBannerIndicator(@NonNull Context context) {
        this(context, null);
    }

    public IBannerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IBannerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, true);
    }

    public IBannerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean isSupperAttrs) {
        super(context, isSupperAttrs ? attrs : null, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IBannerIndicator);
        space = (int) a.getDimension(R.styleable.IBannerIndicator_ind_space, ViewPagerUtils.dip2px(context, space));
        selectDraw = a.getDrawable(R.styleable.IBannerIndicator_ind_select);
        noSelectDraw = a.getDrawable(R.styleable.IBannerIndicator_ind_no_select);
        oneDateIsShow = a.getBoolean(R.styleable.IBannerIndicator_ind_oneDateIsShow, true);
        if (selectDraw == null) {
            selectDraw = getResources().getDrawable(R.drawable.banner_circle_select, context.getTheme());
        }
        if (noSelectDraw == null) {
            noSelectDraw = getResources().getDrawable(R.drawable.banner_circle_default, context.getTheme());
        }
        a.recycle();
        selectDraw.setBounds(0, 0, selectDraw.getIntrinsicWidth(), selectDraw.getIntrinsicHeight());
        noSelectDraw.setBounds(0, 0, noSelectDraw.getIntrinsicWidth(), noSelectDraw.getIntrinsicHeight());
        setBackgroundColor(Color.TRANSPARENT);
        initView(context, attrs);
    }

    protected abstract void initView(Context context, AttributeSet attrs);

    /**
     * 底部指示器资源图片
     *
     * @param
     */
    public abstract IBannerIndicator notifyDataSetChanged(int selectIndex);

    /**
     * 添加数据
     *
     * @param datas
     */
    public IBannerIndicator setPages(List<Object> datas, int selectIndex) {
        this.datas = datas;
        this.notifyDataSetChanged(selectIndex);
        return this;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public Drawable getSelectDraw() {
        return selectDraw;
    }

    public Drawable getNoSelectDraw() {
        return noSelectDraw;
    }

    public int getSpace() {
        return space;
    }

    public IBannerIndicator setSelectDraw(Drawable selectDraw, int selectIndex) {
        if (selectDraw != null) {
            this.selectDraw = selectDraw;
            this.notifyDataSetChanged(selectIndex);
        }
        return this;
    }

    public IBannerIndicator setNoSelectDraw(Drawable noSelectDraw, int selectIndex) {
        if (noSelectDraw != null) {
            this.noSelectDraw = noSelectDraw;
            this.notifyDataSetChanged(selectIndex);
        }
        return this;
    }

    public class MyOnPageChangeListener extends RealOnPageChangeCallback {

        public MyOnPageChangeListener(BannerViewPager bannerViewPager) {
            super(onPageChangeCallback, bannerViewPager);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = bannerViewPager.getRealPosition(position);
            onPointScrolledReal(position, realPosition, positionOffset, positionOffsetPixels);
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            int realPosition = bannerViewPager.getRealPosition(position);
            onPointSelectedReal(position, realPosition);
            super.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            IBannerIndicator.this.onPageScrollStateChanged(state);
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeCallback = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position >= getItemCount()) {
                return;
            }
            onPointScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            if (datas == null) {
                return;
            }
            if (position >= getItemCount()) {
                return;
            }
            onPointSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void onPageScrollStateChanged(int state) {
    }

    public abstract void onPointSelected(int position);

    public void onPointSelectedReal(int position, int positionReal) {

    }

    public abstract void onPointScrolled(int position, float positionOffset, int positionOffsetPixels);


    public void onPointScrolledReal(int position, int positionReal, float positionOffset, int positionOffsetPixels) {

    }
}
