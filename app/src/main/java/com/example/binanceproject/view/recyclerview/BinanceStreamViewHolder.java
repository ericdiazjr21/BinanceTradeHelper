package com.example.binanceproject.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.baseresources.model.TickerStream;
import com.example.binanceproject.R;

class BinanceStreamViewHolder extends RecyclerView.ViewHolder {

    private TextView tickerName;
    private TextView tickerPrice;

    BinanceStreamViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(@NonNull View itemView) {
        tickerName = itemView.findViewById(R.id.ticker_name);
        tickerPrice = itemView.findViewById(R.id.ticker_price);
    }

    void onBind(TickerStream tickerStream) {
        if (tickerStream != null) {
            tickerName.setText(tickerStream.getStream());
            tickerPrice.setText(tickerStream.getData().getLastPrice());
        }
    }
}
