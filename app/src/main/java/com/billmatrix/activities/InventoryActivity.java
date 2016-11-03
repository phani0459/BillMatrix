package com.billmatrix.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomersFragment;
import com.billmatrix.fragments.VendorsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 26-10-2016.
 */

public class InventoryActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    CustomersFragment customersFragment;
    VendorsFragment vendorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(String.format("<span>%s Inventory </span>", getArrowString()));
        addTabButtons(2, "INVENTORY", "Vendors");

        frameLayout.setVisibility(View.VISIBLE);

        toggleSearchLayout(View.VISIBLE);

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideSoftKeyboard(searchView);
                    if (selectedTab.equalsIgnoreCase("Vendors")) {
                        if (vendorsFragment != null) {
                            vendorsFragment.searchClicked(searchView.getText().toString());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        searchView.setText("");
        Utils.hideSoftKeyboard(searchView);

        customersFragment = new CustomersFragment();
        vendorsFragment = new VendorsFragment();

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("INVENTORY")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, customersFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, customersFragment).commit();
            }
            searchTextView.setText(getString(R.string.inventory_search_text));
            searchView.setHint(getString(R.string.inventory_search_hint));
        } else {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, vendorsFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, vendorsFragment).commit();
            }
            searchTextView.setText(getString(R.string.vendor_search_text));
            searchView.setHint(getString(R.string.vendor_search_hint));
        }
    }
}
