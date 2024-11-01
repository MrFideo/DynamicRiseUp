package com.tecmilenio.dynamicriseup.alarm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tecmilenio.dynamicriseup.R;

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

                TextView alarmTextView = new TextView(getContext());
                alarmTextView.setText(dateTime);
                alarmTextView.setPadding(16, 16, 16, 16);

                alarmListContainer.addView(alarmTextView);

                Toast.makeText(getContext(), "Alarma añadida: " + dateTime, Toast.LENGTH_SHORT).show();

            }, hour, minute, true);
            timePickerDialog.show();

        }, year, month, day);
        datePickerDialog.show();
    }

}

