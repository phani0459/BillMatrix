package com.billmatrix.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.DrawableClickListener;
import com.billmatrix.utils.ConnectivityReceiver;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public abstract class BaseTabActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.navigateTextView)
    public TextView pageTitleTextView;
    @BindView(R.id.layout)
    public LinearLayout linearLayout;
    @BindView(R.id.searchLayout)
    public LinearLayout searchLayout;
    @BindView(R.id.tv_searchText)
    public TextView searchTextView;
    @BindView(R.id.searchView)
    public EditText searchView;

    Context mContext;
    BillMatrixDaoImpl billMatrixDaoImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tab);

        ButterKnife.bind(this);
        Fresco.initialize(getApplicationContext());

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        pageTitleTextView.setOnTouchListener(new DrawableClickListener.LeftDrawableClickListener(pageTitleTextView) {
            @Override
            public boolean onDrawableClick() {
                Intent intent = new Intent(mContext, ControlPanelActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    @OnClick(R.id.im_billmatrix_logo)
    public void logoClicked() {
        Intent intent = new Intent(mContext, ControlPanelActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectivityReceiver.connectivityReceiverListener = null;
    }

    public void toggleSearchLayout(int visibility) {
        searchLayout.setVisibility(visibility);
    }

    public void addTabButtons(int n, String... names) {
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                RelativeLayout buttonLayout = (RelativeLayout) layoutInflater.inflate(R.layout.tab_button, null);
                final Button button = (Button) buttonLayout.findViewById(R.id.btn_tab);
                final Button tab_bottom_view = (Button) buttonLayout.findViewById(R.id.tab_bottom_view);

                button.setText(names[i]);

                if (i != 0) {
                    button.getBackground().setLevel(1);
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    tab_bottom_view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                } else {
                    button.getBackground().setLevel(0);
                    button.setTextColor(getResources().getColor(android.R.color.black));
                    selectedTab = button.getText().toString();
                    tabChanged(button.getText().toString(), true);
                    tab_bottom_view.setBackgroundColor(getResources().getColor(R.color.tabButtonBG));
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selectedTab.equalsIgnoreCase(button.getText().toString())) {
                            if (ifTabCanChange) {
                                selectedTab = button.getText().toString();
                                if (linearLayout.getChildCount() > 0) {
                                    for (int j = 0; j < linearLayout.getChildCount(); j++) {
                                        View view = linearLayout.getChildAt(j);
                                        Button tab = (Button) view.findViewById(R.id.btn_tab);
                                        Button tabBtmView = (Button) view.findViewById(R.id.tab_bottom_view);
                                        tab.getBackground().setLevel(1);
                                        tab.setTextColor(getResources().getColor(android.R.color.white));
                                        tabBtmView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                    }
                                }
                                v.getBackground().setLevel(0);
                                ((Button) v).setTextColor(getResources().getColor(android.R.color.black));
                                View parent = (View) v.getParent();
                                Button tabBtmView = (Button) parent.findViewById(R.id.tab_bottom_view);
                                tabBtmView.setBackgroundColor(getResources().getColor(R.color.tabButtonBG));
                            }
                            tabChanged(((Button) v).getText().toString(), false);
                        }
                    }
                });
                linearLayout.addView(buttonLayout);
            }
        }
    }

    public boolean ifTabCanChange = true;

    public void removePreferences() {
        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, null).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, "").apply();
        FileUtils.deleteFile(mContext, Constants.EMPLOYEE_FILE_NAME);
    }

    @OnTouch(R.id.tv_reports)
    public boolean reports(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, ReportsActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    @OnTouch(R.id.tv_settings)
    public boolean settings(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        showAlertDialog(getString(R.string.logout), getString(R.string.wanna_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removePreferences();
                // to remove any activities that are in stack
                ActivityCompat.finishAffinity(BaseTabActivity.this);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void showAlertDialog(String title, String msg, DialogInterface.OnClickListener okayClickListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(android.R.string.yes), okayClickListner);
        builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    String selectedTab;

    public abstract void tabChanged(String selectedTab, boolean isInit);

    public void setPageTitle(String msg) {
        this.pageTitleTextView.setText(Html.fromHtml(msg));
    }

    public String getArrowString() {
        return /*" \u203A "*/ " ";
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            showAlertDialog("You are Connected to Internet", "Do you want to sync with Server", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
