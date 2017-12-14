package com.jeve.cr.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.jeve.cr.tool.DeviceTool;

/**
 * 图片剪裁控件
 * lijiawei
 * 2017-12-13
 */
public class FlexImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint linePaint;
    private RectF rectF;
    private int leftX, rightX, topY, bottomY;
    private int critical = 20;
    private int criticalC = 40;
    private int deviceWidth;
    private int deviceHeight;
    private int paintWidth = 4;
    private int bitmapWidth;
    private int bitmapHeight;

    public FlexImageView(Context context) {
        this(context, null);
    }

    public FlexImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(paintWidth * 2 - 2);
        linePaint.setStyle(Paint.Style.STROKE);//空心

        DeviceTool.WH wh = DeviceTool.getWidthAndHeight(context);
        deviceWidth = wh.width - 10;
        deviceHeight = wh.height - 10;

        leftX = 10;
        topY = 10;
        rightX = deviceWidth;
        bottomY = deviceHeight;
        rectF = new RectF(leftX, topY, rightX, bottomY);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        bitmapWidth = bm.getWidth();
        bitmapHeight = bm.getHeight();
        layoutParams.width = bitmapWidth;
        layoutParams.height = bitmapHeight;
        setLayoutParams(layoutParams);
        topY = paintWidth;
        bottomY = bitmapHeight - paintWidth;
        leftX = paintWidth;
        rightX = bitmapWidth - paintWidth;
        rectF = new RectF(leftX, topY, rightX, bottomY);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rectF, linePaint);
    }

    private int touchWitch = 0;
    private float touchX = 0, touchY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                if (touchX < leftX + critical && touchX > leftX - critical
                        && touchY > topY + criticalC && touchY < bottomY - criticalC) {
                    Log.d("LJW", "左边边界");
                    touchWitch = 1;
                } else if (touchX < rightX + critical && touchX > rightX - critical
                        && touchY > topY + criticalC && touchY < bottomY - criticalC) {
                    Log.d("LJW", "右边边界");
                    touchWitch = 2;
                } else if (touchY > topY - critical && touchY < topY + critical
                        && touchX > leftX + criticalC && touchX < rightX - criticalC) {
                    Log.d("LJW", "上边边界");
                    touchWitch = 3;
                } else if (touchY > bottomY - critical && touchY < bottomY + critical
                        && touchX > leftX + criticalC && touchX < rightX - criticalC) {
                    Log.d("LJW", "下边边界");
                    touchWitch = 4;
                } else if (touchX < leftX + criticalC && touchX > leftX - criticalC &&
                        touchY > topY - criticalC && touchY < topY + criticalC) {
                    Log.d("LJW", "左上边界");
                    touchWitch = 5;
                } else if (touchX < rightX + criticalC && touchX > rightX - criticalC &&
                        touchY > topY - criticalC && touchY < topY + criticalC) {
                    Log.d("LJW", "右上边界");
                    touchWitch = 6;
                } else if (touchX < leftX + criticalC && touchX > leftX - criticalC &&
                        touchY > bottomY - criticalC && touchY < bottomY + criticalC) {
                    Log.d("LJW", "左下边界");
                    touchWitch = 7;
                } else if (touchX < rightX + criticalC && touchX > rightX - criticalC &&
                        touchY > bottomY - criticalC && touchY < bottomY + criticalC) {
                    Log.d("LJW", "右下边界");
                    touchWitch = 8;
                } else if (touchX > leftX + criticalC && touchX < rightX - criticalC &&
                        touchY > topY + criticalC && touchY < bottomY - criticalC) {
                    Log.d("LJW", "中间");
                    touchWitch = 9;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (touchWitch) {
                    case 1:
                        leftLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 2:
                        rightLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 3:
                        topLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 4:
                        bottomLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 5:
                        leftLimit(event);
                        topLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 6:
                        rightLimit(event);
                        topLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 7:
                        leftLimit(event);
                        bottomLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 8:
                        rightLimit(event);
                        bottomLimit(event);
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        break;
                    case 9:
                        float nowX = event.getX();
                        float nowY = event.getY();
                        float changeX = nowX - touchX;
                        float changeY = nowY - touchY;
                        boolean change = true;
                        if (leftX <= paintWidth) {
                            leftX = paintWidth;
                            if (changeX < 0) {
                                change = false;
                            } else if (changeX > 0) {
                                change = true;
                            }
                        }

                        if (topY <= paintWidth) {
                            topY = paintWidth;
                            if (changeY < 0) {
                                change = false;
                            } else if (changeY > 0) {
                                change = true;
                            }
                        }

                        if (rightX >= bitmapWidth - paintWidth) {
                            rightX = bitmapWidth - paintWidth;
                            if (changeX > 0) {
                                change = false;
                            } else if (changeX < 0) {
                                change = true;
                            }
                        }

                        if (bottomY >= bitmapHeight - paintWidth) {
                            bottomY = bitmapHeight - paintWidth;
                            if (changeY > 0) {
                                change = false;
                            } else if (changeY < 0) {
                                change = true;
                            }
                        }
                        if (change) {
                            leftX += changeX;
                            rightX += changeX;
                            topY += changeY;
                            bottomY += changeY;
                        }
                        rectF = new RectF(leftX, topY, rightX, bottomY);
                        invalidate();
                        touchX = nowX;
                        touchY = nowY;
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchWitch = 0;
                break;
        }
        return true;
    }

    private void leftLimit(MotionEvent event) {
        leftX = (int) event.getX();
        if (leftX <= paintWidth) {
            leftX = paintWidth;
        }
        if (leftX >= rightX - criticalC) {
            leftX = rightX - criticalC;
        }
    }

    private void rightLimit(MotionEvent event) {
        rightX = (int) event.getX();
        if (rightX >= bitmapWidth - paintWidth) {
            rightX = bitmapWidth - paintWidth;
        }
        if (rightX <= leftX + criticalC) {
            rightX = leftX + criticalC;
        }
    }

    private void topLimit(MotionEvent event) {
        topY = (int) event.getY();
        if (topY <= paintWidth) {
            topY = paintWidth;
        }
        if (topY >= bottomY - criticalC) {
            topY = bottomY - criticalC;
        }
    }

    private void bottomLimit(MotionEvent event) {
        bottomY = (int) event.getY();
        if (bottomY >= bitmapHeight - paintWidth) {
            bottomY = bitmapHeight - paintWidth;
        }
        if (bottomY <= topY + criticalC) {
            bottomY = topY + criticalC;
        }
    }

    public Bitmap cutImage() {
        setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        int border = paintWidth * 2 - 2;
        bitmap = Bitmap.createBitmap(bitmap, leftX + border, topY + border, rightX -
                leftX - 2 * (border), bottomY - topY - 2 * (border));
        return bitmap;
    }

}
