package com.example.binanceproject.model;

import com.google.gson.annotations.SerializedName;

public class TickerPrice {

    @SerializedName("symbol")
    private final String symbol;

    @SerializedName("price")
    private final String price;

    public TickerPrice(String symbol, String price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "TickerPrice{" +
                "symbol='" + symbol + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
