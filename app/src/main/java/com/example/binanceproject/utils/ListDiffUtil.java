package com.example.binanceproject.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.binanceproject.model.TickerStream;

import java.util.List;

public class ListDiffUtil extends DiffUtil.Callback {

    private List<TickerStream> oldList;
    private List<TickerStream> newList;

    public ListDiffUtil(List<TickerStream> oldList, List<TickerStream> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldList.get(i).getData().getLastPrice().equals(newList.get(i1).getData().getLastPrice());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return oldList.get(i).getData().getLastPrice().equals(newList.get(i1).getData().getLastPrice());

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
