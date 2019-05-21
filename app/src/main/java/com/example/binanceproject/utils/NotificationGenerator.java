package com.example.binanceproject.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.binanceproject.R;
import com.example.binanceproject.view.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationGenerator {

    private static final String NOTIFICATION_CHANNEL_ID = "Trade_Helper_2019";
    private static final int NOTIFICATION_ID = 360;
    private Context context;

    public NotificationGenerator(Context context) {
        this.context = context;
        createNotificationManager();
    }

    private void createNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
              "Binance Project Notification",
              NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public Notification getNotification() {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
          NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notify = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
          .setContentTitle("New Order Posted!")
          .setContentText("Coin Pair: ")
          .setSmallIcon(R.drawable.ic_monetization_on_black_24dp)
          .setContentIntent(pendingIntent);
        return notify.build();
    }

}
