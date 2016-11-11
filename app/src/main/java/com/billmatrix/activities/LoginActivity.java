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
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        ButterKnife.bind(this);

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
        if (Utils.isInternetAvailable(mContext)) {
            login();
        }
    }


    public void login() {
        if (!verify()) {
            return;
        }

        final ProgressDialog progressDialog = Utils.showProgressDialog(mContext);
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).login(userName, password);

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
        /*if (TextUtils.isEmpty(licenceKey)) {
            showToast("Enter Licence Key");
            return false;
        }*/

        if (TextUtils.isEmpty(imeiNumber)) {
            showToast("Cannot get IMEI Number");
            return false;
        }

        userName = "nag";
        password = "nag123";
        licenceKey = "L123";
        imeiNumber = "8234123";

        return true;
    }
}
