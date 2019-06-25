package com.example.baseresources.utils;

import com.example.baseresources.model.Data;
import com.example.baseresources.model.TickerStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public final class TickerStreamConverter {

    private TickerStreamConverter() {
    }

    public static Data dataDeserializer(String message) {
        return new Gson().<TickerStream>fromJson(message,
                new TypeToken<TickerStream>() {}.getType()).getData();
    }

    public static TickerStream tickerStreamDeserializer(String message) {
        return new Gson().fromJson(message, new TypeToken<TickerStream>() {}.getType());
    }
}
