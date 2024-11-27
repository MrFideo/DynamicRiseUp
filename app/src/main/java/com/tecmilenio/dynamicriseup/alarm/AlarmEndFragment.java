package com.tecmilenio.dynamicriseup.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tecmilenio.dynamicriseup.R;

public class AlarmEndFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_end, container, false);

        Button stopButton = view.findViewById(R.id.stop_alarm_button);
        stopButton.setOnClickListener(v -> {
            // Lógica para apagar la alarma
            Toast.makeText(requireContext(), "Alarma apagada", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        });

        return view;
    }
}
