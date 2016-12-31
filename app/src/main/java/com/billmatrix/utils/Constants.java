package com.billmatrix.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

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
    public static final String PREF_ADMIN_NAME = "adminName";
    public static final String PREF_LOGIN_ID = "loginID";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_ADMIN_ID = "adminID";
    public static final String PREF_EMP_LOGIN_ID = "employeeLoginId";
    public static final String PREF_DISCOUNT_VALUE = "discountValue_Selected";
    public static final String PREF_DISCOUNT_CODE = "discountCode_Selected";

    /**
     * File Names
     */
    public static String PROFILE_FILE_NAME = "ProfileModel";
    public static String EMPLOYEE_FILE_NAME = "Employee_Profile";
    public static Gson gson;
    public static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String dateFormat = "dd-MM-yyyy";

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        return gson;
    }

    public static SimpleDateFormat simpleDateFormat;
    public static SimpleDateFormat simpleDateTimeFormat;

    public static SimpleDateFormat getDateTimeFormat() {
        if (simpleDateTimeFormat == null) {
            simpleDateTimeFormat = new SimpleDateFormat(Constants.dateTimeFormat, Locale.getDefault());
        }
        return simpleDateTimeFormat;
    }

    public static SimpleDateFormat getDateFormat() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());
        }
        return simpleDateFormat;
    }

    public static Type hashMapType = new TypeToken<HashMap<String, String>>() {
    }.getType();
}
