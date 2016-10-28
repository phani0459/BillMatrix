package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomersFragment;

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

        setTextView("<span>" + getArrowString() + " Customers </span>");
        addTabButtons(3, "Customers", "Customer wise Due", "Customer Transactions");

        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new CustomersFragment()).commit();
    }
}
