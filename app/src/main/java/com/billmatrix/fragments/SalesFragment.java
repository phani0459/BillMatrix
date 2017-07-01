package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.billmatrix.adapters.SalesReportsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Transaction;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.sp_sales_select)
    public Spinner saleSelectSpinner;
    @BindView(R.id.et_sale_from_date)
    public EditText fromDate_EditText;
    @BindView(R.id.et_sale_to_date)
    public EditText toDate_EditText;
    @BindView(R.id.sp_sales_item)
    public Spinner saleItemSpinner;
    @BindView(R.id.disble_spinner)
    public View spinnerDisableView;
    @BindView(R.id.atv_sales_item)
    public AutoCompleteTextView salesItemACTextView;
    @BindView(R.id.tv_qtyHeading)
    public TextView h6TextView;
    @BindView(R.id.tv_amountHeading)
    public TextView h5TextView;
    @BindView(R.id.tv_vendorHeading)
    public TextView h4TextView;
    @BindView(R.id.tv_itemNameHeading)
    public TextView h3TextView;
    @BindView(R.id.salesList)
    public RecyclerView reportsRecyclerView;

    private Context mContext;
    private boolean isSalesTab;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private ArrayList<String> spinnerTwo_Strings;
    private ArrayList<Vendor.VendorData> vendors;
    private ArrayList<Discount.DiscountData> discounts;
    private ArrayList<Inventory.InventoryData> inventory;
    private SalesReportsAdapter reportsAdapter;

    /*
     * For All screens: SNO and Date are common.
     * For Sale Total: Only Item Name and QTY are required (h3 and h6)
     * For Sale BY ITEM: h3 and h6 are required
     * For Sale By VENDOR: h3, h4 and h6 are required
     * For Sale By Profit: h4 and h5 are required
     * For Sale By Discount: h3, h4, h5 and h6 are required
     */

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
        } else {
            Utils.loadSpinner(saleSelectSpinner, mContext, R.array.purchase_by_array);
            loadVendors();
        }

        fromDate_EditText.setInputType(InputType.TYPE_NULL);
        toDate_EditText.setInputType(InputType.TYPE_NULL);

        fromDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, fromDate_EditText, true, false);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, -2);
                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
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
                        DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, toDate_EditText, Constants.getDateFormat().parse(fromDate).getTime(), false);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                v.clearFocus();
            }
        });

        List<Transaction> reports = new ArrayList<>();
        ServerUtils.setIsSync(false);

        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        reportsAdapter = new SalesReportsAdapter(reports, this, billMatrixDaoImpl);
        reportsRecyclerView.setAdapter(reportsAdapter);

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
            inventory = billMatrixDaoImpl.getInventory(null);
            if (inventory != null && inventory.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventory) {
                    spinnerTwo_Strings.add(inventoryData.item_name.toUpperCase());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_dropdown_item, spinnerTwo_Strings);
        salesItemACTextView.setThreshold(1);//will start working from first character
        salesItemACTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    @Override
    public void onResume() {
        super.onResume();
        saleSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (reportsAdapter != null) {
                    reportsAdapter.removeAllReports();
                    reportsAdapter.setSalesItemType(null);
                }

                fromDate_EditText.setText("");
                toDate_EditText.setText("");
                saleItemSpinner.setSelection(0);
                salesItemACTextView.setText("");

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
                    h3TextView.setVisibility(View.VISIBLE);
                    h4TextView.setVisibility(View.VISIBLE);
                    h5TextView.setVisibility(View.VISIBLE);
                    h6TextView.setVisibility(View.VISIBLE);

                    if (selectedString.equalsIgnoreCase("SALE BY VENDOR")) {
                        h5TextView.setVisibility(View.GONE);
                        h3TextView.setVisibility(View.GONE);

                        h4TextView.setText(getString(R.string.vendor));
                        h6TextView.setText(getString(R.string.TOTAL_SALES));

                        loadVendors();
                    } else if (selectedString.equalsIgnoreCase("SALE BY DISCOUNT")) {
                        h3TextView.setText(getString(R.string.TOTAL_DISCOUNTED_BILL_AMT));
                        h6TextView.setText(getString(R.string.DISCOUNT_CODE));
                        h4TextView.setText(getString(R.string.TOTAL_SALES));
                        h5TextView.setText(getString(R.string.TOTAL_DISC));

                        loadDiscounts();
                    } else if (selectedString.equalsIgnoreCase("SALE PROFIT") || selectedString.equalsIgnoreCase("SALE TOTAL") || selectedString.equalsIgnoreCase("SELECT ONE")) {
                        saleItemSpinner.setEnabled(false);
                        spinnerDisableView.setVisibility(View.VISIBLE);
                        if (selectedString.equalsIgnoreCase("SALE PROFIT")) {
                            h3TextView.setVisibility(View.GONE);
                            h6TextView.setVisibility(View.GONE);

                            h4TextView.setText(getString(R.string.TOTAL_SALES));
                            h5TextView.setText(getString(R.string.TOTAL_PROFIT));
                        }

                        if (selectedString.equalsIgnoreCase("SALE TOTAL")) {
                            h4TextView.setVisibility(View.GONE);
                            h5TextView.setVisibility(View.GONE);

                            h3TextView.setText(getString(R.string.ITEM_NAME));
                            h6TextView.setText(getString(R.string.QTY));
                        }

                    } else if (selectedString.equalsIgnoreCase("SALE BY ITEM")) {
                        saleItemSpinner.setVisibility(View.INVISIBLE);
                        salesItemACTextView.setVisibility(View.VISIBLE);
                        h4TextView.setVisibility(View.GONE);
                        h5TextView.setVisibility(View.GONE);

                        h3TextView.setText(getString(R.string.ITEM_NAME));
                        h6TextView.setText(getString(R.string.QTY));

                        loadInventory();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @OnClick(R.id.btn_viewSales)
    public void viewReports() {
        Utils.hideSoftKeyboard(fromDate_EditText);
        Utils.hideSoftKeyboard(salesItemACTextView);

        String fromDate = fromDate_EditText.getText().toString();

        if (TextUtils.isEmpty(fromDate)) {
            Utils.showToast("Enter From Date", mContext);
            return;
        }

        try {
            fromDate = Constants.getSQLiteDateFormat().format(Constants.getDateFormat().parse(fromDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String toDate = toDate_EditText.getText().toString();

        if (TextUtils.isEmpty(toDate)) {
            Utils.showToast("Enter To Date", mContext);
            return;
        }

        try {
            toDate = Constants.getSQLiteDateFormat().format(Constants.getDateFormat().parse(toDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String itemName = salesItemACTextView.getText().toString();
        String spinnerItemName = saleItemSpinner.getSelectedItem().toString();

        String query = "";
        ArrayList<Transaction> transactions = new ArrayList<>();

        if (reportsAdapter != null) {
            reportsAdapter.removeAllReports();
            reportsAdapter.setSalesItemType(null);
        }

        if (!isSalesTab) {
            viewPurchasesReports(fromDate, toDate, spinnerItemName);
            return;
        }

        switch (saleSelectSpinner.getSelectedItem().toString()) {
            case "SELECT ONE":
                reportsAdapter.setSalesType("SELECT ONE");
                Utils.showToast("Select Sale type to view report", mContext);
                return;
            case "SALE TOTAL":
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DBConstants.DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
                transactions = billMatrixDaoImpl.getTransactions(query);
                reportsAdapter.setSalesType("SALE TOTAL");
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllReports(transactions);
                }
                break;
            case "SALE BY ITEM":
                if (TextUtils.isEmpty(itemName)) {
                    Utils.showToast("Enter Item Name", mContext);
                    return;
                }
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DBConstants.DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
                transactions = billMatrixDaoImpl.getTransactions(query);
                reportsAdapter.setSalesType("SALE BY ITEM");
                reportsAdapter.setSalesItemType(itemName);
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllReports(transactions);
                }
                break;
            case "SALE BY VENDOR":
                if (TextUtils.isEmpty(spinnerItemName) || spinnerItemName.equalsIgnoreCase("SELECT ONE")) {
                    Utils.showToast("Select Vendor", mContext);
                    return;
                }
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DBConstants.DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
                transactions = billMatrixDaoImpl.getTransactions(query);
                reportsAdapter.setSalesType("SALE BY VENDOR");
                reportsAdapter.setSalesItemType(billMatrixDaoImpl.getVendorId(spinnerItemName));
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllReports(transactions);
                }
                break;
            case "SALE PROFIT":
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DBConstants.DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
                transactions = billMatrixDaoImpl.getTransactions(query);
                reportsAdapter.setSalesType("SALE PROFIT");
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllReports(transactions);
                }
                break;
            case "SALE BY DISCOUNT":
                if (TextUtils.isEmpty(spinnerItemName) || spinnerItemName.equalsIgnoreCase("SELECT ONE")) {
                    Utils.showToast("Select Discount", mContext);
                    return;
                }
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE + " WHERE " + DBConstants.DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
                transactions = billMatrixDaoImpl.getTransactions(query);
                reportsAdapter.setSalesType("SALE BY DISCOUNT");
                reportsAdapter.setSalesItemType(spinnerItemName);
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllReports(transactions);
                }
                break;
        }
    }

    private void viewPurchasesReports(String fromDate, String toDate, String spinnerItemName) {
        ArrayList<Inventory.InventoryData> transactions = new ArrayList<>();

        switch (saleSelectSpinner.getSelectedItem().toString()) {
            case "SELECT ONE":
                reportsAdapter.setSalesType("SELECT ONE");
                Utils.showToast("Select Purchase type to view report", mContext);
                return;
            case "PURCHASE TOTAL":
                transactions = billMatrixDaoImpl.getInventory(null);
                reportsAdapter.setSalesType("PURCHASE TOTAL");
                if (transactions != null && transactions.size() > 0) {
                    reportsAdapter.addAllInventories(transactions);
                }
                break;
            case "PURCHASE BY VENDOR":
                if (TextUtils.isEmpty(spinnerItemName) || spinnerItemName.equalsIgnoreCase("SELECT ONE")) {
                    Utils.showToast("Select Vendor", mContext);
                    return;
                }
                reportsAdapter.setSalesType("PURCHASE BY VENDOR");
                transactions = billMatrixDaoImpl.getInventory(null);
                if (transactions != null && transactions.size() > 0) {
                    ArrayList<Inventory.InventoryData> localInventories = new ArrayList<>();
                    for (int i = 0; i < transactions.size(); i++) {
                        if (transactions.get(i).vendor.equalsIgnoreCase(billMatrixDaoImpl.getVendorId(spinnerItemName))) {
                            localInventories.add(transactions.get(i));
                        }
                    }
                    if (localInventories.size() > 0) {
                        reportsAdapter.addAllInventories(localInventories);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
