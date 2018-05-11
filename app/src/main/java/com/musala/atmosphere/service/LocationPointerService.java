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

package com.musala.atmosphere.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.service.locationpointerview.LocationPointerConstants;
import com.musala.atmosphere.service.locationpointerview.LocationPointerView;

/**
 * A service class designed to put views with pointers that are representing a tap on the testing device screen.
 * 
 * @author yavor.stankov
 *
 */
public class LocationPointerService extends Service {
    private static final int LOCATION_POINTER_SHOW_TIMEOUMT = 1000;

    private static final int LOCATION_POINTER_DIAMETER = 26;

    private static final int LOCATION_POINTER_RADIUS = 13;

    private WindowManager windowManager;

    private class ShowPointerTask extends AsyncTask<WindowManager.LayoutParams, Void, Void> {
        private View pointerView;

        private WindowManager.LayoutParams params;

        public ShowPointerTask(WindowManager.LayoutParams params) {
            pointerView = new LocationPointerView(LocationPointerService.this);
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            windowManager.addView(pointerView, params);
        }

        @Override
        protected Void doInBackground(WindowManager.LayoutParams... params) {
            try {
                Thread.sleep(LOCATION_POINTER_SHOW_TIMEOUMT);
            } catch (InterruptedException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pointerView != null) {
                windowManager.removeView(pointerView);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Point touchLocation = (Point) intent.getSerializableExtra(LocationPointerConstants.CENTER_POINT_INTENT_NAME.getValue());

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(densityIndependentPixelToPixel(LOCATION_POINTER_DIAMETER),
                                                                           densityIndependentPixelToPixel(LOCATION_POINTER_DIAMETER),
                                                                           WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                                                                           WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                                                   | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                                                   | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                                                                           PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = touchLocation.getX() - densityIndependentPixelToPixel(LOCATION_POINTER_RADIUS);
        params.y = (touchLocation.getY() - densityIndependentPixelToPixel(LOCATION_POINTER_RADIUS))
                - getStatusBarHeight();

        ShowPointerTask locationPointerTask;

        locationPointerTask = new ShowPointerTask(params);
        locationPointerTask.execute(params);

        return START_NOT_STICKY;
    }

    /**
     * Converts density independent pixels to pixels.
     * 
     * @param densityIndependentPixel
     *        - density independent pixels to be converted
     * @return the given density independent pixels converted to pixels
     */
    private int densityIndependentPixelToPixel(int densityIndependentPixel) {
        return (int) (densityIndependentPixel * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Calculate the height of the device status bar.
     * 
     * @return the height of the device status bar
     */
    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);

        return statusBarHeight;
    }
}
