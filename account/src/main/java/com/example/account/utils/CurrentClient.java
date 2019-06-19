package com.example.account.utils;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;

import io.reactivex.annotations.NonNull;

public final class CurrentClient {

    private static BinanceApiAsyncRestClient currentClient;

    public static void setCurrentClient(@NonNull final BinanceApiAsyncRestClient currentClient) {
        CurrentClient.currentClient = currentClient;
    }

    public static BinanceApiAsyncRestClient getCurrentClient() {
        return currentClient;
    }
}
