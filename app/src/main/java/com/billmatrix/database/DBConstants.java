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
    public final static String STATUS = "Status";
    public final static String ADMIN_ID = "adminID";
    public final static String ID = "_id";
    public final static String IMEI = "imei";
    public final static String CREATE_DATE = "create_Date";
    public final static String UPDATE_DATE = "update_date";
    public final static String BRANCH = "branch";
    public final static String LOCATION = "location";
    public final static String DATE = "date";
    public final static String TYPE = "type";


    public final static String EMPLOYEES_TABLE = "TABLE_EMPLOYEES";
    public final static String VENDORS_TABLE = "TABLE_VENDORS";
    public final static String CUSTOMERS_TABLE = "TABLE_CUSTOMERS";
    public final static String INVENTORY_TABLE = "TABLE_INVENTORY";
    public final static String TAX_TABLE = "TABLE_TAX";

    /**
     * Employees Column Names
     */
    public final static String EMPLOYEE_NAME = "Employee_Name";
    public final static String EMPLOYEE_LOGINID = "Employee_LoginID";
    public final static String EMPLOYEE_PASSWORD = "Employee_Pwd";
    public final static String EMPLOYEE_MOBILE = "Employee_Mobile";

    /**
     * Vendors Column Names
     */
    public final static String VENDOR_NAME = "Vendor_Name";
    public final static String VENDOR_ID = "vendor_id";
    public final static String VENDOR_SINCE = "vendor_since";
    public final static String VENDOR_ADDRESS = "vendor_ADD";

    /**
     * Customer Column Names
     */
    public final static String CUSTOMER_NAME = "Customer_Name";
    public final static String CUSTOMER_CONTACT = "customer_contact";

    /**
     * Inventory Column Names
     */
    public final static String ITEM_NAME = "itemName";
    public final static String ITEM_CODE = "itemCode";
    public final static String UNIT = "unit";
    public final static String QUANTITY = "quantity";
    public final static String PRICE = "price";
    public final static String MY_COST = "myCost";
    public final static String WAREHOUSE = "warehouse";
    public final static String VENDOR = "vendor";
    public final static String BARCODE = "barCode";
    public final static String PHOTO = "photo";


    /**
     * TAX Column Names
     */
    public final static String TAX_TYPE = "taxType";
    public final static String TAX_DESC = "taxDescription";
    public final static String TAX_RATE = "taxRate";

    public static final String TAG = "BillMatrix_DataBase";
}
