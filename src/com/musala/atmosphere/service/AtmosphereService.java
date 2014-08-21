package com.musala.atmosphere.service;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.musala.atmosphere.commons.ad.service.ServiceConstants;
import com.musala.atmosphere.service.broadcastreceiver.ServiceControlReceiver;
import com.musala.atmosphere.service.location.LocationMockHandler;
import com.musala.atmosphere.service.socket.AgentRequestHandler;
import com.musala.atmosphere.service.socket.ServiceSocketServer;

/**
 * A service class designed to communicate with the agent and operate on the testing device.
 * 
 * @author yordan.petrov
 * 
 */
public class AtmosphereService extends Service {
    private final static String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

    private final static String ATMOSPHERE_SERVICE_CREATE_INFO = "Atmosphere service has been created.";

    private final static String ATMOSPHERE_SERVICEDESTROY_INFO = "Atmosphere service has been destroyed.";

    private final static String ATMOSPHERE_SERVICE_CONTROL_INTENT = "com.musala.atmosphere.service.SERVICE_CONTROL";

    private ServiceControlReceiver serviceControlReceiver;

    private static ServiceSocketServer serviceSocketServer;

    private AgentRequestHandler agentRequestHandler;

    private LocationMockHandler mockLocationHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter controlIntentFilter = new IntentFilter(ATMOSPHERE_SERVICE_CONTROL_INTENT);
        serviceControlReceiver = new ServiceControlReceiver();
        registerReceiver(serviceControlReceiver, controlIntentFilter);

        Context serviceContext = this.getApplicationContext();
        LocationManager locationManager = (LocationManager) serviceContext.getSystemService(Context.LOCATION_SERVICE);
        mockLocationHandler = new LocationMockHandler(locationManager, serviceContext.getContentResolver());
        agentRequestHandler = new AgentRequestHandler(serviceContext, mockLocationHandler);

        try {
            serviceSocketServer = new ServiceSocketServer(agentRequestHandler, ServiceConstants.SERVICE_PORT);
            serviceSocketServer.start();
            Log.i(ATMOSPHERE_SERVICE_TAG, "Service socket server started successfully.");
        } catch (IOException e) {
            Log.e(ATMOSPHERE_SERVICE_TAG, "Could not start ATMOSPHERE socket server", e);
        }

        Log.i(ATMOSPHERE_SERVICE_TAG, ATMOSPHERE_SERVICE_CREATE_INFO);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(serviceControlReceiver);

        if (serviceSocketServer != null) {
            serviceSocketServer.terminate();
        }

        mockLocationHandler.disableAllMockProviders();

        Log.i(ATMOSPHERE_SERVICE_TAG, ATMOSPHERE_SERVICEDESTROY_INFO);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Return null since there is no need the service to be binded.
        return null;
    }

}
