// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

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
