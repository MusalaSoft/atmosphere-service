package com.musala.atmosphere.service.socket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.TelephonyInformation;
import com.musala.atmosphere.commons.ad.Request;
import com.musala.atmosphere.commons.ad.RequestHandler;
import com.musala.atmosphere.commons.ad.service.ServiceRequest;
import com.musala.atmosphere.commons.beans.BatteryLevel;
import com.musala.atmosphere.commons.beans.BatteryState;
import com.musala.atmosphere.commons.beans.PowerSource;
import com.musala.atmosphere.commons.util.telephony.CallState;
import com.musala.atmosphere.commons.util.telephony.DataActivity;
import com.musala.atmosphere.commons.util.telephony.DataState;
import com.musala.atmosphere.commons.util.telephony.NetworkType;
import com.musala.atmosphere.commons.util.telephony.PhoneType;
import com.musala.atmosphere.commons.util.telephony.SimState;
import com.musala.atmosphere.service.helpers.OrientationFetchingHelper;
import com.musala.atmosphere.service.sensoreventlistener.AccelerationEventListener;

/**
 * Class that handles request from the agent and responds to them.
 * 
 * @author yordan.petrov
 * 
 */
public class AgentRequestHandler implements RequestHandler<ServiceRequest> {

    /**
     * This will be returned when some Intent.getIntExtra() method fails to retrieve the required information.
     */
    private static final int GET_INT_EXTRA_FAILED_VALUE = -1;

    private Context context;

    public AgentRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public Object handle(Request<ServiceRequest> socketServerRequest) {
        ServiceRequest requestType = socketServerRequest.getType();
        Object[] arguments = socketServerRequest.getArguments();

        Object response;
        switch (requestType) {
            case VALIDATION:
                response = validate();
                break;

            case GET_POWER_PROPERTIES:
                response = getPowerProperties();
                break;

            case GET_CONNECTION_TYPE:
                response = getConnectionType();
                break;

            case GET_ORIENTATION_READINGS:
                response = getOrientationReadings();
                break;

            case SET_WIFI:
                response = setWiFi(arguments);
                break;

            case GET_ACCELERATION_READINGS:
                response = getAcceleration();
                break;

            case GET_TELEPHONY_INFORMATION:
                response = getTelephonyInformation();
                break;

            case START_APP:
                response = startApplication(arguments);
                break;

            case GET_AWAKE_STATUS:
                response = isAwake();
                break;

            default:
                response = ServiceRequest.ANY_RESPONSE;
                break;
        }

        return response;
    }

