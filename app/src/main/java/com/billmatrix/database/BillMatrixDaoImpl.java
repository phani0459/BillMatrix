package com.billmatrix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Tax;
import com.billmatrix.models.Transaction;
import com.billmatrix.models.Transport;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.billmatrix.database.DBConstants.*;
/*
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class BillMatrixDaoImpl implements BillMatrixDao {

    private SQLiteDatabase db;

    public BillMatrixDaoImpl(Context context) {
        BillMatrixDBHandler dbHandler = new BillMatrixDBHandler(context);
        db = dbHandler.getWriteDB();
    }

    /************************************************
     * ********* EMPLOYEE METHODS ********************
     *************************************************/

    public long addEmployee(Employee.EmployeeData employeeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EMPLOYEE_NAME, employeeData.username);
        contentValues.put(EMPLOYEE_MOBILE, employeeData.mobile_number);
        contentValues.put(EMPLOYEE_LOGINID, employeeData.login_id);
        contentValues.put(EMPLOYEE_PASSWORD, employeeData.password);
        contentValues.put(STATUS, employeeData.status);
        contentValues.put(ID, employeeData.id);
        contentValues.put(ADD_UPDATE, employeeData.add_update);
        contentValues.put(ADMIN_ID, employeeData.admin_id);
        contentValues.put(CREATE_DATE, employeeData.create_date);
        contentValues.put(UPDATE_DATE, employeeData.update_date);
        contentValues.put(BRANCH, employeeData.branch);
        contentValues.put(LOCATION, employeeData.location);
        contentValues.put(IMEI, employeeData.imei_number);
        contentValues.put(TYPE, employeeData.type);
        return db.insert(EMPLOYEES_TABLE, null, contentValues);
    }

    private boolean isEmployeeIdEmpty(String loginID) {
        String query = "SELECT " + ID + " FROM " + EMPLOYEES_TABLE + " WHERE " + EMPLOYEE_LOGINID + " = '" + loginID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public boolean updateEmployee(Employee.EmployeeData employeeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EMPLOYEE_NAME, employeeData.username);
        contentValues.put(EMPLOYEE_MOBILE, employeeData.mobile_number);
        contentValues.put(EMPLOYEE_LOGINID, employeeData.login_id);
        contentValues.put(EMPLOYEE_PASSWORD, employeeData.password);
        contentValues.put(STATUS, employeeData.status);
        contentValues.put(ADD_UPDATE, employeeData.add_update);
        contentValues.put(ADMIN_ID, employeeData.admin_id);
        contentValues.put(CREATE_DATE, employeeData.create_date);
        contentValues.put(UPDATE_DATE, employeeData.update_date);
        contentValues.put(BRANCH, employeeData.branch);
        contentValues.put(LOCATION, employeeData.location);
        contentValues.put(IMEI, employeeData.imei_number);
        contentValues.put(TYPE, employeeData.type);
        if (!isEmployeeIdEmpty(employeeData.login_id)) {
            return db.update(EMPLOYEES_TABLE, contentValues, ID + "='" + employeeData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, employeeData.id);
            return db.update(EMPLOYEES_TABLE, contentValues, EMPLOYEE_LOGINID + "='" + employeeData.login_id + "'", null) > 0;
        }
    }

    public boolean deleteEmployee(String loginID) {
        if (loginID.contains("'")) {
            loginID = loginID.replace("'", "''");
        }
        return db.delete(EMPLOYEES_TABLE, EMPLOYEE_LOGINID + "='" + loginID + "'", null) > 0;
    }

    public void deleteAllEmployees() {
        db.delete(EMPLOYEES_TABLE, null, null);
    }

    public boolean updateEmployee(String columnName, String status, String empId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(EMPLOYEES_TABLE, contentValues, ID + "='" + empId + "'", null) > 0;
    }

    public Employee.EmployeeData getParticularEmployee(String empLoginId) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + EMPLOYEES_TABLE
                    + " WHERE LOWER(" + EMPLOYEE_LOGINID
                    + ") = LOWER('" + empLoginId + "')";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                Employee.EmployeeData employeeData = new Employee().new EmployeeData();
                do {
                    employeeData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_NAME)));
                    employeeData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    employeeData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_MOBILE)));
                    employeeData.login_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_LOGINID)));
                    employeeData.password = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_PASSWORD)));
                    employeeData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    employeeData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    employeeData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    employeeData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE)));
                    employeeData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE)));
                    employeeData.branch = (cursor.getString(cursor
                            .getColumnIndexOrThrow(BRANCH)));
                    employeeData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(LOCATION)));
                    employeeData.imei_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(IMEI)));
                    employeeData.type = (cursor.getString(cursor
                            .getColumnIndexOrThrow(TYPE)));
                } while (cursor.moveToNext());

                return employeeData;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<Employee.EmployeeData> getEmployees() {
        Cursor cursor = null;
        try {
            cursor = db.query(EMPLOYEES_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Employee.EmployeeData> employeesData = new ArrayList<>();
                do {

                    Employee.EmployeeData employeeData = new Employee().new EmployeeData();
                    employeeData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_NAME)));
                    employeeData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    employeeData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_MOBILE)));
                    employeeData.login_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_LOGINID)));
                    employeeData.password = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMPLOYEE_PASSWORD)));
                    employeeData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    employeeData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ID)));
                    employeeData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    employeeData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE)));
                    employeeData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE)));
                    employeeData.branch = (cursor.getString(cursor
                            .getColumnIndexOrThrow(BRANCH)));
                    employeeData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(LOCATION)));
                    employeeData.imei_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(IMEI)));
                    employeeData.type = (cursor.getString(cursor
                            .getColumnIndexOrThrow(TYPE)));
                    employeesData.add(employeeData);
                } while (cursor.moveToNext());

                return (ArrayList<Employee.EmployeeData>) employeesData;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * *************VENDOR METHODS********************
     *************************************************/

    public long addVendor(Vendor.VendorData vendorData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, vendorData.id);
        contentValues.put(VENDOR_NAME, vendorData.name);
        contentValues.put(VENDOR_SINCE, vendorData.since);
        contentValues.put(VENDOR_ADDRESS, vendorData.address);
        contentValues.put(PHONE, vendorData.phone);
        contentValues.put(EMAIL, vendorData.email);
        contentValues.put(ADMIN_ID, vendorData.admin_id);
        contentValues.put(STATUS, vendorData.status);
        contentValues.put(CREATE_DATE, vendorData.create_date);
        contentValues.put(UPDATE_DATE, vendorData.update_date);
        contentValues.put(ADD_UPDATE, vendorData.add_update);

        return db.insert(VENDORS_TABLE, null, contentValues);
    }

    public boolean deleteVendor(String phone) {
        return db.delete(VENDORS_TABLE, PHONE + "='" + phone + "'", null) > 0;
    }

    public boolean updateVendor(String columnName, String status, String vendorPhone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(VENDORS_TABLE, contentValues, PHONE + "='" + vendorPhone + "'", null) > 0;
    }

    public boolean updateVendor(Vendor.VendorData vendorData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, vendorData.id);
        contentValues.put(VENDOR_NAME, vendorData.name);
        contentValues.put(VENDOR_SINCE, vendorData.since);
        contentValues.put(VENDOR_ADDRESS, vendorData.address);
        contentValues.put(PHONE, vendorData.phone);
        contentValues.put(EMAIL, vendorData.email);
        contentValues.put(ADMIN_ID, vendorData.admin_id);
        contentValues.put(STATUS, vendorData.status);
        contentValues.put(ADD_UPDATE, vendorData.add_update);
        contentValues.put(CREATE_DATE, vendorData.create_date);
        contentValues.put(UPDATE_DATE, vendorData.update_date);
        contentValues.put(ADD_UPDATE, vendorData.add_update);

        if (!isVendorIdEmpty(vendorData.phone)) {
            return db.update(VENDORS_TABLE, contentValues, ID + "='" + vendorData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, vendorData.id);
            return db.update(VENDORS_TABLE, contentValues, PHONE + "='" + vendorData.phone + "'", null) > 0;
        }
    }

    public void deleteAllVendors() {
        db.delete(VENDORS_TABLE, null, null);
    }

    private boolean isVendorIdEmpty(String phone) {
        String query = "SELECT " + ID + " FROM " + VENDORS_TABLE + " WHERE " + PHONE + " = '" + phone + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public ArrayList<Vendor.VendorData> getVendors() {
        Cursor cursor = null;
        try {
            cursor = db.query(VENDORS_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Vendor.VendorData> vendors = new ArrayList<>();
                do {
                    Vendor.VendorData vendorData = new Vendor().new VendorData();
                    vendorData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ID)));
                    vendorData.name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_NAME)));
                    vendorData.email = (cursor.getString(cursor
                            .getColumnIndexOrThrow(EMAIL)));
                    vendorData.since = (cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_SINCE)));
                    vendorData.address = (cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_ADDRESS)));
                    vendorData.phone = (cursor.getString(cursor
                            .getColumnIndexOrThrow(PHONE)));
                    vendorData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    vendorData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    vendorData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    vendorData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE)));
                    vendorData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE)));
                    vendorData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    vendors.add(vendorData);
                } while (cursor.moveToNext());

                return (ArrayList<Vendor.VendorData>) vendors;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * *************CUSTOMER METHODS********************
     *************************************************/

    public long addCustomer(Customer.CustomerData customerData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, customerData.id);
        contentValues.put(CUSTOMER_NAME, customerData.username);
        contentValues.put(CUSTOMER_CONTACT, customerData.mobile_number);
        contentValues.put(DATE, customerData.date);
        contentValues.put(LOCATION, customerData.location);
        contentValues.put(STATUS, customerData.status);
        contentValues.put(ADMIN_ID, customerData.admin_id);
        contentValues.put(CREATE_DATE, customerData.create_date);
        contentValues.put(ADD_UPDATE, customerData.add_update);
        contentValues.put(UPDATE_DATE, customerData.update_date);
        return db.insert(CUSTOMERS_TABLE, null, contentValues);
    }

    private boolean isCustomerIdEmpty(String phone) {
        String query = "SELECT " + ID + " FROM " + CUSTOMERS_TABLE + " WHERE " + CUSTOMER_CONTACT + " = '" + phone + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public void deleteAllCustomers() {
        db.delete(CUSTOMERS_TABLE, null, null);
    }

    public boolean updateCustomer(Customer.CustomerData customerData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, customerData.username);
        contentValues.put(CUSTOMER_CONTACT, customerData.mobile_number);
        contentValues.put(DATE, customerData.date);
        contentValues.put(LOCATION, customerData.location);
        contentValues.put(STATUS, customerData.status);
        contentValues.put(ADMIN_ID, customerData.admin_id);
        contentValues.put(ADD_UPDATE, customerData.add_update);
        contentValues.put(CREATE_DATE, customerData.create_date);
        contentValues.put(UPDATE_DATE, customerData.update_date);
        if (!isCustomerIdEmpty(customerData.mobile_number)) {
            return db.update(CUSTOMERS_TABLE, contentValues, ID + "='" + customerData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, customerData.id);
            return db.update(CUSTOMERS_TABLE, contentValues, CUSTOMER_CONTACT + "='" + customerData.mobile_number + "'", null) > 0;
        }
    }

    public boolean deleteCustomer(String columnName, String string) {
        return db.delete(CUSTOMERS_TABLE, columnName + "='" + string + "'", null) > 0;
    }

    public boolean updateCustomer(String columnName, String status, String customerMobile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(CUSTOMERS_TABLE, contentValues, CUSTOMER_CONTACT + "='" + customerMobile + "'", null) > 0;
    }

    public ArrayList<Customer.CustomerData> getCustomers(String adminID) {
        Cursor cursor = null;
        try {

            if (TextUtils.isEmpty(adminID)) {
                cursor = db.query(CUSTOMERS_TABLE, null,
                        SNO + "<>?", new String[]{""},
                        null, null, null);
            } else {
                cursor = db.query(CUSTOMERS_TABLE, null,
                        ADMIN_ID + " = ?", new String[]{adminID},
                        null, null, null);
            }

            if (cursor.moveToFirst()) {
                List<Customer.CustomerData> customers = new ArrayList<>();
                do {
                    Customer.CustomerData customerData = new Customer().new CustomerData();
                    customerData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ID)));
                    customerData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME)));
                    customerData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_CONTACT)));
                    customerData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    customerData.date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE)));
                    customerData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(LOCATION)));
                    customerData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    customerData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    customerData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE)));
                    customerData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE)));
                    customers.add(customerData);
                } while (cursor.moveToNext());

                return (ArrayList<Customer.CustomerData>) customers;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ********* INVENTORY METHODS ********************
     *************************************************/

    public long addInventory(Inventory.InventoryData inventoryData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_CODE, inventoryData.item_code);
        contentValues.put(ITEM_NAME, inventoryData.item_name);
        contentValues.put(UNIT, inventoryData.unit);
        contentValues.put(QUANTITY, inventoryData.qty);
        contentValues.put(PRICE, inventoryData.price);
        contentValues.put(MY_COST, inventoryData.mycost);
        contentValues.put(ID, inventoryData.id);
        contentValues.put(DATE, inventoryData.date);
        contentValues.put(WAREHOUSE, inventoryData.warehouse);
        contentValues.put(VENDOR_NAME, inventoryData.vendor);
        contentValues.put(BARCODE, (!TextUtils.isEmpty(inventoryData.barcode) ? inventoryData.barcode : null));
        contentValues.put(PHOTO, inventoryData.photo);
        contentValues.put(STATUS, inventoryData.status);
        contentValues.put(ADMIN_ID, inventoryData.admin_id);
        contentValues.put(CREATE_DATE, inventoryData.create_date);
        contentValues.put(UPDATE_DATE, inventoryData.update_date);
        contentValues.put(ADD_UPDATE, inventoryData.add_update);
        return db.insert(INVENTORY_TABLE, null, contentValues);
    }

    public boolean deleteInventory(String itemCode) {
        return db.delete(INVENTORY_TABLE, ITEM_CODE + "='" + itemCode + "'", null) > 0;
    }

    public boolean updateInventory(String column, String status, String itemCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, status);
        return db.update(INVENTORY_TABLE, contentValues, ITEM_CODE + "='" + itemCode + "'", null) > 0;
    }

    public void deleteAllInventories() {
        db.delete(INVENTORY_TABLE, null, null);
    }

    public boolean updateInventory(Inventory.InventoryData inventoryData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_CODE, inventoryData.item_code);
        contentValues.put(ITEM_NAME, inventoryData.item_name);
        contentValues.put(UNIT, inventoryData.unit);
        contentValues.put(QUANTITY, inventoryData.qty);
        contentValues.put(PRICE, inventoryData.price);
        contentValues.put(MY_COST, inventoryData.mycost);
        contentValues.put(DATE, inventoryData.date);
        contentValues.put(WAREHOUSE, inventoryData.warehouse);
        contentValues.put(VENDOR_NAME, inventoryData.vendor);
        contentValues.put(BARCODE, (!TextUtils.isEmpty(inventoryData.barcode) ? inventoryData.barcode : null));
        contentValues.put(PHOTO, inventoryData.photo);
        contentValues.put(STATUS, inventoryData.status);
        contentValues.put(ADMIN_ID, inventoryData.admin_id);
        contentValues.put(CREATE_DATE, inventoryData.create_date);
        contentValues.put(UPDATE_DATE, inventoryData.update_date);
        contentValues.put(ADD_UPDATE, inventoryData.add_update);

        if (!isInventoryIdEmpty(inventoryData.item_code)) {
            return db.update(INVENTORY_TABLE, contentValues, ID + "='" + inventoryData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, inventoryData.id);
            return db.update(INVENTORY_TABLE, contentValues, ITEM_CODE + "='" + inventoryData.item_code + "'", null) > 0;
        }
    }

    private boolean isInventoryIdEmpty(String itemCode) {
        String query = "SELECT " + ID + " FROM " + INVENTORY_TABLE + " WHERE " + ITEM_CODE + " = '" + itemCode + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public boolean updateVendorName(String tabelName, String columnName, String vendorName, String previousVendorName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, vendorName);
        return db.update(tabelName, contentValues, columnName + "='" + previousVendorName + "'", null) > 0;
    }

    public boolean updateInventoryOffline(String columnName, String status, String vendorid) {
        /*
         * In Inventory Table vendor name is saved as vendor ID
         */
        String add_update = status;
        String query = "SELECT " + ADD_UPDATE + " FROM " + INVENTORY_TABLE + " WHERE " + VENDOR_NAME + " = '" + vendorid + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            add_update = cursor.getString(cursor.getColumnIndex(ADD_UPDATE));
            cursor.close();
            if (!add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                add_update = Constants.ADD_OFFLINE;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, add_update);
        return db.update(INVENTORY_TABLE, contentValues, VENDOR_NAME + "='" + vendorid + "'", null) > 0;
    }

    public Inventory.InventoryData getInventoryonByBarcode(String columnName, String barCode) {
        Cursor cursor = null;
        try {
            String query = "SELECT " + "*" + " FROM " + INVENTORY_TABLE + " WHERE " + columnName + " = '" + barCode + "'";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
                do {
                    inventoryData.item_code = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_CODE));
                    inventoryData.item_name = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_NAME));
                    inventoryData.unit = cursor.getString(cursor
                            .getColumnIndexOrThrow(UNIT));
                    inventoryData.qty = cursor.getString(cursor
                            .getColumnIndexOrThrow(QUANTITY));
                    inventoryData.price = cursor.getString(cursor
                            .getColumnIndexOrThrow(PRICE));
                    inventoryData.mycost = cursor.getString(cursor
                            .getColumnIndexOrThrow(MY_COST));
                    inventoryData.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    inventoryData.warehouse = cursor.getString(cursor
                            .getColumnIndexOrThrow(WAREHOUSE));
                    inventoryData.vendor = cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_NAME));
                    inventoryData.barcode = cursor.getString(cursor
                            .getColumnIndexOrThrow(BARCODE));
                    inventoryData.photo = cursor.getString(cursor
                            .getColumnIndexOrThrow(PHOTO));
                    inventoryData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    inventoryData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    inventoryData.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    inventoryData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    inventoryData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    inventoryData.add_update = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE));
                } while (cursor.moveToNext());

                return (Inventory.InventoryData) inventoryData;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<Inventory.InventoryData> getInventory() {
        Cursor cursor = null;
        try {
            cursor = db.query(INVENTORY_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Inventory.InventoryData> inventories = new ArrayList<>();
                do {
                    Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
                    inventoryData.item_code = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_CODE));
                    inventoryData.item_name = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_NAME));
                    inventoryData.unit = cursor.getString(cursor
                            .getColumnIndexOrThrow(UNIT));
                    inventoryData.qty = cursor.getString(cursor
                            .getColumnIndexOrThrow(QUANTITY));
                    inventoryData.price = cursor.getString(cursor
                            .getColumnIndexOrThrow(PRICE));
                    inventoryData.mycost = cursor.getString(cursor
                            .getColumnIndexOrThrow(MY_COST));
                    inventoryData.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    inventoryData.warehouse = cursor.getString(cursor
                            .getColumnIndexOrThrow(WAREHOUSE));
                    inventoryData.vendor = cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_NAME));
                    inventoryData.barcode = cursor.getString(cursor
                            .getColumnIndexOrThrow(BARCODE));
                    inventoryData.photo = cursor.getString(cursor
                            .getColumnIndexOrThrow(PHOTO));
                    inventoryData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    inventoryData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    inventoryData.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    inventoryData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    inventoryData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    inventoryData.add_update = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE));
                    inventories.add(inventoryData);
                } while (cursor.moveToNext());

                return (ArrayList<Inventory.InventoryData>) inventories;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public String getVendorId(String vendor) {
        String query = "SELECT " + ID + " FROM " + VENDORS_TABLE + " WHERE " + VENDOR_NAME + " = '" + vendor + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return id;
        }
        return "";
    }

    public String getVendorName(String _id) {
        if (TextUtils.isEmpty(_id)) {
            return "";
        }

        if (!TextUtils.isDigitsOnly(_id)) {
            return _id;
        }

        String query = "SELECT " + VENDOR_NAME + " FROM " + VENDORS_TABLE + " WHERE " + ID + " = '" + _id + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(VENDOR_NAME));
            cursor.close();
            return name;
        }
        return "";
    }

    /************************************************
     * ********* TAX METHODS ********************
     *************************************************/

    public long addTax(Tax.TaxData taxData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAX_TYPE, taxData.tax_type);
        contentValues.put(TAX_DESC, taxData.tax_description);
        contentValues.put(TAX_RATE, taxData.tax_rate);
        contentValues.put(ID, taxData.id);
        contentValues.put(ADMIN_ID, taxData.admin_id);
        contentValues.put(STATUS, taxData.status);
        contentValues.put(CREATE_DATE, taxData.create_date);
        contentValues.put(UPDATE_DATE, taxData.update_date);
        contentValues.put(ADD_UPDATE, taxData.add_update);
        return db.insert(TAX_TABLE, null, contentValues);
    }

    public void deleteAllTaxes() {
        db.delete(TAX_TABLE, null, null);
    }

    public boolean deleteTax(String taxType) {
        return db.delete(TAX_TABLE, TAX_TYPE + "='" + taxType + "'", null) > 0;
    }

    public boolean updateTax(String status, String taxType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        return db.update(TAX_TABLE, contentValues, TAX_TYPE + "='" + taxType + "'", null) > 0;
    }

    public boolean updateTax(Tax.TaxData taxData) {
        Log.e(TAG, "updateTax: " + taxData.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAX_TYPE, taxData.tax_type);
        contentValues.put(TAX_DESC, taxData.tax_description);
        contentValues.put(TAX_RATE, taxData.tax_rate);
        contentValues.put(ADMIN_ID, taxData.admin_id);
        contentValues.put(STATUS, taxData.status);
        contentValues.put(CREATE_DATE, taxData.create_date);
        contentValues.put(UPDATE_DATE, taxData.update_date);
        contentValues.put(ADD_UPDATE, taxData.add_update);
        if (!isTaxIdEmpty(taxData.tax_type)) {
            return db.update(TAX_TABLE, contentValues, ID + "='" + taxData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, taxData.id);
            return db.update(TAX_TABLE, contentValues, TAX_TYPE + "='" + taxData.tax_type + "'", null) > 0;
        }
    }

    private boolean isTaxIdEmpty(String tax_type) {
        String query = "SELECT " + ID + " FROM " + TAX_TABLE + " WHERE " + TAX_TYPE + " = '" + tax_type + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public ArrayList<Tax.TaxData> getTax() {
        Cursor cursor = null;
        try {
            cursor = db.query(TAX_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Tax.TaxData> taxes = new ArrayList<>();
                do {
                    Tax.TaxData tax = new Tax().new TaxData();
                    tax.tax_type = cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_TYPE));
                    tax.tax_description = cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_DESC));
                    tax.tax_rate = cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_RATE));
                    tax.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    tax.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    tax.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    tax.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    tax.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    tax.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    taxes.add(tax);
                } while (cursor.moveToNext());

                return (ArrayList<Tax.TaxData>) taxes;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ********* DISCOUNT METHODS ********************
     *************************************************/

    public long addDiscount(Discount.DiscountData discountData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DISCOUNT_CODE, discountData.discount_code);
        contentValues.put(DISCOUNT_DESC, discountData.discount_description);
        contentValues.put(DISCOUNT_VALUE, discountData.discount);
        contentValues.put(ID, discountData.id);
        contentValues.put(ADMIN_ID, discountData.admin_id);
        contentValues.put(STATUS, discountData.status);
        contentValues.put(CREATE_DATE, discountData.create_date);
        contentValues.put(UPDATE_DATE, discountData.update_date);
        contentValues.put(ADD_UPDATE, discountData.add_update);
        return db.insert(DISCOUNT_TABLE, null, contentValues);
    }

    public boolean deleteDiscount(String discCode) {
        return db.delete(DISCOUNT_TABLE, DISCOUNT_CODE + "='" + discCode + "'", null) > 0;
    }

    public boolean updateDiscount(String columnName, String status, String discCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(DISCOUNT_TABLE, contentValues, DISCOUNT_CODE + "='" + discCode + "'", null) > 0;
    }

    public void deleteAllDiscounts() {
        db.delete(DISCOUNT_TABLE, null, null);
    }

    public boolean updateDiscount(Discount.DiscountData discountData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DISCOUNT_CODE, discountData.discount_code);
        contentValues.put(DISCOUNT_DESC, discountData.discount_description);
        contentValues.put(DISCOUNT_VALUE, discountData.discount);
        contentValues.put(ADMIN_ID, discountData.admin_id);
        contentValues.put(STATUS, discountData.status);
        contentValues.put(CREATE_DATE, discountData.create_date);
        contentValues.put(UPDATE_DATE, discountData.update_date);
        contentValues.put(ADD_UPDATE, discountData.add_update);

        if (!isDiscountIdEmpty(discountData.discount_code)) {
            return db.update(DISCOUNT_TABLE, contentValues, ID + "='" + discountData.discount_code + "'", null) > 0;
        } else {
            contentValues.put(ID, discountData.id);
            return db.update(DISCOUNT_TABLE, contentValues, DISCOUNT_CODE + "='" + discountData.discount_code + "'", null) > 0;
        }
    }

    private boolean isDiscountIdEmpty(String discount_code) {
        String query = "SELECT " + ID + " FROM " + DISCOUNT_TABLE + " WHERE " + DISCOUNT_CODE + " = '" + discount_code + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public ArrayList<Discount.DiscountData> getDiscount() {
        Cursor cursor = null;
        try {
            cursor = db.query(DISCOUNT_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Discount.DiscountData> discounts = new ArrayList<>();
                do {
                    Discount.DiscountData discount = new Discount().new DiscountData();
                    discount.discount_code = cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_CODE));
                    discount.discount_description = cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_DESC));
                    discount.discount = cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_VALUE));
                    discount.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    discount.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    discount.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    discount.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    discount.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    discount.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    discounts.add(discount);
                } while (cursor.moveToNext());

                return (ArrayList<Discount.DiscountData>) discounts;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ********* POS ITEMS METHODS ********************
     *************************************************/

    public long addPOSItem(String customerName, Inventory.InventoryData inventoryData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, customerName);
        contentValues.put(QUANTITY, inventoryData.qty);
        contentValues.put(SELECTED_QTY, inventoryData.selectedQTY);
        contentValues.put(QUANTITY, inventoryData.qty);
        contentValues.put(BARCODE, (!TextUtils.isEmpty(inventoryData.barcode) ? inventoryData.barcode : null));
        contentValues.put(CREATE_DATE, inventoryData.create_date);
        contentValues.put(UPDATE_DATE, inventoryData.update_date);
        contentValues.put(ADD_UPDATE, inventoryData.add_update);
        contentValues.put(ITEM_CODE, inventoryData.item_code);
        contentValues.put(ITEM_NAME, inventoryData.item_name);
        contentValues.put(UNIT, inventoryData.unit);
        contentValues.put(PRICE, inventoryData.price);
        contentValues.put(MY_COST, inventoryData.mycost);
        contentValues.put(DATE, inventoryData.date);
        contentValues.put(WAREHOUSE, inventoryData.warehouse);
        contentValues.put(VENDOR_NAME, inventoryData.vendor);
        contentValues.put(PHOTO, inventoryData.photo);
        contentValues.put(ID, inventoryData.id);
        contentValues.put(STATUS, inventoryData.status);
        contentValues.put(ADMIN_ID, inventoryData.admin_id);
        contentValues.put(DISCOUNT_VALUE, inventoryData.discountValue);
        contentValues.put(DISCOUNT_CODE, inventoryData.discountCode);
        contentValues.put(Z_BILL, inventoryData.isZbillChecked);
        return db.insert(POS_ITEMS_TABLE, null, contentValues);
    }

    public void deletePOSItem(String itemCode, String customerName) {
        String query = "DELETE FROM " + POS_ITEMS_TABLE + " WHERE " + ITEM_CODE + "='" + itemCode + "' AND " + CUSTOMER_NAME + "='" + customerName + "'";
        db.execSQL(query);
    }

    public void deleteAllPOSItems() {
        db.execSQL("DELETE FROM " + POS_ITEMS_TABLE);
    }

    public void deleteAllCustomerItems(String customerName) {
        String query = "DELETE FROM " + POS_ITEMS_TABLE + " WHERE " + CUSTOMER_NAME + "='" + customerName + "'";
        db.execSQL(query);
    }

    public void updatePOSItem(String quantity, String itemCode, String customerName) {
        String query = "UPDATE " + POS_ITEMS_TABLE + " SET " + SELECTED_QTY + "='" + quantity
                + "' WHERE " + CUSTOMER_NAME + "='" + customerName + "' AND " + ITEM_CODE + "='" + itemCode + "'";
        db.execSQL(query);
    }

    public void updatePOSZBill(boolean isZbill, String customerName) {
        String query = "UPDATE " + POS_ITEMS_TABLE + " SET " + Z_BILL + "='" + isZbill
                + "' WHERE " + CUSTOMER_NAME + "='" + customerName + "'";
        db.execSQL(query);
    }

    public void updatePOSDiscount(String code, String value, String customerName) {
        String query = "UPDATE " + POS_ITEMS_TABLE + " SET " + DISCOUNT_CODE + "='" + code
                + "'," + DISCOUNT_VALUE + "='" + value + "'" + " WHERE " + CUSTOMER_NAME + "='" + customerName + "'";
        db.execSQL(query);
    }

    public boolean updateCustomerName(String tabelName, String columnName, String customerName, String previousCustomerName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, customerName);
        return db.update(tabelName, contentValues, columnName + "='" + previousCustomerName + "'", null) > 0;
    }

    public ArrayList<Inventory.InventoryData> getPOSItem(String customerName) {
        if (customerName == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + POS_ITEMS_TABLE + " WHERE " + CUSTOMER_NAME + " = '" + customerName + "'";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                List<Inventory.InventoryData> inventories = new ArrayList<>();
                do {
                    Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
                    inventoryData.discountCode = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_CODE)));
                    inventoryData.isZbillChecked = Boolean.parseBoolean(cursor.getString(cursor
                            .getColumnIndexOrThrow(Z_BILL)));
                    inventoryData.discountValue = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_VALUE)));
                    inventoryData.item_code = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_CODE));
                    inventoryData.item_name = cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_NAME));
                    inventoryData.unit = cursor.getString(cursor
                            .getColumnIndexOrThrow(UNIT));
                    inventoryData.qty = cursor.getString(cursor
                            .getColumnIndexOrThrow(QUANTITY));
                    inventoryData.price = cursor.getString(cursor
                            .getColumnIndexOrThrow(PRICE));
                    inventoryData.mycost = cursor.getString(cursor
                            .getColumnIndexOrThrow(MY_COST));
                    inventoryData.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    inventoryData.warehouse = cursor.getString(cursor
                            .getColumnIndexOrThrow(WAREHOUSE));
                    inventoryData.vendor = cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR_NAME));
                    inventoryData.barcode = cursor.getString(cursor
                            .getColumnIndexOrThrow(BARCODE));
                    inventoryData.photo = cursor.getString(cursor
                            .getColumnIndexOrThrow(PHOTO));
                    inventoryData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    inventoryData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    inventoryData.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    inventoryData.selectedQTY = (cursor.getString(cursor
                            .getColumnIndexOrThrow(SELECTED_QTY)));
                    inventoryData.customerName = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME)));
                    inventoryData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    inventoryData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    inventoryData.add_update = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE));
                    inventories.add(inventoryData);
                } while (cursor.moveToNext());

                return (ArrayList<Inventory.InventoryData>) inventories;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ********* PURCHASES METHODS ********************
     *************************************************/
    public long addPayment(Payments.PaymentData paymentData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, paymentData.id);
        contentValues.put(PAYEE_NAME, paymentData.payee_name);
        contentValues.put(DATE, paymentData.date_of_payment);
        contentValues.put(AMOUNT, paymentData.amount);
        contentValues.put(ADMIN_ID, paymentData.admin_id);
        contentValues.put(CREATE_DATE, paymentData.create_date);
        contentValues.put(UPDATE_DATE, paymentData.update_date);
        contentValues.put(STATUS, paymentData.status);
        contentValues.put(PURPOSE, paymentData.purpose_of_payment);
        contentValues.put(MODE, paymentData.mode_of_payment);
        contentValues.put(PAYMENT_TYPE, paymentData.payment_type);
        contentValues.put(ADD_UPDATE, paymentData.add_update);
        return db.insert(PAYMENTS_TABLE, null, contentValues);
    }

    public boolean deletePayment(String columnName, String compareColumn) {
        return db.delete(PAYMENTS_TABLE, columnName + "='" + compareColumn + "'", null) > 0;
    }

    public void deleteAllPayments() {
        db.delete(PAYMENTS_TABLE, null, null);
    }

    public boolean updatePaymentStatus(String columnName, String status, String _id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(PAYMENTS_TABLE, contentValues, ID + "='" + _id + "'", null) > 0;
    }

    public boolean updatePaymentOffline(String columnName, String status, String payeeName) {
        String add_update = status;
        String query = "SELECT " + ADD_UPDATE + " FROM " + PAYMENTS_TABLE + " WHERE " + PAYEE_NAME + " = '" + payeeName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            add_update = cursor.getString(cursor.getColumnIndex(ADD_UPDATE));
            cursor.close();
            if (!add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                add_update = Constants.ADD_OFFLINE;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, add_update);
        return db.update(PAYMENTS_TABLE, contentValues, PAYEE_NAME + "='" + payeeName + "'", null) > 0;
    }

    public boolean updatePayment(Payments.PaymentData paymentData, String previousID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAYEE_NAME, paymentData.payee_name);
        contentValues.put(DATE, paymentData.date_of_payment);
        contentValues.put(AMOUNT, paymentData.amount);
        contentValues.put(ADMIN_ID, paymentData.admin_id);
        contentValues.put(CREATE_DATE, paymentData.create_date);
        contentValues.put(UPDATE_DATE, paymentData.update_date);
        contentValues.put(STATUS, paymentData.status);
        contentValues.put(PURPOSE, paymentData.purpose_of_payment);
        contentValues.put(MODE, paymentData.mode_of_payment);
        contentValues.put(PAYMENT_TYPE, paymentData.payment_type);
        contentValues.put(ADD_UPDATE, paymentData.add_update);

        if (!TextUtils.isEmpty(previousID) && previousID.contains("PM_")) {
            contentValues.put(ID, paymentData.id);
            return db.update(PAYMENTS_TABLE, contentValues, ID + "='" + previousID + "'", null) > 0;
        } else {
            return db.update(PAYMENTS_TABLE, contentValues, ID + "='" + paymentData.id + "'", null) > 0;
        }
    }

    public int getPaymentsCount() {
        String countQuery = "SELECT * FROM " + PAYMENTS_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public ArrayList<Payments.PaymentData> getCustomerPayments(String paymentType, String customerName) {
        Cursor cursor = null;
        try {
            String query;

            if (TextUtils.isEmpty(paymentType)) {
                query = "SELECT * FROM " + PAYMENTS_TABLE;
            } else {
                query = "SELECT * FROM " + PAYMENTS_TABLE + " WHERE " + PAYMENT_TYPE + " = '" + paymentType + "' AND " + PAYEE_NAME + " = '" + customerName + "'";
            }

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                List<Payments.PaymentData> payments = new ArrayList<>();
                do {
                    Payments.PaymentData paymentData = new Payments().new PaymentData();
                    paymentData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    paymentData.payee_name = cursor.getString(cursor
                            .getColumnIndexOrThrow(PAYEE_NAME));
                    paymentData.date_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    paymentData.amount = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT));
                    paymentData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    paymentData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    paymentData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    paymentData.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    paymentData.purpose_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(PURPOSE));
                    paymentData.mode_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(MODE));
                    paymentData.payment_type = cursor.getString(cursor
                            .getColumnIndexOrThrow(PAYMENT_TYPE));
                    paymentData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    payments.add(paymentData);
                } while (cursor.moveToNext());

                return (ArrayList<Payments.PaymentData>) payments;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<Payments.PaymentData> getPayments(String paymentType) {
        Cursor cursor = null;
        try {
            String query;

            if (TextUtils.isEmpty(paymentType)) {
                query = "SELECT * FROM " + PAYMENTS_TABLE;
            } else {
                query = "SELECT * FROM " + PAYMENTS_TABLE + " WHERE " + PAYMENT_TYPE + " = '" + paymentType + "'";
            }

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                List<Payments.PaymentData> payments = new ArrayList<>();
                do {
                    Payments.PaymentData paymentData = new Payments().new PaymentData();
                    paymentData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    paymentData.payee_name = cursor.getString(cursor
                            .getColumnIndexOrThrow(PAYEE_NAME));
                    paymentData.date_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    paymentData.amount = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT));
                    paymentData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    paymentData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    paymentData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    paymentData.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS));
                    paymentData.purpose_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(PURPOSE));
                    paymentData.mode_of_payment = cursor.getString(cursor
                            .getColumnIndexOrThrow(MODE));
                    paymentData.payment_type = cursor.getString(cursor
                            .getColumnIndexOrThrow(PAYMENT_TYPE));
                    paymentData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    payments.add(paymentData);
                } while (cursor.moveToNext());

                return (ArrayList<Payments.PaymentData>) payments;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ************* CUSTOMER TRANSACTIONS METHODS*****
     *************************************************/

    public long addTransaction(Transaction transaction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, transaction.id);
        contentValues.put(BILL_NO, transaction.billNumber);
        contentValues.put(INVENTORY_JSON, transaction.inventoryJson);
        contentValues.put(TOTAL_AMOUNT, transaction.totalAmount);
        contentValues.put(AMOUNT_PAID, transaction.amountPaid);
        contentValues.put(CUSTOMER_NAME, transaction.customerName);
        contentValues.put(DATE, transaction.date);
        contentValues.put(STATUS, transaction.status);
        contentValues.put(DBConstants.AMOUNT_DUE, (!TextUtils.isEmpty(transaction.amountDue) ? transaction.amountDue : "0.0"));
        contentValues.put(ADMIN_ID, transaction.admin_id);
        contentValues.put(CREATE_DATE, transaction.create_date);
        contentValues.put(UPDATE_DATE, transaction.update_date);
        contentValues.put(ADD_UPDATE, transaction.add_update);
        contentValues.put(Z_BILL, transaction.isZbillChecked);
        contentValues.put(DISCOUNT_CODE, transaction.discountCodeApplied);
        contentValues.put(DISCOUNT_VALUE, transaction.discountPercentApplied);

        contentValues.put(SUB_TOTAL, transaction.subTotal);
        contentValues.put(DISC_ON_TRANS, transaction.totalDiscount);
        contentValues.put(TAX_ON_TRANSACTION, transaction.taxCalculated);

        return db.insert(CUSTOMER_TRANSACTIONS_TABLE, null, contentValues);
    }

    public int getTransactionsCount(String todaysDate) {
        String countQuery = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DATE + " = '" + todaysDate + "'";
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public float getCustomerTotalDue(String customerName) {
        Cursor cursor = null;
        try {
            String dueQuery = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + CUSTOMER_NAME + " = '" + customerName + "'";
            cursor = db.rawQuery(dueQuery, null);
            if (cursor.moveToFirst()) {
                float customerDue = 0.0f;
                do {
                    String due = cursor.getString(cursor.getColumnIndexOrThrow(AMOUNT_DUE));
                    customerDue = customerDue + Float.parseFloat(due);
                } while (cursor.moveToNext());

                return customerDue;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public ArrayList<String> getBillNumbers(String customerName) {
        Cursor cursor = null;
        try {
            String query;

            if (TextUtils.isEmpty(customerName)) {
                query = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE;
            } else {
                query = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE LOWER(" + CUSTOMER_NAME + ") = LOWER('" + customerName + "')";
            }

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                List<String> billNumbers = new ArrayList<>();
                do {
                    String billNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(BILL_NO));
                    billNumbers.add(billNumber);
                } while (cursor.moveToNext());

                return (ArrayList<String>) billNumbers;
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public Transaction getCustomerTransaction(String customerName, String billNumber) {
        Cursor cursor = null;
        try {
            String query;

            if (!TextUtils.isEmpty(customerName)) {
                query = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + CUSTOMER_NAME + " ='" + customerName + "' AND " + BILL_NO + "='" + billNumber + "'";
            } else {
                query = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + BILL_NO + "='" + billNumber + "'";
            }

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                Transaction transaction = new Transaction();
                do {
                    transaction.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    transaction.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    transaction.billNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(BILL_NO));
                    transaction.customerName = cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME));
                    transaction.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    transaction.inventoryJson = cursor.getString(cursor
                            .getColumnIndexOrThrow(INVENTORY_JSON));
                    transaction.totalAmount = cursor.getString(cursor
                            .getColumnIndexOrThrow(TOTAL_AMOUNT));
                    transaction.amountPaid = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_PAID));
                    transaction.amountDue = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_DUE));
                    transaction.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    transaction.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    transaction.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    transaction.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    transaction.discountPercentApplied = (cursor.getFloat(cursor
                            .getColumnIndexOrThrow(DISCOUNT_VALUE)));
                    transaction.discountCodeApplied = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_CODE)));
                    transaction.subTotal = (cursor.getString(cursor
                            .getColumnIndexOrThrow(SUB_TOTAL)));
                    transaction.totalDiscount = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISC_ON_TRANS)));
                    transaction.taxCalculated = (cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_ON_TRANSACTION)));

                } while (cursor.moveToNext());

                return transaction;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<Transaction> getCustomerTransactions(String customerName) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + CUSTOMER_NAME + "='" + customerName + "'";

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ArrayList<Transaction> transactions = new ArrayList<>();
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    transaction.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    transaction.billNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(BILL_NO));
                    transaction.customerName = cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME));
                    transaction.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    transaction.inventoryJson = cursor.getString(cursor
                            .getColumnIndexOrThrow(INVENTORY_JSON));
                    transaction.totalAmount = cursor.getString(cursor
                            .getColumnIndexOrThrow(TOTAL_AMOUNT));
                    transaction.amountPaid = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_PAID));
                    transaction.amountDue = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_DUE));
                    transaction.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    transaction.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    transaction.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    transaction.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    transaction.discountPercentApplied = (cursor.getFloat(cursor
                            .getColumnIndexOrThrow(DISCOUNT_VALUE)));
                    transaction.discountCodeApplied = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_CODE)));

                    transaction.subTotal = (cursor.getString(cursor
                            .getColumnIndexOrThrow(SUB_TOTAL)));
                    transaction.totalDiscount = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISC_ON_TRANS)));
                    transaction.taxCalculated = (cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_ON_TRANSACTION)));
                    transactions.add(transaction);
                } while (cursor.moveToNext());

                return transactions;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<Transaction> getTransactions(String query) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ArrayList<Transaction> transactions = new ArrayList<>();
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    transaction.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    transaction.billNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(BILL_NO));
                    transaction.customerName = cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME));
                    transaction.date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE));
                    transaction.inventoryJson = cursor.getString(cursor
                            .getColumnIndexOrThrow(INVENTORY_JSON));
                    transaction.totalAmount = cursor.getString(cursor
                            .getColumnIndexOrThrow(TOTAL_AMOUNT));
                    transaction.amountPaid = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_PAID));
                    transaction.amountDue = cursor.getString(cursor
                            .getColumnIndexOrThrow(AMOUNT_DUE));
                    transaction.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    transaction.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    transaction.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    transaction.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    transaction.discountPercentApplied = (cursor.getFloat(cursor
                            .getColumnIndexOrThrow(DISCOUNT_VALUE)));
                    transaction.discountCodeApplied = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISCOUNT_CODE)));

                    transaction.subTotal = (cursor.getString(cursor
                            .getColumnIndexOrThrow(SUB_TOTAL)));
                    transaction.totalDiscount = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DISC_ON_TRANS)));
                    transaction.taxCalculated = (cursor.getString(cursor
                            .getColumnIndexOrThrow(TAX_ON_TRANSACTION)));

                    transactions.add(transaction);
                } while (cursor.moveToNext());

                return transactions;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /************************************************
     * ************* TRANSPORT TRANSACTIONS METHODS*****
     *************************************************/

    public long addTransport(Transport.TransportData transportData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, transportData.id);
        contentValues.put(TRANSPORT_NAME, transportData.transportName);
        contentValues.put(PHONE, transportData.phone);
        contentValues.put(LOCATION, transportData.location);
        contentValues.put(STATUS, transportData.status);
        contentValues.put(CREATE_DATE, transportData.create_date);
        contentValues.put(UPDATE_DATE, transportData.update_date);
        contentValues.put(ADD_UPDATE, transportData.add_update);
        contentValues.put(ADMIN_ID, transportData.admin_id);

        return db.insert(TRANSPORT_TABLE, null, contentValues);
    }

    public boolean deleteTransport(String columnName, String string) {
        return db.delete(TRANSPORT_TABLE, columnName + "='" + string + "'", null) > 0;
    }

    public boolean updateTransport(String columnName, String status, String phone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, status);
        return db.update(TRANSPORT_TABLE, contentValues, PHONE + "='" + phone + "'", null) > 0;
    }

    private boolean isTransportIdEmpty(String phone) {
        String query = "SELECT " + ID + " FROM " + TRANSPORT_TABLE + " WHERE " + PHONE + " = '" + phone + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(ID));
            cursor.close();
            return TextUtils.isEmpty(id);
        }
        return true;
    }

    public void deleteAllTransports() {
        db.delete(TRANSPORT_TABLE, null, null);
    }

    public boolean updateTransport(Transport.TransportData transportData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSPORT_NAME, transportData.transportName);
        contentValues.put(PHONE, transportData.phone);
        contentValues.put(LOCATION, transportData.location);
        contentValues.put(STATUS, transportData.status);
        contentValues.put(ADMIN_ID, transportData.admin_id);
        contentValues.put(ADD_UPDATE, transportData.add_update);
        contentValues.put(CREATE_DATE, transportData.create_date);
        contentValues.put(UPDATE_DATE, transportData.update_date);
        if (!isCustomerIdEmpty(transportData.phone)) {
            return db.update(TRANSPORT_TABLE, contentValues, ID + "='" + transportData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, transportData.id);
            return db.update(TRANSPORT_TABLE, contentValues, PHONE + "='" + transportData.phone + "'", null) > 0;
        }
    }

    public ArrayList<Transport.TransportData> getTransports(String adminId) {
        Cursor cursor = null;
        try {

            if (TextUtils.isEmpty(adminId)) {
                cursor = db.query(TRANSPORT_TABLE, null,
                        SNO + "<>?", new String[]{""},
                        null, null, null);
            } else {
                cursor = db.query(TRANSPORT_TABLE, null,
                        ADMIN_ID + " = ?", new String[]{adminId},
                        null, null, null);
            }

            if (cursor.moveToFirst()) {
                ArrayList<Transport.TransportData> transportDatas = new ArrayList<>();
                do {
                    Transport.TransportData transportData = new Transport().new TransportData();
                    transportData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ID));
                    transportData.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID));
                    transportData.transportName = cursor.getString(cursor
                            .getColumnIndexOrThrow(TRANSPORT_NAME));
                    transportData.phone = cursor.getString(cursor
                            .getColumnIndexOrThrow(PHONE));
                    transportData.location = cursor.getString(cursor
                            .getColumnIndexOrThrow(LOCATION));
                    transportData.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE));
                    transportData.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE));
                    transportData.add_update = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADD_UPDATE)));
                    transportData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    transportDatas.add(transportData);
                } while (cursor.moveToNext());

                return transportDatas;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public void beginTransaction() {
        db.beginTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        db.endTransaction();
    }

}
