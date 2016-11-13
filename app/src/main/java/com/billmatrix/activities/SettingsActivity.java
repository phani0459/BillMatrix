package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.DiscountsFragment;
import com.billmatrix.fragments.GenExpensesFragment;
import com.billmatrix.fragments.PayInsFragment;
import com.billmatrix.fragments.SalesFragment;
import com.billmatrix.fragments.TaxFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 03-11-2016.
 */

public class SettingsActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Settings </span>", getArrowString()));
        addTabButtons(5, "STORE", "DATABASE", "TAX", "HARDWARE", "DISCOUNTS");

        frameLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("STORE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new SalesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SalesFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("DATABASE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new PayInsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PayInsFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("TAX")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new TaxFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new TaxFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("HARDWARE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new GenExpensesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new GenExpensesFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new DiscountsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DiscountsFragment()).commit();
            }
        }

    }
}
