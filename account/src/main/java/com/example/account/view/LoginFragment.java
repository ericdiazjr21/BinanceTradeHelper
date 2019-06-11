package com.example.account.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.account.R;
import com.example.account.controller.AccountViewModel;
import com.example.baseresources.utils.SharedPrefsManager;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private View rootView;
    private SharedPrefsManager prefsManager;
    private EditText apiKey;
    private EditText secKey;
    private Switch toggle;
    private OnFragmentInteractionListener listener;
    private AccountViewModel accountViewModel;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        findViews();
        initSharedPrefs();
        initViewModel();
        checkSharedPrefs();
        initLoginButtonListener(rootView.findViewById(R.id.login_button));
        return rootView;
    }

    private void findViews() {
        apiKey = rootView.findViewById(R.id.api_key__edit_text);
        secKey = rootView.findViewById(R.id.sec_key_edit_text);
        toggle = rootView.findViewById(R.id.save_password_toggle);
    }

    private void initSharedPrefs() {
        prefsManager = new SharedPrefsManager(getContext());
    }

    private void initViewModel() {
        accountViewModel = AccountViewModel.getSingleInstance();
    }

    private void checkSharedPrefs() {
        if (prefsManager.getToggleState()) {
            apiKey.setText(prefsManager.getSign1());
            secKey.setText(prefsManager.getSign2());
            toggle.setChecked(true);
        }
    }

    private void initLoginButtonListener(Button loginButton) {
        loginButton.setOnClickListener(v -> checkLoginState());
    }

    private void checkLoginState() {
        String apiKey = this.apiKey.getText().toString();
        String secKey = this.secKey.getText().toString();
        Log.d(TAG, "checkLoginState: " + prefsManager.isPrefsEmpty());
        if (toggle.isChecked() && prefsManager.isPrefsEmpty()) {
            prefsManager.storeKeys(apiKey, secKey, toggle.isChecked());
            makeToast("Saved Login Credentials");
        } else if (!toggle.isChecked()) {
            prefsManager.clearPrefs();
            makeToast("Cleared Login Credentials");
        } else {
            makeToast("Prefs Unchanged");
        }
        initClient(apiKey, secKey);
    }

    private void initClient(String apiKey, String secKey) {
        accountViewModel.getClient(apiKey, secKey).ping(response -> {
            Log.d(TAG, "onResponse: " + response);
            makeToast("Sign On Successful");
            listener.closeFragment();
        });
    }

    private void makeToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void closeFragment();
    }

}
