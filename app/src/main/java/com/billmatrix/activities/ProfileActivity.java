package com.billmatrix.activities;

import android.os.Bundle;
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
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle("<span>" + getArrowString() + " ProfileModel </span>");
        addTabButtons(1, "ProfileModel");

        profileLayout.setVisibility(View.VISIBLE);

        Call<Profile> call = Utils.getBillMatrixAPI(mContext).getProfile();

        call.enqueue(new Callback<Profile>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.body() != null) {
                    profile = response.body();
                    Log.e("SUCCEESS RESPONSE RAW", profile.toString() + "");
                    if (profile.status == 200 && profile.userdata.equalsIgnoreCase("success")) {
                        FileUtils.writeToFile(mContext, Constants.PROFILE_FILE_NAME, profile.toString());
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
