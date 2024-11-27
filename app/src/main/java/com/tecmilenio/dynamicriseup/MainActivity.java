package com.tecmilenio.dynamicriseup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tecmilenio.dynamicriseup.alarm.AlarmEndActivity;
import com.tecmilenio.dynamicriseup.alarm.AlarmFragment;
import com.tecmilenio.dynamicriseup.home.HomeFragment;
import com.tecmilenio.dynamicriseup.alarm.SensorHandler;

public class MainActivity extends AppCompatActivity {

    private SensorHandler sensorHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Manejo de insets para Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Manejar el Intent recibido al iniciar la aplicación
        if (getIntent().getBooleanExtra("openAlarmFragment", false)) {
            loadFragment(new AlarmFragment());
        } else {
            loadFragment(new HomeFragment()); // Fragmento predeterminado
        }

        // Configuración del BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navigation_alarms) {
                selectedFragment = new AlarmFragment();
            } else if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            }
            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
