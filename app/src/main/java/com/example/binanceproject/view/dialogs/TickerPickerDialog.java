package com.example.binanceproject.view.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.baseresources.constants.AppConstants;
import com.example.binanceproject.viewmodel.BinanceStreamViewModel;
import com.example.service.ServiceViewModel;

public class TickerPickerDialog {

    private Context context;
    private BinanceStreamViewModel viewModel;
    private ServiceViewModel serviceViewModel;


    public TickerPickerDialog(Context context) {
        this.context = context;
        this.viewModel = BinanceStreamViewModel.getSingleInstance();
        this.serviceViewModel = ServiceViewModel.getSingleInstance();
    }

    public void inflateTickerPickerDialog() {
        StringBuilder multipleStreamUrl = new StringBuilder(AppConstants.MULTIPLE_STREAMS);
        new AlertDialog.Builder(context)
          .setMultiChoiceItems(AppConstants.ALL_TICKERS, null,
            (dialog, which, isChecked) -> {
                if (isChecked) {
                    multipleStreamUrl.append(AppConstants.ALL_TICKERS[which]);
                }
            })
          .setPositiveButton("Done", (dialog, which) -> {
              context = null;
              serviceViewModel.setRequestUrl(multipleStreamUrl.toString());
              viewModel.setRequestUrl(multipleStreamUrl.toString());
              if (!viewModel.close(AppConstants.NEW_CONNECTION)) {
                  viewModel.requestBinanceDataStream();
              }

              dialog.dismiss();
          }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }


}
