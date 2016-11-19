package com.billmatrix.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Profile;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.copyrightTextView)
    public TextView copyrightTextView;

    @BindView(R.id.et_user_name)
    public TextInputEditText userNameEditText;
    @BindView(R.id.et_password)
    public TextInputEditText passwordEditText;
    @BindView(R.id.et_licenceKey)
    public TextInputEditText licenceEditText;
    @BindView(R.id.cb_rememberMe)
    public CheckBox rememberMeCheckBox;
    private BillMatrixDaoImpl billMatrixDaoImpl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        ButterKnife.bind(this);
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        /**
         * if user is previously looged in, directly open control panel
         */
        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.IS_LOGGED_IN, false)) {
            Intent intent = new Intent(mContext, ControlPanelActivity.class);
            startActivity(intent);
            finish();
        }

        /**
         * if user has logout, then show the previously logged in licence key in the field and disable the edit field
         */
        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, null))) {
            licenceEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, ""));
        }

        /**
         * if user name and password are present in prefs, show them in the edit text fields.
         */
        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_NAME, null))) {
            userNameEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_NAME, ""));
        }

        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_PASSWORD, null))) {
            passwordEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_PASSWORD, ""));
        }

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
    }

    @OnClick(R.id.btn_login)
    public void checkInternetAndLogin() {

        if (!verify()) {
            return;
        }

        if (Utils.isInternetAvailable(mContext)) {
            login();
        } else {
            offlineLogin();
        }
    }

    private void offlineLogin() {
        if (FileUtils.isFileExists(Constants.PROFILE_FILE_NAME, mContext)) {
            Log.e(TAG, "Profile is from file");
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            Profile profile = Constants.getGson().fromJson(profileString, Profile.class);
            if (profile != null && profile.data != null) {
                if (userName.equalsIgnoreCase(profile.data.username) && password.equalsIgnoreCase(profile.data.password) && imeiNumber.equalsIgnoreCase(profile.data.imei_number)) {
                    /**
                     * save licence key, and disable the licence edit text so that other user cannot login from this tab
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, profile.data.type).apply();

                    /**
                     * if remember me is checked, save user name and pwd in pref if not remove them
                     */
                    if (rememberMeCheckBox.isChecked()) {
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, userName).apply();
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                    } else {
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, "").apply();
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                    }

                    Intent intent = new Intent(mContext, ControlPanelActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Check for employees");
                    Employee.EmployeeData loggedInEmployee = new Employee().new EmployeeData();
                    boolean isEmployee = false;
                    ArrayList<Employee.EmployeeData> employees = billMatrixDaoImpl.getEmployees();
                    if (employees != null && employees.size() > 0) {
                        for (Employee.EmployeeData employeeData : employees) {
                            if (userName.equalsIgnoreCase(employeeData.email) && password.equalsIgnoreCase(employeeData.password) && imeiNumber.equalsIgnoreCase(employeeData.imei_number)) {
                                isEmployee = true;
                                loggedInEmployee = employeeData;
                                break;
                            }
                        }

                        if (isEmployee) {
                            /**
                             * save licence key, and disable the licence edit text so that other user cannot login from this tab
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, loggedInEmployee.type).apply();

                            /**
                             * if remember me is checked, save user name and pwd in pref if not remove them
                             */
                            if (rememberMeCheckBox.isChecked()) {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, userName).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                            } else {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, "").apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                            }

                            Intent intent = new Intent(mContext, ControlPanelActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("username/password is wrong");
                        }
                    }
                }
            }
        } else {
            showToast("unable to log! Check for Internet connection");
        }
    }


    public void login() {
        final ProgressDialog progressDialog = Utils.showProgressDialog(mContext);
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).login(userName, password, imeiNumber);

        call.enqueue(new Callback<HashMap<String, String>>() {
            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                progressDialog.dismiss();
                if (response.body() != null) {
                    HashMap<String, String> loginMap = response.body();
                    if (loginMap.get("status").equalsIgnoreCase("200")) {
                        if (loginMap.get("login").equalsIgnoreCase("success")) {
                            /**
                             * save licence key, and disable the licence edit text so that other user cannot login from this tab
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, loginMap.get("user_type")).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LICENECE_KEY, loginMap.get("imei_number")).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_ADMIN_ID, loginMap.containsKey("user_id") ? loginMap.get("user_id") : "").apply();

                            /**
                             * if remember me is checked, save user name and pwd in pref if not remove them
                             */
                            if (rememberMeCheckBox.isChecked()) {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, userName).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                            } else {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_NAME, "").apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                            }

                            Intent intent = new Intent(mContext, ControlPanelActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("username/password is wrong");
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
                progressDialog.dismiss();
                showToast("unable to login");
            }
        });
    }

    public void showForgotDialog(View v) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_pwd);

        Button backBtn = (Button) dialog.findViewById(R.id.btn_back);
        Button submitBtn = (Button) dialog.findViewById(R.id.btn_submit);
        TextInputEditText mobileNumberEditText = (TextInputEditText) dialog.findViewById(R.id.et_mobilenumber);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void getIMEINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imeiNumber = telephonyManager.getDeviceId();
    }

    String userName;
    String password;
    String licenceKey;
    String imeiNumber;

    private boolean verify() {
        userName = userNameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        licenceKey = licenceEditText.getText().toString();
        getIMEINumber();
        Log.e(TAG, "verify: " + imeiNumber);

        if (TextUtils.isEmpty(userName)) {
            showToast("Enter user name");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("Enter password");
            return false;
        }
        if (TextUtils.isEmpty(imeiNumber)) {
            showToast("Cannot get IMEI Number");
            return false;
        }
        imeiNumber = "8234123";

        return true;
    }
}
