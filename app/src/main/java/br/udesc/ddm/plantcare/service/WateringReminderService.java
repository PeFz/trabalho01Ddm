package br.udesc.ddm.plantcare.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.PlantDatabase;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.ui.MainActivity;

/**
 * Serviço em Background para monitorar a hidratação das plantas cadastradas no Room
 * e disparar alertas/notificações para o jardineiro.
 */
public class WateringReminderService extends Service {

    public static final String CHANNEL_ID = "plantcare_watering_channel";
    public static final String ACTION_CHECK_NOW = "br.udesc.ddm.plantcare.ACTION_CHECK_NOW";
    private static final int NOTIFICATION_ID = 1001;
    private static final long CHECK_INTERVAL_MS = 60 * 60 * 1000L; // 1 hora

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable periodicCheckRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        periodicCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkPlantsAndNotify();
                handler.postDelayed(this, CHECK_INTERVAL_MS);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_CHECK_NOW.equals(intent.getAction())) {
            checkPlantsAndNotify();
        } else {
            handler.removeCallbacks(periodicCheckRunnable);
            handler.post(periodicCheckRunnable);
        }
        return START_STICKY;
    }

    private void checkPlantsAndNotify() {
        PlantDatabase.databaseWriteExecutor.execute(() -> {
            List<PlantEntity> allPlants = PlantDatabase.getDatabase(getApplicationContext())
                    .plantDao()
                    .getAllPlantsSync();

            List<String> thirstyNames = new ArrayList<>();
            for (PlantEntity plant : allPlants) {
                if (plant.isReminderEnabled() && plant.needsWater()) {
                    thirstyNames.add(plant.getName());
                }
            }

            if (!thirstyNames.isEmpty()) {
                String title = thirstyNames.size() == 1
                        ? "1 planta precisa de água hoje!"
                        : thirstyNames.size() + " plantas precisam de água hoje!";
                String namesJoined = android.text.TextUtils.join(", ", thirstyNames);
                String body = "Lembrete do Santuário Botânico: " + namesJoined + " anseiam por uma hidratação fresca.";
                sendWateringNotification(title, body);
            } else {
                sendWateringNotification(
                        "Santuário Botânico em Harmonia",
                        "Todas as suas plantas estão bem hidratadas no momento!"
                );
            }
        });
    }

    private void sendWateringNotification(String title, String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_water_drop)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            try {
                manager.notify(NOTIFICATION_ID, builder.build());
            } catch (SecurityException ignored) {
                // Caso o usuário ainda não tenha concedido POST_NOTIFICATIONS no Android 13+
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Rega PlantCare",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações do serviço em background para cuidado das plantas");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(periodicCheckRunnable);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

