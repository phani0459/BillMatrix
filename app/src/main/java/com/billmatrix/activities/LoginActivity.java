package com.billmatrix.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.models.Profile;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        ButterKnife.bind(this);

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
    }

    @OnClick(R.id.btn_login)
    public void login() {
        if (!verify()) {
            return;
        }

        final ProgressDialog progressDialog = Utils.showProgressDialog(mContext);
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).login(userName, password,
                licenceKey, imeiNumber);

        call.enqueue(new Callback<HashMap<String, String>>() {
            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.body() + "");
                progressDialog.dismiss();
                if (response.body() != null) {
                    HashMap<String, String> loginMap = response.body();
                    if (loginMap.get("status").equalsIgnoreCase("200")) {
                        if (loginMap.get("login").equalsIgnoreCase("success")) {
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
        if (TextUtils.isEmpty(licenceKey)) {
            showToast("Enter Licence Key");
            return false;
        }

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
