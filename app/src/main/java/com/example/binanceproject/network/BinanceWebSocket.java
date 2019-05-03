package com.example.binanceproject.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BinanceWebSocket {

    private static WebSocket webSocket;

    public BinanceWebSocket() {
        super();
    }

    public Request getRequest(String requestURL) {
        return new Request.Builder()
                .url(requestURL)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public void run(OkHttpClient client, Request request, WebSocketListener listener) {
        webSocket = client.newWebSocket(request, listener);
    }

    public boolean close(String reason) {
        if (webSocket != null) {
            return webSocket.close(1000, reason);
        }
        return false;
    }

}


