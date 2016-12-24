package com.billmatrix.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.billmatrix.LogoutService;
import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Profile;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ControlPanelActivity extends AppCompatActivity {

    private static final String TAG = ControlPanelActivity.class.getSimpleName();
    @BindView(R.id.copyrightTextView)
    public TextView copyrightTextView;
    private Context mContext;

    @BindView(R.id.im_cp_profile)
    public ImageView profileImageView;
    @BindView(R.id.im_cp_settings)
    public ImageView settingsImageView;
    @BindView(R.id.im_cp_reports)
    public ImageView reportsImageView;
    @BindView(R.id.im_cp_customers)
    public ImageView customersImageView;
    @BindView(R.id.im_cp_employee)
    public ImageView employeeImageView;

    @BindView(R.id.tv_cp_profile)
    public TextView profileTextView;
    @BindView(R.id.tv_cp_settings)
    public TextView settingsTextView;
    @BindView(R.id.tv_cp_reports)
    public TextView reportsTextView;
    @BindView(R.id.tv_cp_customers)
    public TextView customersTextView;
    @BindView(R.id.tv_cp_employee)
    public TextView employeeTextView;

    @BindView(R.id.ll_customers)
    public LinearLayout customersLinearLayout;
    @BindView(R.id.ll_profile)
    public LinearLayout profileLinearLayout;
    @BindView(R.id.ll_settings)
    public LinearLayout settingsLinearLayout;
    @BindView(R.id.ll_reports)
    public LinearLayout reportsLinearLayout;
    @BindView(R.id.ll_employees)
    public LinearLayout employeesLinearLayout;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));

        String userType = Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_TYPE, null);
        String adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (!TextUtils.isEmpty(userType)) {
            if (!userType.equalsIgnoreCase("admin")) {
//                getEmployeeProfileFromServer();
                disableOptions();
            }
        } else {
            disableOptions();
        }

        Intent logoutService = new Intent(mContext, LogoutService.class);
        startService(logoutService);

        /**
         * Sequence to fetch data from server
         * Profile
         * Employees
         * Customers
         * Vendors
         * Inventory
         */
        getDataFromServer(adminId);
    }

    private void getEmployeeProfileFromServer() {
        final String empId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_EMP_LOGIN_ID, null);
        if (!FileUtils.isFileExists(Constants.EMPLOYEE_FILE_NAME, mContext)) {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(empId)) {
                    progressDialog = Utils.getProgressDialog(mContext, "Loading...");
                    progressDialog.show();
                    Log.e(TAG, "getEmployeeProfileFromServer: ");
                    Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile(empId);

                    call.enqueue(new Callback<Profile>() {


                        /**
                         * Successful HTTP response.
                         * @param call server call
                         * @param response server response
                         */
                        @Override
                        public void onResponse(Call<Profile> call, Response<Profile> response) {
                            Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null) {
                                Profile empProfile = response.body();
                                if (empProfile.status == 200 && empProfile.userdata.equalsIgnoreCase("success")) {
                                    FileUtils.writeToFile(mContext, Constants.EMPLOYEE_FILE_NAME, Constants.getGson().toJson(empProfile));
                                }
                            }
                        }

                        /**
                         *  Invoked when a network or unexpected exception occurred during the HTTP request.
                         * @param call server call
                         * @param t error
                         */
                        @Override
                        public void onFailure(Call<Profile> call, Throwable t) {
                            Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            } else {
                Utils.showToast("Unable to fetch Data! Check for Internet connection.", mContext);
            }
        }
    }

    public void getDataFromServer(String adminId) {
        if (!FileUtils.isFileExists(Constants.PROFILE_FILE_NAME, mContext)) {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    progressDialog = Utils.getProgressDialog(mContext, "Loading...");
                    progressDialog.show();
                    getProfilefromServer(adminId);
                }
            } else {
                Utils.showToast("Unable to fetch Data! Check for Internet connection.", mContext);
            }
        } else {
            getEmployeesFromServer(adminId);
        }
    }

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
                                        billMatrixDaoImpl.addEmployee(employeeData);
                                    }
                                }
                            }
                            getCustomersFromServer(adminId);
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

    private void getCustomersFromServer(final String adminId) {
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
                                        billMatrixDaoImpl.addCustomer(customerData);
                                    }
                                }
                            }

                            getVendorsFromServer(adminId);
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

    public void getProfilefromServer(final String adminId) {
        Log.e(TAG, "getProfilefromServer: ");
        Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile(adminId);

        call.enqueue(new Callback<Profile>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    Profile profile = response.body();
                    if (profile.status == 200 && profile.userdata.equalsIgnoreCase("success")) {
                        FileUtils.writeToFile(mContext, Constants.PROFILE_FILE_NAME, Constants.getGson().toJson(profile));
                        getEmployeesFromServer(adminId);
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getVendorsFromServer(final String adminId) {
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
                                        billMatrixDaoImpl.addVendor(vendorData);
                                    }
                                }
                            }

                            getInventoryFromServer(adminId);

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

    private void getInventoryFromServer(String adminId) {
        Log.e(TAG, "getInventoryFromServer: ");
        ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();
        if (inventories != null && inventories.size() > 0) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
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
                                        billMatrixDaoImpl.addInventory(inventoryData);
                                    }
                                }
                            }

                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

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

    public void disableOptions() {
        float[] colorMatrix = {
                0.33f, 0.33f, 0.33f, 0, 0, //red
                0.33f, 0.33f, 0.33f, 0, 0, //green
                0.33f, 0.33f, 0.33f, 0, 0, //blue
                0, 0, 0, 1, 0    //alpha
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        settingsImageView.setColorFilter(colorFilter);
        reportsImageView.setColorFilter(colorFilter);
        customersImageView.setColorFilter(colorFilter);
        employeeImageView.setColorFilter(colorFilter);

        settingsTextView.setTextColor(getResources().getColor(android.R.color.white));
        reportsTextView.setTextColor(getResources().getColor(android.R.color.white));
        customersTextView.setTextColor(getResources().getColor(android.R.color.white));
        employeeTextView.setTextColor(getResources().getColor(android.R.color.white));

        settingsLinearLayout.setEnabled(false);
        reportsLinearLayout.setEnabled(false);
        customersLinearLayout.setEnabled(false);
        employeesLinearLayout.setEnabled(false);
    }

    public void removePreferences() {
        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, null).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, "").apply();
        FileUtils.deleteFile(mContext, Constants.EMPLOYEE_FILE_NAME);
    }

    @OnClick(R.id.im_logout)
    public void logout() {
        showAlertDialog(getString(R.string.logout), getString(R.string.wanna_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removePreferences();
                // to remove any activities that are in stack
                ActivityCompat.finishAffinity(ControlPanelActivity.this);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void showAlertDialog(String title, String msg, DialogInterface.OnClickListener okayClickListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(android.R.string.yes), okayClickListner);
        builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick(R.id.ll_profile)
    public void profile() {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_reports)
    public void reports() {
        Intent intent = new Intent(mContext, ReportsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_employees)
    public void employees() {
        Intent intent = new Intent(mContext, EmployeesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_customers)
    public void customers() {
        Intent intent = new Intent(mContext, CustomersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_inventory)
    public void inventory() {
        Intent intent = new Intent(mContext, InventoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_payments)
    public void payments() {
        Intent intent = new Intent(mContext, PaymentsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_pos)
    public void pos() {
        Intent intent = new Intent(mContext, POSActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_settings)
    public void settings() {
        Intent intent = new Intent(mContext, SettingsActivity.class);
        startActivity(intent);
    }
}
