package com.example.service;

import android.content.Context;

import com.example.baseresources.database.AccountDatabase;
import com.example.baseresources.model.interfaces.TradeHelperDatabase;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

class ServiceRepository {

    private BinanceServiceWebSocket webSocket;
    private TradeHelperDatabase database;

    ServiceRepository(@NonNull final Context context) {
        database = AccountDatabase.getSingleInstance(context);
        webSocket = new BinanceServiceWebSocket();
    }

    Observable<String> getBinanceWebSocketStream(String url) {
        return webSocket.getBinanceStreamSubscription(url);
    }

    Observable<Map<String, String>> getAllTransactions() {
        return database.getAllTransactions();
    }


}
