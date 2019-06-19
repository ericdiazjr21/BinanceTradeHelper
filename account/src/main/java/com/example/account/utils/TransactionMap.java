package com.example.account.utils;

import com.example.account.model.Transaction;

import java.util.HashMap;

import io.reactivex.annotations.NonNull;

public final class TransactionMap {

    private static final HashMap<String, Transaction> TRANSACTION_HASH_MAP = new HashMap<>();

    private TransactionMap() {
    }

    public static void enterTransaction(@NonNull final String symbol,
                                        @NonNull final Transaction transaction) {
        TRANSACTION_HASH_MAP.put(symbol, transaction);
    }

    public static boolean containsOrder(@NonNull final String symbol) {
        return TRANSACTION_HASH_MAP.containsKey(symbol);
    }

    public static Transaction getTransaction(@NonNull final String symbol) {
        return TRANSACTION_HASH_MAP.get(symbol);
    }

    public static void removeTransaction(@NonNull final String symbol) {
        TRANSACTION_HASH_MAP.remove(symbol);
    }

    public static int getSize() {
        return TRANSACTION_HASH_MAP.size();
    }

}
