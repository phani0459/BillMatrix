package com.billmatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.GenExpensesFragment;
import com.billmatrix.fragments.PayInsFragment;
import com.billmatrix.fragments.PayOutsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 03-11-2016.
 */

public class PaymentsActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    public static final String PAYOUT = "PAY OUTS";
    public static final String PAYIN = "PAY INS";
    public static final String GEN_EXPENSE = "GEN. EXPENSES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Payments </span>", getArrowString()));
        addTabButtons(3, PAYOUT, PAYIN, GEN_EXPENSE);

        frameLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("PAY OUTS")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new PayOutsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PayOutsFragment()).commit();
            }
        } else if (selectedTab.equalsIgnoreCase("PAY INS")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new PayInsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PayInsFragment()).commit();
            }
        } else {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new GenExpensesFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new GenExpensesFragment()).commit();
            }
        }

    }
}
