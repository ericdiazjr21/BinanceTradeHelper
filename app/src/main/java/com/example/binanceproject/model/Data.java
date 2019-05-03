package com.example.binanceproject.model;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("c")
    private final String lastPrice;

    public Data(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLastPrice() {
        return lastPrice;
    }
}
