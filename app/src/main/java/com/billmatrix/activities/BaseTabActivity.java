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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BaseTabActivity extends AppCompatActivity {

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
        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
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

                            tabChanged(((Button) v).getText().toString(), false);
                        }
                    }
                });
                linearLayout.addView(buttonLayout);
            }
        }
    }

    public void removePreferences() {
        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        showAlertDialog(getString(R.string.logout), getString(R.string.wanna_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removePreferences();
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

    public void showToast(String msg) {
        Toast.makeText(mContext, msg + "", Toast.LENGTH_LONG).show();
    }

    String selectedTab;

    public abstract void tabChanged(String selectedTab, boolean isInit);

    public void setPageTitle(String msg) {
        this.pageTitleTextView.setText(Html.fromHtml(msg));
    }

    public String getArrowString() {
        return " \u203A ";
    }

}