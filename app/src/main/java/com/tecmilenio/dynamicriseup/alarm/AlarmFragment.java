package com.tecmilenio.dynamicriseup.alarm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tecmilenio.dynamicriseup.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;

public class AlarmFragment extends Fragment {

    private LinearLayout alarmListContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        alarmListContainer = view.findViewById(R.id.alarm_list_container);
        Button buttonAddAlarm = view.findViewById(R.id.button_add_alarm);

        buttonAddAlarm.setOnClickListener(v -> showDateTimePicker());

        return view;
    }

    private void showDateTimePicker() {
        String[] monthNames = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + " de " + monthNames[selectedMonth] + " de " + selectedYear;

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, selectedHour, selectedMinute) -> {
                String dateTime = "Alarma puesta para las " + String.format("%02d:%02d", selectedHour, selectedMinute) +
                        " del día " + date;

                addAlarmToList(dateTime);

                Toast.makeText(getContext(), "Alarma añadida: " + dateTime, Toast.LENGTH_SHORT).show();

            }, hour, minute, true);
            timePickerDialog.show();

        }, year, month, day);
        datePickerDialog.show();
    }

    private void addAlarmToList(String dateTime) {
        // Crear un contenedor para cada alarma
        LinearLayout alarmItem = new LinearLayout(getContext());
        alarmItem.setOrientation(LinearLayout.HORIZONTAL);
        alarmItem.setPadding(8, 8, 8, 8);

        TextView alarmTextView = new TextView(getContext());
        alarmTextView.setText(dateTime);
        alarmTextView.setPadding(16, 16, 16, 16);
        alarmTextView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        // Establecer el color del texto al mismo que `background_color`
        alarmTextView.setTextColor(getResources().getColor(R.color.background_color));

        // Crear botón para escaneo de QR (flecha)
        ImageView qrButton = new ImageView(getContext());
        qrButton.setImageResource(R.drawable.ic_arrow); // Asegúrate de tener un ícono ic_arrow
        qrButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        qrButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setOrientationLocked(false);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Escanea un código QR");
            integrator.initiateScan();
        });

        // Crear botón para eliminar (bote de basura)
        ImageView deleteButton = new ImageView(getContext());
        deleteButton.setImageResource(R.drawable.ic_trash); // Asegúrate de tener un ícono ic_trash
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        deleteButton.setOnClickListener(v -> {
            alarmListContainer.removeView(alarmItem);
            Toast.makeText(getContext(), "Alarma eliminada", Toast.LENGTH_SHORT).show();
        });

        // Agregar elementos al contenedor
        alarmItem.addView(alarmTextView);
        alarmItem.addView(qrButton);
        alarmItem.addView(deleteButton);

        // Agregar el contenedor al `alarmListContainer`
        alarmListContainer.addView(alarmItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Manejar el resultado del escaneo de QR
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Mostrar un Toast con el contenido del código escaneado
                Toast.makeText(getContext(), "QR Escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                // Mostrar un Toast si el escaneo fue cancelado
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
