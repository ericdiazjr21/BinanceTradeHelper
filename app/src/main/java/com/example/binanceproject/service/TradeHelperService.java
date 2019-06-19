package com.example.binanceproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.baseresources.utils.NotificationGenerator;
import com.example.service.ServiceViewModel;

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
          serviceViewModel = ServiceViewModel.getSingleInstance())
          .subscribeOn(Schedulers.io())
          .subscribe());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new NotificationGenerator(this).getNotificationForeground());

        compositeDisposable.add(serviceViewModel.getBinanceStream()
          .doOnNext(symbolName -> {
              if (symbolName.equals("Order Processed"))
                  new NotificationGenerator(TradeHelperService.this)
                    .sendNotification();
          }).subscribe(symbolStream -> Log.d(TAG, "accept: " + symbolStream),
            throwable -> Log.d(TAG, "accept: " + throwable.getMessage())));

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
