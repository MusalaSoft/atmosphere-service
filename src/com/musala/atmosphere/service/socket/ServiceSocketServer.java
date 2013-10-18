package com.musala.atmosphere.service.socket;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.musala.atmosphere.commons.as.ServiceConstants;
import com.musala.atmosphere.commons.as.ServiceRequest;

/**
 * A socket server that listens for sockets requests by the Agent, sends them to the {@link AgentRequestHandler} and
 * sends its response back to the Agent.
 * 
 * @author yordan.petrov
 * 
 */
public class ServiceSocketServer extends AsyncTask<Void, Void, Void>
{
	public static final String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

	private Context context;

	private AgentRequestHandler agentRequestHandler;

	public ServiceSocketServer(Context context)
	{
		super();

		this.context = context;
		this.agentRequestHandler = new AgentRequestHandler(context);
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		try
		{
			SocketObjectServer objectServer = new SocketObjectServer(ServiceConstants.SERVICE_PORT);
			Log.i(ATMOSPHERE_SERVICE_TAG, "Server started.");

			while (true)
			{
				Log.i(ATMOSPHERE_SERVICE_TAG, "Waiting for connection.");
				objectServer.acceptConnection();

				Log.i(ATMOSPHERE_SERVICE_TAG, "Connection accepted, receiving request.");
				ServiceRequest request = (ServiceRequest) objectServer.receiveObject();

				Log.i(ATMOSPHERE_SERVICE_TAG, "Handling request '" + request + "'.");
				Object response = agentRequestHandler.handle(request);

				Log.i(ATMOSPHERE_SERVICE_TAG, "Sending request response.");
				objectServer.sendObject(response);
				Log.i(ATMOSPHERE_SERVICE_TAG, "Closing connection.");
				objectServer.endConnection();
			}
		}
		catch (IOException e)
		{
			Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
		}
		catch (ClassNotFoundException e)
		{
			Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
		}
		return null;
	}
}
