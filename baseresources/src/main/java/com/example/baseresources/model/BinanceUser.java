package com.example.baseresources.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class BinanceUser {

    @SerializedName("apiKey")
    @Expose
    private final String apiKey;

    @SerializedName("secretKey")
    @Expose
    private final String secretKey;

    public BinanceUser(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return "BinanceUser{" +
                "apiKey='" + apiKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}