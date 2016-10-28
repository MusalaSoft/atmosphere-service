package com.musala.atmosphere.service.location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.ContentResolver;
import android.location.Criteria;
import android.location.LocationManager;
import android.provider.Settings;

import com.musala.atmosphere.commons.util.GeoLocation;

/**
 * A location provider class used to mock locations on the device.
 * 
 * @author delyan.dimitrov
 * 
 */
public class LocationMockHandler {
    private static final long MOCK_TIMEOUT = 5000;

    private static final String DISABLED_MOCK_LOCATION_VALUE = "0";

    private LocationManager locationManager;

    private ContentResolver contentResolver;

    private Map<String, Thread> providerMockThreads;

    private Map<String, MockLocationRunnable> providerRunnables;

    /**
     * Creates a {@link LocationMockHandler} that mocks locations using the passed {@link LocationManager}.
     * 
     * @param locationManager
     *        - the location manager used to mock locations on test providers
     */
    public LocationMockHandler(LocationManager locationManager, ContentResolver contentResolver) {
        this.locationManager = locationManager;
        this.contentResolver = contentResolver;
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
        String mockLocationSettingValue = Settings.Secure.getString(contentResolver,
                                                                    Settings.Secure.ALLOW_MOCK_LOCATION);
        if (mockLocationSettingValue.equals(DISABLED_MOCK_LOCATION_VALUE)) {
            return false;
        }

        String providerName = location.getProvider();
        addProvider(providerName);
        startMockThread(location);

        return true;
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
