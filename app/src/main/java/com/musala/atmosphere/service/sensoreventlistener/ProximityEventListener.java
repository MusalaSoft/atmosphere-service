package com.musala.atmosphere.service.sensoreventlistener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Listens for proximity change event and saves the measured proximity data.
 * 
 * @author simeon.ivanov
 * 
 */
public class ProximityEventListener implements SensorEventListener {
    private final Context context;

    private float proximity;

    private boolean measured = false;

    public ProximityEventListener(Context context) {
        this.context = context;
    }

    /**
     * Registers the proximity event listener to the sensor manager.
     * 
     */
    public void register() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                                       sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                                       SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Checks whether the proximity has been measured.
     * 
     * @return true if it has been measured, false otherwise
     */
    public boolean isMeasured() {
        return measured;
    }

    /**
     * Gets the proximity of the device.
     * 
     * @return a float representing the device proximity, null if the proximity is not measured
     */
    public float getProximity() {
        return proximity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity = event.values[0];
            measured = true;

            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
        }
    }
}
