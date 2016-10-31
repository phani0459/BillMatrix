package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;

import com.billmatrix.R;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 23-10-2016.
 */

public class EmployeesActivity extends BaseTabActivity {

    @BindView(R.id.employees)
    public View employeesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle("<span>" + getArrowString() + " Employees </span>");
        addTabButtons(1, "Employees");

        employeesLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in Employees
         */
    }
}
