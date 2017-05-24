package com.billmatrix.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.fragments.InventoryFragment;
import com.billmatrix.fragments.VendorsFragment;
import com.billmatrix.utils.Utils;

import butterknife.BindView;

/*
 * Created by KANDAGATLAs on 26-10-2016.
 */

public class InventoryActivity extends BaseTabActivity {

    @BindView(R.id.frameLayout)
    public FrameLayout frameLayout;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(String.format("<span>%s Inventory </span>", getArrowString()));

        addTabButtons(2, "INVENTORY", "Vendors");

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "Current_Fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }

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

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (searchView.getText().toString().length() > 0) {
                            searchView.setText("");
                            if (selectedTab.equalsIgnoreCase("Vendors")) {
                                if (currentFragment != null && currentFragment instanceof VendorsFragment) {
                                    ((VendorsFragment) currentFragment).searchClosed();
                                }
                            } else {
                                if (currentFragment != null && currentFragment instanceof InventoryFragment) {
                                    ((InventoryFragment) currentFragment).searchClosed();
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
                        if (currentFragment != null && currentFragment instanceof VendorsFragment) {
                            ((VendorsFragment) currentFragment).searchClicked(searchView.getText().toString());
                        }
                    } else {
                        if (currentFragment != null && currentFragment instanceof InventoryFragment) {
                            ((InventoryFragment) currentFragment).searchClicked(searchView.getText().toString());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "Current_Fragment", currentFragment);
    }

    String scannedBarcode = "";

    /*
     * to listen to the barcode scanner value
     *
     * @param e event triggered by scanner
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (currentFragment != null && selectedTab.equalsIgnoreCase("INVENTORY")) {
            if (e.getAction() != KeyEvent.ACTION_DOWN) {
                char pressedKey = (char) e.getUnicodeChar();
                scannedBarcode += pressedKey;
                if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!TextUtils.isEmpty(scannedBarcode)) {
                        ((InventoryFragment) currentFragment).fetchInventoryByBarcode(scannedBarcode);
                        scannedBarcode = "";
                    }
                }
            }
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null && selectedTab.equalsIgnoreCase("INVENTORY") && currentFragment instanceof InventoryFragment) {
            ((InventoryFragment) currentFragment).onBackPressed();
        } else if (currentFragment != null && selectedTab.equalsIgnoreCase("Vendors") && currentFragment instanceof VendorsFragment) {
            ((VendorsFragment) currentFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        searchView.setText("");
        Utils.hideSoftKeyboard(searchView);

        if (currentFragment != null && currentFragment instanceof InventoryFragment) {
            if (((InventoryFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        if (currentFragment != null && currentFragment instanceof VendorsFragment) {
            if (((VendorsFragment) currentFragment).isEditing) {
                Utils.showToast("Save the changes made before going to other tab", mContext);
                return;
            }
        }

        isInit = true;
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            isInit = false;
        }

        if (selectedTab.equalsIgnoreCase("INVENTORY")) {
            currentFragment = new InventoryFragment();
            searchTextView.setText(getString(R.string.inventory_search_text));
            searchView.setHint(getString(R.string.inventory_search_hint));
        } else {
            currentFragment = new VendorsFragment();
            searchTextView.setText(getString(R.string.vendor_search_text));
            searchView.setHint(getString(R.string.vendor_search_hint));
        }

        if (isInit) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, currentFragment).commit();
        }

    }
}
