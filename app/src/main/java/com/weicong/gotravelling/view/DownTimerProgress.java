package com.weicong.gotravelling.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.View;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.DensityUtil;

public class DownTimerProgress extends View implements Runnable {

    private static final int DEFAULT_MAX_TIME = 60;

    private int DEFAULT_BACKGROUND_COLOR;
    private int DEFAULT_FOREGROUND_COLOR;
    private float DEFAULT_TEXT_SIZE;
    private int DEFAULT_TEXT_COLOR;
    private float DEFAULT_CIRCLE_WIDTH;
    private int DEFAULT_SIZE;

    private Paint mCirclePaint;
    private Paint mTextPaint;

    private int mBackgroundColor;
    private int mForegroundColor;
    private float mTextSize;
    private int mTextColor;
    private int mMaxTime;
    private float mCircleWidth;

    private float mProgress;
    private float mDelta;
    private int mTime;
    private int mSize;
    private RectF mRectF = new RectF();
    private float mStartAngel;
    private float mSweepAngel;
    private float mTextHeight;
    private String mText;
    private Callback mCallback;

    private boolean mIsRun = false;

    public DownTimerProgress(Context context) {
        this(context, null);
    }

    public DownTimerProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownTimerProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefault();
        TypedArray ta = context.obtainStyledAttributes(
                attrs, R.styleable.DownTimerProgress, defStyleAttr, 0);
        initAttributes(ta);
        ta.recycle();
        initPaints();

        mTime = mMaxTime;
        mProgress = 360f;
        mDelta = mProgress / mTime * 0.1f;
        mText = String.valueOf(mTime);
    }

    /**
     * 初始化默认值
     */
    private void initDefault() {
        DEFAULT_BACKGROUND_COLOR = Color.rgb(232, 232, 232);
        DEFAULT_FOREGROUND_COLOR = Color.rgb(3, 169, 244);
        DEFAULT_TEXT_SIZE = DensityUtil.sp2px(getContext(), 16);
        DEFAULT_TEXT_COLOR = Color.rgb(3, 169, 244);
        DEFAULT_CIRCLE_WIDTH = DensityUtil.dp2px(getContext(), 4);
        DEFAULT_SIZE = (int) DensityUtil.dp2px(getContext(), 48);
    }

    /**
     * 初始化属性
     *
     * @param ta TypedArray
     */
    private void initAttributes(TypedArray ta) {
        mBackgroundColor = ta.getColor(R.styleable.DownTimerProgress_background_color,
                DEFAULT_BACKGROUND_COLOR);
        mForegroundColor = ta.getColor(R.styleable.DownTimerProgress_foreground_color,
                DEFAULT_FOREGROUND_COLOR);
        mTextSize = ta.getDimension(R.styleable.DownTimerProgress_text_size,
                DEFAULT_TEXT_SIZE);
        mTextColor = ta.getColor(R.styleable.DownTimerProgress_text_color,
                DEFAULT_TEXT_COLOR);
        mMaxTime = ta.getInt(R.styleable.DownTimerProgress_max_time,
                DEFAULT_MAX_TIME);
        mCircleWidth = ta.getDimension(R.styleable.DownTimerProgress_circle_width,
                DEFAULT_CIRCLE_WIDTH);

    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mBackgroundColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(mCircleWidth);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    /**
     * 测量大小
     *
     * @param widthMeasureSpec 宽测量参数
     * @param heightMeasureSpec 高测量参数
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);

        mSize = Math.min(width, height);
        mRectF.set(mCircleWidth, mCircleWidth,
                mSize-mCircleWidth, mSize-mCircleWidth);
        setMeasuredDimension(mSize, mSize);
    }

    /**
     * 测量大小
     *
     * @param measureSpec 测量参数
     * @return 最终大小
     */
    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = DEFAULT_SIZE;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCirclePaint.setColor(mBackgroundColor);
        canvas.drawArc(mRectF, 0, 360, false, mCirclePaint);

        if (mProgress > 0) {

            mCirclePaint.setColor(mForegroundColor);
            mSweepAngel = mProgress;
            mStartAngel = (360f - mSweepAngel + 270f) % 360f;
            canvas.drawArc(mRectF, mStartAngel, mSweepAngel, false, mCirclePaint);

            mTextHeight = mTextPaint.descent() + mTextPaint.ascent();
            canvas.drawText(mText, (getWidth() - mTextPaint.measureText(mText)) / 2.0f,
                    (getWidth() - mTextHeight) / 2.0f, mTextPaint);
        }
    }

    private int mCount = 0;

    @Override
    public void run() {
        if (mIsRun && mTime > 0) {
            postInvalidate();
            mProgress = mProgress - mDelta;
            if (mCount == 10) {
                mCount = 0;
                mTime--;
                setText(String.valueOf(mTime));
            }
            mCount++;
            postDelayed(this, 100);
        } else {
            mIsRun = false;
            if (mCallback != null) {
                mCallback.onFinished();
            }
        }
    }

    public interface Callback {
        void onFinished();
    }

    /**
     * 设置回调函数
     *
     * @param callback callback
     */
    public void setCallback (Callback callback) {
        mCallback = callback;
    }

    /**
     * 开始运行
     */
    public void beginRun() {
        mIsRun = true;
        run();
    }

    /**
     * 停止运行
     */
    public void stopRun() {
        mIsRun = false;
    }

    /**
     * 恢复到初始状态
     */
    public void reset() {
        mIsRun = false;
        mProgress = 360f;
        mTime = mMaxTime;
        mText = String.valueOf(mTime);
    }
}
