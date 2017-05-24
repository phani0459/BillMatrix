package com.billmatrix.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomerTransFragment;
import com.billmatrix.fragments.CustomerWiseDueFragment;
import com.billmatrix.fragments.CustomersFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/*
 * Created by KANDAGATLAs on 26-10-2016.
 */

public class CustomersActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle("<span>" + getArrowString() + " Customers </span>");

        addTabButtons(3, "Customers", "Customer wise Due", "Customer Transactions");

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
        if (currentFragment != null && selectedTab.equalsIgnoreCase("Customers")) {
            ((CustomersFragment) currentFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        isInit = true;

        if (currentFragment != null && selectedTab.equalsIgnoreCase("Customers") && currentFragment instanceof CustomersFragment ) {
            if (((CustomersFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }


        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("Customers")) {
            currentFragment = new CustomersFragment();
        } else if (selectedTab.equalsIgnoreCase("Customer wise Due")) {
            currentFragment = new CustomerWiseDueFragment();
        } else {
            currentFragment = new CustomerTransFragment();
        }

        if (isInit) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }
    }
}
