package com.example.account.model;

import android.util.Log;

import com.example.account.utils.CurrentClient;
import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;

import net.sealake.binance.api.client.domain.TimeInForce;
import net.sealake.binance.api.client.domain.account.NewOrder;

import io.reactivex.annotations.NonNull;

public final class Transaction implements TradeHelperTransaction {

    private static final String TAG = "Transaction";
    private String symbol;
    private String strikePrice;
    private String executePrice;
    private String quantity;
    private String orderType;


    public Transaction() {
    }

    public void placeBuyOrder(@NonNull final String symbol,
                              @NonNull final String strikePrice,
                              @NonNull final String purchasePrice,
                              @NonNull final String quantity) {
        this.symbol = symbol;
        this.strikePrice = strikePrice;
        this.quantity = quantity;
        this.executePrice = purchasePrice;
        this.orderType = AppConstants.BUY;
    }

    public void placeSellOrder(@NonNull final String symbol,
                               @NonNull final String strikePrice,
                               @NonNull final String purchasePrice,
                               @NonNull final String quantity) {
        this.symbol = symbol;
        this.strikePrice = strikePrice;
        this.quantity = quantity;
        this.executePrice = purchasePrice;
        this.orderType = AppConstants.SELL;
    }
    public String getSymbol() {
        return symbol;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExecutePrice() {
        return executePrice;
    }

    public String getStrikePrice() {
        return strikePrice;
    }

    public String getOrderType() {
        return orderType;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute: " + this.toString());
        if (orderType.equals(AppConstants.BUY)) {
            CurrentClient.getCurrentClient().newOrder(NewOrder.limitBuy(symbol, TimeInForce.GTC, quantity, executePrice),
              response -> Log.d(TAG, "onResponse: " + response.getStatus()));
        } else {
            CurrentClient.getCurrentClient().newOrder(NewOrder.limitSell(symbol, TimeInForce.GTC, quantity, executePrice),
              response -> Log.d(TAG, "onResponse: " + response.getStatus()));
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
          "symbol='" + symbol + '\'' +
          ", strikePrice='" + strikePrice + '\'' +
          ", executePrice='" + executePrice + '\'' +
          ", quantity='" + quantity + '\'' +
          ", orderType='" + orderType + '\'' +
          '}';
    }
}
