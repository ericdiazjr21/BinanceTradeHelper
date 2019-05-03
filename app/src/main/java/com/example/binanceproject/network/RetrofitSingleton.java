package com.example.binanceproject.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static final String BASE_URL = "https://api.binance.com";

    private static Retrofit singleInstance;
    private static Retrofit noRXSingleInstance;
    private static BinanceService binanceService;
    private static BinanceService noRxBinanceService;

    private RetrofitSingleton() {
    }

    private static OkHttpClient time() {
        return new OkHttpClient.Builder()
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
    }

    private static Retrofit getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(time())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return singleInstance;
    }

    private static Retrofit getNoRXSingleInstance() {
        if (noRXSingleInstance == null) {
            noRXSingleInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return noRXSingleInstance;
    }

    public static BinanceService getBinanceService() {
        if (binanceService == null) {
            binanceService = getSingleInstance().create(BinanceService.class);
        }
        return binanceService;
    }

    public static BinanceService getNoRXBinanceService() {
        if (noRxBinanceService == null) {
            noRxBinanceService = getNoRXSingleInstance().create(BinanceService.class);
        }
        return noRxBinanceService;
    }


}
