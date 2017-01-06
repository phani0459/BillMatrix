package com.billmatrix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Tax;
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

import static com.billmatrix.database.DBConstants.TAG;

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
        contentValues.put(DBConstants.EMPLOYEE_LOGINID, employeeData.login_id);
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

    public boolean updateEmployee(String status, String empId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.STATUS, status);
        return db.update(DBConstants.EMPLOYEES_TABLE, contentValues, DBConstants.ID + "='" + empId + "'", null) > 0;
    }

    public Employee.EmployeeData getParticularEmployee(String empLoginId) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DBConstants.EMPLOYEES_TABLE
                    + " WHERE " + DBConstants.EMPLOYEE_LOGINID
                    + " = '" + empLoginId + "'";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                Employee.EmployeeData employeeData = new Employee().new EmployeeData();
                do {
                    employeeData.username = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_NAME)));
                    employeeData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_MOBILE)));
                    employeeData.login_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_LOGINID)));
                    employeeData.password = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_PASSWORD)));
                    employeeData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    employeeData.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID));
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
                    employeeData.login_id = (cursor.getString(cursor
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

    public boolean updateVendor(String status, String vendorPhone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.STATUS, status);
        return db.update(DBConstants.VENDORS_TABLE, contentValues, DBConstants.PHONE + "='" + vendorPhone + "'", null) > 0;
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
        contentValues.put(DBConstants.ID, customerData.id);
        contentValues.put(DBConstants.CUSTOMER_NAME, customerData.username);
        contentValues.put(DBConstants.CUSTOMER_CONTACT, customerData.mobile_number);
        contentValues.put(DBConstants.DATE, customerData.date);
        contentValues.put(DBConstants.LOCATION, customerData.location);
        contentValues.put(DBConstants.STATUS, customerData.status);
        contentValues.put(DBConstants.ADMIN_ID, customerData.admin_id);
        contentValues.put(DBConstants.CREATE_DATE, customerData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, customerData.update_date);
        return db.insert(DBConstants.CUSTOMERS_TABLE, null, contentValues);
    }

    public boolean updateCustomer(Customer.CustomerData customerData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.CUSTOMER_NAME, customerData.username);
        contentValues.put(DBConstants.CUSTOMER_CONTACT, customerData.mobile_number);
        contentValues.put(DBConstants.DATE, customerData.date);
        contentValues.put(DBConstants.LOCATION, customerData.location);
        contentValues.put(DBConstants.STATUS, customerData.status);
        contentValues.put(DBConstants.ADMIN_ID, customerData.admin_id);
        contentValues.put(DBConstants.CREATE_DATE, customerData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, customerData.update_date);
        return db.update(DBConstants.CUSTOMERS_TABLE, contentValues, DBConstants.ID + "='" + customerData.id + "'", null) > 0;
    }

    public boolean deleteCustomer(String phone) {
        return db.delete(DBConstants.CUSTOMERS_TABLE, DBConstants.CUSTOMER_CONTACT + "='" + phone + "'", null) > 0;
    }

    public boolean updateCustomer(String status, String customerMobile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.STATUS, status);
        return db.update(DBConstants.CUSTOMERS_TABLE, contentValues, DBConstants.CUSTOMER_CONTACT + "='" + customerMobile + "'", null) > 0;
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
                            .getColumnIndexOrThrow(DBConstants.DATE)));
                    customerData.location = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.LOCATION)));
                    customerData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    customerData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID)));
                    customerData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE)));
                    customerData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE)));
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
        contentValues.put(DBConstants.ITEM_CODE, inventoryData.item_code);
        contentValues.put(DBConstants.ITEM_NAME, inventoryData.item_name);
        contentValues.put(DBConstants.UNIT, inventoryData.unit);
        contentValues.put(DBConstants.QUANTITY, inventoryData.qty);
        contentValues.put(DBConstants.PRICE, inventoryData.price);
        contentValues.put(DBConstants.MY_COST, inventoryData.mycost);
        contentValues.put(DBConstants.DATE, inventoryData.date);
        contentValues.put(DBConstants.WAREHOUSE, inventoryData.warehouse);
        contentValues.put(DBConstants.VENDOR, inventoryData.vendor);
        contentValues.put(DBConstants.BARCODE, inventoryData.barcode);
        contentValues.put(DBConstants.PHOTO, inventoryData.photo);
        contentValues.put(DBConstants.ID, inventoryData.id);
        contentValues.put(DBConstants.STATUS, inventoryData.status);
        contentValues.put(DBConstants.ADMIN_ID, inventoryData.admin_id);
        contentValues.put(DBConstants.CREATE_DATE, inventoryData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, inventoryData.update_date);
        return db.insert(DBConstants.INVENTORY_TABLE, null, contentValues);
    }

    public boolean deleteInventory(String itemCode) {
        return db.delete(DBConstants.INVENTORY_TABLE, DBConstants.ITEM_CODE + "='" + itemCode + "'", null) > 0;
    }

    public boolean updateInventory(String status, String itemCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.STATUS, status);
        return db.update(DBConstants.INVENTORY_TABLE, contentValues, DBConstants.ITEM_CODE + "='" + itemCode + "'", null) > 0;
    }

    public ArrayList<Inventory.InventoryData> getInventory() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.INVENTORY_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Inventory.InventoryData> inventories = new ArrayList<>();
                do {
                    Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
                    inventoryData.item_code = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ITEM_CODE)));
                    inventoryData.item_name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ITEM_NAME)));
                    inventoryData.unit = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UNIT)));
                    inventoryData.qty = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.QUANTITY)));
                    inventoryData.price = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.PRICE)));
                    inventoryData.mycost = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.MY_COST)));
                    inventoryData.date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.DATE)));
                    inventoryData.warehouse = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.WAREHOUSE)));
                    inventoryData.vendor = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.VENDOR)));
                    inventoryData.barcode = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.BARCODE)));
                    inventoryData.photo = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.PHOTO)));
                    inventoryData.id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID)));
                    inventoryData.admin_id = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID)));
                    inventoryData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS)));
                    inventoryData.create_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE)));
                    inventoryData.update_date = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE)));
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
        contentValues.put(DBConstants.TAX_TYPE, taxData.taxType);
        contentValues.put(DBConstants.TAX_DESC, taxData.taxDescription);
        contentValues.put(DBConstants.TAX_RATE, taxData.taxRate);
        contentValues.put(DBConstants.ID, taxData.id);
        contentValues.put(DBConstants.ADMIN_ID, taxData.admin_id);
        contentValues.put(DBConstants.STATUS, taxData.status);
        contentValues.put(DBConstants.CREATE_DATE, taxData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, taxData.update_date);
        return db.insert(DBConstants.TAX_TABLE, null, contentValues);
    }

    public boolean deleteTax(String taxType) {
        return db.delete(DBConstants.TAX_TABLE, DBConstants.TAX_TYPE + "='" + taxType + "'", null) > 0;
    }

    public ArrayList<Tax.TaxData> getTax() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TAX_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Tax.TaxData> taxes = new ArrayList<>();
                do {
                    Tax.TaxData tax = new Tax().new TaxData();
                    tax.taxType = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.TAX_TYPE));
                    tax.taxDescription = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.TAX_DESC));
                    tax.taxRate = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.TAX_RATE));
                    tax.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID));
                    tax.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID));
                    tax.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS));
                    tax.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE));
                    tax.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE));
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
        contentValues.put(DBConstants.DISCOUNT_CODE, discountData.discount_code);
        contentValues.put(DBConstants.DISCOUNT_DESC, discountData.discountDescription);
        contentValues.put(DBConstants.DISCOUNT_VALUE, discountData.discount);
        contentValues.put(DBConstants.ID, discountData.id);
        contentValues.put(DBConstants.ADMIN_ID, discountData.admin_id);
        contentValues.put(DBConstants.STATUS, discountData.status);
        contentValues.put(DBConstants.CREATE_DATE, discountData.create_date);
        contentValues.put(DBConstants.UPDATE_DATE, discountData.update_date);
        return db.insert(DBConstants.DISCOUNT_TABLE, null, contentValues);
    }

    public boolean deleteDiscount(String discCode) {
        return db.delete(DBConstants.DISCOUNT_TABLE, DBConstants.DISCOUNT_CODE + "='" + discCode + "'", null) > 0;
    }

    public ArrayList<Discount.DiscountData> getDiscount() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.DISCOUNT_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Discount.DiscountData> discounts = new ArrayList<>();
                do {
                    Discount.DiscountData discount = new Discount().new DiscountData();
                    discount.discount_code = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.DISCOUNT_CODE));
                    discount.discountDescription = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.DISCOUNT_DESC));
                    discount.discount = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.DISCOUNT_VALUE));
                    discount.id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ID));
                    discount.admin_id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.ADMIN_ID));
                    discount.status = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.STATUS));
                    discount.update_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.UPDATE_DATE));
                    discount.create_date = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.CREATE_DATE));
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
