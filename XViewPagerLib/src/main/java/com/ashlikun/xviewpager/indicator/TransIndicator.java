package com.ashlikun.xviewpager.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.ViewPagerUtils;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/8/6　9:22
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：平移的Indicator
 */
public class TransIndicator extends IBannerIndicator {

    /**
     * 整个移动的距离
     */
    private float moveDistance = 0;
    /**
     * 2个view的距离
     */
    private int moveSize = 0;
    private int[] firstPosition = new int[2];
    private int[] secondPositon = new int[2];
    private int currentSelect = 0;

    public TransIndicator(Context context) {
        this(context, null);
    }

    public TransIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint mPaint;

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DefaultIndicator);
        space = (int) a.getDimension(R.styleable.DefaultIndicator_ind_select, ViewPagerUtils.dip2px(context, space));
        selectDraw = a.getDrawable(R.styleable.DefaultIndicator_ind_select);
        noSelectDraw = a.getDrawable(R.styleable.DefaultIndicator_ind_no_select);
        if (selectDraw == null) {
            selectDraw = getResources().getDrawable(R.drawable.banner_circle_select);
        }
        if (noSelectDraw == null) {
            noSelectDraw = getResources().getDrawable(R.drawable.banner_circle_default);
        }
        a.recycle();
        setGravity(Gravity.CENTER);
        setClipChildren(false);
        setClipToPadding(false);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffff0000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(height,
                Math.max(noSelectDraw.getIntrinsicHeight(), selectDraw.getIntrinsicHeight())));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (pointViews.size() < 2) {
            return;
        }
        pointViews.get(0).getLocationOnScreen(firstPosition);
        pointViews.get(1).getLocationOnScreen(secondPositon);
        moveSize = secondPositon[0] - firstPosition[0];
        moveDistance = firstPosition[0] - moveSize + currentSelect * moveSize;
    }

    @Override
    public TransIndicator setPages(List<Object> datas, int selectIndex) {
        this.datas = datas;
        this.notifyDataSetChanged(selectIndex);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param
     */
    @Override
    public TransIndicator notifyDataSetChanged(int selectIndex) {
        removeAllViews();
        pointViews.clear();
        if (datas == null) {
            return this;
        }
        currentSelect = selectIndex;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(space, 0, space, 0);
        for (int count = 0; count < datas.size(); count++) {
            // 翻页指示的点
            View pointView = new ImageView(getContext());
            pointView.setBackground(noSelectDraw);
            pointViews.add(pointView);
            addView(pointView, params);
        }
        return this;
    }

    @Override
    public IBannerIndicator setSelectDraw(Drawable selectDraw, int selectIndex) {
        selectDraw.setBounds(0, 0, selectDraw.getIntrinsicWidth(), selectDraw.getIntrinsicHeight());
        super.setSelectDraw(selectDraw, selectIndex);
        return this;

    }

    @Override
    public void onPointScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("aaaa", "position = " + position + "    positionOffset = " + positionOffset + "    positionOffsetPixels = " + positionOffsetPixels);
        if (position == pointViews.size() - 1 && positionOffset > 0) {
            //最后一个向第一个移动
            moveDistance = firstPosition[0] - moveSize;
        } else {
            moveDistance = firstPosition[0] - moveSize + (positionOffset * moveSize + position * moveSize);
        }
        invalidate();
    }

    @Override
    public void onPointSelected(int selectIndex) {
        currentSelect = selectIndex;
    }

    /**
     * 重绘圆点的运动
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(moveDistance, 0);
        selectDraw.draw(canvas);
        canvas.restore();
    }


}