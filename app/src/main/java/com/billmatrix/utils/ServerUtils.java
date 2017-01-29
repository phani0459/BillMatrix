package com.billmatrix.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.CreateJob;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KANDAGATLAs on 06-01-2017.
 */

public class ServerUtils {

    private static final String TAG = ServerUtils.class.getSimpleName();

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
        final Customer.CustomerData updatedCustomer = customerData;
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
                            Utils.showToast("Customer Added successfully", mContext);
                            updatedCustomer.id = customerStatus.data.id;
                            updatedCustomer.username = customerStatus.data.username;
                            updatedCustomer.mobile_number = customerStatus.data.mobile_number;
                            updatedCustomer.date = customerStatus.data.date;
                            updatedCustomer.location = customerStatus.data.location;
                            updatedCustomer.status = customerStatus.data.status;
                            updatedCustomer.admin_id = customerStatus.data.admin_id;
                            updatedCustomer.create_date = customerStatus.data.create_date;
                            updatedCustomer.update_date = customerStatus.data.update_date;
                            billMatrixDaoImpl.updateCustomer(updatedCustomer);
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

        return updatedCustomer;
    }

    public static Customer.CustomerData updateCustomertoServer(final Customer.CustomerData customerData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        Log.e(TAG, "updateCustomertoServer: ");
        final Customer.CustomerData updatedCustomer = customerData;
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
                Log.e(TAG, "onResponse: " + response.body());
                if (response.body() != null) {
                    CreateJob customerStatus = response.body();
                    if (customerStatus.status.equalsIgnoreCase("200")) {
                        if (!TextUtils.isEmpty(customerStatus.update_customer) && customerStatus.update_customer.equalsIgnoreCase("Successfully Updated")) {
                            Utils.showToast("Customer Updated successfully", mContext);
                        }
                        updatedCustomer.id = customerStatus.data.id;
                        updatedCustomer.username = customerStatus.data.username;
                        updatedCustomer.mobile_number = customerStatus.data.mobile_number;
                        updatedCustomer.date = customerStatus.data.date;
                        updatedCustomer.location = customerStatus.data.location;
                        updatedCustomer.status = customerStatus.data.status;
                        updatedCustomer.admin_id = customerStatus.data.admin_id;
                        updatedCustomer.create_date = customerStatus.data.create_date;
                        updatedCustomer.update_date = customerStatus.data.update_date;
                        billMatrixDaoImpl.updateCustomer(updatedCustomer);
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
                Log.e(TAG, "onFailure: " + call.request().url().queryParameterNames());
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
        return updatedCustomer;
    }

    /*********************************************************************
     * ************************** EMPLOYEES ******************************
     *********************************************************************/

    public static void updateEmployeetoServer(final Employee.EmployeeData employeeData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl) {
        final Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).updateEmployee(employeeData.id, employeeData.username,
                employeeData.password, employeeData.mobile_number, employeeData.login_id, employeeData.imei_number, employeeData.location, employeeData.branch,
                employeeData.status);

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
                        if (employeeStatus.containsKey("update_employee") && employeeStatus.get("update_employee").equalsIgnoreCase("Successfully Updated")) {
                            if (!isSync) Utils.showToast("Employee Updated successfully", mContext);
                            billMatrixDaoImpl.addUpdateEmployee(Constants.DATA_FROM_SERVER, employeeData.login_id);
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

    public static boolean isSync() {
        return isSync;
    }

    public static void setIsSync(boolean isSync) {
        ServerUtils.isSync = isSync;
    }

    private static boolean isSync;

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

    public static void addEmployeetoServer(final Employee.EmployeeData employeeData, final Context mContext, final BillMatrixDaoImpl billMatrixDaoImpl, String adminId) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).addEmployee(employeeData.username, employeeData.password, employeeData.mobile_number, adminId,
                employeeData.login_id, employeeData.imei_number, employeeData.location, employeeData.branch);

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
                        if (employeeStatus.get("create_employee").equalsIgnoreCase("success")) {
                            if (!isSync) Utils.showToast("Employee Added successfully", mContext);
                            billMatrixDaoImpl.addUpdateEmployee(Constants.DATA_FROM_SERVER, employeeData.login_id);
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

}
