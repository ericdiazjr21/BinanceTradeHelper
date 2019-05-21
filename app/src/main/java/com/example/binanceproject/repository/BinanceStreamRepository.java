package com.example.binanceproject.repository;

import android.util.Log;

import com.example.binanceproject.constants.AppConstants;
import com.example.binanceproject.network.BinanceWebSocket;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BinanceStreamRepository extends WebSocketListener {

    private static BinanceStreamRepository binanceStreamRepository;
    private OnDataStreamOpenListener listener;
    private BinanceWebSocket webSocket;
    private String requestUrl;

    private BinanceStreamRepository() {
        webSocket = new BinanceWebSocket();
    }

    public static BinanceStreamRepository getBinanceStreamRepository(){
        if(binanceStreamRepository == null){
            binanceStreamRepository = new BinanceStreamRepository();
        }return binanceStreamRepository;
    }

    public void initWebSocketConnection(String requestUrl){
        this.requestUrl = requestUrl;
        tryConnection(this.requestUrl);
    }

    public boolean closeWebSocket(String reason){
        return webSocket.close(reason);
    }

    public void setListener(OnDataStreamOpenListener listener) {
        this.listener = listener;
    }

    private void tryConnection(String streams) {
        this.webSocket.run(this.webSocket.getOkHttpClient(), this.webSocket.getRequest(streams), this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onOpen: " + response.toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        listener.onDataStreamMessageOpen(Observable.just(text));
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
        listener.onDataStreamClosed(reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        Log.d(AppConstants.BINANCE_WEB_SOCKET_TAG, "onFailure: " + t.toString());
        listener.onConnectionError(t.toString());
        tryConnection(requestUrl);
    }

    public interface OnDataStreamOpenListener {
        void onDataStreamMessageOpen(Observable<String> message);
        void onDataStreamClosed(String reason);
        void onConnectionError(String throwableResponse);
    }

}
