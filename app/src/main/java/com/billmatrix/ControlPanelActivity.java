package com.billmatrix;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;


public class ControlPanelActivity extends AppCompatActivity {


    public TextView copyrightTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_panel);
        copyrightTextView = (TextView) findViewById(R.id.copyrightTextView);


        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));

    }


}
