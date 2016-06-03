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

            locationManager.setTestProviderLocation(providerName, mockLocation);

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
