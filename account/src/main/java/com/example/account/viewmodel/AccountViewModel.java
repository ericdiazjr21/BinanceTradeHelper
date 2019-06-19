package com.example.account.viewmodel;

import android.util.Log;

import com.example.account.model.Client;
import com.example.account.model.Order;
import com.example.account.model.Transaction;
import com.example.account.utils.CurrentClient;
import com.example.account.utils.TransactionMap;
import com.example.baseresources.callbacks.TearDownManager;
import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.repository.BinanceStreamRepository;
import com.example.baseresources.utils.GsonConverter;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccountViewModel implements
  AccountWebsocketListener,
  TearDownManager {

    private static final String TAG = "AccountViewModel";
    private static AccountViewModel singleInstance;
    private static OnTransactionExecutedListener transactionExecutedListener;
    private Transaction transaction;
    private Disposable disposable;

    private AccountViewModel() {
        BinanceStreamRepository streamRepository = BinanceStreamRepository.getBinanceStreamRepository();
        streamRepository.setAccountWebsocketListener(this);
    }

    public static AccountViewModel getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new AccountViewModel();
        }
        return singleInstance;
    }

    public TearDownManager getTearDownManager() {
        return this;
    }

    public BinanceApiAsyncRestClient getClient(@NonNull final String apiKey,
                                               @NonNull final String secKey) {
        CurrentClient.setCurrentClient(new Client(apiKey, secKey).create());
        return CurrentClient.getCurrentClient();
    }

    public AccountViewModel beginTransaction() {
        transaction = new Transaction();
        return this;
    }

    public void setTransactionExecutedListener(OnTransactionExecutedListener transactionExecutedListener) {
        AccountViewModel.transactionExecutedListener = transactionExecutedListener;
    }

    public void placeOrder(@NonNull final Order order) {
        Log.d(TAG, "placeOrder: " + order.getSymbol());
        switch (order.getOrderType()) {
            case AppConstants.BUY:
                transaction.placeBuyOrder(order.getSymbol(), order.getStrikePrice(), order.getExecutePrice(), order.getQuantity());
                break;
            case AppConstants.SELL:
                transaction.placeSellOrder(order.getSymbol(), order.getStrikePrice(), order.getExecutePrice(), order.getQuantity());
                break;
        }TransactionMap.enterTransaction(order.getSymbol() + order.getStrikePrice(), transaction);
    }

    public void checkTransactionMap(Observable<String> symbolData) {
        disposable = symbolData
          .map(GsonConverter::tickerStreamDeserializer)
          .doOnNext(tickerStream -> {
              String transactionId = tickerStream.getStream()
                .replace("@ticker", "")
                .toUpperCase()  + tickerStream.getData().getLastPrice();
              Log.d(TAG, "onDataReceived: " + TransactionMap.containsOrder(transactionId));
              if (TransactionMap.containsOrder(transactionId)) {
                  TransactionMap.getTransaction(transactionId).execute();
                  TransactionMap.removeTransaction(transactionId);
                  transactionExecutedListener.sendNotification();
              }
          }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(throwable -> Log.d(TAG, "accept: " + throwable.toString()));
    }

    @Override
    public void onDataReceived(Observable<String> symbolData) {
        checkTransactionMap(symbolData);
    }

    @Override
    public void tearDown() {
        singleInstance = null;
        if (disposable != null) {
            disposable.dispose();
        }
    }


    public interface OnTransactionExecutedListener {
        void sendNotification();
    }
}
