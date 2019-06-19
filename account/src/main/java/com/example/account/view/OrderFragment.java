package com.example.account.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.account.R;
import com.example.account.model.Order;
import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.callbacks.OnFragmentInteractionListener;
import com.example.baseresources.constants.AppConstants;

public final class OrderFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private OnFragmentInteractionListener interactionListener;
    private String orderType;

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

        EditText coinPairEditText = view.findViewById(R.id.coin_pair_edit_text);
        EditText strikePriceEditText = view.findViewById(R.id.strike_price_edit_text);
        EditText executePriceEditText = view.findViewById(R.id.purchase_price_edit_text);
        EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);

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

        coinPairEditText.setText("MATICUSDT");
        strikePriceEditText.setText("0.02407000");
        executePriceEditText.setText("0.04000000");
        quantityEditText.setText("500");

        view.<Button>findViewById(R.id.submit_order_button).setOnClickListener(v -> {
              accountViewModel
                .beginTransaction()
                .placeOrder(new Order(coinPairEditText.getText().toString().toUpperCase(),
                  strikePriceEditText.getText().toString(),
                  executePriceEditText.getText().toString(),
                  quantityEditText.getText().toString(), orderType));

              interactionListener.closeFragment();
          }

        );
        return view;
    }

    @Override
    public void onDestroy() {
        accountViewModel.getTearDownManager().tearDown();
        super.onDestroy();
    }
}
