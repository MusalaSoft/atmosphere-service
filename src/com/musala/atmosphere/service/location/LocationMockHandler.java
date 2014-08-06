package com.musala.atmosphere.service.location;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
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

    private Context context;

    private Map<String, Thread> providerMockThreads;

    private Map<String, MockLocationRunnable> providerRunnables;

    /**
     * Creates a {@link LocationMockHandler} that mocks locations using a {@link LocationManager} from the passed
     * context.
     * 
     * @param context
     *        - a context for the {@link LocationManager location manager} used to mock locations
     */
    public LocationMockHandler(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
        String mockLocationSettingValue = Settings.Secure.getString(context.getContentResolver(),
                                                                    Settings.Secure.ALLOW_MOCK_LOCATION);
        if (mockLocationSettingValue.equals(DISABLED_MOCK_LOCATION_VALUE)) {
            return false;
        }

        String providerName = location.getProvider();
        if (!providerMockThreads.containsKey(providerName)) {
            // if there is a mock thread, the test provider is already enabled
            if (locationManager.isProviderEnabled(providerName)) {
                locationManager.removeTestProvider(providerName);
            }

            addProvider(providerName);
        }

        startMockThread(location);
        return true;
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
        locationManager.addTestProvider(providerName, false, // does not require network
                                        false, // does not require satellite
                                        false, // does not require cell
                                        false, // no monetary cost
                                        true, // supports altitude
                                        true, // supports bearing
                                        true, // supports speed
                                        Criteria.POWER_LOW,
                                        Criteria.ACCURACY_HIGH);
        locationManager.setTestProviderEnabled(providerName, true);
    }
}
