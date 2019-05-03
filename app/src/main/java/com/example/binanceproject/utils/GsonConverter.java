package com.example.binanceproject.utils;

import com.example.binanceproject.model.Data;
import com.example.binanceproject.model.TickerStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public final class GsonConverter {

    private GsonConverter() {
    }

    public static Data dataDeserializer(String message) {
        return new Gson().<TickerStream>fromJson(message,
                new TypeToken<TickerStream>() {}.getType()).getData();
    }

    public static TickerStream tickerStreamDeserializer(String message) {
        return new Gson().fromJson(message, new TypeToken<TickerStream>() {}.getType());
    }
}
