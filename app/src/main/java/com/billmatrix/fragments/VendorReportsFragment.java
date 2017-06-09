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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Context mContext;

    public VendorReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vendor_reports, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        boolean isVendorsTab = false;

        String selectedTab = getArguments().getString("selectedTab");

        if (selectedTab != null && selectedTab.equalsIgnoreCase("VENDOR")) {
            isVendorsTab = true;
        }

        BillMatrixDaoImpl billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

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

}
