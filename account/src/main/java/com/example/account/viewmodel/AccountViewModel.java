package com.example.account.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.example.account.model.Client;
import com.example.account.model.Order;
import com.example.account.model.Transaction;
import com.example.account.utils.CurrentClient;
import com.example.account.utils.TransactionDeserializer;
import com.example.account.utils.TransactionMap;
import com.example.baseresources.callbacks.TearDownManager;
import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.model.TickerPrice;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;
import com.example.baseresources.repository.BinanceStreamRepository;
import com.example.baseresources.repository.OrdersRepository;
import com.example.baseresources.utils.TickerStreamConverter;

import net.sealake.binance.api.client.BinanceApiAsyncRestClient;
import net.sealake.binance.api.client.BinanceApiCallback;
import net.sealake.binance.api.client.domain.account.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class AccountViewModel extends ViewModel implements
  AccountWebsocketListener,
  TearDownManager {

    private static final String TAG = "AccountViewModel";
    private static AccountViewModel singleInstance;
    private static OnTransactionExecutedListener transactionExecutedListener;
    private final BinanceStreamRepository streamRepository;
    private final OrdersRepository ordersRepository;
    private Transaction transaction;
    private Disposable disposable;

    private AccountViewModel(Context context) {
        streamRepository = BinanceStreamRepository.getBinanceStreamRepository();
        streamRepository.setAccountWebsocketListener(this);
        ordersRepository = new OrdersRepository(context);
    }

    public static AccountViewModel getSingleInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = new AccountViewModel(context);
        }
        return singleInstance;
    }

    public BinanceApiAsyncRestClient getClient(@NonNull final String apiKey,
                                               @NonNull final String secKey) {
        BinanceApiAsyncRestClient currentClient = new Client(apiKey, secKey).create();
        CurrentClient.setCurrentClient(currentClient);
        return currentClient;
    }

    public Single<String> getTickerPrice(String symbol) {
        return streamRepository.getTickerPrice(symbol)
          .subscribeOn(Schedulers.io())
          .map(TickerPrice::getPrice);
    }

    public void getAccountBalance(BinanceApiCallback<Account> accountBinanceApiCallback) {
        CurrentClient.getCurrentClient().getAccount(accountBinanceApiCallback);
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
        }
        ordersRepository.addTransaction(transaction);
//        TransactionMap.addTransaction(order.getSymbol() + order.getStrikePrice(), transaction);
    }

    public Single<List<TradeHelperTransaction>> getAllTransactions() {
        return ordersRepository.getAllTransactions()
          .subscribeOn(Schedulers.io())
          .map(transactionMap -> {
              List<TradeHelperTransaction> transactionList = new ArrayList<>();
              for (String transactionJson : transactionMap.values()) {
                  transactionList.add(TransactionDeserializer.deserializeTransaction(transactionJson));
              }
              return transactionList;
          });
    }

    private void checkTransactionMap(Observable<String> symbolData) {
        disposable = symbolData
          .map(TickerStreamConverter::tickerStreamDeserializer)
          .doOnNext(tickerStream -> {
              String transactionId = tickerStream.getStream()
                .replace("@ticker", "")
                .toUpperCase() + tickerStream.getData().getLastPrice();
              Log.d(TAG, "onDataReceived: " + TransactionMap.containsOrder(transactionId));
              if (TransactionMap.containsOrder(transactionId)) {
                  TransactionMap.getTransaction(transactionId).execute();
                  Transaction transaction = TransactionMap.removeTransaction(transactionId);
                  transactionExecutedListener.sendNotification("New Order Posted!", transaction.getSymbol());
              }
          }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(throwable -> Log.d(TAG, "accept: " + throwable.toString()));
    }

    public TearDownManager getTearDownManager() {
        return this;
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
        void sendNotification(@NonNull final String title,
                              @NonNull final String message);
    }
}
