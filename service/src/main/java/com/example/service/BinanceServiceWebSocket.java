package com.example.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class BinanceServiceWebSocket {

    private Request getRequest(String url) {
        return new Request.Builder()
          .url(url)
          .build();
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
          .readTimeout(0, TimeUnit.MILLISECONDS)
          .retryOnConnectionFailure(true)
          .build();
    }

    public Observable<String> getBinanceStreamSubscription(String url) {

        final PublishSubject<String> publishSubject = PublishSubject.create();

        getOkHttpClient().newWebSocket(getRequest(url), new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                publishSubject.onNext(response.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                publishSubject.onNext(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                publishSubject.onNext(reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                publishSubject.onNext(reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                publishSubject.onError(t);
            }
        });
        return publishSubject;
    }
}
