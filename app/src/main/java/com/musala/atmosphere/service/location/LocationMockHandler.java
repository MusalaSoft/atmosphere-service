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

import android.app.AppOpsManager;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.musala.atmosphere.commons.util.GeoLocation;
import com.musala.atmosphere.service.BuildConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A location provider class used to mock locations on the device.
 * 
 * @author delyan.dimitrov
 * 
 */
public class LocationMockHandler {
    private static final long MOCK_TIMEOUT = 5000;

    private static final String ENABLED_MOCK_LOCATION_VALUE = "1";

    private LocationManager locationManager;

    private Context context;

    private Map<String, Thread> providerMockThreads;

    private Map<String, MockLocationRunnable> providerRunnables;

    /**
     * Creates a {@link LocationMockHandler} that mocks locations using the passed {@link LocationManager}.
     * 
     * @param locationManager
     *        - the location manager used to mock locations on test providers
     */
    public LocationMockHandler(LocationManager locationManager, Context context) {
        this.locationManager = locationManager;
        this.context = context;
        providerMockThreads = new HashMap<String, Thread>();
        providerRunnables = new HashMap<String, MockLocationRunnable>();
    }

    /**
     * Mocks the location of the device using the given one.
     * 
     * @param location
     *        - the mock location
     * @return <code>true</code> if mocking was successful, and <code>false</code> otherwise
     */
    public boolean mockLocation(GeoLocation location) {
        if (!isMockLocationEnabled()) {
            return false;
        }

        String providerName = location.getProvider();
        addProvider(providerName);
        startMockThread(location);

        return true;
    }

    private boolean isMockLocationEnabled() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                return (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION,
                        android.os.Process.myUid(),
                        BuildConfig.APPLICATION_ID)
                        == AppOpsManager.MODE_ALLOWED);
            } else {
                String mockLocationSettingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
                return (mockLocationSettingValue.equals(ENABLED_MOCK_LOCATION_VALUE));
            }
        } catch (SecurityException e) {
            return false;
        }
    }

    /**
     * Stops the thread sending location data for the given test provider.
     * 
     * @param providerName
     *        - the provider to be disabled
     */
    public void disableMockLocation(String providerName) {
        if (providerMockThreads.containsKey(providerName)) {
            MockLocationRunnable providerMockRunnable = providerRunnables.remove(providerName);
            Thread providerMockThread = providerMockThreads.remove(providerName);

            providerMockRunnable.terminate();
            try {
                providerMockThread.join(2 * MOCK_TIMEOUT);
            } catch (InterruptedException e) {
                // nothing to do here
            }

            locationManager.removeTestProvider(providerName);
        }
    }

    /**
     * Stops the threads sending location data for all test providers enabled by the this handler.
     */
    public void disableAllMockProviders() {
        Set<String> testProviders = new HashSet<String>(providerMockThreads.keySet());
        for (String testProviderName : testProviders) {
            disableMockLocation(testProviderName);
        }
    }

    /**
     * Starts a thread that periodically sends the passed location data. If one already exists for this provider, the
     * location it sends is replaced with the passed location.
     * 
     * @param location
     *        - the fake location data to be sent
     */
    private void startMockThread(GeoLocation location) {
        String providerName = location.getProvider();

        if (providerRunnables.containsKey(providerName)) {
            MockLocationRunnable mockRunnable = providerRunnables.get(providerName);
            mockRunnable.changeLocation(location);
        } else {
            MockLocationRunnable mockRunnable = new MockLocationRunnable(locationManager, location, MOCK_TIMEOUT);
            providerRunnables.put(providerName, mockRunnable);

            Thread mockThread = new Thread(mockRunnable);
            providerMockThreads.put(providerName, mockThread);

            mockThread.start();
        }
    }

    /**
     * Registers a test provider with the given name on the location manager.
     * 
     * @param providerName
     *        - the name of the test provider to be registered
     */
    private void addProvider(String providerName) {
        try {
            locationManager.addTestProvider(providerName, false, // does not require network
                                            false, // does not require satellite
                                            false, // does not require cell
                                            false, // no monetary cost
                                            true, // supports altitude
                                            true, // supports bearing
                                            true, // supports speed
                                            Criteria.POWER_LOW,
                                            Criteria.ACCURACY_HIGH);
        } catch (IllegalArgumentException e) {
            // guarding from registering a test provider that is already registered
        }

        locationManager.setTestProviderEnabled(providerName, true);
    }
}
