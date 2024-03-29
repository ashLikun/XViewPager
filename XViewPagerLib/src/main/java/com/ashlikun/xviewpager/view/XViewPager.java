package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;

import androidx.viewpager.widget.ViewPager;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.anim.VerticalTransformer;
import com.ashlikun.xviewpager.listener.OnCanScroll;

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


    private float startX, startY;
    private ArrayList<Class> classes;
    //ViewPager是否可以滑动
    private boolean isCanSlide = true;
    private View refreshLayout;
    private Boolean refreshLayoutEnable = null;
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
    //切换速度
    protected int scrollTime = -1;
    private OnCanScroll onCanScroll = null;
    private OnTouchListener dispatchTouchEvent = null;
    private OnTouchListener interceptTouchEvent = null;
    private OnTouchListener touchEvent = null;

    public boolean isNeedWindowInsetsBug = true;


    public XViewPager(Context context) {
        this(context, null);
    }

    public XViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, true);
    }

    public XViewPager(Context context, AttributeSet attrs, boolean isSupperAttrs) {
        super(context, isSupperAttrs ? attrs : null);
        initView(context, attrs);
    }

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
        if (a.hasValue(R.styleable.XViewPager_xvp_isNeedWindowInsetsBug)) {
            isNeedWindowInsetsBug = a.getBoolean(R.styleable.XViewPager_xvp_isNeedWindowInsetsBug, isNeedWindowInsetsBug);
        }
        //滚动速度
        setScrollTime(a.getInteger(R.styleable.XViewPager_xvp_scrollTime, scrollTime));
        a.recycle();
        windowInsetsBug();
    }

    /**
     * 解决在低于30 的手机上 导致主线程一直重绘问题,从而导致onStop onDestroy 回调延迟
     * https://www.jianshu.com/p/25c139e9666a
     */
    public void windowInsetsBug() {
        if (!isNeedWindowInsetsBug) return;
        if (Build.VERSION.SDK_INT < 30) {
            setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                private Rect mTempRect = new Rect();

                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    //消费事件
                    final WindowInsets applied = insets;
                    if (applied.isConsumed()) {
                        return applied;
                    }
                    final Rect res = mTempRect;
                    res.left = applied.getSystemWindowInsetLeft();
                    res.top = applied.getSystemWindowInsetTop();
                    res.right = applied.getSystemWindowInsetRight();
                    res.bottom = applied.getSystemWindowInsetBottom();

                    for (int i = 0, count = getChildCount(); i < count; i++) {

                        final WindowInsets childInsets = getChildAt(i).dispatchApplyWindowInsets(applied);
                        // Now keep track of any consumed by tracking each dimension's min
                        // value
                        res.left = Math.min(childInsets.getSystemWindowInsetLeft(),
                                res.left);
                        res.top = Math.min(childInsets.getSystemWindowInsetTop(),
                                res.top);
                        res.right = Math.min(childInsets.getSystemWindowInsetRight(),
                                res.right);
                        res.bottom = Math.min(childInsets.getSystemWindowInsetBottom(),
                                res.bottom);
                    }
                    // 这里必须消费
                    return applied.replaceSystemWindowInsets(res.left, res.top, res.right, res.bottom)
                            .consumeSystemWindowInsets();
                }
            });
        }
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
        //优先使用外部判断
        if (onCanScroll != null) {
            return onCanScroll.canScroll(v, checkV, dx, x, y);
        }
        String className = v.getClass().getName();
        return super.canScroll(v, checkV, dx, x, y)
                || BAIDU_MAP1.equals(className)
                || BAIDU_MAP2.equals(className)
                || GAODE_MAP1.equals(className)
                || GAODE_MAP2.equals(className)
                || (classes != null && classes.contains(className));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispatchTouchEvent != null) {
            if (dispatchTouchEvent.onTouch(this, ev)) {
                super.dispatchTouchEvent(ev);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanSlide) {
            if (refreshLayout != null) {
                if (refreshLayoutEnable == null)
                    refreshLayoutEnable = refreshLayout.isEnabled();
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (refreshLayoutEnable == null)
                            refreshLayoutEnable = refreshLayout.isEnabled();
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
                            if (refreshLayoutEnable != null && refreshLayoutEnable) {
                                refreshLayout.setEnabled(false);
                            }

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (refreshLayoutEnable != null) {
                            refreshLayout.setEnabled(refreshLayoutEnable);
                            refreshLayoutEnable = null;
                        }
                        break;
                    default:
                        break;
                }
            }
            //可能发生  IllegalArgumentException: pointerIndex out of range报错字符串索引超出范围
            try {
                if (touchEvent != null) {
                    if (touchEvent.onTouch(this, ev)) {
                        super.onTouchEvent(ev);
                        return true;
                    }
                }
                if (scrollMode == ScrollMode.VERTICAL) {
                    MotionEvent event = swapTouchEvent(ev);
                    if (touchEvent != null) {
                        if (touchEvent.onTouch(this, event)) {
                            super.onTouchEvent(event);
                            return true;
                        }
                    }
                    return super.onTouchEvent(event);
                }
                if (touchEvent != null) {
                    if (touchEvent.onTouch(this, ev)) {
                        super.onTouchEvent(ev);
                        return true;
                    }
                }
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanSlide) {

            if (refreshLayout == null) {
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录手指按下的位置
                        startY = ev.getY();
                        startX = ev.getX();
//                        getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally);
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
                    if (interceptTouchEvent != null) {
                        if (interceptTouchEvent.onTouch(this, swapTouchEvent(ev))) {
                            swapTouchEvent(ev);
                            return true;
                        }
                    }
                    boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(ev));
                    swapTouchEvent(ev);
                    return intercept;
                }
                if (interceptTouchEvent != null) {
                    if (interceptTouchEvent.onTouch(this, ev)) {
                        return true;
                    }
                }
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return false;
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

    public void setOnCanScroll(OnCanScroll onCanScroll) {
        this.onCanScroll = onCanScroll;
    }

    public OnCanScroll getOnCanScroll() {
        return onCanScroll;
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

    public void setDispatchTouchEvent(OnTouchListener dispatchTouchEvent) {
        this.dispatchTouchEvent = dispatchTouchEvent;
    }

    public void setInterceptTouchEvent(OnTouchListener interceptTouchEvent) {
        this.interceptTouchEvent = interceptTouchEvent;
    }

    public void setTouchEvent(OnTouchListener touchEvent) {
        this.touchEvent = touchEvent;
    }

    public int getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(int scrollTime) {
        this.scrollTime = scrollTime;
        //无法还原
        if (scrollTime > 0) {
            ViewPagerUtils.initSwitchTime(this, scrollTime);
        }
    }

    public interface OnTouchListener {
        boolean onTouch(XViewPager viewPager, MotionEvent event);
    }
}