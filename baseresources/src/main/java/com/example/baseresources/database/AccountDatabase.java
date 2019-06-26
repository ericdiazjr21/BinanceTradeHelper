package com.example.baseresources.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.baseresources.model.interfaces.TradeHelperDatabase;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;
import com.example.baseresources.utils.TransactionSerializer;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AccountDatabase extends SQLiteOpenHelper implements TradeHelperDatabase {

    private static final String TAG = "AccountDatabase";
    private static final String DATABASE_NAME = "AccountDatabaseV1.db";
    private static final String TABLE_NAME = "TransactionTable";
    private static final int SCHEMA = 1;
    private static AccountDatabase accountDatabaseSingleInstance;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SQLiteDatabase db;

    private AccountDatabase(final Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static AccountDatabase getSingleInstance(Context context) {
        if (accountDatabaseSingleInstance == null) {
            accountDatabaseSingleInstance = new AccountDatabase(context);
        }
        return accountDatabaseSingleInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        compositeDisposable.add(Completable.fromAction(() ->
          db.execSQL(
            "CREATE TABLE " + TABLE_NAME +
              " (_id INTEGER PRIMARY KEY, " +
              "transaction_key TEXT, transaction_value TEXT);"
          )).subscribeOn(Schedulers.io())
          .subscribe(() -> Log.d(TAG, "Database onCreate: Database created!"),
            throwable -> Log.d(TAG, "accept: " + throwable.getMessage())));
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        this.db = db;
        super.onOpen(this.db);
    }

    @Override
    public Observable<Map<String, String>> getAllTransactions() {
        Map<String, String> transactionModelJsonMap = new HashMap<>();
        return Observable.just(transactionModelJsonMap)
          .map(jsonTransactionMap -> {
              Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_NAME + ";", null);
              if (cursor != null) {
                  if (cursor.moveToFirst()) {
                      do {
                          Log.d(TAG, "getAllTransactions: " + cursor.getString(cursor.getColumnIndex("_id"))
                            + cursor.getString(cursor.getColumnIndex("transaction_key")));
                          transactionModelJsonMap.put(
                            cursor.getString(cursor.getColumnIndex("transaction_key")),
                            cursor.getString(cursor.getColumnIndex("transaction_value")));
                      } while (cursor.moveToNext());
                  }
              }
              return transactionModelJsonMap;
          });
    }

    @Override
    public Completable addTransaction(@NonNull final TradeHelperTransaction transaction) {
        return Completable.fromAction(() -> {
            Cursor cursor = getReadableDatabase().rawQuery(
              "SELECT * FROM " + TABLE_NAME + " WHERE transaction_key = '" + transaction.getTransactionId() +
                "' AND transaction_value = '" + TransactionSerializer.serializeTransaction(transaction) +
                "';", null);
            if (cursor.getCount() == 0) {
                getWritableDatabase().execSQL("INSERT INTO " + TABLE_NAME +
                  "(transaction_key,transaction_value) VALUES('" +
                  transaction.getTransactionId() + "', '" +
                  TransactionSerializer.serializeTransaction(transaction) + "');");
            }
            cursor.close();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable deleteTransaction(@NonNull final TradeHelperTransaction transaction) {
        return Completable.fromAction(() ->
          db.delete(TABLE_NAME, "transaction_key=" + transaction.getTransactionId(), null));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        super.close();
        compositeDisposable.dispose();
    }
}
