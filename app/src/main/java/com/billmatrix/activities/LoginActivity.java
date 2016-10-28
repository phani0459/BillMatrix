package com.billmatrix.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.utils.FileUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;


    @BindView(R.id.copyrightTextView)
    public TextView copyrightTextView;


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
        FileUtils.saveLogin(mContext);
        Intent intent = new Intent(mContext, ControlPanelActivity.class);
        startActivity(intent);
        finish();
    }
}
