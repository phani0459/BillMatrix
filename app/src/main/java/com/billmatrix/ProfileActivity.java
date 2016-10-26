package com.billmatrix;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileActivity extends BaseTabActivity {

    @BindView(R.id.profile)
    public View profileLayout;
    @BindView(R.id.et_storeAdmin)
    public EditText storeAdminEditText;
    @BindView(R.id.et_profile_password)
    public EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTextView("<span>" + getArrowString() + " Profile </span>");
        addTabButtons(1, "Profile");

        profileLayout.setVisibility(View.VISIBLE);
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
