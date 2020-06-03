package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.viewpager.widget.ViewPager;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.anim.VerticalTransformer;

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

public class XViewPager extends ViewPager {
    private static final String BAIDU_MAP1 = "com.baidu.mapapi.map.MapView";
    private static final String BAIDU_MAP2 = "com.baidu.mapapi.map.TextureMapView";
    private static final String GAODE_MAP1 = "com.amap.api.maps.MapView";
    private static final String GAODE_MAP2 = "com.amap.api.maps.TextureMapView";


    public XViewPager(Context context) {
        this(context, null);
    }

    public XViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        initView(context, attrs);
    }

    private float startX, startY;
    private ArrayList<Class> classes;
    //ViewPager是否可以滑动
    private boolean isCanSlide = true;
    private View refreshLayout;
    private int touchSlop;
    //滑动模式
    private ScrollMode scrollMode = ScrollMode.HORIZONTAL;
    //缩放比例
    protected float ratio = 0;
    //按照那个值为基础 0:宽度 1：高度
    protected int orientation = 0;
    //用于裁剪画布的路径
    protected Path clipPath;
    //圆角半径  左上
    protected float radiusLeftTop = -1;
    //圆角半径  右上
    protected float radiusRightTop = -1;
    //圆角半径  右下
    protected float radiusRightBottom = -1;
    //圆角半径  坐下
    protected float radiusLeftBottom = -1;


    protected void initView(Context context, AttributeSet attrs) {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XViewPager);
        ratio = a.getFloat(R.styleable.XViewPager_xvp_ratio, 0);
        orientation = a.getInt(R.styleable.XViewPager_xvp_orientation, 0);
        isCanSlide = a.getBoolean(R.styleable.XViewPager_xvp_isCanSlide, isCanSlide);
        setScrollMode(ScrollMode.getScrollMode(a.getInt(R.styleable.XViewPager_xvp_scrollMode, scrollMode.id)));
        //圆角设置
        if (a.hasValue(R.styleable.XViewPager_xvp_radius)) {
            radiusLeftTop = radiusRightTop = radiusRightBottom = radiusLeftBottom = a.getDimension(R.styleable.XViewPager_xvp_radius, -1);
        }
        if (a.hasValue(R.styleable.XViewPager_xvp_radiusLeftTop)) {
            radiusLeftTop = a.getDimension(R.styleable.XViewPager_xvp_radiusLeftTop, radiusLeftTop);
        }
        if (a.hasValue(R.styleable.XViewPager_xvp_radiusRightTop)) {
            radiusRightTop = a.getDimension(R.styleable.XViewPager_xvp_radiusRightTop, radiusRightTop);
        }
        if (a.hasValue(R.styleable.XViewPager_xvp_radiusRightBottom)) {
            radiusRightBottom = a.getDimension(R.styleable.XViewPager_xvp_radiusRightBottom, radiusRightBottom);
        }
        if (a.hasValue(R.styleable.XViewPager_xvp_radiusLeftBottom)) {
            radiusLeftBottom = a.getDimension(R.styleable.XViewPager_xvp_radiusLeftBottom, radiusLeftBottom);
        }
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
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isCanSlide) {
            return false;
        } else {
            if (refreshLayout != null) {
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
                            refreshLayout.setEnabled(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        refreshLayout.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }
            //可能发生  IllegalArgumentException: pointerIndex out of range报错字符串索引超出范围
            try {
                if (scrollMode == ScrollMode.VERTICAL) {
                    return super.onTouchEvent(swapTouchEvent(ev));
                }
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isCanSlide) {
            return false;
        } else {

            if (refreshLayout == null) {
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
                                int or = (int) (startX - endX);
                                boolean canScrollHorizontally = canScrollHorizontally(or);
                                getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally);
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
            }

            //可能发生  IllegalArgumentException: pointerIndex out of range报错字符串索引超出范围
            try {
                if (scrollMode == ScrollMode.VERTICAL) {
                    boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(ev));
                    swapTouchEvent(ev);
                    return intercept;
                }
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (radiusLeftTop != -1 || radiusRightTop != -1 || radiusRightBottom != -1 || radiusLeftBottom != -1) {
            if (clipPath == null) {
                clipPath = new Path();
            }
            clipPath.reset();
            float[] radii = new float[]{radiusLeftTop, radiusLeftTop, radiusRightTop, radiusRightTop, radiusRightBottom, radiusRightBottom, radiusLeftBottom, radiusLeftBottom};

            clipPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radii, Path.Direction.CW);
            //viewPage内部是滚动实现，这里要加上偏移量
            Matrix matrix = new Matrix();
            matrix.setTranslate(getScrollX(), getScaleY());
            clipPath.transform(matrix);

            canvas.save();
            canvas.clipPath(clipPath);
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    /**
     * 交换触摸事件
     *
     * @param event
     * @return
     */
    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();
        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;
        event.setLocation(swappedX, swappedY);
        return event;
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
     * 设置滚动类型
     *
     * @param scrollMode
     */
    public void setScrollMode(ScrollMode scrollMode) {
        ScrollMode oldMode = this.scrollMode;
        this.scrollMode = scrollMode;
        if (scrollMode == ScrollMode.VERTICAL) {
            setPageTransformer(false, new VerticalTransformer());
            setOverScrollMode(OVER_SCROLL_NEVER);
        } else if (oldMode == ScrollMode.VERTICAL) {
            setPageTransformer(false, null);
            setOverScrollMode(OVER_SCROLL_ALWAYS);
        }
    }

    public ScrollMode getScrollMode() {
        return scrollMode;
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
     * 这里如果 下拉控件子控件满足isNestedScrollingEnabled  就不用调用这个方法，内部自己处理
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

    /**
     * 4个方向的圆角
     */
    public void setRadius(float radius) {
        radiusLeftTop = radius;
        radiusRightTop = radius;
        radiusRightBottom = radius;
        radiusLeftBottom = radius;
        invalidate();
    }

    /**
     * 4个方向的圆角
     */
    public void setRadius(float radiusLeftTop, float radiusRightTop, float radiusRightBottom, float radiusLeftBottom) {
        this.radiusLeftTop = radiusLeftTop;
        this.radiusRightTop = radiusRightTop;
        this.radiusRightBottom = radiusRightBottom;
        this.radiusLeftBottom = radiusLeftBottom;
        invalidate();
    }

    /**
     * 左上的圆角
     */
    public void setRadiusLeftTop(float radiusLeftTop) {
        this.radiusLeftTop = radiusLeftTop;
        invalidate();
    }

    /**
     * 右上的圆角
     */
    public void setRadiusRightTop(float radiusRightTop) {
        this.radiusRightTop = radiusRightTop;
        invalidate();
    }

    /**
     * 右下的圆角
     */
    public void setRadiusRightBottom(float radiusRightBottom) {
        this.radiusRightBottom = radiusRightBottom;
        invalidate();
    }

    /**
     * 坐下的圆角
     */
    public void setRadiusLeftBottom(float radiusLeftBottom) {
        this.radiusLeftBottom = radiusLeftBottom;
        invalidate();
    }
}