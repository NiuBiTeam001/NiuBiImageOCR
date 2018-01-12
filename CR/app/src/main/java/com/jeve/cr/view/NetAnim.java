package com.jeve.cr.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jeve.cr.tool.DeviceTool;


public class NetAnim extends View {

    private Paint paint;
    private RectF rectF;
    private int changeAngle;
    private float lineX, lineY;
    private float circleR;
    private float mar;
    private int lineLength;
    private boolean animStop = false;
    private int lineAnimValue;
    private boolean setLayout = false;

    public NetAnim(Context context) {
        this(context, null);
    }

    public NetAnim(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetAnim(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);//设置空心

        int screenX = DeviceTool.getScreenSize(context).x;

        changeAngle = 0;
        lineLength = 0;
        circleR = screenX * 0.028f;
        mar = screenX * 0.005f;
        lineAnimValue = (int) (screenX * 0.017f);

        rectF = new RectF(mar, mar, circleR * 2 + mar, circleR * 2 + mar);

        lineX = (float) Math.sqrt(circleR * circleR / 2) + circleR + mar;
        lineY = (float) Math.sqrt(circleR * circleR / 2) + circleR + mar;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!setLayout && widthMeasureSpec != 0) {
            setLayout = true;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            float more = (float) Math.sqrt(lineAnimValue * lineAnimValue / 2);
            layoutParams.width = (int) (circleR * 2 + more + 10);
            layoutParams.height = (int) (circleR * 2 + more + 10);
            setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rectF, 45, changeAngle, false, paint);
        canvas.drawLine(lineX, lineY, lineX + lineLength, lineY + lineLength, paint);
    }

    public void animStart() {
        if (animStop) {
            animStop = false;
            return;
        }

        ValueAnimator animator = ValueAnimator.ofInt(0, 360);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                int animatedValue = (int) animation.getAnimatedValue();
                changeAngle = animatedValue;
                invalidate();
            }
        });
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lineStart();
            }
        });
    }

    private void lineStart() {
        ValueAnimator animator = ValueAnimator.ofInt(0, lineAnimValue);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                int animatedValue = (int) animation.getAnimatedValue();
                lineLength = animatedValue;
                invalidate();
            }
        });
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animReset();
            }
        });
    }

    private void animReset() {
        ValueAnimator animator = ValueAnimator.ofInt(0, lineAnimValue);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeAngle = 0;
                lineLength = 0;
                invalidate();
                animStart();
            }
        });
    }

    public void stopAnim() {
        animStop = true;
    }
}
