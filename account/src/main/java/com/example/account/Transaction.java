package com.example.account;

import android.util.Log;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;
import net.sealake.binance.api.client.domain.TimeInForce;
import net.sealake.binance.api.client.domain.account.NewOrder;

public final class Transaction {

    private static final String TAG = "Transaction";
    private final BinanceApiAsyncRestClient client;
    private String symbol;
    private String quantity;
    private String purchasePrice;


    public Transaction(BinanceApiAsyncRestClient client) {
        this.client = client;
    }

    public void placeBuyOrder(String symbol, String quantity, String purchasePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public void execute() {
        client.newOrder(NewOrder.limitBuy(symbol, TimeInForce.GTC, quantity, purchasePrice),
                response -> Log.d(TAG, "onResponse: " + response.getStatus()));
    }

    public String getSymbol() {
        return symbol;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }
}
