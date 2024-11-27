package com.tecmilenio.dynamicriseup.alarm;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomCaptureActivity extends CaptureActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView statusTextView;
    private boolean isAligned; // Booleano para el estado de los sensores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa los sensores y el texto en pantalla
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        statusTextView = new TextView(this);
        statusTextView.setText("Alinea el dispositivo y escanea el código QR.");
        statusTextView.setTextSize(18);
        statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        addContentView(statusTextView, new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL
        ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        isAligned = false;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xValue = event.values[0];
            float yValue = event.values[1];
            float zValue = event.values[2];

            // Detectar posición vertical u horizontal
            boolean isVertical = (zValue > 7.0 && zValue < 11.0) && Math.abs(xValue) < 3.0 && Math.abs(yValue) < 3.0;
            boolean isHorizontal = (Math.abs(xValue) > 5.0 || Math.abs(yValue) > 5.0) && zValue > -3.0 && zValue < 3.0;

            boolean previousAligned = isAligned; // Estado anterior
            isAligned = isVertical || isHorizontal;

            if (isAligned != previousAligned) {
                Log.d("CustomCaptureActivity", String.format(
                        "Estado cambiado. isAligned ahora es: %b | Sensor Values -> X: %.2f, Y: %.2f, Z: %.2f",
                        isAligned, xValue, yValue, zValue
                ));
            }

            // Guardar el valor de isAligned en SharedPreferences
            SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isAligned", isAligned);
            editor.apply();  // Guardar los cambios

            // Cambiar el texto de estado
            if (isAligned) {
                statusTextView.setText("¡Sensores alineados correctamente! Escanea el QR.");
                statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                statusTextView.setText("Alinea el dispositivo para escanear.");
                statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar nada aquí
    }

    // Método para verificar si los sensores están alineados
    public boolean isAligned() {
        return isAligned;
    }
}