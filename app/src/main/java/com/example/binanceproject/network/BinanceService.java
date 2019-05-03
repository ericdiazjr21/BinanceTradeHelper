package com.example.binanceproject.network;

import com.example.binanceproject.model.BinanceUser;
import com.example.binanceproject.model.TickerPrice;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface BinanceService {

    @POST("api/v3/order")
    @FormUrlEncoded
    Observable<BinanceUser> validateUser(@Field("apiKay") String apiKey,
                                         @Field("secretKey") String secretKey);

    @GET("/api/v3/ticker/price")
    @Streaming
    Observable<TickerPrice> getTickerPrice(@Query("symbol") String symbol);

    @GET("/api/v3/ticker/price")
    Call<TickerPrice> getNoRXTickerPrice(@Query("symbol") String symbol);

}
