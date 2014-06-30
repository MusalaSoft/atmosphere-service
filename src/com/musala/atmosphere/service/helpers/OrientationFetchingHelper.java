package com.musala.atmosphere.service.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationFetchingHelper implements SensorEventListener {
    private SensorManager sensorManager;

    private float[] gravitationReadings;

    private float[] geomagneticReadings;

    private float[] resultOrientation = new float[3];

    private float[] rotationMatrix = new float[9]; // Use [16] to co-operate with android.opengl.Matrix

    private boolean failureFlag = true;

    public OrientationFetchingHelper(Context context) {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                                       sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                                       SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,
                                       sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                       SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravitationReadings = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:

                geomagneticReadings = event.values.clone();
                break;
            default:
                return;
        }

        if (gravitationReadings == null || gravitationReadings[2] == 0 || geomagneticReadings == null
                || geomagneticReadings[0] == 0) {
            return;
        }

        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravitationReadings, geomagneticReadings)) {
            SensorManager.getOrientation(rotationMatrix, resultOrientation);
            onSuccess();
        } else {
            onFailure();
        }
    }

    private void onSuccess() {
        sensorManager.unregisterListener(this);
        failureFlag = false;
    }

    private void onFailure() {
        failureFlag = true;
    }

    public float[] getOrientation() {
        // these next lines first convert the radians to degrees and then make sure the results are in the [0, 360]
        // range.
        resultOrientation[0] = (float) Math.round(Math.toDegrees(resultOrientation[0]) + 360.0f) % 360.0f;
        resultOrientation[1] = (float) Math.round(Math.toDegrees(resultOrientation[1]));
        resultOrientation[2] = (float) Math.round(Math.toDegrees(resultOrientation[2]));

        return resultOrientation;
    }

    public boolean isReady() {
        return !failureFlag;
    }
}
