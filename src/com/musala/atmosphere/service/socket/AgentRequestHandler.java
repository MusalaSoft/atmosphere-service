package com.musala.atmosphere.service.socket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

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
	 * @return a response to the request.
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

			case GET_BATTERY_LEVEL:
				response = getBatteryLevel();
				break;

			case GET_POWER_STATE:
				response = getPowerState();
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
	 * @return validation response.
	 */
	private Object validate()
	{
		return ServiceRequestProtocol.VALIDATION;
	}

	/**
	 * Gets the battery state of the device.
	 * 
	 * @return the battery state of the device.
	 */
	private Object getBatteryState()
	{
		// TODO implement logic behind getting battery state.

		return BatteryState.UNKNOWN;
	}

	private Object getBatteryLevel()
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, filter);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		Integer batteryLevel = (100 * level) / scale;
		return batteryLevel;
	}

	private Object getPowerState()
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, filter);

		int state = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		// 0 => battery, other => power source connected
		Boolean returnValue = (state != 0);
		return returnValue;
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
