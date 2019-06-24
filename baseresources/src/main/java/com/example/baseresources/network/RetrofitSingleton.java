package com.example.baseresources.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitSingleton {

    private static final String BASE_URL = "https://api.binance.com";

    private static Retrofit singleInstance;
    private static BinanceService binanceService;

    private RetrofitSingleton() {
    }

    private static Retrofit getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .build();
        }
        return singleInstance;
    }

    public static BinanceService getBinanceService() {
        if (binanceService == null) {
            binanceService = getSingleInstance().create(BinanceService.class);
        }
        return binanceService;
    }


}
