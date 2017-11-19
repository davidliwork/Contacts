package com.android.developer.contacts.base;

import android.util.Log;

import rx.Observer;

/**
 * Created by DavidLi on 2017-11-17.
 */

public class BaseObserver<T> implements Observer<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e(getClass().getName(), e.toString());
    }

    @Override
    public void onNext(T t) {

    }

}
