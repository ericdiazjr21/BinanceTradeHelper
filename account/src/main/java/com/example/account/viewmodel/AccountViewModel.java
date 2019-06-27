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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class AccountViewModel extends ViewModel implements
  AccountWebsocketListener,
  TearDownManager {

    private static final String TAG = "AccountViewModel";
    private static AccountViewModel singleInstance;
    private static OnTransactionExecutedListener transactionExecutedListener;
    private final BinanceStreamRepository streamRepository;
    private final OrdersRepository ordersRepository;
    private OnTransactionDeletedListener transactionDeletedListener;
    private Transaction transaction;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        transaction = new Transaction(new Random().nextInt(9999999));
        return this;
    }

    public void setTransactionExecutedListener(OnTransactionExecutedListener transactionExecutedListener) {
        AccountViewModel.transactionExecutedListener = transactionExecutedListener;
    }

    public String getUsdtValue(String assetValue, String executePrice) {
        return String.valueOf(new DecimalFormat("#.##")
          .format(Double.valueOf(assetValue) * Double.valueOf(executePrice)));
    }

    public String getAssetRatio(String usdtValue, String executePrice) {
        return new DecimalFormat("#.##")
          .format(Double.valueOf(usdtValue) / Double.parseDouble(executePrice));
    }

    public String getAssetValueFormatted(String value) {
        return new DecimalFormat("#").format(Double.valueOf(value) - 1);
    }

    public Completable placeOrder(@NonNull final Order order) {
        return Completable.fromAction(() -> {
            Log.d(TAG, "placeOrder: " + order.getSymbol());
            switch (order.getOrderType()) {
                case AppConstants.BUY:
                    transaction.placeBuyOrder(order.getSymbol(), order.getStrikePrice(), order.getExecutePrice(), order.getQuantity());
                    break;
                case AppConstants.SELL:
                    transaction.placeSellOrder(order.getSymbol(), order.getStrikePrice(), order.getExecutePrice(), order.getQuantity());
                    break;
            }
        }).mergeWith(ordersRepository.addTransaction(transaction));

    }

    public Observable<List<TradeHelperTransaction>> getAllTransactions() {
        return ordersRepository.getAllTransactions()
          .subscribeOn(Schedulers.io())
          .map(jsonTransactionMap -> {
              for (String transactionJson : jsonTransactionMap.values()) {
                  Transaction transaction = (Transaction) TransactionDeserializer.deserializeTransaction(transactionJson);
                  TransactionMap.addTransaction(transaction.getSymbol() + transaction.getStrikePrice(), transaction);
                  Log.d(TAG, "getAllTransactions: " + TransactionMap.getSize());
              }
              List<TradeHelperTransaction> transactionList = new ArrayList<>();
              for (String transactionJson : jsonTransactionMap.values()) {
                  transactionList.add(TransactionDeserializer.deserializeTransaction(transactionJson));
              }
              return transactionList;
          });

    }

    public void deleteTransaction(TradeHelperTransaction transaction) {
        compositeDisposable.add(Completable.fromAction(() -> {
            compositeDisposable.add(ordersRepository.deleteTransaction(transaction)
              .subscribe(() -> Log.d(TAG, "run: database delete complete"),
                throwable -> Log.d(TAG, "accept: " + throwable.getMessage())));
            TransactionMap.removeTransaction(transaction.getSymbol() + transaction.getStrikePrice());
        }).subscribeOn(Schedulers.io())
          .subscribe(() -> {
              Log.d(TAG, "run: Transaction Deleted " + TransactionMap.getSize());
              transactionDeletedListener.transactionDeleted();
          }));
    }

    private void checkTransactionMap(Observable<String> symbolData) {
        Log.d(TAG, "checkTransactionMap: Size: " + TransactionMap.getSize());
        compositeDisposable.add(symbolData
          .map(TickerStreamConverter::tickerStreamDeserializer)
          .doOnNext(tickerStream -> {
              String transactionId = tickerStream.getStream()
                .replace("@ticker", "")
                .toUpperCase() + tickerStream.getData().getLastPrice();
              Log.d(TAG, "onDataReceived: " + TransactionMap.containsOrder(transactionId));
              if (TransactionMap.containsOrder(transactionId)) {
                  TransactionMap.getTransaction(transactionId).execute();
                  Transaction transaction = TransactionMap.removeTransaction(transactionId);
                  deleteTransaction(transaction);
                  transactionExecutedListener.sendNotification("New Order Posted!", transaction.getSymbol());
              }
          }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(symbol -> Log.d(TAG, "accept: " + symbol.getData().getLastPrice())));
    }

    public void setTransactionDeletedListener(OnTransactionDeletedListener transactionDeletedListener) {
        this.transactionDeletedListener = transactionDeletedListener;
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
        compositeDisposable.dispose();
    }

    public interface OnTransactionExecutedListener {
        void sendNotification(@NonNull final String title,
                              @NonNull final String message);
    }

    public interface OnTransactionDeletedListener {
        void transactionDeleted();
    }
}
