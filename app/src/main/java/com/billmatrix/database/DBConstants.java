package com.billmatrix.database;

/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class DBConstants {

    public static final String DB_NAME = "BillMatrix.db";
    public static final int DB_VERSION = 1;

    /**
     * Common Column Names
     */
    public final static String SNO = "SNo";
    public final static String PHONE = "phone";
    public final static String EMAIL = "email";

    public final static String EMPLOYEES_TABLE = "TABLE_EMPLOYEES";
    public final static String VENDORS_TABLE = "TABLE_VENDORS";
    /**
     * Employees Column Names
     */
    public final static String EMPLOYEE_NAME = "Employee_Name";
    public final static String EMPLOYEE_LOGINID = "Employee_LoginID";
    public final static String EMPLOYEE_PASSWORD = "Employee_Pwd";
    public final static String EMPLOYEE_MOBILE = "Employee_Mobile";
    public final static String EMPLOYEE_STATUS = "Employee_Status";

    /**
     * Vendors Column Names
     */
    public final static String VENDOR_NAME = "Vendor_Name";
    public final static String VENDOR_ID = "vendor_id";
    public final static String VENDOR_SINCE = "vendor_since";
    public final static String VENDOR_ADDRESS = "vendor_ADD";


    public static final String TAG = "BillMatrix_DataBase";
}
