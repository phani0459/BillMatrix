package com.billmatrix.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Profile;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import java.util.HashMap;

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
    @BindView(R.id.et_location)
    public EditText locationAdminEditText;
    @BindView(R.id.et_branch)
    public EditText branchAdminEditText;
    @BindView(R.id.btn_saveProfile)
    public Button saveProfileButton;
    @BindView(R.id.im_edit_storeDetails)
    public ImageButton editStoreDetailsButton;
    @BindView(R.id.imBtn_editPwd)
    public ImageButton editPwdButton;
    @BindView(R.id.tv_administrator)
    public TextView administratorTextView;

    public boolean isAdmin;

    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(String.format("<span>%s Profile </span>", getArrowString()));
        addTabButtons(1, "Profile");

        isAdmin = true;

        profileLayout.setVisibility(View.VISIBLE);

        String loginId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        String userType = Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_TYPE, null);

        /**
         * If employee logged in disable profile attrs
         */
        if (!TextUtils.isEmpty(userType)) {
            if (!userType.equalsIgnoreCase("admin")) {
                isAdmin = false;
            }
        } else {
            isAdmin = false;
        }

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

            if (isAdmin) {
                loadProfile();
            } else {
                administratorTextView.setText("Employee Name");
                disableProfile();
            }
        }


        loginIdmEditText.setFilters(Utils.getInputFilter(12));
        passwordEditText.setFilters(Utils.getInputFilter(12));
        mobNumEditText.setFilters(Utils.getInputFilter(10));
        branchAdminEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        locationAdminEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        adminNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeAdminEditText.setText(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void disableProfile() {
        saveProfileButton.setEnabled(false);
        saveProfileButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
        adminNameEditText.setEnabled(false);
        adminNameEditText.setBackgroundResource(R.drawable.edit_text_disabled_border);
        loginIdmEditText.setBackgroundResource(R.drawable.edit_text_disabled_border);
        loginIdmEditText.setEnabled(false);
        mobNumEditText.setEnabled(false);
        mobNumEditText.setBackgroundResource(R.drawable.edit_text_disabled_border);

        editStoreDetailsButton.setVisibility(View.INVISIBLE);
        editPwdButton.setVisibility(View.INVISIBLE);

        loadEmployeeProfile();
    }

    private void loadEmployeeProfile() {
        final String empId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_EMP_LOGIN_ID, null);
        Employee.EmployeeData employeeData = billMatrixDaoImpl.getParticularEmployee(empId);
        if (employeeData != null) {
            passwordEditText.setText(employeeData.password);
            adminNameEditText.setText(employeeData.username);
            mobNumEditText.setText(employeeData.mobile_number);
            loginIdmEditText.setText(employeeData.login_id);
        }

        if (profile != null) {
            storeAdminEditText.setText(profile.data.username.toUpperCase());
            locationAdminEditText.setText(profile.data.location != null ? profile.data.location.toUpperCase() : "");
            branchAdminEditText.setText(profile.data.branch != null ? profile.data.branch.toUpperCase() : "");
        }
    }

    @OnClick(R.id.im_edit_storeDetails)
    public void editStoreDetails() {
        locationAdminEditText.setEnabled(true);
        branchAdminEditText.setEnabled(true);
        locationAdminEditText.setBackgroundResource(R.drawable.edit_text_border);
        branchAdminEditText.setBackgroundResource(R.drawable.edit_text_border);
    }

    public void loadProfile() {
        if (profile != null) {
            passwordEditText.setText(profile.data.password);
            adminNameEditText.setText(profile.data.username);
            mobNumEditText.setText(profile.data.mobile_number);
            loginIdmEditText.setText(profile.data.login_id);
            storeAdminEditText.setText(profile.data.username);
            locationAdminEditText.setText(!TextUtils.isEmpty(profile.data.location) ? profile.data.location.toUpperCase() : "");
            branchAdminEditText.setText(!TextUtils.isEmpty(profile.data.branch) ? profile.data.branch.toUpperCase() : "");
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
                        if (isAdmin) {
                            loadProfile();
                        } else {
                            disableProfile();
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
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    @OnClick(R.id.imBtn_editPwd)
    public void enablePassword() {
        passwordEditText.setEnabled(true);
        passwordEditText.setBackgroundResource(R.drawable.edit_text_border);
        passwordEditText.setTransformationMethod(null);
    }

    @OnClick(R.id.btn_saveProfile)
    public void saveProfile() {
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        String adminName = adminNameEditText.getText().toString();

        if (TextUtils.isEmpty(adminName)) {
            showToast("Enter Administrator");
            return;
        }

        String loginId = loginIdmEditText.getText().toString();

        if (TextUtils.isEmpty(loginId)) {
            showToast("Enter Login Id");
            return;
        }

        if (loginId.length() < 6) {
            showToast("Login Id must be more than 6 characters");
            return;
        }

        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            showToast("Enter Password");
            return;
        }

        if (password.length() < 6) {
            showToast("password must be more than 6 characters");
            return;
        }

        passwordEditText.setEnabled(false);
        passwordEditText.setBackgroundResource(R.drawable.edit_text_disabled_border);

        String mobile = mobNumEditText.getText().toString();

        if (TextUtils.isEmpty(mobile)) {
            showToast("Enter Mobile Number");
            return;
        }

        if (!Utils.isPhoneValid(mobile)) {
            showToast("Enter Valid Mobile Number");
            return;
        }

        String branch = branchAdminEditText.getText().toString();

        if (TextUtils.isEmpty(branch)) {
            showToast("Enter branch Name");
            return;
        }

        branchAdminEditText.setBackgroundResource(android.R.color.transparent);
        branchAdminEditText.setEnabled(false);

        String location = locationAdminEditText.getText().toString();

        if (TextUtils.isEmpty(location)) {
            showToast("Enter location");
            return;
        }

        locationAdminEditText.setBackgroundResource(android.R.color.transparent);
        locationAdminEditText.setEnabled(false);

        Profile newProfile = new Profile();
        newProfile.status = 200;
        newProfile.userdata = "success";

        Profile.ProfileData newData = new Profile().new ProfileData();
        newData.id = profile.data.id;
        newData.admin_id = profile.data.id;
        newData.username = adminName;
        newData.login_id = loginId;
        newData.mobile_number = mobile;
        newData.password = password;
        newData.imei_number = profile.data.imei_number;
        newData.type = "admin";
        newData.branch = branch;
        newData.location = location;
        newData.status = profile.data.status;
        newData.create_date = profile.data.create_date;
        ;
        newData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());

        newProfile.data = newData;
        FileUtils.deleteFile(mContext, Constants.PROFILE_FILE_NAME);
        FileUtils.writeToFile(mContext, Constants.PROFILE_FILE_NAME, Constants.getGson().toJson(newProfile));

        if (Utils.isInternetAvailable(mContext)) {
            Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).updateEmployee(profile.data.id, adminName, password, mobile,
                    loginId, profile.data.imei_number, location, branch, profile.data.status);

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
                                showToast("Profile Updated successfully");
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
        } else {
            showToast("Profile Updated successfully");
        }


    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in profile
         */
    }


}
