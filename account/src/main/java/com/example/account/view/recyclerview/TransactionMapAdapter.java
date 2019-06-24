package com.example.account.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.account.R;
import com.example.account.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapAdapter extends RecyclerView.Adapter<TransactionMapViewHolder> {

    private List<Transaction> transactionList = new ArrayList<>();

    @NonNull
    @Override
    public TransactionMapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TransactionMapViewHolder(LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.transaction_map_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionMapViewHolder transactionMapViewHolder, int i) {
        transactionMapViewHolder.onBind(transactionList.get(i));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setData(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }
}
