package com.billmatrix;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.billmatrix.utils.ConnectivityReceiver;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

public class LogoutService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Utils.getSharedPreferences(this).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
        Utils.getSharedPreferences(this).edit().putString(Constants.PREF_USER_TYPE, null).apply();
        Utils.getSharedPreferences(this).edit().putString(Constants.PREF_EMP_LOGIN_ID, "").apply();
        FileUtils.deleteFile(this, Constants.EMPLOYEE_FILE_NAME);
        this.stopSelf();
    }
}