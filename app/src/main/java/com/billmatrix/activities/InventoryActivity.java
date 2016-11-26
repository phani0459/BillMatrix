package com.billmatrix.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.fragments.CustomersFragment;
import com.billmatrix.fragments.InventoryFragment;
import com.billmatrix.fragments.VendorsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/**
 * Created by KANDAGATLAs on 26-10-2016.
 */

public class InventoryActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    InventoryFragment inventoryFragment;
    VendorsFragment vendorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(String.format("<span>%s Inventory </span>", getArrowString()));
        addTabButtons(2, "INVENTORY", "Vendors");

        frameLayout.setVisibility(View.VISIBLE);

        toggleSearchLayout(View.VISIBLE);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
                } else {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (searchView.getText().toString().length() > 0) {
                            searchView.setText("");
                            if (selectedTab.equalsIgnoreCase("Vendors")) {
                                if (vendorsFragment != null) {
                                    vendorsFragment.searchClosed();
                                }
                            } else {
                                if (inventoryFragment != null) {
                                    inventoryFragment.searchClosed();
                                }
                            }
                        }
                        return false;
                    }
                }
                v.clearFocus();
                return false;
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideSoftKeyboard(searchView);
                    if (selectedTab.equalsIgnoreCase("Vendors")) {
                        if (vendorsFragment != null) {
                            vendorsFragment.searchClicked(searchView.getText().toString());
                        }
                    } else {
                        if (inventoryFragment != null) {
                            inventoryFragment.searchClicked(searchView.getText().toString());
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

        inventoryFragment = new InventoryFragment();
        vendorsFragment = new VendorsFragment();

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("INVENTORY")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, inventoryFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, inventoryFragment).commit();
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
