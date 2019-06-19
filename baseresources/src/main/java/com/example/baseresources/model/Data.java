package com.example.baseresources.model;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("c")
    private final String lastPrice;
    @SerializedName("s")
    private final String symbol;

    public Data(String lastPrice, String symbol) {
        this.lastPrice = lastPrice;
        this.symbol = symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getSymbol() {
        return symbol;
    }
}
