package com.example.baseresources.controller;

import io.reactivex.Observable;

public interface AccountWebsocketListener {

    void onDataReceived(Observable<String> symbolData);
}
