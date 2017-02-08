package com.billmatrix.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

class BillMatrixDBHandler extends DBHandler {

    BillMatrixDBHandler(Context context) {
        super(context, DBConstants.DB_NAME, DBConstants.DB_VERSION, DBConstants.TAG);
    }

    private final static String CREATE_EMP_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.EMPLOYEES_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.EMPLOYEE_NAME + " VARCHAR,"
            + DBConstants.EMPLOYEE_LOGINID + " VARCHAR UNIQUE," + DBConstants.EMPLOYEE_PASSWORD + " VARCHAR,"
            + DBConstants.EMPLOYEE_MOBILE + " VARCHAR," + DBConstants.STATUS + " VARCHAR," + DBConstants.IMEI + " VARCHAR,"
            + DBConstants.TYPE + " VARCHAR," + DBConstants.LOCATION + " VARCHAR," + DBConstants.BRANCH + " VARCHAR,"
            + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.CREATE_DATE + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR,"
            + DBConstants.ID + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";

    private final static String CREATE_VENDOR_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.VENDORS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.VENDOR_NAME + " VARCHAR,"
            + DBConstants.VENDOR_ID + " VARCHAR," + DBConstants.VENDOR_SINCE + " VARCHAR,"
            + DBConstants.VENDOR_ADDRESS + " VARCHAR," + DBConstants.PHONE + " VARCHAR UNIQUE,"
            + DBConstants.EMAIL + " VARCHAR," + DBConstants.ID + " VARCHAR," + DBConstants.CREATE_DATE
            + " VARCHAR," + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.LOCATION + " VARCHAR,"
            + DBConstants.STATUS + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";

    private final static String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.CUSTOMERS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.CUSTOMER_NAME + " VARCHAR," + DBConstants.ID + " VARCHAR," + DBConstants.EMAIL + " VARCHAR,"
            + DBConstants.CUSTOMER_CONTACT + " VARCHAR UNIQUE," + DBConstants.DATE + " VARCHAR," + DBConstants.LOCATION + " VARCHAR,"
            + DBConstants.STATUS + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR,"
            + DBConstants.CREATE_DATE + " VARCHAR," + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";

    private final static String CREATE_INVENTORY_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.INVENTORY_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.ITEM_CODE + " VARCHAR UNIQUE," + DBConstants.ITEM_NAME + " VARCHAR," + DBConstants.UNIT + " VARCHAR,"
            + DBConstants.QUANTITY + " VARCHAR," + DBConstants.PRICE + " VARCHAR," + DBConstants.MY_COST + " VARCHAR,"
            + DBConstants.DATE + " VARCHAR," + DBConstants.WAREHOUSE + " VARCHAR," + DBConstants.VENDOR + " VARCHAR,"
            + DBConstants.BARCODE + " VARCHAR UNIQUE," + DBConstants.PHOTO + " VARCHAR," + DBConstants.STATUS + " VARCHAR," + DBConstants.ID + " VARCHAR,"
            + DBConstants.ADMIN_ID + " VARCHAR," + DBConstants.CREATE_DATE + " VARCHAR," + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";

    private final static String CREATE_POS_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.POS_ITEMS_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.ITEM_CODE + " VARCHAR," + DBConstants.ITEM_NAME + " VARCHAR," + DBConstants.UNIT + " VARCHAR,"
            + DBConstants.QUANTITY + " VARCHAR," + DBConstants.PRICE + " VARCHAR," + DBConstants.MY_COST + " VARCHAR,"
            + DBConstants.DATE + " VARCHAR," + DBConstants.WAREHOUSE + " VARCHAR," + DBConstants.VENDOR + " VARCHAR,"
            + DBConstants.BARCODE + " VARCHAR," + DBConstants.PHOTO + " VARCHAR," + DBConstants.STATUS + " VARCHAR," + DBConstants.ID + " VARCHAR,"
            + DBConstants.ADMIN_ID + " VARCHAR," + DBConstants.CUSTOMER_NAME + " VARCHAR," + DBConstants.SELECTED_QTY + " VARCHAR" + ")";

    private final static String CREATE_TAX_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.TAX_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.TAX_TYPE + " VARCHAR UNIQUE," + DBConstants.ID + " VARCHAR," + DBConstants.TAX_DESC + " VARCHAR,"
            + DBConstants.TAX_RATE + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR," + DBConstants.STATUS + " VARCHAR,"
            + DBConstants.CREATE_DATE + " VARCHAR," + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";


    private final static String CREATE_DISCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.DISCOUNT_TABLE + " (" + DBConstants.SNO
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBConstants.DISCOUNT_CODE + " VARCHAR UNIQUE," + DBConstants.ID + " VARCHAR," + DBConstants.DISCOUNT_DESC + " VARCHAR,"
            + DBConstants.DISCOUNT_VALUE + " VARCHAR," + DBConstants.ADMIN_ID + " VARCHAR," + DBConstants.STATUS + " VARCHAR,"
            + DBConstants.CREATE_DATE + " VARCHAR," + DBConstants.UPDATE_DATE + " VARCHAR," + DBConstants.ADD_UPDATE + " VARCHAR" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EMP_TABLE);
        db.execSQL(CREATE_VENDOR_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
        db.execSQL(CREATE_TAX_TABLE);
        db.execSQL(CREATE_DISCOUNT_TABLE);
        db.execSQL(CREATE_POS_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
