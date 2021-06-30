package com.myriding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomView extends View {
    final int round = 25;
    final int leftPadding = 15;
    final int rightPadding = 15;
    final int topBottomPadding = 50;

    private Paint paint, grayPaint;
    public int senValue1 = 13, senValue2 = 13, senValue3 = 13;

    public CustomView(Context context) {
        super(context);

        init(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomView(Context context, int sen1, int sen2, int sen3) {
        super(context);

        senValue1 = sen1;
        senValue2 = sen2;
        senValue3 = sen3;

        init(context);
    }


    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.RED);

        grayPaint = new Paint();
        grayPaint.setColor(Color.rgb(245, 245, 245));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* int left = 380;
        int right = 450;
        int top = 50;
        int bottom = 100;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue1) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }
            left = left - leftPadding;
            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;


            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }

        left = 460;
        right = 620;
        top = 50;
        bottom = 100;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue2) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }
            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;

            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }

        left = 630;
        right = 700;
        top = 50;
        bottom = 100;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue3) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }
            right = right + rightPadding;
            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;

            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }*/

        int left = 225;
        int right = left + 60;
        int top = 50;
        int bottom = 90;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue1) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }

            if(i > 7) {
                left = left + leftPadding + 15;
            } else {
                left = left - leftPadding;
            }

            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;

            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }

        left = 295;
        right = 415;
        top = 50;
        bottom = 90;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue2) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }
            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;

            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }

        left = 425;
        right = 485;
        top = 50;
        bottom = 90;
        paint.setColor(Color.RED);

        for(int i = 0; i < 12; i++) {
            if(i >= senValue3) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, paint);
            } else {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), round, round, grayPaint);
            }

            if(i > 7) {
                right = right - rightPadding - 15;
            } else {
                right = right + rightPadding;
            }

            top = top + topBottomPadding;
            bottom = bottom + topBottomPadding;

            if(i == 2) {
                paint.setColor(Color.rgb(255, 148, 54));
            } else if(i == 6) {
                paint.setColor(Color.YELLOW);
            }
        }

        invalidate();
    }
}
