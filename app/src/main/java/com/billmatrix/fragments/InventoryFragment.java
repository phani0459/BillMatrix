package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.adapters.InventoryAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment implements OnItemClickListener {

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    @BindView(R.id.inventoryList)
    public RecyclerView inventoryRecyclerView;
    public InventoryAdapter inventoryAdapter;
    @BindView(R.id.sp_inven_unit)
    public Spinner unitSpinner;
    @BindView(R.id.et_inven_date)
    public EditText dateEditText;
    @BindView(R.id.sp_inven_vendor)
    public Spinner vendorSpinner;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        Utils.loadSpinner(unitSpinner, mContext, R.array.units_array);

        dateEditText.setInputType(InputType.TYPE_NULL);

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }

        }
        Utils.loadSpinner(vendorSpinner, mContext, strings);

        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Inventory.InventoryData> inventoryDatas = new ArrayList<>();

        inventoryAdapter = new InventoryAdapter(inventoryDatas, this);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        return v;
    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
