package com.example.binanceproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.baseresources.utils.NotificationGenerator;
import com.example.service.ServiceViewModel;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TradeHelperService extends Service {

    private static final String TAG = "TradeHelperService";
    private ServiceViewModel serviceViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        compositeDisposable.add(Maybe.fromAction(() ->
          serviceViewModel = ServiceViewModel.getSingleInstance(this))
          .subscribeOn(Schedulers.io())
          .subscribe());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new NotificationGenerator(this).getNotificationForeground());

        compositeDisposable.add(Completable.fromAction(() -> serviceViewModel.loadAllTransactions()).subscribe());
        compositeDisposable.add(serviceViewModel.getBinanceStream()
          .doOnNext(symbolName -> {
              if (symbolName.contains("Order Processed"))
                  new NotificationGenerator(TradeHelperService.this)
                    .sendNotification("Order Processed!", symbolName);
          }).subscribe(symbolStream -> Log.d(TAG, "accept: " + symbolStream),
            throwable -> {
                Log.d(TAG, "accept: " + throwable.getMessage());
                new NotificationGenerator(this)
                  .sendNotification("WebSocket Error", "Connection failed: " + throwable.getMessage());
            }, () -> new NotificationGenerator(TradeHelperService.this)
              .sendNotification("WebSocket Closed", "Might be a regular close!")));

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
