package com.billmatrix;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;


public class ControlPanelActivity extends AppCompatActivity {


    public TextView copyrightTextView;

    public TextView  tv_profile, tv_pos, tv_inventory, tv_payments, tv_settings, tv_reports, tv_customers, tv_employee;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_panel);

          tv_pos = (TextView) findViewById(R.id.tv_pos);
          tv_inventory = (TextView) findViewById(R.id.tv_inventory);
          tv_profile = (TextView) findViewById(R.id.tv_profile);
          tv_payments = (TextView) findViewById(R.id.tv_payments);
          tv_settings = (TextView) findViewById(R.id.tv_settings);
          tv_reports = (TextView) findViewById(R.id.tv_reports);
          tv_customers = (TextView) findViewById(R.id.tv_customers);
          tv_employee = (TextView) findViewById(R.id.tv_employee);
          copyrightTextView = (TextView) findViewById(R.id.copyrightTextView);

        scaleTVDrawables(tv_pos,0.6);
        scaleTVDrawables(tv_inventory,0.6);
        scaleTVDrawables(tv_profile,0.6);
        scaleTVDrawables(tv_payments,0.6);
        scaleTVDrawables(tv_settings,0.6);
        scaleTVDrawables(tv_reports,0.6);
        scaleTVDrawables(tv_customers,0.6);
        scaleTVDrawables(tv_employee,0.6);

        copyrightTextView.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));

    }


    public static void scaleTVDrawables(TextView textView, double fitFactor) {
        Drawable[] drawables = textView.getCompoundDrawables();

        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                if (drawables[i] instanceof ScaleDrawable) {
                    drawables[i].setLevel(1);
                }
                drawables[i].setBounds(0, 0, (int) (drawables[i].getIntrinsicWidth() * fitFactor),
                        (int) (drawables[i].getIntrinsicHeight() * fitFactor));
                ScaleDrawable sd = new ScaleDrawable(drawables[i], 0, drawables[i].getIntrinsicWidth(), drawables[i].getIntrinsicHeight());
                if(i == 0) {
                    textView.setCompoundDrawables(sd.getDrawable(), drawables[1], drawables[2], drawables[3]);
                } else if(i == 1) {
                    textView.setCompoundDrawables(drawables[0], sd.getDrawable(), drawables[2], drawables[3]);
                } else if(i == 2) {
                    textView.setCompoundDrawables(drawables[0], drawables[1], sd.getDrawable(), drawables[3]);
                } else {
                    textView.setCompoundDrawables(drawables[0], drawables[1], drawables[2], sd.getDrawable());
                }
            }
        }
    }

}
