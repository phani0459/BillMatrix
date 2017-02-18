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
    private PayOutsFragment payOutsFragment;
    private PayInsFragment payInsFragment;
    private GenExpensesFragment genExpensesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Payments </span>", getArrowString()));
        addTabButtons(3, PAYOUT, PAYIN, GEN_EXPENSE);

        frameLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (payOutsFragment != null && selectedTab.equalsIgnoreCase(PAYOUT)) {
            payOutsFragment.onBackPressed();
        } else if (payInsFragment != null && selectedTab.equalsIgnoreCase(PAYIN)) {
            payInsFragment.onBackPressed();
        } else if (genExpensesFragment != null && selectedTab.equalsIgnoreCase(GEN_EXPENSE)) {
            genExpensesFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        payOutsFragment = new PayOutsFragment();
        payInsFragment = new PayInsFragment();
        genExpensesFragment = new GenExpensesFragment();

        if (selectedTab.equalsIgnoreCase(PAYOUT)) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, payOutsFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, payOutsFragment).commit();
            }
        } else if (selectedTab.equalsIgnoreCase(PAYIN)) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, payInsFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, payInsFragment).commit();
            }
        } else {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, genExpensesFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, genExpensesFragment).commit();
            }
        }

    }
}
