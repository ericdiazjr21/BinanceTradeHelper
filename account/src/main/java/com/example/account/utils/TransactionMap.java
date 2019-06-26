package com.example.account.utils;

import com.example.account.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;

public final class TransactionMap {

    private static final HashMap<String, Transaction> TRANSACTION_HASH_MAP = new HashMap<>();

    private TransactionMap() {
    }

    public static void addTransaction(@NonNull final String transactionKey,
                                      @NonNull final Transaction transaction) {
        TRANSACTION_HASH_MAP.put(transactionKey, transaction);
    }

    public static boolean containsOrder(@NonNull final String transactionKey) {
        return TRANSACTION_HASH_MAP.containsKey(transactionKey);
    }

    public static Transaction getTransaction(@NonNull final String transactionKey) {
        return TRANSACTION_HASH_MAP.get(transactionKey);
    }

    public static Transaction removeTransaction(@NonNull final String transactionKey) {
        return  TRANSACTION_HASH_MAP.remove(transactionKey);
    }

    public static int getSize() {
        return TRANSACTION_HASH_MAP.size();
    }

    public static List<Transaction> getAllTransactionsList() {
        return new ArrayList<>(TRANSACTION_HASH_MAP.values());
    }
}
