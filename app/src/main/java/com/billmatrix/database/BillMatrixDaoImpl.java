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
import com.billmatrix.models.Tax;
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

import static com.billmatrix.database.DBConstants.*;

/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class BillMatrixDaoImpl implements BillMatrixDao {

    private SQLiteDatabase db;

    public BillMatrixDaoImpl(Context context) {
        BillMatrixDBHandler dbHandler = new BillMatrixDBHandler(context);
        db = dbHandler.getWriteDB();
    }

    /*************************************************
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
        contentValues.put(ID, employeeData.id);
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
        return db.delete(EMPLOYEES_TABLE, EMPLOYEE_LOGINID + "='" + loginID + "'", null) > 0;
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
                    + " WHERE " + EMPLOYEE_LOGINID
                    + " = '" + empLoginId + "'";

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

    /*************************************************
     * *************VENDOR METHODS********************
     *************************************************/

    public long addVendor(Vendor.VendorData vendorData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VENDOR_ID, vendorData.id);
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
        contentValues.put(VENDOR_ID, vendorData.id);
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

        if (!isVendorIdEmpty(vendorData.phone)) {
            return db.update(VENDORS_TABLE, contentValues, ID + "='" + vendorData.id + "'", null) > 0;
        } else {
            contentValues.put(ID, vendorData.id);
            return db.update(VENDORS_TABLE, contentValues, PHONE + "='" + vendorData.phone + "'", null) > 0;
        }
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
                            .getColumnIndexOrThrow(VENDOR_ID)));
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

    /*************************************************
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

    public ArrayList<Customer.CustomerData> getCustomers() {
        Cursor cursor = null;
        try {
            cursor = db.query(CUSTOMERS_TABLE, null,
                    SNO + "<>?", new String[]{""},
                    null, null, null);

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

    /*************************************************
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
        contentValues.put(DATE, inventoryData.date);
        contentValues.put(WAREHOUSE, inventoryData.warehouse);
        contentValues.put(VENDOR, inventoryData.vendor);
        contentValues.put(BARCODE, inventoryData.barcode);
        contentValues.put(PHOTO, inventoryData.photo);
        contentValues.put(ID, inventoryData.id);
        contentValues.put(STATUS, inventoryData.status);
        contentValues.put(ADMIN_ID, inventoryData.admin_id);
        contentValues.put(CREATE_DATE, inventoryData.create_date);
        contentValues.put(UPDATE_DATE, inventoryData.update_date);
        return db.insert(INVENTORY_TABLE, null, contentValues);
    }

    public boolean deleteInventory(String itemCode) {
        return db.delete(INVENTORY_TABLE, ITEM_CODE + "='" + itemCode + "'", null) > 0;
    }

    public boolean updateInventory(String status, String itemCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        return db.update(INVENTORY_TABLE, contentValues, ITEM_CODE + "='" + itemCode + "'", null) > 0;
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
                    inventoryData.item_code = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_CODE)));
                    inventoryData.item_name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_NAME)));
                    inventoryData.unit = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UNIT)));
                    inventoryData.qty = (cursor.getString(cursor
                            .getColumnIndexOrThrow(QUANTITY)));
                    inventoryData.price = (cursor.getString(cursor
                            .getColumnIndexOrThrow(PRICE)));
                    inventoryData.mycost = (cursor.getString(cursor
                            .getColumnIndexOrThrow(MY_COST)));
                    inventoryData.date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE)));
                    inventoryData.warehouse = (cursor.getString(cursor
                            .getColumnIndexOrThrow(WAREHOUSE)));
                    inventoryData.vendor = (cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR)));
                    inventoryData.barcode = (cursor.getString(cursor
                            .getColumnIndexOrThrow(BARCODE)));
                    inventoryData.photo = (cursor.getString(cursor
                            .getColumnIndexOrThrow(PHOTO)));
                    inventoryData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ID)));
                    inventoryData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    inventoryData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    inventoryData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CREATE_DATE)));
                    inventoryData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UPDATE_DATE)));
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

    /*************************************************
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
        return db.insert(TAX_TABLE, null, contentValues);
    }

    public boolean deleteTax(String taxType) {
        return db.delete(TAX_TABLE, TAX_TYPE + "='" + taxType + "'", null) > 0;
    }

    public boolean updateTax(String status, String taxType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        return db.update(TAX_TABLE, contentValues, TAX_TYPE + "='" + taxType + "'", null) > 0;
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

    /*************************************************
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
        return db.insert(DISCOUNT_TABLE, null, contentValues);
    }

    public boolean deleteDiscount(String discCode) {
        return db.delete(DISCOUNT_TABLE, DISCOUNT_CODE + "='" + discCode + "'", null) > 0;
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

    /*************************************************
     * ********* POS ITEMS METHODS ********************
     *************************************************/

    public long addPOSItem(String customerName, Inventory.InventoryData inventoryData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, customerName);
        contentValues.put(QUANTITY, inventoryData.qty);
        contentValues.put(SELECTED_QTY, inventoryData.selectedQTY);
        contentValues.put(ITEM_CODE, inventoryData.item_code);
        contentValues.put(ITEM_NAME, inventoryData.item_name);
        contentValues.put(UNIT, inventoryData.unit);
        contentValues.put(PRICE, inventoryData.price);
        contentValues.put(MY_COST, inventoryData.mycost);
        contentValues.put(DATE, inventoryData.date);
        contentValues.put(WAREHOUSE, inventoryData.warehouse);
        contentValues.put(VENDOR, inventoryData.vendor);
        contentValues.put(BARCODE, inventoryData.barcode);
        contentValues.put(PHOTO, inventoryData.photo);
        contentValues.put(ID, inventoryData.id);
        contentValues.put(STATUS, inventoryData.status);
        contentValues.put(ADMIN_ID, inventoryData.admin_id);
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

    public ArrayList<Inventory.InventoryData> getPOSItem(String customerName) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + POS_ITEMS_TABLE + " WHERE " + CUSTOMER_NAME + " = '" + customerName + "'";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                List<Inventory.InventoryData> inventories = new ArrayList<>();
                do {
                    Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
                    inventoryData.item_code = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_CODE)));
                    inventoryData.item_name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ITEM_NAME)));
                    inventoryData.unit = (cursor.getString(cursor
                            .getColumnIndexOrThrow(UNIT)));
                    inventoryData.qty = (cursor.getString(cursor
                            .getColumnIndexOrThrow(QUANTITY)));
                    inventoryData.selectedQTY = (cursor.getString(cursor
                            .getColumnIndexOrThrow(SELECTED_QTY)));
                    inventoryData.price = (cursor.getString(cursor
                            .getColumnIndexOrThrow(PRICE)));
                    inventoryData.mycost = (cursor.getString(cursor
                            .getColumnIndexOrThrow(MY_COST)));
                    inventoryData.date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DATE)));
                    inventoryData.warehouse = (cursor.getString(cursor
                            .getColumnIndexOrThrow(WAREHOUSE)));
                    inventoryData.vendor = (cursor.getString(cursor
                            .getColumnIndexOrThrow(VENDOR)));
                    inventoryData.barcode = (cursor.getString(cursor
                            .getColumnIndexOrThrow(BARCODE)));
                    inventoryData.photo = (cursor.getString(cursor
                            .getColumnIndexOrThrow(PHOTO)));
                    inventoryData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ID)));
                    inventoryData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(ADMIN_ID)));
                    inventoryData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(STATUS)));
                    inventoryData.customerName = (cursor.getString(cursor
                            .getColumnIndexOrThrow(CUSTOMER_NAME)));
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
