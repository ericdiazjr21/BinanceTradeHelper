package com.example.binanceproject.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.binanceproject.constants.AppConstants;
import com.example.binanceproject.model.TickerStream;
import com.example.binanceproject.repository.BinanceStreamRepository;
import com.example.binanceproject.utils.GsonConverter;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BinanceStreamViewModel implements BinanceStreamRepository.OnDataStreamOpenListener {

    private static String requestUrl;
    private BinanceStreamRepository repository;
    private OnDataReceivedListener listener;
    private static Map<String, TickerStream> tickerLastPriceMap;
    private static BinanceStreamViewModel singleInstance;

    private BinanceStreamViewModel() {
        this.repository = BinanceStreamRepository.getBinanceStreamRepository();
        tickerLastPriceMap = new HashMap<>();
        repository.setListener(this);
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
        }else{
            repository.initWebSocketConnection(AppConstants.SINGLE_STREAM);
        }
    }

    public void setListener(OnDataReceivedListener listener) {
        this.listener = listener;
    }

    public boolean close(String reason) {
        return repository.closeWebSocket(reason);
    }

    public void setRequestUrl(String requestUrl){
        BinanceStreamViewModel.requestUrl = requestUrl;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onDataStreamMessageOpen(String message) {
        Observable.just(message)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<String, ObservableSource<String>>) json -> {
                    TickerStream tickerStream = GsonConverter.tickerStreamDeserializer(json);
                    tickerLastPriceMap.put(tickerStream.getStream(), tickerStream);
                    return Observable.just(tickerStream.getData().getLastPrice());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lastPrice -> {
                    listener.setListOfValues(tickerLastPriceMap);
                }, throwable -> Log.d(AppConstants.BINANCE_STREAM_VIEW_MODEL_TAG, "accept: " + throwable.toString()));

    }

    @Override
    public void onDataStreamClosed(String reason) {
        listener.notifyStreamClosed(reason);
    }

    @Override
    public void onConnectionError(String throwableResponse) {
        listener.notifyFailedConnection(throwableResponse);
    }


    public interface OnDataReceivedListener {
        void setListOfValues(Map<String, TickerStream> tickerStreamMap);
        void notifyStreamClosed(String reason);
        void notifyFailedConnection(String throwableResponse);
    }


}
