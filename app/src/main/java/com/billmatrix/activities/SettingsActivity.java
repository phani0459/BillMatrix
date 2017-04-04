package com.billmatrix.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.DatabaseFragment;
import com.billmatrix.fragments.DiscountsFragment;
import com.billmatrix.fragments.HardwareFragment;
import com.billmatrix.fragments.StoreFragment;
import com.billmatrix.fragments.TaxFragment;
import com.billmatrix.fragments.TransportFragment;
import com.billmatrix.models.Tax;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 03-11-2016.
 */

public class SettingsActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Settings </span>", getArrowString()));

        addTabButtons(6, "STORE", "DATABASE", "TAX", "HARDWARE", "DISCOUNTS", "TRANSPORT");

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "Current_Fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }

        frameLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "Current_Fragment", currentFragment);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null && selectedTab.equalsIgnoreCase("TAX")) {
            ((TaxFragment) currentFragment).onBackPressed();
        } else if (currentFragment != null && selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            ((DiscountsFragment) currentFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        if (currentFragment != null && selectedTab.equalsIgnoreCase("TAX")) {
            if (currentFragment instanceof TaxFragment) {
                if (((TaxFragment) currentFragment).isEditing) {
                    Utils.showToast("Save the changes made before going to other tab", mContext);
                    return;
                }
            }
        }

        if (currentFragment != null && selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            if (currentFragment instanceof DiscountsFragment) {
                if (((DiscountsFragment) currentFragment).isEditing) {
                    Utils.showToast("Save the changes made before going to other tab", mContext);
                    return;
                }
            }
        }

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("STORE")) {
            currentFragment = StoreFragment.getInstance();
        } else if (selectedTab.equalsIgnoreCase("DATABASE")) {
            currentFragment = new DatabaseFragment();
        } else if (selectedTab.equalsIgnoreCase("TAX")) {
            currentFragment = TaxFragment.getInstance();
        } else if (selectedTab.equalsIgnoreCase("HARDWARE")) {
            currentFragment = new HardwareFragment();
        } else if (selectedTab.equalsIgnoreCase("DISCOUNTS")) {
            currentFragment = DiscountsFragment.getInstance();
        } else if (selectedTab.equalsIgnoreCase("TRANSPORT")) {
            currentFragment = new TransportFragment();
        }

        if (isInit) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }

    }
}
