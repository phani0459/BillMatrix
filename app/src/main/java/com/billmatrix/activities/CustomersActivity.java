package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomerTransFragment;
import com.billmatrix.fragments.CustomerWiseDueFragment;
import com.billmatrix.fragments.CustomersFragment;
import com.billmatrix.fragments.VendorsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 26-10-2016.
 */

public class CustomersActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle("<span>" + getArrowString() + " Customers </span>");
        addTabButtons(3, "Customers", "Customer wise Due", "Customer Transactions");

        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        isInit = true;

        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("Customers")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new CustomersFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CustomersFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("Customer wise Due")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new CustomerWiseDueFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CustomerWiseDueFragment()).commit();
            }
        } else {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new CustomerTransFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CustomerTransFragment()).commit();
            }
        }
    }
}
