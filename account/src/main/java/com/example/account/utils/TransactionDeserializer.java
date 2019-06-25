package com.example.account.utils;

import com.example.account.model.Transaction;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public final class TransactionDeserializer {

    private TransactionDeserializer() {
    }

    public static TradeHelperTransaction deserializeTransaction(String json) {
        return new Gson().fromJson(json, new TypeToken<Transaction>() {}.getType());
    }
}
