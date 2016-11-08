package com.billmatrix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.billmatrix.models.Employee;


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
            + DBConstants.EMPLOYEE_MOBILE + " VARCHAR," + DBConstants.EMPLOYEE_STATUS + " VARCHAR" + ")";

    public final static String CREATE_VENDOR_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.VENDORS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.VENDOR_NAME + " VARCHAR,"
            + DBConstants.VENDOR_ID + " VARCHAR UNIQUE," + DBConstants.VENDOR_SINCE + " VARCHAR,"
            + DBConstants.VENDOR_ADDRESS + " VARCHAR," + DBConstants.PHONE + " VARCHAR," + DBConstants.EMAIL + " VARCHAR" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EMP_TABLE);
        db.execSQL(CREATE_VENDOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
