package com.billmatrix.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomersFragment;
import com.billmatrix.fragments.GenExpensesFragment;
import com.billmatrix.fragments.PayInsFragment;
import com.billmatrix.fragments.PayOutsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/*
 * Created by KANDAGATLAs on 03-11-2016.
 */

public class PaymentsActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    public static final String PAYOUT = "PAY OUTS";
    public static final String PAYIN = "PAY INS";
    public static final String GEN_EXPENSE = "GEN. EXPENSES";
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageTitle(String.format("<span>%s Payments </span>", getArrowString()));
        addTabButtons(3, PAYOUT, PAYIN, GEN_EXPENSE);

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
        if (currentFragment != null && selectedTab.equalsIgnoreCase(PAYOUT) && currentFragment instanceof PayOutsFragment) {
            ((PayOutsFragment) currentFragment).onBackPressed();
        } else if (currentFragment != null && selectedTab.equalsIgnoreCase(PAYIN) && currentFragment instanceof PayInsFragment) {
            ((PayInsFragment) currentFragment).onBackPressed();
        } else if (currentFragment != null && selectedTab.equalsIgnoreCase(GEN_EXPENSE) && currentFragment instanceof GenExpensesFragment) {
            ((GenExpensesFragment) currentFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        Utils.hideSoftKeyboard(searchView);

        if (currentFragment != null && selectedTab.equalsIgnoreCase(PAYOUT) && currentFragment instanceof PayOutsFragment) {
            if (((PayOutsFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        if (currentFragment != null && selectedTab.equalsIgnoreCase(PAYIN) && currentFragment instanceof PayInsFragment ) {
            if (((PayInsFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        if (currentFragment != null && selectedTab.equalsIgnoreCase(GEN_EXPENSE) && currentFragment instanceof GenExpensesFragment ) {
            if (((GenExpensesFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase(PAYOUT)) {
            currentFragment = new PayOutsFragment();
        } else if (selectedTab.equalsIgnoreCase(PAYIN)) {
            currentFragment = new PayInsFragment();
        } else {
            currentFragment = new GenExpensesFragment();
        }

        if (isInit) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }

    }
}
