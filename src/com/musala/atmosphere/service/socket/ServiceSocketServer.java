package com.musala.atmosphere.service.socket;

import java.io.IOException;
import java.net.SocketException;

import android.util.Log;

import com.musala.atmosphere.commons.ad.DeviceSocketServer;
import com.musala.atmosphere.commons.ad.Request;
import com.musala.atmosphere.commons.ad.service.ServiceRequest;

/**
 * A socket server that listens for sockets requests by the Agent, sends them to the {@link AgentRequestHandler} and
 * sends its response back to the Agent.
 * 
 * @author yordan.petrov
 * 
 */
public class ServiceSocketServer {
    private static final int SOCKET_THREAD_KILL_TIMEOUT = 1000;

    public static final String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

    private SocketServerRunnable runnable;

    private Thread socketServerThread;

    /**
     * Inner class, responsible for receiving and handling agent requests while the atmosphere service is running.
     */
    private static class SocketServerRunnable implements Runnable {

        private DeviceSocketServer<ServiceRequest> serviceRequestServer;

        private volatile boolean isRunning;

        public SocketServerRunnable(AgentRequestHandler agentRequestHandler, int port) throws IOException {
            serviceRequestServer = new DeviceSocketServer<ServiceRequest>(agentRequestHandler, port);
            isRunning = false;
        }

        @Override
        public void run() {
            isRunning = true;

            while (isRunning) {
                try {
                    Log.i(ATMOSPHERE_SERVICE_TAG, "Waiting for connection.");
                    serviceRequestServer.acceptConnection();

                    Log.i(ATMOSPHERE_SERVICE_TAG, "Connection accepted, receiving request.");
                    Request<ServiceRequest> request = serviceRequestServer.handle();

                    Log.i(ATMOSPHERE_SERVICE_TAG, "Handled request '" + request.getType() + "'.");

                    serviceRequestServer.endConnection();
                    Log.i(ATMOSPHERE_SERVICE_TAG, "Connection closed.");
                } catch (SocketException se) {
                    Log.d(ATMOSPHERE_SERVICE_TAG, "Error in connection : " + se.getMessage());
                } catch (IOException ioe) {
                    Log.d(ATMOSPHERE_SERVICE_TAG, "Error in I/O: " + ioe.getMessage());
                } catch (ClassNotFoundException e) {
                    Log.d(ATMOSPHERE_SERVICE_TAG, "Update your dependency jars for the project.", e);
                }
            }
        }

        private void terminate() {
            isRunning = false;
        }

        private boolean isRunning() {
            return isRunning;
        }

        private void terminateSocketServer() throws IOException {
            serviceRequestServer.stop();
        }
    }

    public ServiceSocketServer(AgentRequestHandler agentRequestHandler, int port) throws IOException {
        runnable = new SocketServerRunnable(agentRequestHandler, port);
    }

    public void start() throws IOException {

        boolean isSocketThreadInitialized = (socketServerThread != null && socketServerThread.isAlive());
        boolean isSocketServerRunning = runnable.isRunning();

        if (!isSocketServerRunning && !isSocketThreadInitialized) {
            socketServerThread = new Thread(runnable);
            socketServerThread.start();
            Log.i(ATMOSPHERE_SERVICE_TAG, "Started network thread.");
        } else {
            Log.e(ATMOSPHERE_SERVICE_TAG, "Failed to start network thread.");
        }
    }

    public void terminate() {
        Log.wtf(ATMOSPHERE_SERVICE_TAG, "Stopping socket server...");
        runnable.terminate();

        if (socketServerThread != null && socketServerThread.isAlive()) {
            try {
                socketServerThread.join(SOCKET_THREAD_KILL_TIMEOUT);
                Log.d(ATMOSPHERE_SERVICE_TAG, "Network thread should be stopped now...");
            } catch (InterruptedException e) {
                Log.d(ATMOSPHERE_SERVICE_TAG, "Error stopping network thread...", e);
            }
        }

        try {
            runnable.terminateSocketServer();
            Log.e(ATMOSPHERE_SERVICE_TAG, "Socket connection stopped successfully!");
        } catch (IOException e) {
            Log.e(ATMOSPHERE_SERVICE_TAG, "Could not close opened connection.");
        }
    }

}
