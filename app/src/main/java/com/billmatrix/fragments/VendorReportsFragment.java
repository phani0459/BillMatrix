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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.adapters.ItemReportAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Transaction;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * A simple {@link Fragment} subclass.
 */
public class VendorReportsFragment extends Fragment {

    @BindView(R.id.sp_report_vendor_types)
    public Spinner vendorTypeSpinner;
    @BindView(R.id.sp_report_vendors)
    public Spinner vendorsSpinner;
    @BindView(R.id.et_report_vendor_from_date)
    public EditText fromDate_EditText;
    @BindView(R.id.et_report_vendor_to_date)
    public EditText toDate_EditText;
    @BindView(R.id.vendorReportsList)
    public RecyclerView reportsRecyclerView;

    private Context mContext;
    private boolean isVendorsTab;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private ItemReportAdapter itemReportAdapter;

    public VendorReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vendor_reports, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        isVendorsTab = false;

        String selectedTab = getArguments().getString("selectedTab");

        if (selectedTab != null && selectedTab.equalsIgnoreCase("VENDOR")) {
            isVendorsTab = true;
        }

        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        ServerUtils.setIsSync(false);

        ArrayList<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        itemReportAdapter = new ItemReportAdapter(inventoryDatas, mContext);
        reportsRecyclerView.setAdapter(itemReportAdapter);

        if (isVendorsTab) {
            vendorsSpinner.setVisibility(View.VISIBLE);
            Utils.loadSpinner(vendorTypeSpinner, mContext, R.array.vendors_by_array);

            ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
            ArrayList<String> strings = new ArrayList<>();
            strings.add("SELECT VENDOR");

            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    strings.add(vendorData.name);
                }

            }
            Utils.loadSpinner(vendorsSpinner, mContext, strings);

        } else {
            vendorsSpinner.setVisibility(View.GONE);
            Utils.loadSpinner(vendorTypeSpinner, mContext, R.array.items_by_array);
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

        return v;
    }

    private Comparator<Inventory.InventoryData> getQTYComparator() {
        return new Comparator<Inventory.InventoryData>() {
            @Override
            public int compare(Inventory.InventoryData dataOne, Inventory.InventoryData dataTwo) {
                return dataTwo.selectedQTY.compareTo(dataOne.selectedQTY);
            }
        };
    }

    @OnClick(R.id.btn_viewReports)
    public void viewItemReports() {
        Utils.hideSoftKeyboard(fromDate_EditText);

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

        String reportTypeString = vendorTypeSpinner.getSelectedItem().toString();
        String vendorString = vendorsSpinner.getSelectedItem().toString();
        ArrayList<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        String query = "";

        switch (reportTypeString) {
            case "SELECT ONE":
                Utils.showToast("Select Type to view report", mContext);
                return;
            case "Available Stock":
                inventoryDatas = billMatrixDaoImpl.getInventory(null);
                if (inventoryDatas != null && inventoryDatas.size() > 0) {
                    itemReportAdapter.addAllInventory(inventoryDatas);
                }
                break;
            case "Bottom selling items":
                query = "SELECT * FROM " + DBConstants.INVENTORY_TABLE;
                inventoryDatas = billMatrixDaoImpl.getInventory(query);
                if (inventoryDatas != null && inventoryDatas.size() > 0) {
                    itemReportAdapter.addAllInventory(inventoryDatas);
                }
                break;
            case "Top Selling Items":
                query = "SELECT * FROM " + DBConstants.CUSTOMER_TRANSACTIONS_TABLE;
                ArrayList<Transaction> transactions = billMatrixDaoImpl.getTransactions(query);
                if (transactions != null && transactions.size() > 0) {
                    for (int i = 0; i < transactions.size(); i++) {
                        ArrayList<Inventory.InventoryData> transInventories = Constants.getGson().fromJson(transactions.get(i).inventoryJson, Constants.inventoryDatasMapType);
                        if (transInventories != null && transInventories.size() > 0) {
                            inventoryDatas.addAll(transInventories);
                        }
                    }
                }
                if (inventoryDatas.size() > 0) {
                    Collections.sort(inventoryDatas, getQTYComparator());
                    itemReportAdapter.addAllInventory(inventoryDatas);
                }
                break;
        }
    }

}