    /**
     * Checks if the device is awake.
     * 
     * @return <code>true</code> if the device is awake, and <code>false</code> otherwise
     */
    private boolean isAwake() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isAwake = powerManager.isScreenOn();
        return isAwake;
    }

    /**
     * Returns response to a validation request.
     * 
     * @return validation response.
     */
    private Object validate() {
        return ServiceRequest.VALIDATION;
    }

    /**
     * Gets the power environment properties of the device.
     * 
     * @return a {@llink PowerProperties} data container instance.
     */
    private PowerProperties getPowerProperties() {
        PowerProperties properties = new PowerProperties();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent powerEnvironmentIntent = context.registerReceiver(null, intentFilter);

        // battery state extraction
        Integer status = powerEnvironmentIntent.getIntExtra(BatteryManager.EXTRA_STATUS, GET_INT_EXTRA_FAILED_VALUE);
        if (status != GET_INT_EXTRA_FAILED_VALUE) {
            BatteryState currentBatteryState = BatteryState.getStateById(status);
            properties.setBatteryState(currentBatteryState);
        }

        // battery level extraction
        int level = powerEnvironmentIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, GET_INT_EXTRA_FAILED_VALUE);
        int scale = powerEnvironmentIntent.getIntExtra(BatteryManager.EXTRA_SCALE, GET_INT_EXTRA_FAILED_VALUE);
        Integer batteryLevel = 100 * level / scale;
        properties.setBatteryLevel(new BatteryLevel(batteryLevel));

        // power connected state extraction
        int powerSourceInt = powerEnvironmentIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                                                                GET_INT_EXTRA_FAILED_VALUE);
        if (powerSourceInt != GET_INT_EXTRA_FAILED_VALUE) {
            // 0 => battery, other => power source connected
            PowerSource source = PowerSource.getStateById(powerSourceInt);
            properties.setPowerSource(source);
        }

        return properties;
    }

    /**
     * Starts an application identified by its package name on the device.
     */
    private boolean startApplication(Object[] args) {
        PackageManager packageManager = context.getPackageManager();

        String packageName = (String) args[0];
        Intent appStartIntent = packageManager.getLaunchIntentForPackage(packageName);

        if (appStartIntent != null) {
            context.startActivity(appStartIntent);
            return true;
        }

        return false;
    }

    private float[] getOrientationReadings() {
        OrientationFetchingHelper helper = new OrientationFetchingHelper(context);
        while (!helper.isReady()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // dont care
            }
        }
        float[] orientation = helper.getOrientation();

        return orientation;
    }

    /**
     * Gets the connection type of the device.
     * 
     * @return - connection type identifier.
     */
    private Integer getConnectionType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // TODO consider returning a NetworkInfo object. Getting the network type does not assure the network is
        // connected, connecting or available.

        if (networkInfo != null) {
            Integer networkType = networkInfo.getType();
            return networkType;
        }

        return -1;
    }

    /**
     * Turns on the WiFi of the device.
     * 
     * @param state
     *        true if the WiFi should be on; false if it should be off.
     * @return a fake response, since we are not requesting any information.
     */
    private Object setWiFi(Object[] arguments) {
        boolean state = (Boolean) arguments[0];

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);

        return ServiceRequest.ANY_RESPONSE;
    }

    /**
     * Gets the acceleration of the device.
     * 
     * @return the acceleration of the device.
     */
    private Object getAcceleration() {
        AccelerationEventListener accelerationListener = new AccelerationEventListener(context);
        accelerationListener.register();

        while (!accelerationListener.isMeasured()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return accelerationListener.getAcceleration();
    }

    /**
     * Obtains information about the telephony services on the device.
     * 
     * @return {@link TelephonyInformation} instance.
     */
    private Object getTelephonyInformation() {
        TelephonyInformation telephonyInformation = new TelephonyInformation();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int callStateId = telephonyManager.getCallState();
        int dataActivityId = telephonyManager.getDataActivity();
        int dataStateId = telephonyManager.getDataState();
        int networkTypeId = telephonyManager.getNetworkType();
        int phoneTypeId = telephonyManager.getPhoneType();
        int simStateId = telephonyManager.getSimState();

        String deviceId = telephonyManager.getDeviceId();
        String deviceSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
        String line1Number = telephonyManager.getLine1Number();
        String networkCountryIso = telephonyManager.getNetworkCountryIso();
        String networkOperator = telephonyManager.getNetworkOperator();
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        String simOperator = telephonyManager.getSimOperator();
        String simOperatorName = telephonyManager.getSimOperatorName();
        String subscriberId = telephonyManager.getSubscriberId();
        String voiceMailAlphaTag = telephonyManager.getVoiceMailAlphaTag();
        String voiceMailNumber = telephonyManager.getVoiceMailNumber();

        CallState callState = CallState.getById(callStateId);
        DataActivity dataActivity = DataActivity.getById(dataActivityId);
        DataState dataState = DataState.getById(dataStateId);
        NetworkType networkType = NetworkType.getById(networkTypeId);
        PhoneType phoneType = PhoneType.getById(phoneTypeId);
        SimState simState = SimState.getById(simStateId);

        telephonyInformation.setCallState(callState);
        telephonyInformation.setDataActivity(dataActivity);
        telephonyInformation.setDataState(dataState);
        telephonyInformation.setDeviceId(deviceId);
        telephonyInformation.setDeviceSoftwareVersion(deviceSoftwareVersion);
        telephonyInformation.setLine1Number(line1Number);
        telephonyInformation.setNetworkCountryIso(networkCountryIso);
        telephonyInformation.setNetworkOperator(networkOperator);
        telephonyInformation.setNetworkOperatorName(networkOperatorName);
        telephonyInformation.setNetworkType(networkType);
        telephonyInformation.setPhoneType(phoneType);
        telephonyInformation.setSimOperator(simOperator);
        telephonyInformation.setSimOperatorName(simOperatorName);
        telephonyInformation.setSimState(simState);
        telephonyInformation.setSubscriberId(subscriberId);
        telephonyInformation.setVoiceMailAlphaTag(voiceMailAlphaTag);
        telephonyInformation.setVoiceMailNumber(voiceMailNumber);

        return telephonyInformation;
    }
}
