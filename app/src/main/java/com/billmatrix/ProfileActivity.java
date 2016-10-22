package com.billmatrix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

public class ProfileActivity extends BaseTabActivity {

    @BindView(R.id.profile)
    public View profileLayout;
    @BindView(R.id.et_storeAdmin)
    public EditText storeAdminEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTextView("<span>" + getArrowString() + " Profile </span>");
        addTabButtons(1, "Profile");

        profileLayout.setVisibility(View.VISIBLE);

        storeAdminEditText.setSelection(storeAdminEditText.getText().length());
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in profile
         */
    }


}
