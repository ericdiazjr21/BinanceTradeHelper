package com.example.binanceproject.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.baseresources.callbacks.TearDownManager;
import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.controller.AccountWebsocketListener;
import com.example.baseresources.model.TickerStream;
import com.example.baseresources.repository.BinanceStreamRepository;
import com.example.baseresources.utils.TickerStreamConverter;
import com.example.baseresources.utils.ListCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BinanceStreamViewModel implements
  BinanceStreamRepository.OnDataStreamOpenListener,
  TearDownManager {

    private static String requestUrl;
    private static BinanceStreamViewModel singleInstance;
    private static Map<String, TickerStream> tickerLastPriceMap;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BinanceStreamRepository repository;
    private OnDataReceivedListener listener;

    private BinanceStreamViewModel() {
        this.repository = BinanceStreamRepository.getBinanceStreamRepository();
        tickerLastPriceMap = new HashMap<>();
        repository.setStreamOpenListener(this);
    }

    public static BinanceStreamViewModel getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new BinanceStreamViewModel();
        }
        return singleInstance;
    }

    public void requestBinanceDataStream() {
        tickerLastPriceMap.clear();
        if (requestUrl != null) {
            repository.initWebSocketConnection(requestUrl);
        } else {
            repository.initWebSocketConnection(AppConstants.SINGLE_STREAM);
        }
    }

    public void setListener(OnDataReceivedListener listener) {
        this.listener = listener;
    }

    public boolean close(String reason) {
        return repository.closeWebSocket(reason);
    }

    public void setRequestUrl(String requestUrl) {
        BinanceStreamViewModel.requestUrl = requestUrl;
    }

    public void setOnDataReceivedListener(AccountWebsocketListener accountWebsocketListener){
        repository.setAccountWebsocketListener(accountWebsocketListener);
    }

    public TearDownManager getTearDownManager(){
        return this;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onDataStreamMessageOpen(Observable<String> message) {
        if (tickerLastPriceMap != null) {
            mapStream(message);
        }

    }

    private void mapStream(Observable<String> message) {
        compositeDisposable.add(message.subscribeOn(Schedulers.io())
          .map(json -> {
              TickerStream tickerStream = TickerStreamConverter.tickerStreamDeserializer(json);
              tickerLastPriceMap.put(tickerStream.getStream(), tickerStream);
              return ListCreator.getStreamList(tickerLastPriceMap);
          }).observeOn(AndroidSchedulers.mainThread())
          .subscribe(tickerStreamList -> listener.setListOfValues(tickerStreamList),
            throwable -> Log.d(AppConstants.BINANCE_STREAM_VIEW_MODEL_TAG, "accept: " + throwable.toString())));
    }

    @Override
    public void onDataStreamClosed(String reason) {
        listener.notifyStreamClosed(reason);
    }

    @Override
    public void onConnectionError(String throwableResponse) {
        listener.notifyFailedConnection(throwableResponse);
    }

    @Override
    public void tearDown() {
        singleInstance = null;
        tickerLastPriceMap = null;
        requestUrl = null;
        compositeDisposable.dispose();
    }

    public interface OnDataReceivedListener {
        void setListOfValues(List<TickerStream> tickerStreamMap);

        void notifyStreamClosed(String reason);

        void notifyFailedConnection(String throwableResponse);
    }


}
