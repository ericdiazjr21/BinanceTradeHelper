package com.example.binanceproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.account.controller.AccountViewModel;
import com.example.binanceproject.utils.NotificationGenerator;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;

public class TradeHelperService extends Service {

    private BinanceStreamViewModel binanceStreamViewModel;
    private AccountViewModel accountViewModel;

    public TradeHelperService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binanceStreamViewModel = BinanceStreamViewModel.getSingleInstance();
        accountViewModel = AccountViewModel.getSingleInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        binanceStreamViewModel.requestBinanceDataStream();

        startForeground(1, new NotificationGenerator(this).getNotification());
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
