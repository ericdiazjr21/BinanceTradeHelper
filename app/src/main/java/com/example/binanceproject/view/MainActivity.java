package com.example.binanceproject.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.account.view.OrderFragment;
import com.example.account.viewmodel.AccountViewModel;
import com.example.baseresources.callbacks.OnFragmentInteractionListener;
import com.example.baseresources.utils.NotificationGenerator;
import com.example.binanceproject.R;
import com.example.binanceproject.service.TradeHelperService;
import com.example.binanceproject.view.fragments.CoinListFragment;
import com.example.login.LoginFragment;

public class MainActivity extends AppCompatActivity implements
  OnFragmentInteractionListener, AccountViewModel.OnTransactionExecutedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AccountViewModel.getSingleInstance().setTransactionExecutedListener(this);
        inflateFragment(LoginFragment.newInstance());
    }

    private void inflateFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
          .replace(R.id.main_activity_root_layout, fragment)
          .addToBackStack(null)
          .commit();
    }

    @Override
    public void closeFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void inflateCoinListFragment() {
        inflateFragment(CoinListFragment.newInstance());
    }

    @Override
    public void inflateOrderFragment() {
        inflateFragment(OrderFragment.newInstance());
    }

    @Override
    public void startService() {
        startService(new Intent(MainActivity.this, TradeHelperService.class));
    }

    @Override
    public void stopService() {
        stopService(new Intent(MainActivity.this, TradeHelperService.class));
    }

    @Override
    public void sendNotification() {
        new NotificationGenerator(this).sendNotification();
    }
}
