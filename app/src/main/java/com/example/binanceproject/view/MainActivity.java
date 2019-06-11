package com.example.binanceproject.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.account.view.LoginFragment;
import com.example.binanceproject.R;
import com.example.binanceproject.service.TradeHelperService;
import com.example.binanceproject.view.fragments.CoinListFragment;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener, CoinListFragment.OnOptionsMenuItemSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        inflateFragment(CoinListFragment.newInstance());
    }

    @Override
    public void startService() {
        startService(new Intent(MainActivity.this, TradeHelperService.class));
    }

    @Override
    public void stopService() {
        stopService(new Intent(MainActivity.this, TradeHelperService.class));
    }
}
