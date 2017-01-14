package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.DatabaseFragment;
import com.billmatrix.fragments.DiscountsFragment;
import com.billmatrix.fragments.GenExpensesFragment;
import com.billmatrix.fragments.HardwareFragment;
import com.billmatrix.fragments.StoreFragment;
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
    public void onBackPressed() {
        if (TaxFragment.getInstance() != null && selectedTab.equalsIgnoreCase("TAX")) {
            TaxFragment.getInstance().onBackPressed();
        } else if (DiscountsFragment.getInstance() != null && selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            DiscountsFragment.getInstance().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        if (TaxFragment.getInstance() != null) {
            if (TaxFragment.getInstance().isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        if (DiscountsFragment.getInstance() != null) {
            if (DiscountsFragment.getInstance().isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("STORE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, StoreFragment.getInstance()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, StoreFragment.getInstance()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("DATABASE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new DatabaseFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DatabaseFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("TAX")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, TaxFragment.getInstance()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, TaxFragment.getInstance()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("HARDWARE")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new HardwareFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HardwareFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, DiscountsFragment.getInstance()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, DiscountsFragment.getInstance()).commit();
            }
        }

    }
}
