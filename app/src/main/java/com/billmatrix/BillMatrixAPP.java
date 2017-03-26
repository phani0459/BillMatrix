package com.billmatrix;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

import com.billmatrix.utils.ConnectivityReceiver;
import com.billmatrix.utils.Utils;

public class BillMatrixAPP extends Application {

    private static BillMatrixAPP mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            Utils.showToast("Barcode Scanner detected. Please turn OFF Hardware/Physical keyboard to enable softkeyboard to function.", this);
        }
    }

    public static synchronized BillMatrixAPP getInstance() {
        return mInstance;
    }
}