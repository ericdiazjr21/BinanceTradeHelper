package com.example.baseresources.repository;

import android.content.Context;

import com.example.baseresources.database.AccountDatabase;
import com.example.baseresources.model.interfaces.TradeHelperDatabase;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;

import java.util.Map;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

public final class OrdersRepository {

    private TradeHelperDatabase database;

    public OrdersRepository(@NonNull final Context context) {
        database = AccountDatabase.getSingleInstance(context);
    }

    public void addTransaction(@NonNull final TradeHelperTransaction transaction) {
        database.addTransaction(transaction);
    }

    public void deleteTransaction(@NonNull final TradeHelperTransaction transaction) {
        database.deleteTransaction(transaction);
    }

    public Single<Map<String, String>> getAllTransactions() {
        return database.getAllTransactions();
    }
}
