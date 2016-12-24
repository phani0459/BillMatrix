package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayOutsFragment extends Fragment {

    @BindView(R.id.sp_pay_outs_vendor)
    public Spinner vendorSpinner;
    @BindView(R.id.sp_pay_outs_payment)
    public Spinner modePaymentSpinner;
    @BindView(R.id.et_payouts_date)
    public EditText dateEditText;
    @BindView(R.id.et_payment_mode)
    public EditText otherPaymentEditText;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    public PayOutsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_outs, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }

        }
        Utils.loadSpinner(vendorSpinner, mContext, strings);
        Utils.loadSpinner(modePaymentSpinner, mContext, R.array.mode_of_pay_array);

        dateEditText.setInputType(InputType.TYPE_NULL);

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, true);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        modePaymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getAdapter().getItem(position).toString();
                if (selectedMode.equalsIgnoreCase("cash")) {
                    otherPaymentEditText.setVisibility(View.GONE);
                } else {
                    otherPaymentEditText.setVisibility(View.VISIBLE);
                    if (selectedMode.equalsIgnoreCase("card")) {
                        otherPaymentEditText.setHint("Last 4 digits of card");
                    } else if (selectedMode.equalsIgnoreCase("cheque")) {
                        otherPaymentEditText.setHint("Cheque No.");
                    } else if (selectedMode.equalsIgnoreCase("other")) {
                        otherPaymentEditText.setHint("PayIn Mode");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

}
