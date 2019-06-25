package com.example.baseresources.utils;

import com.example.baseresources.model.interfaces.TradeHelperTransaction;
import com.google.gson.Gson;

public final class TransactionSerializer {

    private TransactionSerializer() {
    }

    public static String serializeTransaction(TradeHelperTransaction transaction) {
        return new Gson().toJson(transaction);
    }

}
