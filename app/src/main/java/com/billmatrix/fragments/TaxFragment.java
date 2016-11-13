package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.PayIns;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxFragment extends Fragment {

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    @BindView(R.id.sp_tax_type)
    public Spinner taxTypeSpinner;
    @BindView(R.id.et_tax_desc)
    public EditText taxDescEditText;
    @BindView(R.id.et_tax_rate)
    public EditText taxRateEditText;
    @BindView(R.id.taxTypesList)
    public RecyclerView taxTypeRecyclerView;

    public TaxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tax, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        Utils.loadSpinner(taxTypeSpinner, mContext, R.array.tax_type_array);

        return v;
    }

    @OnClick(R.id.btn_addTaxType)
    public void addTaxType() {
        Utils.hideSoftKeyboard(taxDescEditText);

        PayIns.PayInData payInData = new PayIns().new PayInData();
        String taxType = taxTypeSpinner.getSelectedItem().toString();
        String desc = taxDescEditText.getText().toString();
        String rate = taxRateEditText.getText().toString();

        if (!TextUtils.isEmpty(taxType)) {
            if (!TextUtils.isEmpty(rate)) {
                payInData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                payInData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                payInData.customername = taxType;
                payInData.date = desc;
                payInData.amount = rate;

                //long vendorAdded = billMatrixDaoImpl.addVendor(vendorData);

                //if (vendorAdded != -1) {
//                    payInsAdapter.addPayIn(payInData);
//                    payInsRecyclerView.smoothScrollToPosition(payInsAdapter.getItemCount());

                        /*
                         * reset all edit texts
                         */
                taxTypeSpinner.setSelection(0);
                taxRateEditText.setText("");
                taxDescEditText.setText("");
                //} else {
                //   ((BaseTabActivity) mContext).showToast("Vendor Email / Phone must be unique");
                //}
            } else {
                ((BaseTabActivity) mContext).showToast("Enter Tax rate");
            }
        } else {
            ((BaseTabActivity) mContext).showToast("Select Tax Type");
        }
    }

}
