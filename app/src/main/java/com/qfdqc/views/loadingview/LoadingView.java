package com.qfdqc.views.loadingview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 自定义加载动画View
 * 思路
 * 第一步：绘制出外层的灰色圆圈
 * 第二步：绘制中间图标
 * 第三步：绘制圆弧进度圈
 * 第四步：支持转圈动画
 */
public class LoadingView extends View {

    //画笔
    private Paint mPaint;
    //灰色画笔
    private Paint mPaintGrey;
    //圆圈边框宽度
    private int borderWidth = 8;
    //圆形的矩形轮廓
    RectF rectF;
    private int maxProgress = 100;
    private int minProgress = 0;
    //中间的图片
    private Bitmap centerImg;
    private Bitmap scaleImg;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    private void init() {
        borderWidth = DpOrPxUtils.dip2px(getContext(), 2);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#333366"));

        mPaintGrey = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGrey.setStyle(Paint.Style.STROKE);
        mPaintGrey.setColor(Color.parseColor("#eeeeee"));
        mPaintGrey.setStrokeWidth(borderWidth);

        rectF = new RectF();

        centerImg = BitmapFactory.decodeResource(getResources(), R.drawable.loading);

        objectAnimator = ObjectAnimator.ofFloat(
                this,
                "startAngle",
                -90f, 270);
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);

    }

    //是否自动执行动画
    private boolean mAutoStartAni;

    /**
     * 读取属性设置
     *
     * @param context
     * @param attributeSet
     */
    private void getAttrs(Context context, AttributeSet attributeSet) {
        try {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.LoadingView);
            mAutoStartAni = a.getBoolean(R.styleable.LoadingView_autoStartAni, false);
            a.recycle();
        } catch (Exception ex) {

        }

    }

    //圆弧绘制的启始角度,默认-90
    float startAngle = -90;
    //圆弧扫过的角度
    float sweepAngle;

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制最外层的灰色圆形
        //计算圆心
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        //半径
        float radius = (getWidth() / 2.0f - borderWidth);
        canvas.drawCircle(cx, cy, radius, mPaintGrey);

        //计算图片缩放比例
        float scale = Math.min(getWidth() * 1.0f / (centerImg.getWidth() - borderWidth * 4), (getHeight() - borderWidth * 4) * 1.0f / centerImg.getHeight());

        if (scaleImg == null) {
            scaleBitmap(centerImg, scale);
        }
        //绘制中间图标
        canvas.drawBitmap(scaleImg, borderWidth * 2, borderWidth * 2, mPaint);


        //绘制圆弧
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //圆形的矩形轮廓
        rectF.set(borderWidth, borderWidth, getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth);
        //计算图片缩放比例
        float scale = Math.min(getMeasuredWidth() * 1.0f / (centerImg.getWidth() - borderWidth * 4), (getMeasuredHeight() - borderWidth * 4) * 1.0f / centerImg.getHeight());
        scaleBitmap(centerImg, scale);
    }

    private int mProgress;

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        mProgress = progress;
        //根据进度计算出扫过的角度
        sweepAngle = progress / 100f * 360;
        //重新绘制
        postInvalidate();
    }

    /**
     * 设置进度
     */
    public void setPercent(float percent) {
        if (!objectAnimator.isStarted()) {
            //根据进度计算出扫过的角度
            sweepAngle = 360 * percent;
            //重新绘制
            postInvalidate();
        }
    }

    public int getmProgress() {
        return mProgress;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        scaleImg = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return scaleImg;
    }

    ObjectAnimator objectAnimator;

    /**
     * 开始loading加载动画、思路是动画圆弧的起始角度，动画到360度
     */
    public void startLoadingAnimation() {
        if (objectAnimator != null && !objectAnimator.isStarted()) {
            sweepAngle = 30;
            objectAnimator.start();
        }
    }

    public void stopLoadingAnimation() {
        if (objectAnimator == null) {
            return;
        }
        objectAnimator.end();
        startAngle = -90;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoStartAni) {
            startLoadingAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAutoStartAni) {
            stopLoadingAnimation();
        }
    }
}
