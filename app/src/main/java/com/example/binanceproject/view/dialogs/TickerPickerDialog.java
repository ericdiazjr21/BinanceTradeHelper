package com.example.binanceproject.view.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.baseresources.constants.AppConstants;
import com.example.binanceproject.R;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;
import com.example.service.ServiceViewModel;

public class TickerPickerDialog {

    private Context context;
    private BinanceStreamViewModel viewModel;
    private ServiceViewModel serviceViewModel;


    public TickerPickerDialog(Context context) {
        this.context = context;
        this.viewModel = BinanceStreamViewModel.getSingleInstance();
        this.serviceViewModel = ServiceViewModel.getSingleInstance(context);
    }

    public void inflateTickerPickerDialog() {
        StringBuilder multipleStreamUrl = new StringBuilder(AppConstants.MULTIPLE_STREAMS);
        View view = LayoutInflater.from(context).inflate(R.layout.ticker_picker_dialog_item_view, null);
        EditText coinPairEditText =  view.findViewById(R.id.ticker_picker_edit_text);
        new AlertDialog.Builder(context)
          .setCustomTitle(view)
          .setMultiChoiceItems(AppConstants.ALL_TICKERS, null,
            (dialog, which, isChecked) -> {
                if (isChecked) {
                    multipleStreamUrl.append(AppConstants.ALL_TICKERS[which]);
                } else {
                    multipleStreamUrl.delete(multipleStreamUrl.length() - AppConstants.ALL_TICKERS[which].length(), multipleStreamUrl.length());
                }
            })
          .setPositiveButton("Done", (dialog, which) -> {
              context = null;
              if (coinPairEditText != null && !coinPairEditText.getText().toString().equals("")) {
                  multipleStreamUrl.append(coinPairEditText.getText() + "@ticker/");
              }
              initStream(multipleStreamUrl);
              dialog.dismiss();
          }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
          .show();
    }

    private void initStream(StringBuilder multipleStreamUrl) {
        serviceViewModel.setRequestUrl(multipleStreamUrl.toString());
        viewModel.setRequestUrl(multipleStreamUrl.toString());
        if (!viewModel.close(AppConstants.NEW_CONNECTION)) {
            viewModel.requestBinanceDataStream();
        }
    }


}
