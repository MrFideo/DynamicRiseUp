package com.tecmilenio.dynamicriseup.alarm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.tecmilenio.dynamicriseup.R;

public class AlarmDialogFragment extends DialogFragment {

    private OnDismissListener onDismissListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.popup_alarm, null);

        ImageView arrowIcon = view.findViewById(R.id.arrow_icon);
        arrowIcon.setOnClickListener(v -> {
            if (onDismissListener != null) {
                onDismissListener.onArrowClicked();
            }
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(false)
                .create();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public interface OnDismissListener {
        void onArrowClicked();
    }
}

