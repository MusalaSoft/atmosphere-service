package com.musala.atmosphere.service.socket;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioManager;
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
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.util.AtmosphereIntent;
import com.musala.atmosphere.commons.util.GeoLocation;
import com.musala.atmosphere.commons.util.telephony.CallState;
import com.musala.atmosphere.commons.util.telephony.DataActivity;
import com.musala.atmosphere.commons.util.telephony.DataState;
import com.musala.atmosphere.commons.util.telephony.NetworkType;
import com.musala.atmosphere.commons.util.telephony.PhoneType;
import com.musala.atmosphere.commons.util.telephony.SimState;
import com.musala.atmosphere.service.LocationPointerService;
import com.musala.atmosphere.service.helpers.OrientationFetchingHelper;
import com.musala.atmosphere.service.location.LocationMockHandler;
import com.musala.atmosphere.service.locationpointerview.LocationPointerConstants;
import com.musala.atmosphere.service.sensoreventlistener.AccelerationEventListener;
import com.musala.atmosphere.service.sensoreventlistener.ProximityEventListener;

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

    private static final int PROXIMITY_MEASURED_TIMEOUT = 10;

    private static final long BYTES_TO_MB = 1048576L;

    private final Context context;

    private final LocationMockHandler locationProvider;

    KeyguardManager.KeyguardLock keyguardLock;

    public AgentRequestHandler(Context context, LocationMockHandler locationProvider) {
        this.context = context;
        this.locationProvider = locationProvider;

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("AtmosphereKeyguardLock");
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

            case GET_PROXIMITY_READINGS:
                response = getProximity();
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

            case GET_CAMERA_AVAILABILITY:
                response = hasCamera();
                break;

            case GET_PROCESS_RUNNING:
                response = isProcessRunning(arguments);
                break;

            case GET_TOTAL_RAM:
                response = getTotalRam();
                break;

            case SET_KEYGUARD:
                response = setKeyguard(arguments);
                break;

            case IS_LOCKED:
                response = isLocked();
                break;

            case BRING_TASK_TO_FRONT:
                response = bringTaskToFront(arguments);
                break;

            case GET_RUNNING_TASK_IDS:
                response = getRunningTaskIds(arguments);
                break;

            case WAIT_FOR_TASKS_UPDATE:
                response = waitForTasksUpdate(arguments);
                break;

            case MOCK_LOCATION:
                response = mockLocation(arguments);
                break;

            case DISABLE_MOCK_LOCATION:
                response = disalbeMockLocation(arguments);
                break;

            case SEND_BROADCAST:
                response = sendBroadcast(arguments);
                break;
            case OPEN_LOCATION_SETTINGS:
                response = openLocationSettings();
                break;
            case IS_GPS_LOCATION_ENABLED:
                response = isGpsLocationEnabled();
                break;
            case SHOW_TAP_LOCATION:
                response = showTapLocation(arguments);
                break;
            case IS_AUDIO_PLAYING:
                response = isAudioPlaying();
                break;
            default:
                response = ServiceRequest.ANY_RESPONSE;
                break;
        }

        return response;
    }

    /**
     * Mocks the current location of the device with the one passed.
     * 
     * @param arguments
     *        - a {@link GeoLocation} object that is the location to be mocked
     * @return <code>true</code> if mocking was successful, and <code>false</code> otherwise
     */
    private boolean mockLocation(Object[] arguments) {
        return locationProvider.mockLocation((GeoLocation) arguments[0]);
    }

    /**
     * Disables mocking location for the given provider.
     * 
     * @param arguments
     *        - a {@link String} object that is the provider name
     * @return {@link ServiceRequest#ANY_RESPONSE}
     */
    private Object disalbeMockLocation(Object[] arguments) {
        locationProvider.disableMockLocation((String) arguments[0]);
        return ServiceRequest.ANY_RESPONSE;
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
     * @return a {@link PowerProperties} data container instance.
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

    /**
     * Checks if there are any running processes on the device with the given package.
     * 
     * @return - true if there are running process with the given package and false otherwise.
     */
    private boolean isProcessRunning(Object[] args) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
        String packageName = (String) args[0];
        for (ActivityManager.RunningAppProcessInfo currentRunningProcess : runningProcesses) {
            if (packageName.equals(currentRunningProcess.processName))
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
     * Gets the proximity of the device.
     * 
     * @return a float instance representing the proximity of the device
     */
    private Object getProximity() {
        ProximityEventListener proximityListener = new ProximityEventListener(context);
        proximityListener.register();

        while (!proximityListener.isMeasured()) {
            try {
                Thread.sleep(PROXIMITY_MEASURED_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return proximityListener.getProximity();
    }

    /**
     * Dismisses and re enables the keyguard of the device in order to Lock and Unlock it.
     * 
     * @param locked
     *        - <code>true</code> if the keyguard should be re-enabled and <code>false</code> to dismiss it.
     * @return a {@link ServiceRequest#ANY_RESPONSE}, since we are not requesting any information.
     */
    @SuppressWarnings("deprecation")
    private Object setKeyguard(Object[] args) {
        boolean keyguardState = (Boolean) args[0];

        if (keyguardState) {
            keyguardLock.reenableKeyguard();
        } else {
            keyguardLock.disableKeyguard();
        }

        return ServiceRequest.ANY_RESPONSE;
    }

    /**
     * Checks the lock state of the device.
     * 
     * @return <code>true</code> if the device is in locked state and <code>false</code> otherwise.
     */
    private boolean isLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardLocked();
    }

    /**
     * Brings task to the foreground of the device.
     * 
     * @param arguments
     *        - id of the task that is going to be brought to the foreground and timeout to wait for bringing the task
     *        to the front.
     * @return <code>true</code> if the given task is brought to front for the given timeout and <code>false</code>
     *         otherwise.
     */
    private boolean bringTaskToFront(Object[] arguments) {

        int taskId = (Integer) arguments[0];
        int timeout = (Integer) arguments[1];
        final int MOVE_TASK_TO_FRONT_TIMEOUT = 100;
        final int RUNNING_TASKS_SIZE = 1;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (int i = 0; i <= timeout; i += 100) {

            int topTask = getRunningTaskIds(RUNNING_TASKS_SIZE)[0];
            activityManager.moveTaskToFront(taskId, 0);
            try {
                Thread.sleep(MOVE_TASK_TO_FRONT_TIMEOUT);
            } catch (InterruptedException e) {
                // Nothing TODO here
            }
            if (topTask == taskId) {
                return true;
            }
        }
        return false;

    }

    /**
     * Return an array of the tasks id that are currently running, with the most recent being first and older ones after
     * in order.
     * 
     * @param arguments
     *        - max number of tasks that should be returned.
     * @return array containing the id of the tasks.
     * 
     * @deprecated Since LOLLIPOP, this method is no longer available. It will still return a small subset of its data:
     *             at least the caller's own tasks, and possibly some other tasks such as home that are known to not be
     *             sensitive.
     */
    @Deprecated
    private int[] getRunningTaskIds(Object... arguments) {
        int maxTasks = (Integer) arguments[0];

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(maxTasks);

        int[] runningTasksId = new int[maxTasks];
        int index = 0;

        for (ActivityManager.RunningTaskInfo runningTask : runningTasks) {

            runningTasksId[index] = runningTask.id;
            index++;
        }

        return runningTasksId;

    }

    /**
     * Waits for the given task to be moved to the given position.
     * 
     * @param arguments
     *        - taskId of the Task we want to wait for. Position in which the task should be moved to and timeout to
     *        wait for updating the task.
     * @return <code>true</code> if the task is moved to the given position and <code>false</code> otherwise or timeout
     *         runs out.
     * @deprecated Since LOLLIPOP, this method is no longer avaible.
     */
    @Deprecated
    private boolean waitForTasksUpdate(Object[] arguments) {

        int taskId = (Integer) arguments[0];
        int position = (Integer) arguments[1];
        int timeout = (Integer) arguments[2];
        int runningTasksSize = position + 1;
        final int WAIT_FOR_TASK_UPDATE_TIMEOUT = 50;

        for (int i = 0; i <= timeout; i += 50) {

            try {
                Thread.sleep(WAIT_FOR_TASK_UPDATE_TIMEOUT);
            } catch (InterruptedException e) {
                // Nothing TODO here
            }
            int taskOnPosition = getRunningTaskIds(runningTasksSize)[position];
            if (taskOnPosition == taskId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the total RAM the device has.
     *
     * @return the total RAM of the device.
     */
    private int getTotalRam() {
        MemoryInfo memoryInfo = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return (int) (memoryInfo.totalMem / BYTES_TO_MB);
    }

    /**
     * Checks if any audio is currently playing on the device.
     * 
     * @return <code>true</code> if any audio is playing, <code>false</code> otherwise.
     */
    private boolean isAudioPlaying() {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.isMusicActive();
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

    /**
     * Checks if physical cameras are available on this device.
     * 
     * @return <code>true</code> if physical cameras are available, else <code>false</code>
     */
    private boolean hasCamera() {
        return Camera.class != null && Camera.getNumberOfCameras() > 0;
    }

    /**
     * Opens the location settings activity.
     * 
     * @return a {@link ServiceRequest#ANY_RESPONSE}, since we are not requesting any information
     */
    private Object openLocationSettings() {
        Intent openLocationSettingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        openLocationSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(openLocationSettingsIntent);

        return ServiceRequest.ANY_RESPONSE;
    }

    /**
     * Check if the GPS location is enabled on this device.
     * 
     * @return <code>true</code> if the GPS location is enabled, <code>false</code> if it's disabled
     */
    private boolean isGpsLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Shows a tap location on the current device screen.
     * 
     * @param arguments
     *        - the point where the tap will be placed
     * @return a {@link ServiceRequest#ANY_RESPONSE}, since we are not requesting any information
     */
    private Object showTapLocation(Object[] arguments) {
        Point tapPoint = (Point) arguments[0];

        Intent intent = new Intent(context, LocationPointerService.class);
        intent.putExtra(LocationPointerConstants.CENTER_POINT_INTENT_NAME.getValue(), tapPoint);

        context.startService(intent);

        return ServiceRequest.ANY_RESPONSE;
    }

    /**
     * Broadcast the given intent to all interested BroadcastReceivers.
     * 
     * @param args
     *        - args[0] should contain the AtmoshereIntent object for the broadcast
     * @return a {@link ServiceRequest#ANY_RESPONSE}, since we are not requesting any information
     * 
     * @see {@link Context#sendBroadcast(Intent)}
     */
    private Object sendBroadcast(Object[] args) {
        if (args != null && args.length > 0) {
            Object intent = args[0];

            if (intent != null && intent instanceof AtmosphereIntent) {
                context.sendBroadcast(((AtmosphereIntent) intent).toIntent());
            }
        }

        return ServiceRequest.ANY_RESPONSE;
    }
}
