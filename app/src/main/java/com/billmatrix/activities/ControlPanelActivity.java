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
import com.billmatrix.models.Profile;
import com.billmatrix.network.ServerData;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

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
    private ServerData serverData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        progressDialog = Utils.getProgressDialog(mContext, "Loading...");

        serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setProgressDialog(progressDialog);
        serverData.setFromLogin(true);
        serverData.setPaymentType(null);
        serverData.setContext(mContext);

        copyrightTextView.setText(getString(R.string.copyright, 2016));

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

        /*
         * Sequence to fetch data from server
         * Profile
         * Employees
         * Customers
         * Vendors
         * Inventory
         * Tax
         * Discounts
         * Payments
         */
        getDataFromServer(adminId);
    }

    private void getEmployeeProfileFromServer() {
        final String empId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_EMP_LOGIN_ID, null);
        if (!FileUtils.isFileExists(Constants.EMPLOYEE_FILE_NAME, mContext)) {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(empId)) {
                    progressDialog.show();
                    Log.e(TAG, "getEmployeeProfileFromServer: ");
                    Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile(empId);

                    call.enqueue(new Callback<Profile>() {


                        /*
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

                        /*
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
                    progressDialog.show();
                    getProfilefromServer(adminId);
                }
            } else {
                Utils.showToast("Unable to fetch Data! Check for Internet connection.", mContext);
            }
        } else {
            serverData.getEmployeesFromServer(adminId);
        }
    }

    public void getProfilefromServer(final String adminId) {
        Log.e(TAG, "getProfilefromServer: ");
        Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile(adminId);

        call.enqueue(new Callback<Profile>() {


            /*
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
                        serverData.getEmployeesFromServer(adminId);
                    }
                }
            }

            /*
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
