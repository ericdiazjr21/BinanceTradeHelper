package com.example.service;

import android.content.Context;
import android.util.Log;

import com.example.account.utils.TransactionMap;
import com.example.baseresources.model.Data;
import com.example.baseresources.utils.TickerStreamConverter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public final class ServiceViewModel {

    private static final String TAG = "ServiceViewModel";
    private static ServiceViewModel singleInstance;
    private ServiceRepository repository;
    private String requestUrl;

    private ServiceViewModel(Context context) {
        repository = new ServiceRepository(context);
    }

    public static ServiceViewModel getSingleInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = new ServiceViewModel(context);
        }
        return singleInstance;
    }

    public void setRequestUrl(@NonNull final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Observable<Map<String, String>> getAllTransactions() {
        return repository.getAllTransactions();
    }

    public Observable<String> getBinanceStream() {
        return repository.getBinanceWebSocketStream(requestUrl)
          .subscribeOn(Schedulers.io())
          .filter(message -> message.startsWith("{\"stream\""))
          .map(streamMessage -> {
              Data data = TickerStreamConverter.tickerStreamDeserializer(streamMessage).getData();
              return data.getSymbol() + data.getLastPrice();
          })
          .distinctUntilChanged()
          .map(symbolData -> {
              Log.d(TAG, "getBinanceStream: " + TransactionMap.getSize());
              if (TransactionMap.containsOrder(symbolData)) {
                  TransactionMap.getTransaction(symbolData).execute();
                  Observable.just(TransactionMap.removeTransaction(symbolData))
                    .delay(2000, TimeUnit.MILLISECONDS)
                    .subscribe();
                  TransactionMap.removeTransaction(symbolData);
                  return "Order Processed" + symbolData;
              }
              return symbolData;
          });
    }

}
