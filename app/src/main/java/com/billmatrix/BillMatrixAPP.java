package com.billmatrix;

import android.app.Application;

public class BillMatrixAPP extends Application {

    private static BillMatrixAPP mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized BillMatrixAPP getInstance() {
        return mInstance;
    }
}