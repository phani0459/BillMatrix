package com.billmatrix.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.models.Profile;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseTabActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    @BindView(R.id.profile)
    public View profileLayout;
    @BindView(R.id.et_storeAdmin)
    public EditText storeAdminEditText;
    @BindView(R.id.et_profile_password)
    public EditText passwordEditText;
    @BindView(R.id.et_mobilenumber)
    public EditText mobNumEditText;
    @BindView(R.id.et_adminName)
    public EditText adminNameEditText;
    @BindView(R.id.et_loginID)
    public EditText loginIdmEditText;


    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(String.format("<span>%s Profile </span>", getArrowString()));
        addTabButtons(1, "Profile");

        profileLayout.setVisibility(View.VISIBLE);

        String loginId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (!FileUtils.isFileExists(Constants.PROFILE_FILE_NAME, mContext)) {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(loginId)) {
                    getProfilefromServer(loginId);
                }
            }
        } else {
            Log.e(TAG, "Profile is from file");
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            profile = Constants.getGson().fromJson(profileString, Profile.class);
            loadProfile();
        }

        adminNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeAdminEditText.setText(s.toString().toUpperCase());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void loadProfile() {
        if (profile != null) {
            passwordEditText.setText(profile.data.password);
            adminNameEditText.setText(profile.data.username);
            mobNumEditText.setText(profile.data.email);
            loginIdmEditText.setText(profile.data.username);
            storeAdminEditText.setText(profile.data.username.toUpperCase());
        }
    }

    public void getProfilefromServer(String loginId) {
        Log.e(TAG, "getProfilefromServer: ");
        Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile(loginId);

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
                    profile = response.body();
                    if (profile.status == 200 && profile.userdata.equalsIgnoreCase("success")) {
                        FileUtils.writeToFile(mContext, Constants.PROFILE_FILE_NAME, Constants.getGson().toJson(profile));
                        loadProfile();
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
            }
        });
    }

    @OnClick(R.id.imBtn_editPwd)
    public void enablePassword() {
        passwordEditText.setEnabled(true);
        passwordEditText.setBackgroundResource(R.drawable.edit_text_border);
    }

    @OnClick(R.id.btn_saveProfile)
    public void saveProfile() {
        passwordEditText.setEnabled(false);
        passwordEditText.setBackgroundResource(R.drawable.edit_text_disabled_border);
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in profile
         */
    }


}
