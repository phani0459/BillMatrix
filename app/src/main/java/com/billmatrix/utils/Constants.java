package com.billmatrix.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by KANDAGATLAs on 22-10-2016.
 */

public class Constants {
    /**
     * Preferences Constants
     */
    /**
     * remove preferences when logout in base tab activity
     */
    public static final String PREFERENCES_NAME = "billMatrix shared prefs";
    public static final String IS_LOGGED_IN = "isUserLoggedIN";
    public static final String PREF_LICENECE_KEY = "licenceKey";
    public static final String PREF_USER_TYPE = "userType";
    public static final String PREF_USER_NAME = "userName";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_LOGIN_ID = "loginID";

    /**
     * File Names
     */
    public static String PROFILE_FILE_NAME = "ProfileModel";
    public static String EMPLOYEES_FILE_NAME = "Employees";
    public static Gson gson;
    public static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static SimpleDateFormat simpleDateFormat;

    public static SimpleDateFormat getSimpleDateFormat() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(Constants.dateFormat);
        }
        return simpleDateFormat;
    }

    public static Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
}
