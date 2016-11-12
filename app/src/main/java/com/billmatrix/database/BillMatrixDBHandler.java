package com.billmatrix.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class BillMatrixDBHandler extends DBHandler {

    public BillMatrixDBHandler(Context context) {
        super(context, DBConstants.DB_NAME, DBConstants.DB_VERSION, DBConstants.TAG);
    }

    public final static String CREATE_EMP_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.EMPLOYEES_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.EMPLOYEE_NAME + " VARCHAR,"
            + DBConstants.EMPLOYEE_LOGINID + " VARCHAR UNIQUE," + DBConstants.EMPLOYEE_PASSWORD + " VARCHAR,"
            + DBConstants.EMPLOYEE_MOBILE + " VARCHAR," + DBConstants.STATUS + " VARCHAR" + ")";

    public final static String CREATE_VENDOR_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.VENDORS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.VENDOR_NAME + " VARCHAR,"
            + DBConstants.VENDOR_ID + " VARCHAR," + DBConstants.VENDOR_SINCE + " VARCHAR,"
            + DBConstants.VENDOR_ADDRESS + " VARCHAR," + DBConstants.PHONE + " VARCHAR UNIQUE," + DBConstants.EMAIL + " VARCHAR UNIQUE" + ")";

    public final static String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.CUSTOMERS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.CUSTOMER_NAME + " VARCHAR,"
            + DBConstants.CUSTOMER_CONTACT + " VARCHAR UNIQUE," + DBConstants.CUSTOMER_DATE + " VARCHAR,"
            + DBConstants.CUSTOMER_LOCATION + " VARCHAR," + DBConstants.STATUS + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EMP_TABLE);
        db.execSQL(CREATE_VENDOR_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
