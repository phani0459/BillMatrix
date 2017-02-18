package com.billmatrix.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.models.CreateJob;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KANDAGATLAs on 06-01-2017.
 */

public class ServerUtils {

    private static final String TAG = ServerUtils.class.getSimpleName();
    public static final int STATUS_DELETING = -1;
    public static final int STATUS_ADDING = 0;
    public static final int STATUS_UPDATING = 1;

    private static boolean isSync;

    public static boolean isSync() {
        return isSync;
    }

    public static void setIsSync(boolean isSync) {
        ServerUtils.isSync = isSync;
    }

    /*********************************************************************
     ***************************  CUSTOMERS  *****************************
     *********************************************************************/
    /**
     * Add Customer to Server
     *
     * @param customerData customer data to be added to server
     * @param mContext     Context
     * @param adminId      admin ID
     */
    public static Customer.CustomerData addCustomertoServer(final Customer.CustomerData customerData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "addCustomertoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addCustomer(customerData.username, customerData.mobile_number, customerData.location,
                customerData.status, customerData.date, adminId);

        call.enqueue(new Callback<CreateJob>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob customerStatus = response.body();
                    if (customerStatus.status.equalsIgnoreCase("200")) {
                        if (customerStatus.create_customer.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Customer Added successfully", mContext);

                            customerData.id = customerStatus.data.id;
                            customerData.username = customerStatus.data.username;
                            customerData.mobile_number = customerStatus.data.mobile_number;
                            customerData.date = customerStatus.data.date;
                            customerData.location = customerStatus.data.location;
                            customerData.status = customerStatus.data.status;
                            customerData.admin_id = customerStatus.data.admin_id;
                            customerData.create_date = customerStatus.data.create_date;
                            customerData.update_date = customerStatus.data.update_date;
                            customerData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateCustomer(customerData);

                        } else {
                            if (!isSync)
                                Utils.showToast(customerStatus.create_customer + "", mContext);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return customerData;
    }

    public static Customer.CustomerData updateCustomertoServer(final Customer.CustomerData customerData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateCustomertoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updateCustomer(customerData.id, customerData.username, customerData.mobile_number,
                customerData.location, customerData.status, customerData.date);

        call.enqueue(new Callback<CreateJob>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob customerStatus = response.body();
                    if (customerStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(customerStatus.update_customer) && customerStatus.update_customer.equalsIgnoreCase("Successfully Updated")) {
                            if (!isSync) Utils.showToast("Customer Updated successfully", mContext);

                            customerData.id = customerStatus.data.id;
                            customerData.username = customerStatus.data.username;
                            customerData.mobile_number = customerStatus.data.mobile_number;
                            customerData.date = customerStatus.data.date;
                            customerData.location = customerStatus.data.location;
                            customerData.status = customerStatus.data.status;
                            customerData.admin_id = customerStatus.data.admin_id;
                            customerData.create_date = customerStatus.data.create_date;
                            customerData.update_date = customerStatus.data.update_date;
                            customerData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateCustomer(customerData);
                        }
                    }
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return customerData;
    }

    public static void deleteCustomerfromServer(final Customer.CustomerData customer, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteCustomer(customer.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> customerStatus = response.body();
                    if (customerStatus.get("status").equalsIgnoreCase("200")) {
                        if (customerStatus.get("delete_customer").equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Customer Deleted successfully", mContext);
                            billMatrixDaoImpl.deleteCustomer(DBConstants.ID, customer.id);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    /*********************************************************************
     * ************************** EMPLOYEES ******************************
     *********************************************************************/

    public static Employee.EmployeeData updateEmployeetoServer(final Employee.EmployeeData employeeData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        final Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updateEmployee(employeeData.id, employeeData.username,
                employeeData.password, employeeData.mobile_number, employeeData.login_id, employeeData.imei_number, employeeData.location, employeeData.branch,
                employeeData.status);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob employeeStatus = response.body();
                    if (employeeStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(employeeStatus.update_employee) && employeeStatus.update_employee.equalsIgnoreCase("Successfully Updated")) {
                            if (!isSync) Utils.showToast("Employee Updated successfully", mContext);
                            billMatrixDaoImpl.updateEmployee(DBConstants.ADD_UPDATE, Constants.DATA_FROM_SERVER, employeeData.login_id);
                        }

                        employeeData.update_date = employeeStatus.data.update_date;
                        employeeData.login_id = employeeStatus.data.login_id;
                        employeeData.username = employeeStatus.data.username;
                        employeeData.password = employeeStatus.data.password;
                        employeeData.mobile_number = employeeStatus.data.mobile_number;
                        employeeData.status = employeeStatus.data.status;
                        employeeData.imei_number = Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, "");
                        employeeData.type = employeeStatus.data.type;
                        employeeData.admin_id = employeeStatus.data.admin_id;
                        employeeData.id = employeeStatus.data.id;
                        employeeData.create_date = employeeStatus.data.create_date;
                        employeeData.branch = employeeStatus.data.branch;
                        employeeData.location = employeeStatus.data.location;
                        employeeData.add_update = Constants.DATA_FROM_SERVER;

                        billMatrixDaoImpl.updateEmployee(employeeData);

                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return employeeData;
    }

    public static void deleteEmployeefromServer(final Employee.EmployeeData employeeData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteEmployee(employeeData.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> employeeStatus = response.body();
                    if (employeeStatus.get("status").equalsIgnoreCase("200")) {
                        Log.e("employeeStatus.", "" + employeeStatus.get("status"));
                        if (employeeStatus.get("delete_employee").equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Employee Deleted successfully", mContext);
                            billMatrixDaoImpl.deleteEmployee(employeeData.login_id);

                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    public static Employee.EmployeeData addEmployeetoServer(final Employee.EmployeeData employeeData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl, String adminId) {
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addEmployee(employeeData.username, employeeData.password, employeeData.mobile_number, adminId,
                employeeData.login_id, employeeData.imei_number, employeeData.location, employeeData.branch);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob employeeStatus = response.body();
                    if (employeeStatus.status.equalsIgnoreCase("200")) {
                        if (employeeStatus.create_employee.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Employee Added successfully", mContext);

                            employeeData.update_date = employeeStatus.data.update_date;
                            employeeData.login_id = employeeStatus.data.login_id;
                            employeeData.username = employeeStatus.data.username;
                            employeeData.password = employeeStatus.data.password;
                            employeeData.mobile_number = employeeStatus.data.mobile_number;
                            employeeData.status = employeeStatus.data.status;
                            employeeData.imei_number = Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, "");
                            employeeData.type = employeeStatus.data.type;
                            employeeData.admin_id = employeeStatus.data.admin_id;
                            employeeData.id = employeeStatus.data.id;
                            employeeData.create_date = employeeStatus.data.create_date;
                            employeeData.branch = employeeStatus.data.branch;
                            employeeData.location = employeeStatus.data.location;
                            employeeData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateEmployee(employeeData);
                        } else {
                            if (!isSync)
                                Utils.showToast(employeeStatus.create_employee + "", mContext);
                        }

                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return employeeData;
    }

    public static Vendor.VendorData addVendortoServer(final Vendor.VendorData vendorData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "addVendortoServer: " + vendorData.toString());
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addVendor(vendorData.name, vendorData.email, vendorData.phone,
                vendorData.since, vendorData.address, vendorData.status, adminId);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob vendorStatus = response.body();
                    if (vendorStatus.status.equalsIgnoreCase("200")) {
                        if (vendorStatus.create_vendor.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Vendor Added successfully", mContext);

                            vendorData.id = vendorStatus.data.id;
                            vendorData.name = vendorStatus.data.name;
                            vendorData.since = vendorStatus.data.since;
                            vendorData.address = vendorStatus.data.address;
                            vendorData.phone = vendorStatus.data.phone;
                            vendorData.admin_id = vendorStatus.data.admin_id;
                            vendorData.status = vendorStatus.data.status;
                            vendorData.email = vendorStatus.data.email;
                            vendorData.create_date = vendorStatus.data.create_date;
                            vendorData.update_date = vendorStatus.data.update_date;
                            vendorData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateVendor(vendorData);
                        } else {
                            if (!isSync) Utils.showToast(vendorStatus.create_vendor + "", mContext);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return vendorData;
    }

    public static Vendor.VendorData updateVendortoServer(final Vendor.VendorData vendorData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateVendortoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updateVendor(vendorData.id, vendorData.email, vendorData.phone,
                vendorData.since, vendorData.status, vendorData.address, vendorData.name);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob vendorStatus = response.body();
                    if (vendorStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(vendorStatus.update_vendor) && vendorStatus.update_vendor.equalsIgnoreCase("Successfully Updated")) {
                            Utils.showToast("Vendor Updated successfully", mContext);
                        }

                        vendorData.id = vendorStatus.data.id;
                        vendorData.name = vendorStatus.data.name;
                        vendorData.since = vendorStatus.data.since;
                        vendorData.address = vendorStatus.data.address;
                        vendorData.phone = vendorStatus.data.phone;
                        vendorData.admin_id = vendorStatus.data.admin_id;
                        vendorData.status = vendorStatus.data.status;
                        vendorData.email = vendorStatus.data.email;
                        vendorData.create_date = vendorStatus.data.create_date;
                        vendorData.update_date = vendorStatus.data.update_date;
                        vendorData.add_update = Constants.DATA_FROM_SERVER;

                        billMatrixDaoImpl.updateVendor(vendorData);

                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return vendorData;
    }

    public static void deleteVendorfromServer(final Vendor.VendorData vendorData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteVendor(vendorData.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> vendorStatus = response.body();
                    if (vendorStatus.get("status").equalsIgnoreCase("200")) {
                        if (!isSync) Utils.showToast("Vendor Deleted successfully", mContext);
                        billMatrixDaoImpl.deleteVendor(vendorData.phone);
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    /****************************************************************
     * *****************INVENTORY METHODS ***************************
     ****************************************************************/

    public static Inventory.InventoryData updateInventorytoServer(final Inventory.InventoryData inventoryData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateCustomertoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updateInventory(inventoryData.id, inventoryData.item_code, inventoryData.item_name,
                inventoryData.unit, inventoryData.qty, inventoryData.price, inventoryData.mycost, inventoryData.date, inventoryData.warehouse,
                inventoryData.vendor, inventoryData.barcode, inventoryData.photo, inventoryData.status);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob inventoryStatus = response.body();
                    if (inventoryStatus.status.equalsIgnoreCase("200")) {
                        if (!isSync) Utils.showToast("Inventory Updated successfully", mContext);
                    }

                    inventoryData.admin_id = inventoryStatus.data.admin_id;
                    inventoryData.id = inventoryStatus.data.id;
                    inventoryData.item_code = inventoryStatus.data.item_code;
                    inventoryData.item_name = inventoryStatus.data.item_name;
                    inventoryData.unit = inventoryStatus.data.unit;
                    inventoryData.qty = inventoryStatus.data.qty;
                    inventoryData.price = inventoryStatus.data.price;
                    inventoryData.mycost = inventoryStatus.data.mycost;
                    inventoryData.date = inventoryStatus.data.date;
                    inventoryData.warehouse = inventoryStatus.data.warehouse;
                    inventoryData.vendor = inventoryStatus.data.vendor;
                    inventoryData.barcode = inventoryStatus.data.barcode;
                    inventoryData.photo = inventoryStatus.data.photo;
                    inventoryData.status = inventoryStatus.data.status;
                    inventoryData.create_date = inventoryStatus.data.create_date;
                    inventoryData.update_date = inventoryStatus.data.update_date;
                    inventoryData.add_update = Constants.DATA_FROM_SERVER;

                    billMatrixDaoImpl.updateInventory(inventoryData);
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return inventoryData;
    }

    public static void deleteInventoryfromServer(final Inventory.InventoryData inventoryData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteInventory(inventoryData.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> inventoryStatus = response.body();
                    if (inventoryStatus.get("status").equalsIgnoreCase("200")) {
                        if (inventoryStatus.containsKey("delete_inventory") && inventoryStatus.get("delete_inventory").equalsIgnoreCase("success")) {
                            if (!isSync)
                                Utils.showToast("Inventory Deleted successfully", mContext);
                            billMatrixDaoImpl.deleteInventory(inventoryData.item_code);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    public static Inventory.InventoryData addInventorytoServer(final Inventory.InventoryData inventoryData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "addInventorytoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addInventory(adminId, inventoryData.item_code, inventoryData.item_name,
                inventoryData.unit, inventoryData.qty, inventoryData.price, inventoryData.mycost, inventoryData.date, inventoryData.warehouse, inventoryData.vendor,
                inventoryData.barcode, inventoryData.photo, "1");

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob inventoryStatus = response.body();
                    if (inventoryStatus.status.equalsIgnoreCase("200")) {
                        if (!isSync) Utils.showToast("Inventory Added successfully", mContext);
                    }

                    inventoryData.admin_id = inventoryStatus.data.admin_id;
                    inventoryData.id = inventoryStatus.data.id;
                    inventoryData.item_code = inventoryStatus.data.item_code;
                    inventoryData.item_name = inventoryStatus.data.item_name;
                    inventoryData.unit = inventoryStatus.data.unit;
                    inventoryData.qty = inventoryStatus.data.qty;
                    inventoryData.price = inventoryStatus.data.price;
                    inventoryData.mycost = inventoryStatus.data.mycost;
                    inventoryData.date = inventoryStatus.data.date;
                    inventoryData.warehouse = inventoryStatus.data.warehouse;
                    inventoryData.vendor = inventoryStatus.data.vendor;
                    inventoryData.barcode = inventoryStatus.data.barcode;
                    inventoryData.photo = inventoryStatus.data.photo;
                    inventoryData.status = inventoryStatus.data.status;
                    inventoryData.create_date = inventoryStatus.data.create_date;
                    inventoryData.update_date = inventoryStatus.data.update_date;
                    inventoryData.add_update = Constants.DATA_FROM_SERVER;

                    billMatrixDaoImpl.updateInventory(inventoryData);
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return inventoryData;
    }

    /*********************************************************************
     * ************************** PAYMENTS ******************************
     *********************************************************************/
    public static void deletePaymentfromServer(final Payments.PaymentData paymentData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deletePayment(paymentData.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> paymentStatus = response.body();
                    if (paymentStatus.get("status").equalsIgnoreCase("200")) {
                        if (paymentStatus.containsKey("delete_payment") && paymentStatus.get("delete_payment").equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Customer Deleted successfully", mContext);
                            billMatrixDaoImpl.deletePayment(DBConstants.ID, paymentData.id);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    public static Payments.PaymentData addPaymenttoServer(final Payments.PaymentData paymentData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "addPaymenttoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addPayment(adminId, paymentData.payee_name, paymentData.mode_of_payment,
                paymentData.date_of_payment, paymentData.amount, paymentData.status, paymentData.purpose_of_payment, paymentData.payment_type);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob paymentStatus = response.body();
                    String previousID = paymentData.id;
                    if (paymentStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(paymentStatus.create_payment) && paymentStatus.create_payment.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Payment Added successfully", mContext);
                        }
                    }

                    paymentData.admin_id = paymentStatus.data.admin_id;
                    paymentData.id = paymentStatus.data.id;
                    paymentData.payee_name = paymentStatus.data.payee_name;
                    paymentData.mode_of_payment = !TextUtils.isEmpty(paymentStatus.data.mode_of_payment) ? paymentStatus.data.mode_of_payment : "";
                    paymentData.date_of_payment = paymentStatus.data.date_of_payment;
                    paymentData.amount = paymentStatus.data.amount;
                    paymentData.purpose_of_payment = !TextUtils.isEmpty(paymentStatus.data.purpose_of_payment) ? paymentStatus.data.purpose_of_payment : "";
                    paymentData.payment_type = paymentStatus.data.payment_type;
                    paymentData.status = paymentStatus.data.status;
                    paymentData.create_date = paymentStatus.data.create_date;
                    paymentData.update_date = paymentStatus.data.update_date;
                    paymentData.add_update = Constants.DATA_FROM_SERVER;

                    billMatrixDaoImpl.updatePayment(paymentData, previousID);
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return paymentData;
    }

    public static Payments.PaymentData updatePaymenttoServer(final Payments.PaymentData paymentData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateCustomertoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updatePayment(paymentData.id, paymentData.admin_id, paymentData.payee_name,
                paymentData.mode_of_payment, paymentData.date_of_payment, paymentData.amount, paymentData.status, paymentData.purpose_of_payment, paymentData.payment_type);

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob paymentStatus = response.body();
                    String previousID = paymentData.id;
                    if (paymentStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(paymentStatus.update_payment) && paymentStatus.update_payment.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Payment Updated successfully", mContext);
                        }
                    }

                    paymentData.admin_id = paymentStatus.data.admin_id;
                    paymentData.id = paymentStatus.data.id;
                    paymentData.payee_name = paymentStatus.data.payee_name;
                    paymentData.mode_of_payment = !TextUtils.isEmpty(paymentStatus.data.mode_of_payment) ? paymentStatus.data.mode_of_payment : "";
                    paymentData.date_of_payment = paymentStatus.data.date_of_payment;
                    paymentData.amount = paymentStatus.data.amount;
                    paymentData.purpose_of_payment = !TextUtils.isEmpty(paymentStatus.data.purpose_of_payment) ? paymentStatus.data.purpose_of_payment : "";
                    paymentData.payment_type = paymentStatus.data.payment_type;
                    paymentData.status = paymentStatus.data.status;
                    paymentData.create_date = paymentStatus.data.create_date;
                    paymentData.update_date = paymentStatus.data.update_date;
                    paymentData.add_update = Constants.DATA_FROM_SERVER;

                    billMatrixDaoImpl.updatePayment(paymentData, previousID);
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return paymentData;
    }

    /*******************************************
     * **********DISCOUNTS ********************
     ******************************************/

    public static Discount.DiscountData addDiscounttoServer(final Discount.DiscountData discountData, String adminId, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "addDiscounttoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).addDiscount(adminId, discountData.discount_code, discountData.discount_description,
                discountData.discount, "1");

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob discountMap = response.body();
                    if (discountMap.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(discountMap.create_discount) && discountMap.create_discount.equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Discount Added successfully", mContext);

                            discountData.create_date = discountMap.data.create_date;
                            discountData.id = discountMap.data.id;
                            discountData.update_date = discountMap.data.update_date;
                            discountData.discount = discountMap.data.discount;
                            discountData.discount_description = discountMap.data.discount_description;
                            discountData.discount_code = discountMap.data.discount_code;
                            discountData.admin_id = discountMap.data.admin_id;
                            discountData.status = discountMap.data.status;
                            discountData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateDiscount(discountData);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return discountData;
    }

    public static Discount.DiscountData updateDiscounttoServer(final Discount.DiscountData discountData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateDiscounttoServer: ");
        Call<CreateJob> call = Utils.getBillMatrixAPI(mContext).updateDiscount(discountData.id, adminId, discountData.discount_code,
                discountData.discount_description, discountData.discount, "1");

        call.enqueue(new Callback<CreateJob>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<CreateJob> call, Response<CreateJob> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    CreateJob discountMap = response.body();
                    if (discountMap.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(discountMap.update_discount) && discountMap.update_discount.equalsIgnoreCase("Successfully Updated")) {
                            if (!isSync) Utils.showToast("Discount Updated successfully", mContext);

                            discountData.create_date = discountMap.data.create_date;
                            discountData.id = discountMap.data.id;
                            discountData.update_date = discountMap.data.update_date;
                            discountData.discount = discountMap.data.discount;
                            discountData.discount_description = discountMap.data.discount_description;
                            discountData.discount_code = discountMap.data.discount_code;
                            discountData.admin_id = discountMap.data.admin_id;
                            discountData.status = discountMap.data.status;
                            discountData.add_update = Constants.DATA_FROM_SERVER;

                            billMatrixDaoImpl.updateDiscount(discountData);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<CreateJob> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });

        return discountData;
    }

    public static void deleteDiscountfromServer(final Discount.DiscountData discountData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteDiscount(discountData.id);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> taxStatus = response.body();
                    if (taxStatus.get("status").equalsIgnoreCase("200")) {
                        if (!isSync) Utils.showToast("Discount Deleted successfully", mContext);
                        billMatrixDaoImpl.deleteDiscount(discountData.discount_code);
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

}
