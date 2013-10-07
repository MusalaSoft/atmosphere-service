package com.musala.atmosphere.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.musala.atmosphere.service.broadcastreceiver.ServiceControlReceiver;
import com.musala.atmosphere.service.socket.ServiceSocketServer;

/**
 * A service class designed to communicate with the agent and operate on the testing device.
 * 
 * @author yordan.petrov
 * 
 */
public class AtmosphereService extends Service
{
	private final static String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

	private final static String ATMOSPHERE_SERVICE_CREATE_INFO = "Atmosphere service has been created.";

	private final static String ATMOSPHERE_SERVICEDESTROY_INFO = "Atmosphere service has been destroyed.";

	private final static String ATMOSPHERE_SERVICE_CONTROL_INTENT = "com.musala.atmosphere.service.SERVICE_CONTROL";

	private ServiceControlReceiver serviceControlReceiver;

	private ServiceSocketServer serviceSocketServer;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		IntentFilter ATMOSPHERE_SERVICEControlIntentFilter = new IntentFilter(ATMOSPHERE_SERVICE_CONTROL_INTENT);
		serviceControlReceiver = new ServiceControlReceiver();
		registerReceiver(serviceControlReceiver, ATMOSPHERE_SERVICEControlIntentFilter);

		Context serviceContext = this.getApplicationContext();
		serviceSocketServer = new ServiceSocketServer(serviceContext);
		serviceSocketServer.execute();

		Log.i(ATMOSPHERE_SERVICE_TAG, ATMOSPHERE_SERVICE_CREATE_INFO);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		unregisterReceiver(serviceControlReceiver);

		serviceSocketServer.cancel(true);

		Log.i(ATMOSPHERE_SERVICE_TAG, ATMOSPHERE_SERVICEDESTROY_INFO);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// Return null since there is no need the service to be binded.
		return null;
	}

}
