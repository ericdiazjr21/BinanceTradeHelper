package com.example.service;

import io.reactivex.Observable;

public class ServiceRepository {

    private BinanceServiceWebSocket webSocket;

    public ServiceRepository() {
        webSocket = new BinanceServiceWebSocket();
    }

    public Observable<String> getBinanceWebSocketStream(String url){
        return webSocket.getBinanceStreamSubscription(url);
    }
}
