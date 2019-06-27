package com.example.account.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.account.R;
import com.example.account.model.Order;
import com.example.account.view.recyclerview.TransactionMapAdapter;
import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.callbacks.OnFragmentInteractionListener;
import com.example.baseresources.constants.AppConstants;

import net.sealake.binance.api.client.domain.account.AssetBalance;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public final class OrderFragment extends Fragment implements AccountViewModel.OnTransactionDeletedListener {

    private static final String TAG = "OrderFragment";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AccountViewModel accountViewModel;
    private OnFragmentInteractionListener interactionListener;
    private TransactionMapAdapter adapter;
    private EditText symbolEditText;
    private EditText strikePriceEditText;
    private EditText executePriceEditText;
    private EditText quantityEditText;
    private TextView assetBalanceTextView;
    private String orderType;
    private String assetQuantity;
    private String assetValue;

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
        accountViewModel = AccountViewModel.getSingleInstance(getContext());
        accountViewModel.setTransactionDeletedListener(this);
        findViews(view);
        initQuantityEditTextFocusChangeListener();
        initSpinner(view);
        initOrderButtonListener(view);
        initReturnButtonListener(view);
        initAutoGenerateButtonListener(view);
        initRecyclerView(view);
        return view;
    }

    private void initQuantityEditTextFocusChangeListener() {
        executePriceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (orderType.equals(AppConstants.BUY)) {
                    assetQuantity = accountViewModel.getAssetRatio(assetValue, executePriceEditText.getText().toString());
                    quantityEditText.setText(assetQuantity);
                } else {
                    assetBalanceTextView.setText(String.format("USDT: %s", accountViewModel
                      .getUsdtValue(assetValue, executePriceEditText.getText().toString())));                }
            }
        });
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.transaction_map_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionMapAdapter();
        recyclerView.setAdapter(adapter);
        compositeDisposable.add(accountViewModel.getAllTransactions()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(tradeHelperTransactions -> adapter.setData(tradeHelperTransactions),
            throwable -> Log.d(TAG, "accept: " + throwable.getMessage())));
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

    private void initAutoGenerateButtonListener(View view) {
        view.<Button>findViewById(R.id.auto_generate_button).setOnClickListener(v -> {
            String symbol = symbolEditText.getText().toString();
            if (!symbol.equals("")) {
                compositeDisposable.add(accountViewModel.getTickerPrice(symbol.toUpperCase())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(currentPrice -> {
                      executePriceEditText.setText(currentPrice);
                      strikePriceEditText.setText(currentPrice);
                      compositeDisposable.add(Completable.fromAction(() ->
                        accountViewModel.getAccountBalance(response -> {
                            if (orderType.equals(AppConstants.BUY)) {
                                assetValue = response.getAssetBalance("USDT").getFree();
                                assetBalanceTextView.setText(String.format("USDT: %s", assetValue));
                                assetQuantity = accountViewModel.getAssetRatio(assetValue,
                                  executePriceEditText.getText().toString());
                            } else {
                                assetValue = accountViewModel.getAssetValueFormatted(
                                  response.getAssetBalance(symbol.replace("usdt", "").toUpperCase()).getFree());
                                assetBalanceTextView.setText(String.format("USDT: %s", accountViewModel
                                  .getUsdtValue(assetValue, executePriceEditText.getText().toString())));
                                assetQuantity = assetValue;
                            }
                        }))
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> quantityEditText.setText(assetQuantity)));
                  }, throwable -> Log.d(TAG, "accept: " + throwable.getMessage())));

            }
        });
    }

    private void initReturnButtonListener(View view) {
        view.<Button>findViewById(R.id.return_button).setOnClickListener(v -> interactionListener.closeFragment());
    }

    private void initOrderButtonListener(View view) {
        view.<Button>findViewById(R.id.submit_order_button).setOnClickListener(v ->
          compositeDisposable.add(accountViewModel
            .beginTransaction()
            .placeOrder(new Order(symbolEditText.getText().toString().toUpperCase(),
              strikePriceEditText.getText().toString(),
              executePriceEditText.getText().toString(),
              quantityEditText.getText().toString(), orderType))
            .subscribe(() -> compositeDisposable.add(accountViewModel.getAllTransactions()
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(tradeHelperTransactions -> adapter.setData(tradeHelperTransactions),
                throwable -> Log.d(TAG, "accept: " + throwable.getMessage()))))));
    }

    private void findViews(View view) {
        symbolEditText = view.findViewById(R.id.coin_pair_edit_text);
        strikePriceEditText = view.findViewById(R.id.strike_price_edit_text);
        executePriceEditText = view.findViewById(R.id.purchase_price_edit_text);
        quantityEditText = view.findViewById(R.id.quantity_edit_text);
        assetBalanceTextView = view.findViewById(R.id.asset_balance_text_view);
    }

    @Override
    public void onDestroy() {
        accountViewModel.getTearDownManager().tearDown();
        super.onDestroy();
    }

    @Override
    public void transactionDeleted() {
        compositeDisposable.add(accountViewModel.getAllTransactions()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(tradeHelperTransactions -> adapter.setData(tradeHelperTransactions)));
    }
}
