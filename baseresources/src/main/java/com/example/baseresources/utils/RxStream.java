package com.example.baseresources.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public final class RxStream {

    private RxStream() {
    }

    public static Observable<String> fromStringSource(String stream) {
        final PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.onNext(stream);
        return publishSubject;
    }
}
