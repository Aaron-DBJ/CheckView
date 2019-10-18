package com.aaron_dbj.checkview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.aaron_dbj.tickview.R;

public class CheckView extends View {
    private Context mContext;
    //圆形回弹缩放比例
    private static final float scaleRatio = 1.5f;
    //默认圆环背景颜色
    private int mUnCheckedColor;
    //默认最小半径
    private int minDefaultRadius;
    //默认最小线宽
    private int minStrokeWidth;

    //绘制圆环的画笔
    private Paint mRingPaint;
    //绘制收缩实心圆的画笔
    private Paint mCirclePaint;
    //绘制√的画笔
    private Paint mTickPaint;
    //绘制❤形的画笔和路径
    private Paint mHeartPaint;
    private Path path;
    //圆的半径
    private int radius;
    //圆的外接矩形
    private RectF mRectF = new RectF();
    //圆心x轴坐标
    private int centerX;
    //圆心y轴坐标
    private int centerY;
    //该控件是否选中
    private boolean isChecked;
    //圆环进度同实心圆的颜色
    private int mCheckedColor;
    //动画运行标志
    private boolean isAnimRunning = false;

    /**
     * 属性动画操作的具体属性
     */
    //圆环进度
    private int ringProcess;
    //收缩的实心圆半径
    private int shrinkCircleRadius;
    //√号逐渐浮现
    private int tickAlpha;
    //圆环动画绘制时长
    private int mDuration;

    //圆环线条宽度
    private int mStrokeWidth;
    //该视图尺寸，由于视图展示的是圆形，所以让长宽相同
    private int mSize;
    //包裹√号外的一层圆的半径
    private int mTickRadius;
    //√的点集
    private float[] points = new float[8];
    //绘制的形状，有√和❤
    private static final int TICK = 1;
    private static final int HEART = 2;
    private int mShapeStyle;
    private boolean isTiny = false;

