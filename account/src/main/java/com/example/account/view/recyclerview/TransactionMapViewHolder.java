package com.example.account.view.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.account.R;
import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.model.interfaces.TradeHelperTransaction;

public class TransactionMapViewHolder extends RecyclerView.ViewHolder {

    public TransactionMapViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onBind(TradeHelperTransaction transaction) {
        itemView.<TextView>findViewById(R.id.symbol_value_text_view).setText(transaction.getSymbol());
        itemView.<TextView>findViewById(R.id.strike_price_value_text_view).setText(transaction.getStrikePrice());
        itemView.<TextView>findViewById(R.id.execute_price_value_text_view).setText(transaction.getExecutePrice());
        itemView.<TextView>findViewById(R.id.quantity_value_text_view).setText(transaction.getQuantity());
        itemView.<TextView>findViewById(R.id.order_type_value_text_view).setText(transaction.getOrderType());
        itemView.<Button>findViewById(R.id.delete_button).setOnClickListener(v ->
          AccountViewModel.getSingleInstance(itemView.getContext()).deleteTransaction(transaction));
    }

}
