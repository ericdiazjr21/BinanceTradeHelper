package com.example.service;

import android.util.Log;

import com.example.account.utils.TransactionMap;
import com.example.baseresources.model.Data;
import com.example.baseresources.utils.TickerStreamConverter;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public final class ServiceViewModel {

    private static final String TAG = "ServiceViewModel";
    private static ServiceViewModel singleInstance;
    private ServiceRepository repository;
    private String requestUrl;

    private ServiceViewModel() {
        repository = new ServiceRepository();
    }

    public static ServiceViewModel getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new ServiceViewModel();
        }
        return singleInstance;
    }

    public void setRequestUrl(@NonNull final String requestUrl) {
        this.requestUrl = requestUrl;
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
                  TransactionMap.removeTransaction(symbolData);
                  return "Order Processed";
              }
              return symbolData;
          });
    }

}
