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

    @OnTouch(R.id.ll_profile)
    public boolean profile(View v, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
        return false;
    }


}
