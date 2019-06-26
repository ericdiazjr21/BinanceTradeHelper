package com.example.baseresources.repository;

import android.content.Context;

import com.example.baseresources.database.AccountDatabase;
import com.example.baseresources.model.interfaces.TradeHelperDatabase;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public final class OrdersRepository {

    private TradeHelperDatabase database;

    public OrdersRepository(@NonNull final Context context) {
        database = AccountDatabase.getSingleInstance(context);
    }

    public Completable addTransaction(@NonNull final TradeHelperTransaction transaction) {
        return database.addTransaction(transaction);
    }

    public Completable deleteTransaction(@NonNull final TradeHelperTransaction transaction) {
        return database.deleteTransaction(transaction);
    }

    public Observable<Map<String, String>> getAllTransactions() {
        return database.getAllTransactions();
    }
}
