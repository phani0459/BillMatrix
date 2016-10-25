package com.billmatrix;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class ControlPanelActivity extends AppCompatActivity {

    @BindView(R.id.copyrightTextView)
    public TextView copyrightTextView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        ButterKnife.bind(this);
        mContext = this;

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
    }

    @OnClick(R.id.ll_profile)
    public void profile() {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_employees)
    public void employees() {
        Intent intent = new Intent(mContext, EmployeesActivity.class);
        startActivity(intent);
    }


}
