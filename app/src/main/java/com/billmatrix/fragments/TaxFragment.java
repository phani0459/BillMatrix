package com.billmatrix.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.TaxAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Tax;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = TaxFragment.class.getSimpleName();
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
    public TaxAdapter taxAdapter;
    private String adminId;
    public boolean isEditing;

    public TaxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tax, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        Utils.loadSpinner(taxTypeSpinner, mContext, R.array.tax_type_array);

        taxTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Tax.TaxData> taxes = new ArrayList<>();

        taxAdapter = new TaxAdapter(taxes, this);
        taxTypeRecyclerView.setAdapter(taxAdapter);

        taxes = billMatrixDaoImpl.getTax();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (taxes != null && taxes.size() > 0) {
            for (Tax.TaxData taxData : taxes) {
                if (!taxData.status.equalsIgnoreCase("-1")) {
                    taxAdapter.addTax(taxData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
//                    getCustomersFromServer(adminId);
                }
            }
        }

        return v;
    }

    @OnClick(R.id.btn_addTaxType)
    public void addTaxType() {
        Utils.hideSoftKeyboard(taxDescEditText);

        Tax.TaxData taxData = new Tax().new TaxData();
        String taxType = taxTypeSpinner.getSelectedItem().toString();
        String desc = taxDescEditText.getText().toString();
        String rate = taxRateEditText.getText().toString();

        if (TextUtils.isEmpty(taxType) && !taxType.equalsIgnoreCase("select one")) {
            Utils.showToast("Select Tax Type", mContext);
            return;
        }

        if (TextUtils.isEmpty(rate)) {
            Utils.showToast("Enter Tax rate", mContext);
            return;
        }

        taxData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        taxData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        taxData.taxType = taxType;
        taxData.taxDescription = desc;
        taxData.taxRate = rate;
        taxData.status = "1";

        long taxAdded = billMatrixDaoImpl.addTax(taxData);

        if (taxAdded != -1) {
            taxAdapter.addTax(taxData);
            taxTypeRecyclerView.smoothScrollToPosition(taxAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            taxTypeSpinner.setSelection(0);
            taxRateEditText.setText("");
            taxDescEditText.setText("");
            isEditing = false;
        } else {
            Utils.showToast("Tax Type must be unique", mContext);
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Tax Type", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.deleteTax(taxAdapter.getItem(position).taxType);
                        taxAdapter.deleteTax(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                }
                break;
            case 3:
                break;
        }
    }
}
