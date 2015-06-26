package com.musala.atmosphere.service.locationpointerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * View class designed to draw circles.
 * 
 * @author yavor.stankov
 *
 */
public class LocationPointerView extends View {
    private static final int CIRCLE_COLOR_ALPHA_VALUE = 150;

    public LocationPointerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LocationPointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationPointerView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float circleCenterXCoordinate = getWidth() / 2;
        float circleCenterYCoordinate = getHeight() / 2;
        float radius = Math.min(circleCenterXCoordinate, circleCenterYCoordinate);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        paint.setAlpha(CIRCLE_COLOR_ALPHA_VALUE);

        canvas.drawCircle(circleCenterXCoordinate, circleCenterYCoordinate, radius, paint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
