package com.example.account.controller;

import android.util.Log;

import com.example.account.Client;
import com.example.account.Transaction;
import com.example.baseresources.callbacks.TearDownManager;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.repository.BinanceStreamRepository;
import com.example.baseresources.utils.GsonConverter;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccountViewModel implements AccountWebsocketListener, TearDownManager {

    private static final String TAG = "AccountViewModel";
    private Transaction transaction;
    private static AccountViewModel singleInstance;
    private static Map<String, Transaction> transactionMap;
    private BinanceApiAsyncRestClient client;
    private Disposable disposable;

    private AccountViewModel() {
    }

    public static AccountViewModel getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new AccountViewModel();
        }
        return singleInstance;
    }

    public BinanceApiAsyncRestClient getClient(String apiKey, String secKey) {
        BinanceStreamRepository streamRepository = BinanceStreamRepository.getBinanceStreamRepository();
        streamRepository.setAccountWebsocketListener(this);
        client = new Client(apiKey, secKey).create();
        return client;
    }

    public AccountViewModel beginTransaction() {
        if(transactionMap == null){
            transactionMap = new HashMap<>();
        }
        transaction = new Transaction(client);
        return this;
    }

    public void placeBuyOrder(String symbol, String quantity, String purchasePrice) {
        transaction.placeBuyOrder(symbol, quantity, purchasePrice);
        transactionMap.put(symbol, transaction);
    }

    public TearDownManager getTearDownManager(){
        return this;
    }

    @Override
    public void onDataReceived(Observable<String> symbolData) {
        disposable = symbolData.subscribeOn(Schedulers.io())
                .map(message -> GsonConverter.tickerStreamDeserializer(message))
                .doOnNext(tickerStream -> {
                    if (transactionMap.containsKey(tickerStream.getStream())) {
                        transactionMap.get(tickerStream.getStream()).execute();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tickerStream -> transactionMap.remove(tickerStream.getStream()),
                        throwable -> Log.d(TAG, "accept: " + throwable.toString()));
    }

    @Override
    public void tearDown() {
        singleInstance = null;
        transactionMap = null;
        disposable.dispose();
    }
}
