package com.billmatrix.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.models.CreateJob;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
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

    public static boolean isSync() {
        return isSync;
    }

    public static void setIsSync(boolean isSync) {
        ServerUtils.isSync = isSync;
    }

    private static boolean isSync;

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

    public static void addVendortoServer(final Vendor.VendorData vendorData, final Context mContext, String adminId, final BillMatrixDaoImpl billMatrixDaoImpl) {
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
    }

    public static void updateVendortoServer(final Vendor.VendorData vendorData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
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

}
