package com.tecmilenio.dynamicriseup.alarm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tecmilenio.dynamicriseup.R;

public class AlarmEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_end);

        // Cargar el fragmento de la alarma activada
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AlarmEndFragment())
                    .commit();
        }
    }
}
