package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment {

    @BindView(R.id.sp_sales_select)
    public Spinner saleSelectSpinner;
    @BindView(R.id.et_sale_from_date)
    public EditText fromDate_EditText;
    @BindView(R.id.et_sale_to_date)
    public EditText toDate_EditText;
    @BindView(R.id.sp_sales_item)
    public Spinner saleItemSpinner;
    @BindView(R.id.tv_dateHeading)
    public TextView dateHeadingTextView;
    @BindView(R.id.disble_spinner)
    public View spinnerDisableView;
    @BindView(R.id.atv_sales_item)
    public AutoCompleteTextView salesItemACTextView;

    private Context mContext;
    private boolean isSalesTab;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private ArrayList<String> spinnerTwo_Strings;
    private ArrayList<Vendor.VendorData> vendors;
    private ArrayList<Discount.DiscountData> discounts;
    private ArrayList<Inventory.InventoryData> inventory;

    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        isSalesTab = false;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        spinnerTwo_Strings = new ArrayList<>();
        spinnerTwo_Strings.add("SELECT ONE");
        Utils.loadSpinner(saleItemSpinner, mContext, spinnerTwo_Strings);

        String selectedTab = getArguments().getString("selectedTab");

        if (selectedTab != null && selectedTab.equalsIgnoreCase("SALES")) {
            isSalesTab = true;
        }

        if (isSalesTab) {
            Utils.loadSpinner(saleSelectSpinner, mContext, R.array.sale_by_array);
            dateHeadingTextView.setText(getString(R.string.SOLD_DATE));
        } else {
            Utils.loadSpinner(saleSelectSpinner, mContext, R.array.purchase_by_array);
            loadVendors();
            dateHeadingTextView.setText(getString(R.string.PURCHASE_DATE));
        }

        fromDate_EditText.setInputType(InputType.TYPE_NULL);
        toDate_EditText.setInputType(InputType.TYPE_NULL);

        fromDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, fromDate_EditText, true, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        toDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String fromDate = fromDate_EditText.getText().toString();
                    if (TextUtils.isEmpty(fromDate)) {
                        Utils.showToast("Select from Date", mContext);
                        return;
                    }
                    try {
                        DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, toDate_EditText, Constants.getDateFormat().parse(fromDate).getTime());
                        datePickerDialog.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                v.clearFocus();
            }
        });

        return v;
    }

    public void loadVendors() {
        spinnerTwo_Strings = new ArrayList<>();
        spinnerTwo_Strings.add("SELECT ONE");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                spinnerTwo_Strings.add(vendorData.name.toUpperCase());
            }
        } else {
            vendors = billMatrixDaoImpl.getVendors();
            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    spinnerTwo_Strings.add(vendorData.name.toUpperCase());
                }
            }
        }

        Utils.loadSpinner(saleItemSpinner, mContext, spinnerTwo_Strings);
    }

    public void loadDiscounts() {
        spinnerTwo_Strings = new ArrayList<>();
        spinnerTwo_Strings.add("SELECT ONE");

        if (discounts != null && discounts.size() > 0) {
            for (Discount.DiscountData discountData : discounts) {
                spinnerTwo_Strings.add(discountData.discount_code.toUpperCase());
            }
        } else {
            discounts = billMatrixDaoImpl.getDiscount();
            if (discounts != null && discounts.size() > 0) {
                for (Discount.DiscountData discountData : discounts) {
                    spinnerTwo_Strings.add(discountData.discount_code.toUpperCase());
                }
            }
        }

        Utils.loadSpinner(saleItemSpinner, mContext, spinnerTwo_Strings);
    }

    public void loadInventory() {
        spinnerTwo_Strings = new ArrayList<>();
        spinnerTwo_Strings.add("SELECT ONE");

        if (inventory != null && inventory.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventory) {
                spinnerTwo_Strings.add(inventoryData.item_name.toUpperCase());
            }
        } else {
            inventory = billMatrixDaoImpl.getInventory();
            if (inventory != null && inventory.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventory) {
                    spinnerTwo_Strings.add(inventoryData.item_name.toUpperCase());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, spinnerTwo_Strings);
        salesItemACTextView.setThreshold(1);//will start working from first character
        salesItemACTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    @Override
    public void onResume() {
        super.onResume();
        saleSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saleItemSpinner.setSelection(0);
                String selectedString = adapterView.getSelectedItem().toString();
                if (!isSalesTab) {
                    saleItemSpinner.setEnabled(true);
                    saleItemSpinner.setVisibility(View.VISIBLE);
                    spinnerDisableView.setVisibility(View.GONE);
                    salesItemACTextView.setVisibility(View.GONE);
                    if (selectedString.equalsIgnoreCase("PURCHASE TOTAL") || selectedString.equalsIgnoreCase("SELECT ONE")) {
                        saleItemSpinner.setEnabled(false);
                        spinnerDisableView.setVisibility(View.VISIBLE);
                    }
                } else {
                    saleItemSpinner.setEnabled(true);
                    saleItemSpinner.setVisibility(View.VISIBLE);
                    spinnerDisableView.setVisibility(View.GONE);
                    salesItemACTextView.setVisibility(View.GONE);
                    if (selectedString.equalsIgnoreCase("SALE BY VENDOR")) {
                        loadVendors();
                    } else if (selectedString.equalsIgnoreCase("SALE BY DISCOUNT")) {
                        loadDiscounts();
                    } else if (selectedString.equalsIgnoreCase("SALE PROFIT") || selectedString.equalsIgnoreCase("SALE TOTAL") || selectedString.equalsIgnoreCase("SELECT ONE")) {
                        saleItemSpinner.setEnabled(false);
                        spinnerDisableView.setVisibility(View.VISIBLE);
                    } else if (selectedString.equalsIgnoreCase("SALE BY ITEM")) {
                        saleItemSpinner.setVisibility(View.INVISIBLE);
                        salesItemACTextView.setVisibility(View.VISIBLE);
                        loadInventory();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
