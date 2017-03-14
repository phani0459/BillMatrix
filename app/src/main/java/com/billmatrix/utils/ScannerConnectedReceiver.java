package com.billmatrix.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class ScannerConnectedReceiver extends BroadcastReceiver {


    public ScannerConnectedReceiver() {
        super();
    }

    @Override
    public void onReceive(Context mContext, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice accessory = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_BOOL_IS_BARCODE_SCANNER_ATTACHED, false).apply();
        }
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice accessory = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_BOOL_IS_BARCODE_SCANNER_ATTACHED, true).apply();
        }

    }
}