    private AnimatorSet mAnimatorSet;
    private AnimatorUtils animatorUtils;
    private ObjectAnimator boundAnimator;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public void setOnCheckedChangeListener(final OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCheckedChangeListener != null) {
                    toggle();
                    mOnCheckedChangeListener.onCheckedChange(v, isChecked);
                }
            }
        });
    }

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        initPaint();
    }

    private void initAttrs(AttributeSet attributeSet) {
        minDefaultRadius = DisplayUtil.dp2px(mContext, 5);
        minStrokeWidth = DisplayUtil.dp2px(mContext, 1);
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.CheckView);
        radius = (int) typedArray.getDimension(R.styleable.CheckView_radius, minDefaultRadius);
        isChecked = typedArray.getBoolean(R.styleable.CheckView_checked, false);
        mUnCheckedColor = typedArray.getColor(R.styleable.CheckView_mUnCheckedColor, Color.LTGRAY);
        mCheckedColor = typedArray.getColor(R.styleable.CheckView_ringColor, Color.YELLOW);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CheckView_strokeWidth, DisplayUtil.dp2px(mContext, 2));
        mDuration = typedArray.getInt(R.styleable.CheckView_duration, 600);
        mShapeStyle = typedArray.getInteger(R.styleable.CheckView_shapeStyle, 1);
        typedArray.recycle();
    }
    
    private void initPaint() {
        // 初始化圆环画笔
        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPaint.setColor(isChecked ? mCheckedColor : mUnCheckedColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);
        // 初始化收缩的实心圆画笔
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        // 初始化绘制√的画笔
        mTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTickPaint.setColor(mUnCheckedColor);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mStrokeWidth);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        // 初始化绘制❤形的画笔
        mHeartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeartPaint.setStrokeCap(Paint.Cap.ROUND);
        mHeartPaint.setColor(isChecked ? mCheckedColor : mUnCheckedColor);
        mHeartPaint.setStrokeWidth(mStrokeWidth);
        mHeartPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measuredSize((int) ((radius * scaleRatio) * 2 + mStrokeWidth), widthMeasureSpec);
        int height = measuredSize((int) ((radius * scaleRatio) * 2 + mStrokeWidth), heightMeasureSpec);
        mSize = width > height ? width : height;
        setMeasuredDimension(mSize, mSize);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        //设置打钩的几个点坐标，方便后续绘制√
        mTickRadius = radius / 3;
        shrinkCircleRadius = radius - mStrokeWidth / 2;
        points[0] = centerX - mTickRadius;
        points[1] = (float) centerY;
        points[2] = centerX;
        points[3] = centerY + mTickRadius;
        points[4] = centerX;
        points[5] = centerY + mTickRadius;
        points[6] = centerX + mTickRadius;
        points[7] = centerY - mTickRadius;
        initAnimator();
    }

    private int measuredSize(int viewSize, int measureSpec) {
        //mSize为该控件最终确定的尺寸
        int size = viewSize;
        //父视图期望该控件的尺寸和模式
        int measureSpecSize = MeasureSpec.getSize(measureSpec);
        int measureSpecMode = MeasureSpec.getMode(measureSpec);
        switch (measureSpecMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                size = viewSize;
                break;
            case MeasureSpec.EXACTLY:
                if (HEART == mShapeStyle && measureSpecSize < DisplayUtil.dp2px(mContext, 14)) {
                    size = DisplayUtil.dp2px(mContext, 14);
                    isTiny = true;
                } else if (TICK == mShapeStyle && measureSpecSize < DisplayUtil.dp2px(mContext, 12)){
                    size = DisplayUtil.dp2px(mContext, 12);
                }
                if (radius > size / 2) {
                    radius = size / 2 - mStrokeWidth;
                }
                break;
        }
        return size;
    }

    private void initAnimator() {
        animatorUtils = AnimatorUtils.getInstance(this);
        //设置圆环上的进度动画
        ObjectAnimator processAnimator = animatorUtils.ringProcessAnimator("ringProcess", mDuration / 2,
                new AccelerateDecelerateInterpolator(), 0, 360);
        //设置上层实心圆收缩动画，用来逆向显示背景实心圆
        ObjectAnimator circleShrinkAnimator = ObjectAnimator.ofInt(this, "shrinkCircleRadius", radius - mStrokeWidth, 0);
        circleShrinkAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        circleShrinkAnimator.setDuration(mDuration / 2);
        //设置√号浮现动画
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofInt(this, "tickAlpha", 0, 255);
        fadeInAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        //回弹动画
        boundAnimator = animatorUtils.heartBeatAnimator(mDuration, new AccelerateDecelerateInterpolator());

        AnimatorSet fadeAndBoundSet = new AnimatorSet();
        fadeAndBoundSet.playTogether(fadeInAnimator, boundAnimator);
        fadeAndBoundSet.setDuration(mDuration);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(processAnimator, circleShrinkAnimator, fadeAndBoundSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawShape(canvas);

    }

    private void drawShape(Canvas canvas) {
        switch (mShapeStyle) {
            case TICK:
                drawTick(canvas);
                break;
            case HEART:
                drawHeart(canvas);
                break;
        }
    }

    private void drawTick(Canvas canvas) {
        if (!isChecked) {
            canvas.drawArc(mRectF, 90, 360, false, mRingPaint);
            canvas.drawLines(points, mTickPaint);
            return;
        }
        //绘制圆环上的进度
        canvas.drawArc(mRectF, 90, ringProcess, false, mRingPaint);
        //绘制背景实心圆
        mCirclePaint.setColor(mCheckedColor);
        canvas.drawCircle(centerX, centerY, ringProcess == 360 ? radius : 0, mCirclePaint);
        if (ringProcess == 360) {
            mCirclePaint.setColor(Color.WHITE);

            canvas.drawCircle(centerX, centerY, shrinkCircleRadius, mCirclePaint);
        }
        if (shrinkCircleRadius == 0) {
            mTickPaint.setColor(Color.WHITE);
            mTickPaint.setAlpha(tickAlpha);
            canvas.drawLines(points, mTickPaint);
        }
        if (!isAnimRunning) {
            mAnimatorSet.start();
            isAnimRunning = true;
        }
    }

    private void drawHeart(Canvas canvas) {
        path.moveTo((float) (0.5 * mSize), (float) (0.15 * mSize));
        path.cubicTo((float) (0.25 * mSize), (float) (-0.2 * mSize), (float) (-0.45 * mSize), (float) (0.45 * mSize), (float) (0.55 * mSize), (float) (mSize * 0.95));
        path.moveTo((float) (0.55 * mSize), (float) (mSize * 0.95));
        path.cubicTo((float) (mSize + 0.35 * mSize), (float) (0.45 * mSize), (float) (mSize - 0.18 * mSize), (float) (-0.2 * mSize), (float) (0.5 * mSize), (float) (0.15 * mSize));
        if (!isChecked) {
            if (isTiny) {
                mHeartPaint.setStrokeWidth(minStrokeWidth);
            }
            canvas.drawPath(path, mHeartPaint);
            return;
        }
        mHeartPaint.setColor(mCheckedColor);
        mHeartPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, mHeartPaint);
        if (!isAnimRunning) {
            boundAnimator.start();
            isAnimRunning = true;
        }
    }

    /*
     * 因为要使用属性动画来展示圆环的进度以及颜色区域收缩的过程，
     * 所以必须定义该属性的getter和setter方法;注意方法名要正确，否则
     * 创建属性动画对象时会找不到该setter方法
     */
    private int getRingProcess() {
        return ringProcess;
    }

    /**
     * 属性动画依赖该方法设置圆环进度，每设置一个值都需要重绘才能显示动画效果，
     * 所以必须设置postInvalidate方法
     *
     * @param ringProcess
     */
    private void setRingProcess(int ringProcess) {
        this.ringProcess = ringProcess;
        postInvalidate();
    }

    public int getShrinkCircleRadius() {
        return shrinkCircleRadius;
    }

    public void setShrinkCircleRadius(int shrinkCircleRadius) {
        this.shrinkCircleRadius = shrinkCircleRadius;
        postInvalidate();
    }

    public int getTickAlpha() {
        return tickAlpha;
    }

    public void setTickAlpha(int tickAlpha) {
        this.tickAlpha = tickAlpha;
        postInvalidate();
    }

    /**
     * 当前状态是否选中
     *
     * @return
     */
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        reset();
    }

    public void toggle() {
        setChecked(!isChecked);
    }

    /**
     * 重置参数
     */
    private void reset() {
        initPaint();
        mAnimatorSet.cancel();
        tickAlpha = 0;
        ringProcess = 0;
        shrinkCircleRadius = -1;
        isAnimRunning = false;
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        invalidate();
    }
}
