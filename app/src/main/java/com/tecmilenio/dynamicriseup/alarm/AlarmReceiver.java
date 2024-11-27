package com.tecmilenio.dynamicriseup.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.tecmilenio.dynamicriseup.R;

public class AlarmReceiver extends BroadcastReceiver {

    public static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;

        if (powerManager != null) {
            try {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DynamicRiseUp::AlarmWakeLock");
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, "WAKE_LOCK permission required", Toast.LENGTH_LONG).show();
            }
        }

        // Play alarm sound
        playAlarmSound(context);

        // Open MainActivity and AlarmFragment
        Intent mainIntent = new Intent(context, com.tecmilenio.dynamicriseup.MainActivity.class);
        mainIntent.putExtra("openAlarmFragment", true);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(mainIntent);

        // Release WakeLock if acquired
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void playAlarmSound(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.morioh);
        mediaPlayer.setLooping(true); // Hacer que el sonido se repita
        mediaPlayer.start();
    }

    public static void stopAlarmSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                Log.d("AlarmReceiver", "Deteniendo el sonido de la alarma...");
                mediaPlayer.stop(); // Detiene la reproducción
            }
            mediaPlayer.reset(); // Resetea el MediaPlayer
            mediaPlayer.release(); // Libera los recursos
            mediaPlayer = null; // Elimina la referencia
            Log.d("AlarmReceiver", "MediaPlayer detenido y liberado.");
        } else {
            Log.d("AlarmReceiver", "No hay MediaPlayer activo.");
        }
    }
}
