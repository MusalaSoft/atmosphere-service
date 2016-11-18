package com.musala.atmosphere.service;

import java.io.IOException;

import org.apache.log4j.Logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;

import com.musala.atmosphere.commons.ad.service.ConnectionConstants;
import com.musala.atmosphere.commons.ad.service.ServiceRequest;
import com.musala.atmosphere.commons.ad.socket.OnDeviceSocketServer;
import com.musala.atmosphere.service.broadcastreceiver.ServiceControlReceiver;
import com.musala.atmosphere.service.location.LocationMockHandler;
import com.musala.atmosphere.service.logger.Log4JConfigurator;
import com.musala.atmosphere.service.socket.AgentRequestHandler;

/**
 * A service class designed to communicate with the agent and operate on the testing device.
 * 
 * @author yordan.petrov
 * 
 */
public class AtmosphereService extends Service {

    private static final Logger LOGGER = Logger.getLogger(AtmosphereService.class);

    private final static String ATMOSPHERE_SERVICE_CONTROL_INTENT = "com.musala.atmosphere.service.SERVICE_CONTROL";

    private ServiceControlReceiver serviceControlReceiver;

    private static OnDeviceSocketServer<ServiceRequest> serviceSocketServer;

    private AgentRequestHandler agentRequestHandler;

    private LocationMockHandler mockLocationHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log4JConfigurator.configure(getApplicationContext());

        IntentFilter controlIntentFilter = new IntentFilter(ATMOSPHERE_SERVICE_CONTROL_INTENT);
        serviceControlReceiver = new ServiceControlReceiver();
        registerReceiver(serviceControlReceiver, controlIntentFilter);

        Context serviceContext = this.getApplicationContext();
        LocationManager locationManager = (LocationManager) serviceContext.getSystemService(Context.LOCATION_SERVICE);
        mockLocationHandler = new LocationMockHandler(locationManager, serviceContext);
        agentRequestHandler = new AgentRequestHandler(serviceContext, mockLocationHandler);

        try {
            serviceSocketServer = new OnDeviceSocketServer<ServiceRequest>(agentRequestHandler,
                                                                           ConnectionConstants.SERVICE_PORT);
            serviceSocketServer.start();
            LOGGER.info("Service socket server started successfully.");
        } catch (IOException e) {
            LOGGER.error("Could not start ATMOSPHERE socket server", e);
        }

        LOGGER.info("Atmosphere service has been created.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(serviceControlReceiver);

        if (serviceSocketServer != null) {
            serviceSocketServer.terminate();
        }

        mockLocationHandler.disableAllMockProviders();

        LOGGER.info("Atmosphere service has been destroyed.");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Return null since there is no need the service to be binded.
        return null;
    }

}
