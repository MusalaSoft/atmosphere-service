package com.musala.atmosphere.service.socket;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.musala.atmosphere.commons.ad.DeviceSocketServer;
import com.musala.atmosphere.commons.ad.Request;
import com.musala.atmosphere.commons.ad.service.ServiceConstants;
import com.musala.atmosphere.commons.ad.service.ServiceRequest;

/**
 * A socket server that listens for sockets requests by the Agent, sends them to the {@link AgentRequestHandler} and
 * sends its response back to the Agent.
 * 
 * @author yordan.petrov
 * 
 */
public class ServiceSocketServer extends AsyncTask<Void, Void, Void> {
    public static final String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

    private DeviceSocketServer<ServiceRequest> serviceRequestServer;

    public ServiceSocketServer(Context context) throws IOException {
        super();

        int port = ServiceConstants.SERVICE_PORT;
        AgentRequestHandler agentRequestHandler = new AgentRequestHandler(context);
        serviceRequestServer = new DeviceSocketServer<ServiceRequest>(agentRequestHandler, port);

        Log.i(ATMOSPHERE_SERVICE_TAG, "Server started.");
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            while (true) {
                Log.i(ATMOSPHERE_SERVICE_TAG, "Waiting for connection.");
                serviceRequestServer.acceptConnection();

                Log.i(ATMOSPHERE_SERVICE_TAG, "Connection accepted, receiving request.");
                Request<ServiceRequest> request = serviceRequestServer.handle();

                Log.i(ATMOSPHERE_SERVICE_TAG, "Handled request '" + request.getType() + "'.");

                serviceRequestServer.endConnection();
                Log.i(ATMOSPHERE_SERVICE_TAG, "Connection closed.");
            }
        } catch (IOException e) {
            Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
        } catch (ClassNotFoundException e) {
            Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
        }
        return null;
    }
}
