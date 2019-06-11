package com.example.baseresources.repository;

import android.util.Log;

import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.network.BinanceWebSocket;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BinanceStreamRepository extends WebSocketListener {

    private static BinanceStreamRepository binanceStreamRepository;
    private OnDataStreamOpenListener streamOpenListener;
    private AccountWebsocketListener accountWebsocketListener;
    private BinanceWebSocket webSocket;
    private String requestUrl;
    private boolean isWebSocketOpen = false;

    private BinanceStreamRepository() {
        webSocket = new BinanceWebSocket();
    }

    public static BinanceStreamRepository getBinanceStreamRepository() {
        if (binanceStreamRepository == null) {
            binanceStreamRepository = new BinanceStreamRepository();
        }
        return binanceStreamRepository;
    }

    public void initWebSocketConnection(String requestUrl) {
        this.requestUrl = requestUrl;
        tryConnection(this.requestUrl);
    }

    public boolean closeWebSocket(String reason) {
        return webSocket.close(reason);
    }

    public void setStreamOpenListener(OnDataStreamOpenListener streamOpenListener) {
        this.streamOpenListener = streamOpenListener;
    }

    public void setAccountWebsocketListener(AccountWebsocketListener accountWebsocketListener) {
        this.accountWebsocketListener = accountWebsocketListener;
    }

    private void tryConnection(String streams) {
        this.webSocket.run(this.webSocket.getOkHttpClient(), this.webSocket.getRequest(streams), this);
    }

    public boolean isWebSocketOpen() {
        return isWebSocketOpen;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onOpen: " + response.toString());
        isWebSocketOpen = true;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        streamOpenListener.onDataStreamMessageOpen(Observable.just(text));
        accountWebsocketListener.onDataReceived(Observable.just(text));
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onMessage: " + text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onClosing: " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onClosed: " + reason);
        streamOpenListener.onDataStreamClosed(reason);
        isWebSocketOpen = false;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onFailure: " + t.toString());
        streamOpenListener.onConnectionError(t.toString());
        tryConnection(requestUrl);
    }

    public interface OnDataStreamOpenListener {
        void onDataStreamMessageOpen(Observable<String> message);

        void onDataStreamClosed(String reason);

        void onConnectionError(String throwableResponse);
    }

}
