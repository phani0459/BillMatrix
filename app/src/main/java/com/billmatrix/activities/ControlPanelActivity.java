package com.billmatrix.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ControlPanelActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        ButterKnife.bind(this);
        mContext = this;

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));

        String userType = Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_TYPE, null);

        if (!TextUtils.isEmpty(userType)) {
            if (!userType.equalsIgnoreCase("admin")) {
                disableOptions();
            }
        } else {
            disableOptions();
        }
    }

    public void disableOptions() {
        float[] colorMatrix = {
                0.33f, 0.33f, 0.33f, 0, 0, //red
                0.33f, 0.33f, 0.33f, 0, 0, //green
                0.33f, 0.33f, 0.33f, 0, 0, //blue
                0, 0, 0, 1, 0    //alpha
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        profileImageView.setColorFilter(colorFilter);
        settingsImageView.setColorFilter(colorFilter);
        reportsImageView.setColorFilter(colorFilter);
        customersImageView.setColorFilter(colorFilter);
        employeeImageView.setColorFilter(colorFilter);

        profileTextView.setTextColor(getResources().getColor(android.R.color.white));
        settingsTextView.setTextColor(getResources().getColor(android.R.color.white));
        reportsTextView.setTextColor(getResources().getColor(android.R.color.white));
        customersTextView.setTextColor(getResources().getColor(android.R.color.white));
        employeeTextView.setTextColor(getResources().getColor(android.R.color.white));

        profileLinearLayout.setEnabled(false);
        settingsLinearLayout.setEnabled(false);
        reportsLinearLayout.setEnabled(false);
        customersLinearLayout.setEnabled(false);
        employeesLinearLayout.setEnabled(false);

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


}
