package com.ashlikun.xviewpager.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

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
    private int firstLeft = 0;
    private int secondLeft = 0;
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
    public TransIndicator(Context context, AttributeSet attrs, boolean isSupperAttrs) {
        super(context, attrs, 0, isSupperAttrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        setClipChildren(false);
        setClipToPadding(false);
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
        if (pointViews.size() < 1) {
            return;
        }
        firstLeft = pointViews.get(0).getLeft();
        if (pointViews.size() == 1) {
            moveSize = pointViews.get(0).getWidth() + space * 2;
            moveDistance = firstLeft;
            return;
        }
        secondLeft = pointViews.get(1).getLeft();
        moveSize = secondLeft - firstLeft;
        moveDistance = firstLeft + currentSelect * moveSize;
    }

    @Override
    public TransIndicator setPages(List<Object> datas, int selectIndex) {
        super.setPages(datas, selectIndex);
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
        if (datas == null || (!oneDateIsShow && datas.size() == 1)) {
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

    }

    @Override
    public void onPointScrolledReal(int position, int positionReal, float positionOffset, int positionOffsetPixels) {
        super.onPointScrolledReal(position, positionReal, positionOffset, positionOffsetPixels);
        if (positionReal == pointViews.size() - 1 && positionOffset > 0) {
            //最后一个向第一个移动
            if (position >= pointViews.size()) {
                moveDistance = firstLeft + (positionReal * moveSize);
            } else {
                moveDistance = firstLeft + (position * moveSize);
            }

        } else {
            moveDistance = firstLeft + (positionOffset * moveSize + positionReal * moveSize);
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
        if (pointViews.size() > 0) {
            canvas.save();
            canvas.translate(moveDistance, ((getHeight() - selectDraw.getIntrinsicHeight()) / 2));
            selectDraw.draw(canvas);
            canvas.restore();
        }
    }


}
