package com.billmatrix.database;

/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

class DBConstants {

    static final String DB_NAME = "BillMatrix.db";
    static final int DB_VERSION = 1;

    /**
     * Common Column Names
     */
    final static String SNO = "SNo";
    final static String PHONE = "phone";
    final static String EMAIL = "email";
    final static String STATUS = "Status";
    final static String ADMIN_ID = "adminID";
    final static String ID = "_id";
    final static String IMEI = "imei";
    final static String CREATE_DATE = "create_Date";
    final static String UPDATE_DATE = "update_date";
    final static String BRANCH = "branch";
    final static String LOCATION = "location";
    final static String DATE = "date";
    final static String TYPE = "type";
    /**
     * 0 - if add
     * 1 - if update
     * -1 - from server
     */
    final static String ADD_UPDATE = "add_or_update";


    final static String EMPLOYEES_TABLE = "TABLE_EMPLOYEES";
    final static String VENDORS_TABLE = "TABLE_VENDORS";
    final static String CUSTOMERS_TABLE = "TABLE_CUSTOMERS";
    final static String INVENTORY_TABLE = "TABLE_INVENTORY";
    final static String POS_ITEMS_TABLE = "TABLE_POS_ITEMS";
    final static String TAX_TABLE = "TABLE_TAX";
    final static String DISCOUNT_TABLE = "TABLE_DISCOUNT";

    /**
     * Employees Column Names
     */
    final static String EMPLOYEE_NAME = "Employee_Name";
    final static String EMPLOYEE_LOGINID = "Employee_LoginID";
    final static String EMPLOYEE_PASSWORD = "Employee_Pwd";
    final static String EMPLOYEE_MOBILE = "Employee_Mobile";

    /**
     * Vendors Column Names
     */
    final static String VENDOR_NAME = "Vendor_Name";
    final static String VENDOR_ID = "vendor_id";
    final static String VENDOR_SINCE = "vendor_since";
    final static String VENDOR_ADDRESS = "vendor_ADD";

    /**
     * Customer Column Names
     */
    final static String CUSTOMER_NAME = "Customer_Name";
    final static String CUSTOMER_CONTACT = "customer_contact";

    /**
     * Inventory Column Names
     */
    final static String ITEM_NAME = "itemName";
    final static String ITEM_CODE = "itemCode";
    final static String UNIT = "unit";
    final static String QUANTITY = "quantity";
    final static String PRICE = "price";
    final static String MY_COST = "myCost";
    final static String WAREHOUSE = "warehouse";
    final static String VENDOR = "vendor";
    final static String BARCODE = "barCode";
    final static String PHOTO = "photo";


    /**
     * TAX Column Names
     */
    final static String TAX_TYPE = "taxType";
    final static String TAX_DESC = "taxDescription";
    final static String TAX_RATE = "taxRate";


    /**
     * Discount Column Names
     */
    final static String DISCOUNT_CODE = "discCode";
    final static String DISCOUNT_DESC = "discDesc";
    final static String DISCOUNT_VALUE = "discValue";

    /**
     * POS Column Names
     */
    final static String SELECTED_QTY = "discCode";

    static final String TAG = "BillMatrix_DataBase";
}
