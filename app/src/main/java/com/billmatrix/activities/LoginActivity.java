package com.billmatrix.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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
import com.billmatrix.utils.ConnectivityReceiver;
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
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        ButterKnife.bind(this);
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        /*
         * if user is previously looged in, directly open control panel
         */
        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.IS_LOGGED_IN, false)) {
            Intent intent = new Intent(mContext, ControlPanelActivity.class);
            startActivity(intent);
            finish();
        }

        rememberMeCheckBox.setChecked(false);

        /*
         * if user has logout, then show the previously logged in licence key in the field and disable the edit field
         */
        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, null))) {
            licenceEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, ""));
        }

        /*
         * if user name and password are present in prefs, show them in the edit text fields.
         */
        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LOGIN_ID, null))) {
            userNameEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_LOGIN_ID, ""));
            rememberMeCheckBox.setChecked(true);
        }

        if (!TextUtils.isEmpty(Utils.getSharedPreferences(mContext).getString(Constants.PREF_PASSWORD, null))) {
            passwordEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_PASSWORD, ""));
        }

        copyrightTextView.setText(getString(R.string.copyright, 2016));

    }

    @OnClick(R.id.btn_login)
    public void checkInternetAndLogin() {

        Utils.hideSoftKeyboard(userNameEditText);

        if (!verify()) {
            return;
        }

        progressDialog = Utils.getProgressDialog(mContext, "Loading...");
        progressDialog.setCancelable(false);

        if (Utils.isInternetAvailable(mContext)) {
            login();
        } else {
            offlineLogin();
        }
    }

    private void offlineLogin() {
        if (FileUtils.isFileExists(Constants.PROFILE_FILE_NAME, mContext)) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            Profile profile = Constants.getGson().fromJson(profileString, Profile.class);
            if (!Utils.isProfileEmpty(profile)) {
                Log.e(TAG, "Profile is from file");
                if (userName.equalsIgnoreCase(profile.data.login_id) && password.equalsIgnoreCase(profile.data.password) && imeiNumber.equalsIgnoreCase(profile.data.imei_number)) {
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, profile.data.type).apply();

                    /*
                     * if remember me is checked, save user name and pwd in pref if not remove them
                     */
                    if (rememberMeCheckBox.isChecked()) {
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, userName).apply();
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                    } else {
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, "").apply();
                        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                    }

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
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
                            if (userName.equalsIgnoreCase(employeeData.login_id) && password.equalsIgnoreCase(employeeData.password)) {
                                isEmployee = true;
                                loggedInEmployee = employeeData;
                                break;
                            }
                        }

                        if (!imeiNumber.equalsIgnoreCase(loggedInEmployee.imei_number)) {
                            showAlertDialog("Incorrect Licence Key", "You don't have access to this store", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.showToast("Check with Shop Admin", mContext);
                                }
                            });

                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            return;
                        }

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (isEmployee) {
                            if (loggedInEmployee.status.equalsIgnoreCase("1") || loggedInEmployee.status.equalsIgnoreCase("ACTIVE")) {
                                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, loggedInEmployee.type).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, loggedInEmployee.login_id).apply();

                                /*
                                 * if remember me is checked, save user name and pwd in pref if not remove them
                                 */
                                if (rememberMeCheckBox.isChecked()) {
                                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, userName).apply();
                                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                                } else {
                                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, "").apply();
                                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                                }
                                Intent intent = new Intent(mContext, ControlPanelActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Utils.showToast("You are an IN-ACTIVE employee, contact admin to login.", mContext);
                            }
                        } else {
                            Utils.showToast("username/password is wrong", mContext);
                        }
                    }
                }
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Utils.showToast("unable to log! Check for Internet connection", mContext);
        }
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

    public void login() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).login(userName, password, imeiNumber);

        call.enqueue(new Callback<HashMap<String, String>>() {
            /*
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> loginMap = response.body();
                    if (loginMap.get("status").equalsIgnoreCase("200")) {
                        if (loginMap.get("login").equalsIgnoreCase("success")) {
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, loginMap.get("user_type")).apply();
                            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LICENECE_KEY, loginMap.get("imei_number")).apply();
                            if (loginMap.get("user_type").equalsIgnoreCase("admin")) {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_ADMIN_ID, loginMap.containsKey("user_id") ? loginMap.get("user_id") : "").apply();
                            } else {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, userName).apply();
                            }

                            /*
                             * if remember me is checked, save user name and pwd in pref if not remove them
                             */
                            if (rememberMeCheckBox.isChecked()) {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, userName).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, password).apply();
                            } else {
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_LOGIN_ID, "").apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_PASSWORD, "").apply();
                            }

                            Intent intent = new Intent(mContext, ControlPanelActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (loginMap.containsKey("message") && loginMap.get("message").equalsIgnoreCase("incorrect IMEI")) {
                            showAlertDialog("Incorrect Licence Key", "contact BillMatrix admin to reset present Licence key.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.showToast("Call BillMatrix Admin", mContext);
                                }
                            });
                        } else {
                            Utils.showToast("username/password is wrong", mContext);
                        }
                    }
                }

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            /*
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.showToast("unable to login", mContext);
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

    public void getIMEINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imeiNumber = telephonyManager.getDeviceId();
    }

    String userName;
    String password;
    String licenceKey;
    String imeiNumber;

    private boolean verify() {
        userName = userNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        licenceKey = licenceEditText.getText().toString().trim();
        getIMEINumber();
        Log.e(TAG, "verify: " + imeiNumber);

        if (TextUtils.isEmpty(userName)) {
            Utils.showToast("Enter user name", mContext);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Utils.showToast("Enter password", mContext);
            return false;
        }
        if (TextUtils.isEmpty(imeiNumber)) {
            Utils.showToast("Cannot get IMEI Number", mContext);
            return false;
        }
//        imeiNumber = "8234123";

        return true;
    }
}
