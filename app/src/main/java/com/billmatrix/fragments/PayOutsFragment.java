package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

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

        for (Vendor.VendorData vendorData: vendors) {
            strings.add(vendorData.name);
        }

        Utils.loadSpinner(vendorSpinner, mContext, strings);
        Utils.loadSpinner(modePaymentSpinner, mContext, R.array.employee_status);

        return v;
    }

}
