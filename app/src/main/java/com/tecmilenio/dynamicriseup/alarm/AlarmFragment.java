package com.tecmilenio.dynamicriseup.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tecmilenio.dynamicriseup.R;

import java.util.Calendar;
import java.util.Map;

public class AlarmFragment extends Fragment {

    private LinearLayout alarmListContainer;
    private AlarmManager alarmManager;
    private boolean isQrDetected = false;
    private boolean isAligned;
    private CustomCaptureActivity customCaptureActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        alarmListContainer = view.findViewById(R.id.alarm_list_container);
        Button buttonAddAlarm = view.findViewById(R.id.button_add_alarm);

        buttonAddAlarm.setOnClickListener(v -> showTimePickerWithSpinner());
        loadAlarms();
        customCaptureActivity = new CustomCaptureActivity();

        return view;
    }

    private void showTimePickerWithSpinner() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View timePickerView = getLayoutInflater().inflate(R.layout.time_picker_spinner, null);
        builder.setView(timePickerView);

        TimePicker timePicker = timePickerView.findViewById(R.id.time_picker_spinner);
        timePicker.setIs24HourView(true);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();
            showDaysOfWeekDialog(selectedHour, selectedMinute);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showDaysOfWeekDialog(int hour, int minute) {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        boolean[] selectedDays = new boolean[days.length];

        new AlertDialog.Builder(requireContext())
                .setTitle("Selecciona los días")
                .setMultiChoiceItems(days, selectedDays, (dialog, which, isChecked) -> selectedDays[which] = isChecked)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    if (!hasSelectedDays(selectedDays)) {
                        Toast.makeText(requireContext(), "Selecciona al menos un día", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String alarmDescription = String.format("Alarma a las %02d:%02d para días seleccionados", hour, minute);
                    addAlarmToList(alarmDescription, hour, minute, selectedDays);
                    setAlarm(hour, minute, selectedDays);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void addAlarmToList(String description, int hour, int minute, boolean[] days) {
        if (alarmListContainer.getVisibility() == View.GONE) {
            alarmListContainer.setVisibility(View.VISIBLE); // Mostrar el contenedor si está oculto
        }

        // Inflar el diseño del elemento
        View alarmItem = LayoutInflater.from(requireContext()).inflate(R.layout.alarm_item, alarmListContainer, false);

        // Configurar el texto descriptivo
        TextView alarmText = alarmItem.findViewById(R.id.alarm_text);
        alarmText.setText(description);

        // Configurar el botón de detener alarma
        ImageButton stopButton = alarmItem.findViewById(R.id.button_stop_alarm);
        stopButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Deteniendo alarma: " + description, Toast.LENGTH_SHORT).show();
            openCameraForQr(); // Abrir cámara para QR
        });

        // Configurar el botón de eliminar alarma
        ImageButton deleteButton = alarmItem.findViewById(R.id.button_delete_alarm);
        deleteButton.setOnClickListener(v -> {
            alarmListContainer.removeView(alarmItem);
            removeAlarm(description);

            if (alarmListContainer.getChildCount() == 0) {
                alarmListContainer.setVisibility(View.GONE); // Ocultar contenedor si no hay elementos
            }
        });

        // Agregar el elemento al contenedor
        alarmListContainer.addView(alarmItem);

        Log.d("AlarmFragment", "Alarma agregada al ListView: " + description);

        // Guardar la alarma en SharedPreferences
        saveAlarm(description, hour, minute, days);
    }

    private void openCameraForQr() {
        IntentIntegrator integrator = new IntentIntegrator(requireActivity());
        integrator.setCaptureActivity(CustomCaptureActivity.class); // Usa la actividad personalizada
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanea el código QR para apagar la alarma");
        integrator.setBeepEnabled(false); // Elimina el sonido "bip"
        integrator.setOrientationLocked(true);
        qrResultLauncher.launch(integrator.createScanIntent());
    }

    private void setAlarm(int hour, int minute, boolean[] days) {
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Toast.makeText(requireContext(), "Error: AlarmManager no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        int requestCode = generateUniqueRequestCode(hour, minute, days);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Calendar now = Calendar.getInstance();
        Calendar alarmTime = (Calendar) now.clone();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);

        // Ajustar índices de días para coincidir con la representación de Android
        for (int i = 0; i < 7; i++) {
            // Android utiliza 1 = Domingo, 2 = Lunes, ..., 7 = Sábado
            int androidDayIndex = (now.get(Calendar.DAY_OF_WEEK) + i - 1) % 7;
            if (days[(androidDayIndex + 6) % 7]) { // Ajustar el índice para empezar en lunes
                alarmTime.add(Calendar.DAY_OF_MONTH, i);
                break;
            }
        }

        if (alarmTime.before(Calendar.getInstance())) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 7);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int hour, int minute, boolean[] days) {
        int requestCode = generateUniqueRequestCode(hour, minute, days);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private int generateUniqueRequestCode(int hour, int minute, boolean[] days) {
        StringBuilder key = new StringBuilder();
        key.append(hour).append(minute);
        for (boolean day : days) {
            key.append(day ? "1" : "0");
        }
        return key.toString().hashCode();
    }

    private boolean hasSelectedDays(boolean[] days) {
        for (boolean day : days) {
            if (day) return true;
        }
        return false;
    }

    private void saveAlarm(String description, int hour, int minute, boolean[] days) {
        SharedPreferences preferences = requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        StringBuilder daysString = new StringBuilder();
        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                if (daysString.length() > 0) daysString.append(",");
                daysString.append(i);
            }
        }

        editor.putString(description, hour + ":" + minute + ":" + daysString);
        editor.apply();
    }

    private void loadAlarms() {
        SharedPreferences preferences = requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);

        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            String description = entry.getKey();
            String[] alarmData = entry.getValue().toString().split(":");

            int hour = Integer.parseInt(alarmData[0]);
            int minute = Integer.parseInt(alarmData[1]);
            boolean[] days = new boolean[7];
            if (alarmData.length > 2) {
                String[] daysIndices = alarmData[2].split(",");
                for (String index : daysIndices) {
                    days[Integer.parseInt(index)] = true;
                }
            }

            Log.d("AlarmFragment", "Cargando alarma: " + description);

            addAlarmToList(description, hour, minute, days);
        }
    }

    private void removeAlarm(String description) {
        SharedPreferences preferences = requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(description);
        editor.apply();
    }

    private final ActivityResultLauncher<Intent> qrResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    IntentResult qrResult = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, result.getResultCode(), data);

                    if (qrResult != null && qrResult.getContents() != null) {
                        // Confirmación de que el QR se escaneó correctamente
                        Log.d("AlarmFragment", "QR detectado: " + qrResult.getContents());

                        isQrDetected = true; // Configura la variable
                        attemptToStopAlarm(); // Intentar detener la alarma
                    } else {
                        // Escaneo fallido o cancelado
                        Log.d("AlarmFragment", "Escaneo de QR fallido o cancelado.");
                        Toast.makeText(requireContext(), "Escaneo cancelado o fallido. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                        openCameraForQr(); // Reabrir la cámara en caso de error
                    }
                } else {
                    // No se obtuvo un resultado válido
                    Log.d("AlarmFragment", "Resultado inválido del ActivityResult.");
                    Toast.makeText(requireContext(), "Escaneo fallido. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                    openCameraForQr();
                }
            });


    private void attemptToStopAlarm() {
        SharedPreferences preferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        boolean isAligned = preferences.getBoolean("isAligned", false); // Valor por defecto: false

        // Verifica las condiciones
        if (isAligned && isQrDetected) {
            AlarmReceiver.stopAlarmSound();
            Toast.makeText(requireContext(), "Alarma apagada exitosamente.", Toast.LENGTH_SHORT).show();

            // Reemplazamos el fragmento actual con el mismo fragmento
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new AlarmFragment());  // Asegúrate de usar el ID correcto del contenedor
            transaction.commit();
        } else {
            Log.d("AlarmFragment", "Condiciones no cumplidas: isAligned=" + isAligned + ", isQrDetected=" + isQrDetected);
            Toast.makeText(requireContext(), "Condiciones no cumplidas. Sigue intentándolo.", Toast.LENGTH_SHORT).show();

            // Mantener la cámara activa o volver a abrirla
            openCameraForQr();
        }
    }
}