package com.billmatrix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class BillMatrixDaoImpl implements BillMatrixDao {

    private SQLiteDatabase db;
    private BillMatrixDBHandler dbHandler;

    public BillMatrixDaoImpl(Context context) {
        dbHandler = new BillMatrixDBHandler(context);
        db = dbHandler.getWriteDB();
    }

    /*************************************************
     * ********* EMPLOYEE METHODS ********************
     *************************************************/

    public long addEmployee(Employee.EmployeeData employeeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.EMPLOYEE_NAME, employeeData.username);
        contentValues.put(DBConstants.EMPLOYEE_MOBILE, employeeData.mobile_number);
        contentValues.put(DBConstants.EMPLOYEE_LOGINID, employeeData.email);
        contentValues.put(DBConstants.EMPLOYEE_PASSWORD, employeeData.password);
        contentValues.put(DBConstants.STATUS, employeeData.status);
        contentValues.put(DBConstants.ID, employeeData.id);
        contentValues.put(DBConstants.ADMIN_ID, employeeData.admin_id);
        contentValues.put(DBConstants.CREATE_DATE, employeeData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, employeeData.update_date);
        contentValues.put(DBConstants.BRANCH, employeeData.branch);
        contentValues.put(DBConstants.LOCATION, employeeData.location);
        contentValues.put(DBConstants.IMEI, employeeData.imei_number);
        contentValues.put(DBConstants.TYPE, employeeData.type);
        return db.insert(DBConstants.EMPLOYEES_TABLE, null, contentValues);
    }

    public boolean deleteEmployee(String loginID) {
        return db.delete(DBConstants.EMPLOYEES_TABLE, DBConstants.EMPLOYEE_LOGINID + "='" + loginID + "'", null) > 0;
    }

    public ArrayList<Employee.EmployeeData> getEmployees() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.EMPLOYEES_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Employee.EmployeeData> employeesData = new ArrayList<>();
                do {

                    Employee.EmployeeData employeeData = new Employee().new EmployeeData();
                    employeeData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_NAME)));
                    employeeData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_MOBILE)));
                    employeeData.email = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_LOGINID)));
                    employeeData.password = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_PASSWORD)));
                    employeeData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    employeeData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID)));
                    employeeData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID)));
                    employeeData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE)));
                    employeeData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE)));
                    employeeData.branch = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.BRANCH)));
                    employeeData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.LOCATION)));
                    employeeData.imei_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.IMEI)));
                    employeeData.type = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.TYPE)));
                    employeesData.add(employeeData);
                } while (cursor.moveToNext());

                return (ArrayList<Employee.EmployeeData>) employeesData;
            }
        } catch (IllegalArgumentException e) {
            Log.d(DBConstants.TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /*************************************************
     * *************VENDOR METHODS********************
     *************************************************/

    public long addVendor(Vendor.VendorData vendorData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.VENDOR_ID, vendorData.id);
        contentValues.put(DBConstants.VENDOR_NAME, vendorData.name);
        contentValues.put(DBConstants.VENDOR_SINCE, vendorData.since);
        contentValues.put(DBConstants.VENDOR_ADDRESS, vendorData.address);
        contentValues.put(DBConstants.PHONE, vendorData.phone);
        contentValues.put(DBConstants.EMAIL, vendorData.email);
        contentValues.put(DBConstants.ADMIN_ID, vendorData.admin_id);
        contentValues.put(DBConstants.STATUS, vendorData.status);
        contentValues.put(DBConstants.CREATE_DATE, vendorData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, vendorData.update_date);

        return db.insert(DBConstants.VENDORS_TABLE, null, contentValues);
    }

    public boolean deleteVendor(String phone) {
        return db.delete(DBConstants.VENDORS_TABLE, DBConstants.PHONE + "='" + phone + "'", null) > 0;
    }

    public ArrayList<Vendor.VendorData> getVendors() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.VENDORS_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Vendor.VendorData> vendors = new ArrayList<>();
                do {
                    Vendor.VendorData vendorData = new Vendor().new VendorData();
                    vendorData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.VENDOR_ID)));
                    vendorData.name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.VENDOR_NAME)));
                    vendorData.email = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMAIL)));
                    vendorData.since = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.VENDOR_SINCE)));
                    vendorData.address = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.VENDOR_ADDRESS)));
                    vendorData.phone = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.PHONE)));
                    vendorData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID)));
                    vendorData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    vendorData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE)));
                    vendorData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE)));
                    vendors.add(vendorData);
                } while (cursor.moveToNext());

                return (ArrayList<Vendor.VendorData>) vendors;
            }
        } catch (IllegalArgumentException e) {
            Log.d(DBConstants.TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /*************************************************
     * *************CUSTOMER METHODS********************
     *************************************************/

    public long addCustomer(Customer.CustomerData customerData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.ID, customerData.id);
        contentValues.put(DBConstants.CUSTOMER_NAME, customerData.username);
        contentValues.put(DBConstants.CUSTOMER_CONTACT, customerData.mobile_number);
        contentValues.put(DBConstants.CUSTOMER_DATE, customerData.date);
        contentValues.put(DBConstants.CUSTOMER_LOCATION, customerData.location);
        contentValues.put(DBConstants.STATUS, customerData.status);
        contentValues.put(DBConstants.ADMIN_ID, customerData.admin_id);
        contentValues.put(DBConstants.EMAIL, customerData.email);
        contentValues.put(DBConstants.CREATE_DATE, customerData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, customerData.update_date);
        return db.insert(DBConstants.CUSTOMERS_TABLE, null, contentValues);
    }

    public boolean deleteCustomer(String phone) {
        return db.delete(DBConstants.CUSTOMERS_TABLE, DBConstants.CUSTOMER_CONTACT + "='" + phone + "'", null) > 0;
    }

    public ArrayList<Customer.CustomerData> getCustomers() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.CUSTOMERS_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Customer.CustomerData> customers = new ArrayList<>();
                do {
                    Customer.CustomerData customerData = new Customer().new CustomerData();
                    customerData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID)));
                    customerData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CUSTOMER_NAME)));
                    customerData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CUSTOMER_CONTACT)));
                    customerData.date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CUSTOMER_DATE)));
                    customerData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CUSTOMER_LOCATION)));
                    customerData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    customerData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID)));
                    customerData.email = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_MOBILE)));
                    customerData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE)));
                    customerData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE)));
                    customers.add(customerData);

                } while (cursor.moveToNext());

                return (ArrayList<Customer.CustomerData>) customers;
            }
        } catch (IllegalArgumentException e) {
            Log.d(DBConstants.TAG, e.toString());
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
