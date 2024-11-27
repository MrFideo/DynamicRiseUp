package com.tecmilenio.dynamicriseup.alarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHandler implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private boolean isStanding = false;

    // Nuevo rango para detectar "de pie" (más estricto)
    private static final float Z_AXIS_LOWER_THRESHOLD = 8.0f;  // Rango más alto para mayor precisión
    private static final float Z_AXIS_UPPER_THRESHOLD = 9.5f;   // Rango más estrecho

    // Para suavizar lecturas (haciendo que el suavizado sea más sensible)
    private float smoothedZ = 0.0f;
    private static final float SMOOTHING_FACTOR = 0.05f; // Cuanto menor, más sensible

    public SensorHandler(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void startMonitoring() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stopMonitoring() {
        sensorManager.unregisterListener(this);
    }

    public boolean isUserStanding() {
        return isStanding;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2]; // Eje Z indica la verticalidad

            // Suavizar la lectura del eje Z
            smoothedZ = smoothedZ + SMOOTHING_FACTOR * (z - smoothedZ);

            // Comprobar si está dentro del rango más estricto
            isStanding = smoothedZ >= Z_AXIS_LOWER_THRESHOLD && smoothedZ <= Z_AXIS_UPPER_THRESHOLD;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar
    }
}
