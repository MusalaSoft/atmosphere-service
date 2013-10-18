package com.musala.atmosphere.service.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A helper class that represents a socket server that specializes in object sending and receiving.
 * 
 * @author georgi.gaydarov
 * 
 */
public class SocketObjectServer
{
	private final ServerSocket serverSocket;

	private InputStream socketInputStream;

	private OutputStream socketOutputStream;

	/**
	 * Starts a new server on a specified port.
	 * 
	 * @param onPort
	 *        - the port on which the server will listen.
	 * @throws IOException
	 */
	public SocketObjectServer(int onPort) throws IOException
	{
		serverSocket = new ServerSocket(onPort);
	}

	/**
	 * Blocks until a connection from a client is established.
	 * 
	 * @throws IOException
	 */
	public void acceptConnection() throws IOException
	{
		Socket socket = serverSocket.accept();
		socketInputStream = socket.getInputStream();
		socketOutputStream = socket.getOutputStream();
	}

	/**
	 * Fetches the next object that the client sends.
	 * 
	 * @return the fetched object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object receiveObject() throws IOException, ClassNotFoundException
	{
		ObjectInputStream objectIn = new ObjectInputStream(socketInputStream);
		Object result = objectIn.readObject();
		return result;
	}

	/**
	 * Sends an object to the client.
	 * 
	 * @param toSend
	 *        - the object to be sent.
	 * @throws IOException
	 */
	public void sendObject(Object toSend) throws IOException
	{
		ObjectOutputStream objectOut = new ObjectOutputStream(socketOutputStream);
		objectOut.flush();
		objectOut.writeObject(toSend);
		objectOut.flush();
	}

	/**
	 * Closes the connection to the client.
	 * 
	 * @throws IOException
	 */
	public void endConnection() throws IOException
	{
		if (socketInputStream != null)
		{
			socketInputStream.close();
			socketInputStream = null;
		}
		if (socketOutputStream != null)
		{
			socketOutputStream.close();
			socketOutputStream = null;
		}
	}
}
