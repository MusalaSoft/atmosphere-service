package com.musala.atmosphere.service.sensoreventlistener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Listens for acceleration event and saves the measured acceleration data.
 * 
 * @author yordan.petrov
 * 
 */
public class AccelerationEventListener implements SensorEventListener {
    private Context context;

    private Float[] acceleratoin;

    private boolean measured = false;

    public AccelerationEventListener(Context context) {
        this.context = context;
        acceleratoin = new Float[3];
    }

    /**
     * Registers the acceleration listener to the sensor manager.
     * 
     */
    public void register() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                                       sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                       SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Checks whether the acceleration has been measured.
     * 
     * @return true if it has been measured; false otherwise.
     */
    public boolean isMeasured() {
        return measured;
    }

    /**
     * Gets the acceleration of the device.
     * 
     * @return float array representing device acceleration on the X, Y and Z axis. Null if the acceleration is not
     *         measured.
     */
    public Float[] getAcceleration() {
        return acceleratoin;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            for (int i = 0; i < 3; i++) {
                acceleratoin[i] = event.values[i];
            }

            measured = true;

            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
        }
    }
}
