package com.billmatrix.database;

/*
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class DBConstants {

    static final String DB_NAME = "BillMatrix.db";
    static final int DB_VERSION = 1;

    /*
     * Common Column Names
     */
    final static String SNO = "SNo";
    public final static String PHONE = "phone";
    final static String EMAIL = "email";
    public final static String STATUS = "Status";
    final static String ADMIN_ID = "adminID";
    public final static String ID = "_id";
    final static String IMEI = "imei";
    final static String CREATE_DATE = "create_Date";
    final static String UPDATE_DATE = "update_date";
    final static String BRANCH = "branch";
    final static String LOCATION = "location";
    public final static String DATE = "date";
    final static String TYPE = "type";
    /*
     * 0 - if add
     * 1 - if update
     * -1 - from server
     */
    public final static String ADD_UPDATE = "add_or_update";

    final static String EMPLOYEES_TABLE = "TABLE_EMPLOYEES";
    final static String VENDORS_TABLE = "TABLE_VENDORS";
    final static String CUSTOMERS_TABLE = "TABLE_CUSTOMERS";
    public final static String INVENTORY_TABLE = "TABLE_INVENTORY";
    final static String TAX_TABLE = "TABLE_TAX";
    final static String DISCOUNT_TABLE = "TABLE_DISCOUNT";
    public final static String PAYMENTS_TABLE = "TABLE_PAYMENTS";
    public final static String POS_ITEMS_TABLE = "TABLE_POS_ITEMS";
    public final static String TRANSPORT_TABLE = "TABLE_TRANSPORT";
    public final static String CUSTOMER_TRANSACTIONS_TABLE = "TABLE_TRANSACTIONS";

    /*
     * Employees Column Names
     */
    final static String EMPLOYEE_NAME = "Employee_Name";
    final static String EMPLOYEE_LOGINID = "Employee_LoginID";
    final static String EMPLOYEE_PASSWORD = "Employee_Pwd";
    final static String EMPLOYEE_MOBILE = "Employee_Mobile";

    /*
     * Vendors Column Names
     */
    public final static String VENDOR_NAME = "Vendor_Name";
    final static String VENDOR_SINCE = "vendor_since";
    final static String VENDOR_ADDRESS = "vendor_ADD";

    /*
     * Customer Column Names
     */
    public final static String CUSTOMER_NAME = "Customer_Name";
    public final static String CUSTOMER_CONTACT = "customer_contact";

    /*
     * Inventory Column Names
     */
    final static String ITEM_NAME = "itemName";
    public final static String ITEM_CODE = "itemCode";
    final static String UNIT = "unit";
    public final static String QUANTITY = "quantity";
    final static String PRICE = "price";
    final static String MY_COST = "myCost";
    final static String WAREHOUSE = "warehouse";
    public final static String BARCODE = "barCode";
    final static String PHOTO = "photo";


    /*
     * TAX Column Names
     */
    final static String TAX_TYPE = "tax_type";
    final static String TAX_DESC = "tax_description";
    final static String TAX_RATE = "tax_rate";


    /*
     * Discount Column Names
     */
    public final static String DISCOUNT_CODE = "discCode";
    final static String DISCOUNT_DESC = "discDesc";
    final static String DISCOUNT_VALUE = "discValue";

    /*
     * POS Column Names
     */
    final static String SELECTED_QTY = "selectedQTY";

    /*
     * Payments columns
     */
    public final static String PAYEE_NAME = "payee_name";
    final static String AMOUNT = "amount";
    final static String PURPOSE = "purpose_of_payment";
    final static String MODE = "mode_of_payment";
    final static String PAYMENT_TYPE = "payment_type";

    /*
     * Customer Transactions
     */
    final static String BILL_NO = "billNo";
    final static String Z_BILL = "Z_Bill";
    final static String INVENTORY_JSON = "inventoryJSON";
    final static String TOTAL_AMOUNT = "totalAmount";
    final static String AMOUNT_PAID = "amountPaid";
    final static String AMOUNT_DUE = "amountDue";

    /*
     * TRansport columns
     */
    public final static String TRANSPORT_NAME = "payee_name";

    static final String TAG = "BillMatrix_DataBase";
}
