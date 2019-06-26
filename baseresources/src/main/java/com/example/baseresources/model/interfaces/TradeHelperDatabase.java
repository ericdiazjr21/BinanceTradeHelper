package com.example.baseresources.model.interfaces;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface TradeHelperDatabase {
    Completable addTransaction(TradeHelperTransaction transaction);

    Completable deleteTransaction(TradeHelperTransaction transaction);

    Observable<Map<String, String>> getAllTransactions();
}
