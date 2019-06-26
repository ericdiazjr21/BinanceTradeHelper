package com.example.baseresources.model.interfaces;

public interface TradeHelperTransaction {
    void execute();

    String getSymbol();

    String getQuantity();

    String getExecutePrice();

    String getStrikePrice();

    String getOrderType();

    int getTransactionId();
}
