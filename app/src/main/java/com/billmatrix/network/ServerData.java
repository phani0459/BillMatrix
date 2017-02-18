package com.billmatrix.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Tax;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KANDAGATLAs on 01-02-2017.
 */

public class ServerData {

    private static final String TAG = ServerData.class.getSimpleName();
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private boolean fromLogin;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String paymentType;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setOnDataFetchListener(OnDataFetchListener onDataFetchListener) {
        this.onDataFetchListener = onDataFetchListener;
    }

    private OnDataFetchListener onDataFetchListener;

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setBillMatrixDaoImpl(BillMatrixDaoImpl billMatrixDaoImpl) {
        this.billMatrixDaoImpl = billMatrixDaoImpl;
    }

    public boolean isFromLogin() {
        return fromLogin;
    }

    public void setFromLogin(boolean fromLogin) {
        this.fromLogin = fromLogin;
    }

    /**
     * Sequence to fetch data from server
     * Employees
     * Customers
     * Vendors
     * Inventory
     * Tax
     * Discounts
     * Payments
     */

    public void getEmployeesFromServer(final String adminId) {
        ArrayList<Employee.EmployeeData> employeesfromDB = billMatrixDaoImpl.getEmployees();
        if (employeesfromDB != null && employeesfromDB.size() > 0) {
            getCustomersFromServer(adminId);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    Log.e(TAG, "getEmployeesFromServer: ");
                    Call<Employee> call = Utils.getBillMatrixAPI(mContext).getAdminEmployees(adminId);

                    call.enqueue(new Callback<Employee>() {

                        /**
                         * Successful HTTP response.
                         * @param call server call
                         * @param response server response
                         */
                        @Override
                        public void onResponse(Call<Employee> call, Response<Employee> response) {
                            Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null) {
                                Employee employee = response.body();
                                if (employee.status == 200 && employee.employeedata.equalsIgnoreCase("success")) {
                                    for (Employee.EmployeeData employeeData : employee.data) {
                                        employeeData.add_update = Constants.DATA_FROM_SERVER;
                                        billMatrixDaoImpl.addEmployee(employeeData);
                                    }
                                }
                                /**
                                 * To remove pending sync Icon in database page
                                 */
                                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_EMPLOYEES_EDITED_OFFLINE, false).apply();
                            }
                            if (isFromLogin()) getCustomersFromServer(adminId);
                            if (onDataFetchListener != null) onDataFetchListener.onDataFetch(0);
                        }

                        /**
                         *  Invoked when a network or unexpected exception occurred during the HTTP request.
                         * @param call server call
                         * @param t error
                         */
                        @Override
                        public void onFailure(Call<Employee> call, Throwable t) {
                            Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void getCustomersFromServer(final String adminId) {
        ArrayList<Customer.CustomerData> customersfromDB = billMatrixDaoImpl.getCustomers();
        if (customersfromDB != null && customersfromDB.size() > 0) {
            getVendorsFromServer(adminId);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    Log.e(TAG, "getCustomersFromServer: ");
                    Call<Customer> call = Utils.getBillMatrixAPI(mContext).getAdminCustomers(adminId);

                    call.enqueue(new Callback<Customer>() {

                        /**
                         * Successful HTTP response.
                         * @param call server call
                         * @param response server response
                         */
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null) {
                                Customer customer = response.body();
                                if (customer.status == 200 && customer.Customerdata.equalsIgnoreCase("success")) {
                                    for (Customer.CustomerData customerData : customer.data) {
                                        customerData.add_update = Constants.DATA_FROM_SERVER;
                                        billMatrixDaoImpl.addCustomer(customerData);
                                    }
                                }
                                /**
                                 * To remove pending sync Icon in database page
                                 */
                                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, false).apply();
                            }

                            if (isFromLogin()) getVendorsFromServer(adminId);
                            if (onDataFetchListener != null) onDataFetchListener.onDataFetch(1);
                        }

                        /**
                         *  Invoked when a network or unexpected exception occurred during the HTTP request.
                         * @param call server call
                         * @param t error
                         */
                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void getVendorsFromServer(final String adminId) {
        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        if (vendors != null && vendors.size() > 0) {
            getInventoryFromServer(adminId);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    Log.e(TAG, "getVendorsFromServer: ");
                    Call<Vendor> call = Utils.getBillMatrixAPI(mContext).getAdminVendors(adminId);

                    call.enqueue(new Callback<Vendor>() {

                        /**
                         * Successful HTTP response.
                         * @param call server call
                         * @param response server response
                         */
                        @Override
                        public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                            Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null) {
                                Vendor vendor = response.body();
                                if (vendor.status == 200 && vendor.Vendordata.equalsIgnoreCase("success")) {
                                    for (Vendor.VendorData vendorData : vendor.data) {
                                        vendorData.add_update = Constants.DATA_FROM_SERVER;
                                        billMatrixDaoImpl.addVendor(vendorData);
                                    }
                                }
                                /**
                                 * To remove pending sync Icon in database page
                                 */
                                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_VENDORS_EDITED_OFFLINE, false).apply();
                            }

                            if (isFromLogin()) getInventoryFromServer(adminId);
                            if (onDataFetchListener != null) onDataFetchListener.onDataFetch(2);

                        }

                        /**
                         *  Invoked when a network or unexpected exception occurred during the HTTP request.
                         * @param call server call
                         * @param t error
                         */
                        @Override
                        public void onFailure(Call<Vendor> call, Throwable t) {
                            t.printStackTrace();
                            Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void getInventoryFromServer(final String adminId) {
        ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();
        if (inventories != null && inventories.size() > 0) {
            getTaxesFromServer(adminId);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    Log.e(TAG, "getInventoryFromServer: ");
                    Call<Inventory> call = Utils.getBillMatrixAPI(mContext).getAdminInventory(adminId);

                    call.enqueue(new Callback<Inventory>() {

                        /**
                         * Successful HTTP response.
                         * @param call server call
                         * @param response server response
                         */
                        @Override
                        public void onResponse(Call<Inventory> call, Response<Inventory> response) {
                            Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null) {
                                Inventory inventory = response.body();
                                if (inventory.status == 200 && inventory.InventoryData.equalsIgnoreCase("success")) {
                                    for (Inventory.InventoryData inventoryData : inventory.data) {
                                        inventoryData.add_update = Constants.DATA_FROM_SERVER;
                                        billMatrixDaoImpl.addInventory(inventoryData);
                                    }
                                }
                                /**
                                 * To remove pending sync Icon in database page
                                 */
                                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, false).apply();
                            }

                            if (isFromLogin()) getTaxesFromServer(adminId);
                            if (onDataFetchListener != null) onDataFetchListener.onDataFetch(3);
                        }

                        /**
                         *  Invoked when a network or unexpected exception occurred during the HTTP request.
                         * @param call server call
                         * @param t error
                         */
                        @Override
                        public void onFailure(Call<Inventory> call, Throwable t) {
                            Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void getTaxesFromServer(final String adminId) {
        ArrayList<Tax.TaxData> taxes = billMatrixDaoImpl.getTax();
        if (taxes != null && taxes.size() > 0) {
            getDiscountsFromServer(adminId);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                Log.e(TAG, "getTaxesFromServer: ");
                Call<Tax> call = Utils.getBillMatrixAPI(mContext).getAdminTaxes(adminId);

                call.enqueue(new Callback<Tax>() {

                    /**
                     * Successful HTTP response.
                     * @param call server call
                     * @param response server response
                     */
                    @Override
                    public void onResponse(Call<Tax> call, Response<Tax> response) {
                        Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null) {
                            Tax tax = response.body();
                            if (tax.status == 200 && tax.Taxdata.equalsIgnoreCase("success")) {
                                for (Tax.TaxData taxData : tax.data) {
                                    taxData.add_update = Constants.DATA_FROM_SERVER;
                                    billMatrixDaoImpl.addTax(taxData);
                                }
                            }
                            /**
                             * To remove pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TAXES_EDITED_OFFLINE, false).apply();
                        }

                        if (isFromLogin()) getDiscountsFromServer(adminId);
                        if (onDataFetchListener != null) onDataFetchListener.onDataFetch(4);

                    }

                    /**
                     *  Invoked when a network or unexpected exception occurred during the HTTP request.
                     * @param call server call
                     * @param t error
                     */
                    @Override
                    public void onFailure(Call<Tax> call, Throwable t) {
                        Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    public void getDiscountsFromServer(final String adminId) {
        ArrayList<Discount.DiscountData> discounts = billMatrixDaoImpl.getDiscount();
        if (discounts != null && discounts.size() > 0) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                Log.e(TAG, "getDiscountsFromServer: ");
                Call<Discount> call = Utils.getBillMatrixAPI(mContext).getAdminDiscounts(adminId);

                call.enqueue(new Callback<Discount>() {

                    /**
                     * Successful HTTP response.
                     * @param call server call
                     * @param response server response
                     */
                    @Override
                    public void onResponse(Call<Discount> call, Response<Discount> response) {
                        Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null) {
                            Discount discount = response.body();
                            if (discount.status == 200 && discount.Discountdata.equalsIgnoreCase("success")) {
                                for (Discount.DiscountData discountData : discount.data) {
                                    discountData.add_update = Constants.DATA_FROM_SERVER;
                                    billMatrixDaoImpl.addDiscount(discountData);
                                }
                            }
                            /**
                             * To remove pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_DISCS_EDITED_OFFLINE, false).apply();
                        }

                        if (isFromLogin()) getPaymentsFromServer(adminId);
                        if (onDataFetchListener != null) onDataFetchListener.onDataFetch(5);

                    }

                    /**
                     *  Invoked when a network or unexpected exception occurred during the HTTP request.
                     * @param call server call
                     * @param t error
                     */
                    @Override
                    public void onFailure(Call<Discount> call, Throwable t) {
                        Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    public void getPaymentsFromServer(final String adminId) {
        ArrayList<Payments.PaymentData> payments = billMatrixDaoImpl.getPayments(paymentType);
        if (payments != null && payments.size() > 0) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                Log.e(TAG, "getPaymentsFromServer: ");
                Call<Payments> call = Utils.getBillMatrixAPI(mContext).getAdminPayments(adminId, paymentType);

                call.enqueue(new Callback<Payments>() {

                    /**
                     * Successful HTTP response.
                     * @param call server call
                     * @param response server response
                     */
                    @Override
                    public void onResponse(Call<Payments> call, Response<Payments> response) {
                        Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null) {
                            Payments payments = response.body();
                            if (payments.status == 200 && payments.data != null && payments.data.size() > 0) {
                                for (Payments.PaymentData paymentData : payments.data) {
                                    paymentData.add_update = Constants.DATA_FROM_SERVER;
                                    billMatrixDaoImpl.addPayment(paymentData);
                                }
                            }
                            /**
                             * To remove pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, false).apply();
                        }

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (onDataFetchListener != null) onDataFetchListener.onDataFetch(6);

                    }

                    /**
                     *  Invoked when a network or unexpected exception occurred during the HTTP request.
                     * @param call server call
                     * @param t error
                     */
                    @Override
                    public void onFailure(Call<Payments> call, Throwable t) {
                        Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

}
