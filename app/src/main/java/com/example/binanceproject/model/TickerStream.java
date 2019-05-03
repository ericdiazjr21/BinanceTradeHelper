package com.example.binanceproject.model;

public class TickerStream {

    private final String stream;
    private final Data data;

    public TickerStream(String stream, Data data) {
        this.stream = stream;
        this.data = data;
    }

    public String getStream() {
        return stream;
    }

    public Data getData() {
        return data;
    }
}
