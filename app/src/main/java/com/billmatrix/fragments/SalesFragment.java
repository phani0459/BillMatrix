package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.utils.Utils;

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

    private Context mContext;

    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        Utils.loadSpinner(saleSelectSpinner, mContext, R.array.sale_by_array);
        Utils.loadSpinner(saleItemSpinner, mContext, R.array.employee_status);

        fromDate_EditText.setInputType(InputType.TYPE_NULL);
        toDate_EditText.setInputType(InputType.TYPE_NULL);

        fromDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, fromDate_EditText, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        toDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, toDate_EditText, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        return v;
    }

}
