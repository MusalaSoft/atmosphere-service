package com.musala.atmosphere.service.socket;

import android.content.Context;

import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.as.ServiceRequestProtocol;

/**
 * Class that handles request from the agent and responds to them.
 * 
 * @author yordan.petrov
 * 
 */
public class AgentRequestHandler
{
	private Context context;

	public AgentRequestHandler(Context context)
	{
		this.context = context;
	}

	/**
	 * Handles requests and returns responses.
	 * 
	 * @param socketServerRequest
	 * @return - a response to the request.
	 */
	public Object handle(ServiceRequestProtocol socketServerRequest)
	{
		Object response;
		switch (socketServerRequest)
		{
			case VALIDATION:
				response = validate();
				break;

			case GET_BATTERY_STATE:
				response = getBatteryState();
				break;

			case SET_WIFI_ON:
				response = setWiFiOn();
				break;

			default:
				response = new Object();
				break;
		}

		return response;
	}

	/**
	 * Returns response to a validation request.
	 * 
	 * @return - validation response.
	 */
	private Object validate()
	{
		return ServiceRequestProtocol.VALIDATION;
	}

	/**
	 * Gets the battery state of the device.
	 * 
	 * @return - the battery state of the device.
	 */
	private Object getBatteryState()
	{
		// TODO implement logic behind getting battery state.

		return BatteryState.UNKNOWN;
	}

	/**
	 * Turns on the WiFi of the device.
	 * 
	 * @return
	 */
	private Object setWiFiOn()
	{
		// TODO implement logic behind setting WiFi state.

		return null;
	}
}
