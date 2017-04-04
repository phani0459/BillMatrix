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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.text.ParseException;

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

    private Context mContext;
    private boolean isSalesTab;

    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        isSalesTab = false;

        String selectedTab = getArguments().getString("selectedTab");

        if (selectedTab != null && selectedTab.equalsIgnoreCase("SALES")) {
            isSalesTab = true;
        }

        if (isSalesTab) {
            Utils.loadSpinner(saleSelectSpinner, mContext, R.array.sale_by_array);
            dateHeadingTextView.setText(getString(R.string.SOLD_DATE));
        } else {
            Utils.loadSpinner(saleSelectSpinner, mContext, R.array.purchase_by_array);
            dateHeadingTextView.setText(getString(R.string.PURCHASE_DATE));
        }
        Utils.loadSpinner(saleItemSpinner, mContext, R.array.employee_status);

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

}
