package com.example.binanceproject.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.example.binanceproject.R;
import com.example.binanceproject.constants.AppConstants;
import com.example.binanceproject.model.TickerStream;
import com.example.binanceproject.utils.ListCreator;
import com.example.binanceproject.view.dialogs.TickerPickerDialog;
import com.example.binanceproject.view.recyclerview.BinanceStreamAdapter;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;

import java.util.Collections;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements BinanceStreamViewModel.OnDataReceivedListener {

    private static final String TAG = "MainActivity";
    public static final String API_KEY = "1vLpvkhNLLSfwAsJg2Xw3jxiRT4GqcuKHbQinHWCmIZXuuUzH0hauJXQAsBHeKvM";
    public static final String SECRET_KEY = "dGVtHincxQi2NeAEVMGn13CNcNDleJAJ1jjniG7ZZ7nY7gOX4ansCt0CT9jFogR9";
    private Button closeStreamButton;
    private FloatingActionButton floatingActionButton;
    private BinanceStreamViewModel viewModel;
    private BinanceStreamAdapter adapter;
    private ConstraintLayout mainRootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setViewModel();
        setRecyclerView();
        initFloatActionButtonListener();
        initCloseButtonListener();
    }

    private void findViews() {
        mainRootLayout = findViewById(R.id.main_activity_root_layout);
        closeStreamButton = findViewById(R.id.close_stream_button);
        floatingActionButton = findViewById(R.id.add_new_stream);
    }

    private void setViewModel() {
        viewModel = BinanceStreamViewModel.getSingleInstance();
        viewModel.setListener(this);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new BinanceStreamAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initFloatActionButtonListener() {
        floatingActionButton.setOnClickListener(v -> new TickerPickerDialog(MainActivity.this).inflateTickerPickerDialog());
    }

    private void initCloseButtonListener() {
        closeStreamButton.setOnClickListener(v -> {
            viewModel.close(AppConstants.REGULAR_CLOSE);
        });
    }

    @Override
    public void setListOfValues(Map<String, TickerStream> tickerStreamMap) {
        adapter.setData(ListCreator.getStreamList(tickerStreamMap));
    }

    @SuppressLint("CheckResult")
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


//    private void getNoRXTickerPrice(String ticker) {
//        RetrofitSingleton.getNoRXBinanceService().getNoRXTickerPrice(ticker)
//                .enqueue(new Callback<TickerPrice>() {
//                    @Override
//                    public void onResponse(Call<TickerPrice> call, Response<TickerPrice> response) {
//                        Log.d(TAG, "onResponse: " + response.body().toString());
//                        Log.d(TAG, "onResponse: " + call.request().toString());
//                    }
//
//                    @Override
//                    public void onFailure(Call<TickerPrice> call, Throwable t) {
//
//                    }
//                });
//    }
//
//    @SuppressLint("CheckResult")
//    private void getTickerPrice(String ticker) {
//        RetrofitSingleton.getBinanceService()
//                .getTickerPrice(ticker)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<String> event(BufferedSource source) {
//        return Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                Log.d(TAG, "subscribe: " + source.exhausted());
//                try {
//                    while (!source.exhausted()) {
//                        emitter.onNext(source.readUtf8Line());
//                        Log.d(TAG, "subscribe: " + source.readUtf8Line());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    emitter.onComplete();
//                }
//            }
//        });
//    }
//
//    @SuppressLint("CheckResult")
//    private void login() {
//        RetrofitSingleton.getBinanceService()
//                .validateUser(API_KEY, SECRET_KEY)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(binanceUser -> Log.d(TAG, "accept: " + binanceUser.toString()),
//                        throwable -> Log.d(TAG, "accept: " + throwable.toString()));
//    }
}
