package com.musala.atmosphere.service.socket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;

import com.musala.atmosphere.commons.as.ServiceRequestProtocol;

/**
 * Class that handles request from the agent and responds to them.
 * 
 * @author yordan.petrov
 * 
 */
public class AgentRequestHandler
{
	/**
	 * This will be returned when some Intent.getIntExtra() method fails to retrieve the required information.
	 */
	private static final int GET_INT_EXTRA_FAILED_VALUE = -1;

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
				response = setWiFi(true);
				break;

			case SET_WIFI_OFF:
				response = setWiFi(false);
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
	 * @return - integer constant, representing the battery state of the device.
	 */
	private Integer getBatteryState()
	{
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent getBatteryStatusIntent = context.registerReceiver(null, intentFilter);
		Integer status = getBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, GET_INT_EXTRA_FAILED_VALUE);

		return status;
	}

	/**
	 * Gets the battery level of the device.
	 * 
	 * @return - integer constant, representing the battery level of the device.
	 */
	private Integer getBatteryLevel()
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, filter);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, GET_INT_EXTRA_FAILED_VALUE);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, GET_INT_EXTRA_FAILED_VALUE);

		Integer batteryLevel = (100 * level) / scale;
		return batteryLevel;
	}

	/**
	 * Gets the power state of the device.
	 * 
	 * @return - boolean constant, representing the power state of the device. True if the device is connected to power;
	 *         false otherwise.
	 */
	private Boolean getPowerState()
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, filter);

		int state = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, GET_INT_EXTRA_FAILED_VALUE);
		// 0 => battery, other => power source connected
		Boolean returnValue = (state != 0);
		return returnValue;
	}

	/**
	 * Turns on the WiFi of the device.
	 * 
	 * @param state
	 *        true if the WiFi should be on; false if it should be off.
	 * @return a fake response, since we are not requesting any information.
	 */
	private Object setWiFi(boolean state)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(state);

		return ServiceRequestProtocol.FAKE_RESPONSE;
	}
}
