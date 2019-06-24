package com.example.baseresources.network;

import com.example.baseresources.model.TickerPrice;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface BinanceService {

    @GET("/api/v3/ticker/price")
    Single<TickerPrice> getTickerPrice(@Query("symbol") String symbol);

}
