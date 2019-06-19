package com.example.baseresources.utils;

import com.example.baseresources.model.TickerStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ListCreator {

    private static final List<TickerStream> STREAM_LIST = new ArrayList<>();

    private ListCreator() {
    }

    public static List<TickerStream> getStreamList(Map<String, TickerStream> streamMap) {
        STREAM_LIST.clear();
        STREAM_LIST.addAll(streamMap.values());
        return STREAM_LIST;
    }
}
