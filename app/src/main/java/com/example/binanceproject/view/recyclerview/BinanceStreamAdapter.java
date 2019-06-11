package com.example.binanceproject.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.binanceproject.R;
import com.example.baseresources.model.TickerStream;

import java.util.ArrayList;
import java.util.List;

public class BinanceStreamAdapter extends RecyclerView.Adapter<BinanceStreamViewHolder> {

    private List<TickerStream> tickerStreamList = new ArrayList<>();

    @NonNull
    @Override
    public BinanceStreamViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stream_item_view, viewGroup, false);
        return new BinanceStreamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BinanceStreamViewHolder binanceStreamViewHolder, int i) {
        binanceStreamViewHolder.onBind(tickerStreamList.get(i));
    }

    @Override
    public int getItemCount() {
        return tickerStreamList.size();
    }

    public void setData(List<TickerStream> streamList) {
        this.tickerStreamList = streamList;
        notifyDataSetChanged();
    }
}
