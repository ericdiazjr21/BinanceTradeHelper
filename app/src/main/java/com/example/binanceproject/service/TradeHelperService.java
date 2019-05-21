package com.example.binanceproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.binanceproject.utils.NotificationGenerator;

public class TradeHelperService extends Service {


    public TradeHelperService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new NotificationGenerator(this).getNotification());
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
