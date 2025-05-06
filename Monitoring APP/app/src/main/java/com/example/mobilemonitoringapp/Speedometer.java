package com.example.mobilemonitoringapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Speedometer extends View {
    private static final int START_ANGLE = 150;
    private static final int SWEEP_DEGREES = 240;
    private int arcPercent = 0;
    private Paint arcPaint, line;

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        arcPaint = new Paint();
        arcPaint.setColor(Color.TRANSPARENT);
        arcPaint.setAntiAlias(true);

        line = new Paint();
        line.setColor(Color.BLACK);
        line.setStrokeWidth(5);
        line.setAntiAlias(true);
    }

    public void redrawLine(int percent) {
        this.arcPercent = percent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 50;

        float arcAngle = START_ANGLE + ((float) arcPercent / 100) * SWEEP_DEGREES;
        double radian = Math.toRadians(arcAngle);
        int length = radius + 40;

        int endX = (int) (centerX + length * Math.cos(radian));
        int endY = (int) (centerY + length * Math.sin(radian));

        RectF oval = new RectF(50, 50, getWidth() - 50, getHeight() - 50);

        Paint paint = new Paint();
        paint.setStrokeWidth(30);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        paint.setColor(Color.GREEN);
        canvas.drawArc(oval, 150, 80, false, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawArc(oval, 230, 80, false, paint);
        paint.setColor(Color.RED);
        canvas.drawArc(oval, 310, 80, false, paint);

        canvas.drawArc(oval, START_ANGLE, SWEEP_DEGREES, false, arcPaint);
        canvas.drawLine(centerX, centerY, endX, endY, line);
    }
}
