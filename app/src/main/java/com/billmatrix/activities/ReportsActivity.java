package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.GenExpensesFragment;
import com.billmatrix.fragments.PayInsFragment;
import com.billmatrix.fragments.PayOutsFragment;
import com.billmatrix.fragments.SalesFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 03-11-2016.
 */

public class ReportsActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Reports </span>", getArrowString()));
        addTabButtons(5, "SALES", "PURCHASE", "VENDOR", "ITEM", "GENERATE");

        frameLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("SALES")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new SalesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SalesFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("PURCHASE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new PayInsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PayInsFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("VENDOR")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new GenExpensesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new GenExpensesFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("ITEM")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new GenExpensesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new GenExpensesFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("GENERATE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new GenExpensesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new GenExpensesFragment()).commit();
            }
        }

    }
}
