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

import com.billmatrix.R;
import com.billmatrix.adapters.CustomersAdapter;
import com.billmatrix.adapters.GeneratedReportsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.GeneratedReport;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * A simple {@link Fragment} subclass.
 */
public class GenerateReportFragment extends Fragment implements OnItemClickListener {


    @BindView(R.id.sp_rep_unit)
    public Spinner unitSpinner;
    @BindView(R.id.et_report_pur_date)
    public EditText purchaseDate_EditText;
    @BindView(R.id.atv_rep_item_name)
    public AutoCompleteTextView itemNameEditText;
    @BindView(R.id.atv_rep_vendor)
    public AutoCompleteTextView vendorEditText;
    @BindView(R.id.et_report_customunit)
    public EditText customUnitEditText;
    @BindView(R.id.rv_generated_reports)
    public RecyclerView reportsRecyclerView;
    @BindView(R.id.et_report_cost)
    public EditText costEditText;
    @BindView(R.id.et_report_qty)
    public EditText qtyEditText;
    @BindView(R.id.et_report_discount)
    public EditText discountEditText;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private ArrayList<Inventory.InventoryData> inventoryDatas;
    private ArrayList<Vendor.VendorData> vendors;
    private GeneratedReportsAdapter reportsAdapter;
    private String adminId;

    public GenerateReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generate_report, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        Utils.loadSpinner(unitSpinner, mContext, R.array.units_array);

        purchaseDate_EditText.setInputType(InputType.TYPE_NULL);

        purchaseDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, purchaseDate_EditText, true, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        inventoryDatas = billMatrixDaoImpl.getInventory(null);
        vendors = billMatrixDaoImpl.getVendors();

        loadItemNames();
        loadVendors();

        List<GeneratedReport.ReportData> reports = new ArrayList<>();
        ServerUtils.setIsSync(false);

        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        reportsAdapter = new GeneratedReportsAdapter(reports, this);
        reportsRecyclerView.setAdapter(reportsAdapter);

        return v;
    }

    public void loadItemNames() {
        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            String selectedVendor = vendorEditText.getText().toString();
            ArrayList<String> itemNames = new ArrayList<>();
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!TextUtils.isEmpty(selectedVendor)) {
                    if (inventoryData.vendor.equalsIgnoreCase(selectedVendor)) {
                        itemNames.add(inventoryData.item_name);
                    }
                } else {
                    itemNames.add(inventoryData.item_name);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_item, itemNames);
            itemNameEditText.setThreshold(1);//will start working from first character
            itemNameEditText.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }
    }

    public void loadVendors() {
        if (vendors != null && vendors.size() > 0) {
            ArrayList<String> vendorNames = new ArrayList<>();
            for (Vendor.VendorData vendorData : vendors) {
                vendorNames.add(vendorData.name);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_item, vendorNames);
            vendorEditText.setThreshold(1);//will start working from first character
            vendorEditText.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * if a vendor is selected, show only his items in item Names drop down
         */
        vendorEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!TextUtils.isEmpty(adapterView.getAdapter().getItem(i).toString())) {
                    loadItemNames();
                }
            }
        });

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItem(position).toString().equalsIgnoreCase("other")) {
                    customUnitEditText.setVisibility(View.VISIBLE);
                } else {
                    customUnitEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btn_add_report)
    public void addReport() {
        String itemName = itemNameEditText.getText().toString();

        boolean hasItem = false;
        if (!TextUtils.isEmpty(itemName.trim())) {
            if (inventoryDatas != null && inventoryDatas.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventoryDatas) {
                    if (itemName.equalsIgnoreCase(inventoryData.item_name)) {
                        hasItem = true;
                    }
                    if (hasItem) {
                        break;
                    }
                }
            }

            if (!hasItem) {
                Utils.showToast("Enter Valid Item Name", mContext);
                return;
            }
        } else {
            Utils.showToast("Enter Item Name", mContext);
            return;
        }

        String vendor = vendorEditText.getText().toString();

        boolean hasVendor = false;
        if (!TextUtils.isEmpty(vendor.trim())) {
            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    if (vendor.equalsIgnoreCase(vendorData.name)) {
                        hasVendor = true;
                    }
                    if (hasVendor) {
                        break;
                    }
                }
            }

            if (!hasVendor) {
                Utils.showToast("Enter Valid Vendor", mContext);
                return;
            }
        } else {
            Utils.showToast("Enter Vendor", mContext);
            return;
        }

        String date = purchaseDate_EditText.getText().toString();

        if (TextUtils.isEmpty(date.trim())) {
            Utils.showToast("Enter Purchase Date", mContext);
            return;
        }

        String cost = costEditText.getText().toString();

        if (TextUtils.isEmpty(cost.trim())) {
            Utils.showToast("Enter Cost", mContext);
            return;
        }

        String qty = qtyEditText.getText().toString();

        if (TextUtils.isEmpty(qty.trim())) {
            Utils.showToast("Enter Quantity", mContext);
            return;
        }

        String discount = discountEditText.getText().toString();

        if (TextUtils.isEmpty(discount.trim())) {
            Utils.showToast("Enter Discount Percentage", mContext);
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(unit.trim())) {
            Utils.showToast("Select Unit", mContext);
            return;
        }

        if (unit.equalsIgnoreCase("Other")) {
            unit = customUnitEditText.getText().toString();
            if (TextUtils.isEmpty(unit.trim())) {
                Utils.showToast("Enter Custom Unit", mContext);
                return;
            }
        }
        double costDouble = 0.0;
        double qtyDouble = 0.0;

        try {
            costDouble = Double.parseDouble(cost);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showToast("Enter Valid Cost", mContext);
            return;
        }

        try {
            qtyDouble = Double.parseDouble(qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showToast("Enter Valid Quantity", mContext);
            return;
        }

        GeneratedReport.ReportData reportData = new GeneratedReport().new ReportData();
        reportData.itemName = itemName;
        reportData.vendor = vendor;
        reportData.date = date;
        reportData.cost = cost;
        reportData.quantity = qty + " " + unit;
        reportData.discount = discount;
        reportData.total = String.valueOf((qtyDouble * costDouble));
        reportData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        reportData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        reportData.admin_id = adminId;

        reportsAdapter.addReport(reportData);

        itemNameEditText.setText("");
        vendorEditText.setText("");
        purchaseDate_EditText.setText("");
        costEditText.setText("");
        qtyEditText.setText("");
        discountEditText.setText("");


    }


    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
