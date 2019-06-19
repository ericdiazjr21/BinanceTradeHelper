package com.example.account.model;

import io.reactivex.annotations.NonNull;

public final class Order {

    private final String symbol;
    private final String strikePrice;
    private final String executePrice;
    private final String quantity;
    private final String orderType;

    public Order(@NonNull final String symbol,
                 @NonNull final String strikePrice,
                 @NonNull final String executePrice,
                 @NonNull final String quantity, String orderType) {
        this.symbol = symbol;
        this.strikePrice = strikePrice;
        this.executePrice = executePrice;
        this.quantity = quantity;
        this.orderType = orderType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStrikePrice() {
        return strikePrice;
    }

    public String getExecutePrice() {
        return executePrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getOrderType() {
        return orderType;
    }
}
