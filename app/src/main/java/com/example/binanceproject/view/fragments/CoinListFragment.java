package com.example.binanceproject.view.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.baseresources.constants.AppConstants;
import com.example.baseresources.model.TickerStream;
import com.example.binanceproject.R;
import com.example.binanceproject.view.dialogs.TickerPickerDialog;
import com.example.binanceproject.view.recyclerview.BinanceStreamAdapter;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CoinListFragment extends Fragment implements BinanceStreamViewModel.OnDataReceivedListener {


    private View rootView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout mainRootLayout;
    private BinanceStreamViewModel viewModel;
    private BinanceStreamAdapter adapter;
    private OnOptionsMenuItemSelected listener;

    public CoinListFragment() {
    }

    public static CoinListFragment newInstance() {
        return new CoinListFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getTearDownManager().tearDown();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_coin_list, container, false);
        findViews();
        setViewModel();
        setRecyclerView();
        initFloatActionButtonListener();
        return rootView;
    }

    private void findViews() {
        mainRootLayout = rootView.findViewById(R.id.main_activity_root_layout);
        floatingActionButton = rootView.findViewById(R.id.add_new_stream);
    }

    private void setViewModel() {
        viewModel = BinanceStreamViewModel.getSingleInstance();
        viewModel.setListener(this);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        adapter = new BinanceStreamAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }

    private void initFloatActionButtonListener() {
        floatingActionButton.setOnClickListener(v -> new TickerPickerDialog(rootView.getContext()).inflateTickerPickerDialog());
    }

    public void setListener(OnOptionsMenuItemSelected listener) {
        this.listener = listener;
    }

    @Override
    public void setListOfValues(List<TickerStream> tickerStreamList) {
        adapter.setData(tickerStreamList);
    }

    @Override
    public void notifyStreamClosed(String reason) {
        Observable.just(reason)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(reasonCode -> {
              adapter.setData(Collections.EMPTY_LIST);
              if (reasonCode.equals(AppConstants.NEW_CONNECTION))
                  viewModel.requestBinanceDataStream();
          });
    }

    @Override
    public void notifyFailedConnection(String throwableResponse) {
        Snackbar snackbar = Snackbar
          .make(mainRootLayout, throwableResponse, Snackbar.LENGTH_INDEFINITE)
          .setAction("Try Again", v -> viewModel.requestBinanceDataStream());
        snackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_service:
                listener.startService();
                break;
            case R.id.stop_service:
                listener.stopService();
                break;
            case R.id.close_stream:
                viewModel.close(AppConstants.REGULAR_CLOSE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnOptionsMenuItemSelected {
        void startService();

        void stopService();
    }


}
