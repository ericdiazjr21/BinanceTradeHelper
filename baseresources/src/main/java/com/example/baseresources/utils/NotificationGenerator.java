package com.example.baseresources.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.baseresources.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationGenerator {

    private static final String GROUP_KEY = "group_binance_key";
    private static final String NOTIFICATION_SERVICE_CHANNEL_ID = "Trade_Helper_2019_service";
    private static final String NOTIFICATION_CHANNEL_ID = "Trade_Helper_2019";
    private static final int NOTIFICATION_SERVICE_ID = 360;
    private static final int NOTIFICATION_ID = 180;
    private Context context;

    public NotificationGenerator(Context context) {
        this.context = context;
        createNotificationManager();
    }

    private void createNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceNotificationChannel = new NotificationChannel(NOTIFICATION_SERVICE_CHANNEL_ID,
              "Binance Service Notification", NotificationManager.IMPORTANCE_HIGH);
            serviceNotificationChannel.enableLights(true);
            serviceNotificationChannel.setLightColor(Color.RED);
            serviceNotificationChannel.enableVibration(true);
            serviceNotificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(serviceNotificationChannel);

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
              "Binance Project Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Order Submitted");
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }

    private Notification getNotification() {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
          NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notify = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
          .setContentTitle("New Order Posted!")
          .setContentText("Coin Pair: ")
          .setSmallIcon(R.drawable.ic_monetization_on_black_24dp)
          .setContentIntent(pendingIntent)
          .setGroup(GROUP_KEY);
        return notify.build();
    }

    public Notification getNotificationForeground() {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
          NOTIFICATION_SERVICE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notify = new NotificationCompat.Builder(context, NOTIFICATION_SERVICE_CHANNEL_ID)
          .setContentTitle("Tracking Coin Pairs")
          .setContentText("")
          .setSmallIcon(R.drawable.ic_monetization_on_black_24dp)
          .setContentIntent(pendingIntent);
        return notify.build();
    }

    public void sendNotification() {
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, getNotification());
    }

}
