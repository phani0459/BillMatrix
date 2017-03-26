package com.billmatrix.utils;


import android.support.v4.util.ArrayMap;

import com.billmatrix.models.Inventory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static final String PREF_DISCOUNT_FLOAT_VALUE = "discountValue_Selected";
    public static final String PREF_DISCOUNT_CODE = "discountCode_Selected";
    public static final String PREF_TAX_JSON = "taxJSON";
    public static final String PREF_DEFAULT_DISCOUNT_CODE = "discCode";
    public static final String PREF_IS_DISCOUNT_FOOTER_SELECTED = "footerType";
    public static final String PREF_FOOTER_TEXT = "footerText";
    public static final String PREF_BOOL_IS_BARCODE_SCANNER_ATTACHED = "isScannerAttached";

    /**
     * Edited Offline
     */

    public static final String PREF_EMPLOYEES_EDITED_OFFLINE = "empOffline";
    public static final String PREF_INVENTORY_EDITED_OFFLINE = "inventoryOffline";
    public static final String PREF_CUSTOMERS_EDITED_OFFLINE = "customersOffline";
    public static final String PREF_REPORTS_EDITED_OFFLINE = "reportsOffline";
    public static final String PREF_PURCS_EDITED_OFFLINE = "pursOffline";
    public static final String PREF_SALES_EDITED_OFFLINE = "salesOffline";
    public static final String PREF_VENDORS_EDITED_OFFLINE = "vendorsOffline";
    public static final String PREF_DISCS_EDITED_OFFLINE = "discsOffline";
    public static final String PREF_HnF_EDITED_OFFLINE = "HnFOffline";
    public static final String PREF_GEN_REPORT_EDITED_OFFLINE = "GenRepOffline";
    public static final String PREF_TAXES_EDITED_OFFLINE = "TaxesOffline";


    /**
     * File Names
     */
    public static String PROFILE_FILE_NAME = "ProfileModel";
    public static String EMPLOYEE_FILE_NAME = "Employee_Profile";
    private static Gson gson;
    private static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static String dateFormat = "dd-MM-yyyy";
    private static String billDateFormat = "ddMMyyyy";
    private static String timeFormat = "HH:mm:ss";

    /**
     * 0 - if add
     * 1 - if update
     * -1 - from server
     */
    public static String ADD_OFFLINE = "0";
    public static String UPDATE_OFFLINE = "1";
    public static String DATA_FROM_SERVER = "-1";


    public static int DEFAULT_INVENTORY_SHOW_SIZE = 10;
    public static final long PRINTER_DISCOVERY_TIME = 10000;


    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        return gson;
    }

    private static SimpleDateFormat simpleDateFormat, simpleTimeFormat, simpleBillDateFormat;
    private static SimpleDateFormat simpleDateTimeFormat;

    public static SimpleDateFormat getDateTimeFormat() {
        if (simpleDateTimeFormat == null) {
            simpleDateTimeFormat = new SimpleDateFormat(Constants.dateTimeFormat, Locale.getDefault());
        }
        return simpleDateTimeFormat;
    }

    public static SimpleDateFormat getBillDateFormat() {
        if (simpleBillDateFormat == null) {
            simpleBillDateFormat = new SimpleDateFormat(Constants.billDateFormat, Locale.getDefault());
        }
        return simpleBillDateFormat;
    }

    public static SimpleDateFormat getDateFormat() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());
        }
        return simpleDateFormat;
    }

    public static SimpleDateFormat getTimeFormat() {
        if (simpleTimeFormat == null) {
            simpleTimeFormat = new SimpleDateFormat(Constants.timeFormat, Locale.getDefault());
        }
        return simpleTimeFormat;
    }

    public static Type hashMapType = new TypeToken<HashMap<String, String>>() {
    }.getType();

    public static Type floatArrayMapType = new TypeToken<ArrayMap<String, Float>>() {
    }.getType();

    public static Type inventoryDatasMapType = new TypeToken<ArrayList<Inventory.InventoryData>>() {
    }.getType();
}
