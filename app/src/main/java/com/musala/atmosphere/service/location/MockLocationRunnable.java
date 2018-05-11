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

package com.musala.atmosphere.service.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.musala.atmosphere.commons.util.GeoLocation;

/**
 * A runnable that sets a mock location to a given provider over time.
 * 
 * @author delyan.dimitrov
 * 
 */
public class MockLocationRunnable implements Runnable {
    private long mockTimeout;

    private GeoLocation location;

    private LocationManager locationManager;

    private volatile boolean isMockLocationEnabled = true;

    /**
     * Creates a runnable that mocks the location sent by a provider in a given location manager.
     * 
     * @param locationManager
     *        - location manager to mock the location on
     * @param mockLocation
     *        - the location that should be mocked
     */
    public MockLocationRunnable(LocationManager locationManager, GeoLocation mockLocation, long mockTimeout) {
        this.locationManager = locationManager;
        this.location = mockLocation;
        this.mockTimeout = mockTimeout;
    }

    @Override
    public void run() {
        while (isMockLocationEnabled) {
            String providerName = null;
            Location mockLocation = null;

            synchronized (location) {
                providerName = location.getProvider();
                mockLocation = GeoLocationConverter.getLocation(location);
            }

            // stamp the converted location with the current time
            mockLocation.setTime(System.currentTimeMillis());
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

            try {
                locationManager.setTestProviderLocation(providerName, mockLocation);
            } catch (SecurityException e) {
                // the permission to mock the location was revoked, so stop mocking
                terminate();
            }

            try {
                Thread.sleep(mockTimeout);
            } catch (InterruptedException e) {
                // nothing to do here
            }
        }
    }

    /**
     * Stops sending fake locations in the location manager. If sending location data is in progress, it will be
     * completed before the runnable is terminated.
     */
    public synchronized void terminate() {
        isMockLocationEnabled = false;
    }

    /**
     * Changes the mocked location by this runnable.
     * 
     * @param location
     *        - the new location to mock
     */
    public void changeLocation(GeoLocation location) {
        synchronized (this.location) {
            this.location = location;
        }
    }
}
