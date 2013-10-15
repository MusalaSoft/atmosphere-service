package com.musala.atmosphere.service.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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
	private static final String ATMOSPHERE_SERVICE_TAG = "AtmosphereService";

	private Context context;

	private AgentRequestHandler agentRequestHandler;

	private ServerSocketChannel serverSocketChannel;

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
			initializeServer();

			while (true)
			{
				listen();
			}
		}
		catch (IOException e)
		{
			Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
		}

		return null;
	}

	/**
	 * Starts a socket server.
	 * 
	 * @throws IOException
	 */
	private void initializeServer() throws IOException
	{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(true);
		serverSocketChannel.socket().bind(new InetSocketAddress(ServiceConstants.SERVICE_PORT));

		Log.i(ATMOSPHERE_SERVICE_TAG, "Server started.");
	}

	/**
	 * Listens for requests, passes the to the {@link AgentRequestHandler} and returns the reply.
	 * 
	 * @throws IOException
	 */
	private void listen() throws IOException
	{
		SocketChannel socketChannel = serverSocketChannel.accept();

		ObjectInputStream socketServerInputStream = null;
		ObjectOutputStream socketServerOutputStream = null;

		try
		{
			Socket baseSocket = socketChannel.socket();
			socketServerInputStream = new ObjectInputStream(baseSocket.getInputStream());
			ServiceRequest request = (ServiceRequest) socketServerInputStream.readObject();

			Object response = agentRequestHandler.handle(request);

			socketServerOutputStream = new ObjectOutputStream(baseSocket.getOutputStream());
			socketServerOutputStream.writeObject(response);
			socketServerOutputStream.flush();
		}
		catch (EOFException e)
		{
			Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
		}
		catch (ClassNotFoundException e)
		{
			Log.wtf(ATMOSPHERE_SERVICE_TAG, e);
		}
		finally
		{
			if (socketServerInputStream != null)
			{
				socketServerInputStream.close();
			}
			if (socketServerOutputStream != null)
			{
				socketServerOutputStream.close();
			}
			if (socketChannel != null)
			{
				socketChannel.close();
			}
		}
	}
}
