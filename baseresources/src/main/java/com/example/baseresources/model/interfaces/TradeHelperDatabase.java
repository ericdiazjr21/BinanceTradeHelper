package com.example.baseresources.model.interfaces;

import java.util.Map;

import io.reactivex.Single;

public interface TradeHelperDatabase {
    void addTransaction(TradeHelperTransaction transaction);

    void deleteTransaction(TradeHelperTransaction transaction);

    Single<Map<String, String>> getAllTransactions();
}
