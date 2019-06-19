package com.example.account.model;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;
import net.sealake.binance.api.client.BinanceApiClientFactory;

public final class Client {

    private final String api_key;
    private final String sec_key;

    public Client(String api_key, String sec_key) {
        this.api_key = api_key;
        this.sec_key = sec_key;
    }

    public BinanceApiAsyncRestClient create(){
        return BinanceApiClientFactory.newInstance(api_key,sec_key).newAsyncRestClient();
    }
}
