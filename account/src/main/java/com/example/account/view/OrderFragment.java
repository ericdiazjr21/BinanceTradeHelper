package com.example.account.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.account.R;
import com.example.account.model.Order;
import com.example.account.utils.TransactionMap;
import com.example.account.view.recyclerview.TransactionMapAdapter;
import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.callbacks.OnFragmentInteractionListener;
import com.example.baseresources.constants.AppConstants;

public final class OrderFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private OnFragmentInteractionListener interactionListener;
    private String orderType;
    private TransactionMapAdapter adapter;

    public OrderFragment() {
    }

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        accountViewModel = AccountViewModel.getSingleInstance();
        initSpinner(view);
        initOrderAndReturnButtonOnClick(view);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.transaction_map_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionMapAdapter();
        adapter.setData(TransactionMap.getAllTransactionsList());
        recyclerView.setAdapter(adapter);
    }

    private void initSpinner(View view) {
        Spinner orderTypeSpinner = view.findViewById(R.id.buy_sell_spinner);
        orderTypeSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.buy_sell_spinner, R.layout.support_simple_spinner_dropdown_item));
        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        orderType = AppConstants.BUY;
                        break;
                    case 1:
                        orderType = AppConstants.SELL;
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initOrderAndReturnButtonOnClick(View view) {
        view.<Button>findViewById(R.id.submit_order_button).setOnClickListener(v -> {
              accountViewModel
                .beginTransaction()
                .placeOrder(new Order(view.<EditText>findViewById(R.id.coin_pair_edit_text).getText().toString().toUpperCase(),
                  view.<EditText>findViewById(R.id.strike_price_edit_text).getText().toString(),
                  view.<EditText>findViewById(R.id.purchase_price_edit_text).getText().toString(),
                  view.<EditText>findViewById(R.id.quantity_edit_text).getText().toString(), orderType));
              adapter.setData(TransactionMap.getAllTransactionsList());
              Toast.makeText(view.getContext(), "Order Submitted", Toast.LENGTH_SHORT).show();
          }

        );
        view.<Button>findViewById(R.id.return_button).setOnClickListener(v -> interactionListener.closeFragment());
    }

    @Override
    public void onDestroy() {
        accountViewModel.getTearDownManager().tearDown();
        super.onDestroy();
    }
}
