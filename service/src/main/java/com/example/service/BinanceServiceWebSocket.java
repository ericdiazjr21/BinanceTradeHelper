package com.example.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class BinanceServiceWebSocket {

    private Subject<String> subject;

    private Request getRequest(@NonNull final String url) {
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

    public Observable<String> getBinanceStreamSubscription(@NonNull final String url) {
        subject = PublishSubject.create();
        openWebSocketConnection(url, getWebSocketListener());
        return subject;
    }

    private void openWebSocketConnection(@NonNull final String url,
                                         @NonNull final WebSocketListener webSocketListener) {
        getOkHttpClient().newWebSocket(getRequest(url), webSocketListener);
    }

    private WebSocketListener getWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                subject.onNext(response.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                subject.onNext(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                subject.onNext(reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                subject.onNext(reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                subject.onError(t);
            }
        };
    }
}
