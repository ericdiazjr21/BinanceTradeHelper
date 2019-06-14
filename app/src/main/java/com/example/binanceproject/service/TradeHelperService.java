package com.example.binanceproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.utils.NotificationGenerator;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class TradeHelperService extends Service implements AccountWebsocketListener {

    private BinanceStreamViewModel binanceStreamViewModel;
    private AccountViewModel accountViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public TradeHelperService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        compositeDisposable.add(Maybe.fromAction(() -> {
            binanceStreamViewModel = BinanceStreamViewModel.getSingleInstance();
            binanceStreamViewModel.setOnDataReceivedListener(TradeHelperService.this::onDataReceived);
            accountViewModel = AccountViewModel.getSingleInstance();
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new NotificationGenerator(this).getNotificationForeground());

        compositeDisposable.add(Maybe.fromAction(() ->
          binanceStreamViewModel.requestBinanceDataStream())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe());
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDataReceived(Observable<String> symbolData) {
        accountViewModel.checkTransactionMap(symbolData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
