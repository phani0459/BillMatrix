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
import com.billmatrix.utils.Utils;

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

    public PayOutsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_outs, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        Utils.loadSpinner(vendorSpinner, mContext, R.array.employee_status);
        Utils.loadSpinner(modePaymentSpinner, mContext, R.array.employee_status);

        return v;
    }

}
