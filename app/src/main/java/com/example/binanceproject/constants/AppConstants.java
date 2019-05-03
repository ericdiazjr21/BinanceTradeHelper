package com.example.binanceproject.constants;

public class AppConstants {
    //Class Tags
    public static final String BINANCE_WEB_SOCKET_TAG = "BinanceWebSocket";
    public static final String BINANCE_STREAM_VIEW_MODEL_TAG = "BinanceStreamViewModel";
    //Networking Constants
    public static final String MULTIPLE_STREAMS = "wss://stream.binance.com:9443/stream?streams=";
    public static final String SINGLE_STREAM = "wss://stream.binance.com:9443/ws/btcusdt@ticker";

    //Websocket Closing Reason Constants
    public static final String NEW_CONNECTION = "new Connection";
    public static final String REGULAR_CLOSE = "regular_close";

    public static final String BTCUSDT = "btcusdt@ticker/";
    public static final String BNBBTC = "bnbbtc@ticker/";
    public static final String RLCBTC = "rlcbtc@ticker/";
    public static final String ETHBTC = "ethbtc@ticker/";
    public static final String KEYBTC = "keybtc@ticker/";
    public static final String POEBTC = "poebtc@ticker/";
    public static final String QKCBTC = "qkcbtc@ticker/";
    public static final String LENDBTC = "lendbtc@ticker/";
    public static final String CMTBTC = "cmtbtc@ticker/";
    public static final String RCNBTC = "rcnbtc@ticker/";

    public static final String[] ALL_TICKERS = {BTCUSDT, BNBBTC, RLCBTC, ETHBTC, KEYBTC, POEBTC, QKCBTC, LENDBTC, CMTBTC, RCNBTC};
}
