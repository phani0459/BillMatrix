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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.TaxAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Tax;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * A simple {@link Fragment} subclass.
 */
public class TaxFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

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
    @BindView(R.id.et_other_tax)
    public EditText otherTaxEditText;
    @BindView(R.id.btn_addTaxType)
    public Button addTaxButton;

    public TaxAdapter taxAdapter;
    private String adminId;
    public boolean isEditing;
    private Tax.TaxData selectedTaxtoEdit;
    ArrayAdapter<CharSequence> taxSpinnerAdapter;
    private boolean isTaxAdded;

    private static TaxFragment taxFragment;

    public static TaxFragment getInstance() {
        if (taxFragment != null) {
            return taxFragment;
        }

        taxFragment = new TaxFragment();
        return taxFragment;
    }

    public TaxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tax, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        ServerUtils.setIsSync(false);

        if (savedInstanceState != null) {
            selectedTaxtoEdit = (Tax.TaxData) savedInstanceState.getSerializable("EDIT_TAX");
            if (selectedTaxtoEdit != null) {
                Log.e(TAG, "onCreateView: " + selectedTaxtoEdit.tax_type);
                isEditing = false;
                onItemClick(2, -1);
            }
        }

        taxSpinnerAdapter = Utils.loadSpinner(taxTypeSpinner, mContext, R.array.tax_type_array);

        taxTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Tax.TaxData> taxes = new ArrayList<>();

        taxAdapter = new TaxAdapter(taxes, this, mContext);
        taxTypeRecyclerView.setAdapter(taxAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        taxes = billMatrixDaoImpl.getTax();

        if (taxes != null && taxes.size() > 0) {
            for (Tax.TaxData taxData : taxes) {
                if (!taxData.status.equalsIgnoreCase("-1")) {
                    taxAdapter.addTax(taxData, addTaxButton.getText().toString().equalsIgnoreCase("ADD"));
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getTaxesFromServer(adminId);
                }
            }
        }

        return v;
    }

    private void getTaxesFromServer(String adminId) {
        Log.e(TAG, "getTaxesFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getTaxesFromServer(adminId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing && selectedTaxtoEdit != null) {
            Log.e(TAG, "onSaveInstanceState: ");
            outState.putSerializable("EDIT_TAX", selectedTaxtoEdit);
        }
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Tax.TaxData> taxes = billMatrixDaoImpl.getTax();

        if (taxes != null && taxes.size() > 0) {
            for (Tax.TaxData taxData : taxes) {
                if (!taxData.status.equalsIgnoreCase("-1")) {
                    taxAdapter.addTax(taxData, addTaxButton.getText().toString().equalsIgnoreCase("ADD"));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        taxTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItem(position).toString().equalsIgnoreCase("other")) {
                    otherTaxEditText.setVisibility(View.VISIBLE);
                } else {
                    otherTaxEditText.setVisibility(View.GONE);
                    otherTaxEditText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btn_addTaxType)
    public void addTaxType() {
        Utils.hideSoftKeyboard(taxDescEditText);
        isTaxAdded = false;

        Tax.TaxData taxData = new Tax().new TaxData();
        String taxType = taxTypeSpinner.getSelectedItem().toString();
        String desc = taxDescEditText.getText().toString();
        String rate = taxRateEditText.getText().toString();

        if (TextUtils.isEmpty(taxType.trim()) || taxType.equalsIgnoreCase("select one")) {
            Utils.showToast("Select Tax Type", mContext);
            return;
        }

        if (taxType.equalsIgnoreCase("Other")) {
            taxType = otherTaxEditText.getText().toString();
            if (TextUtils.isEmpty(taxType.trim())) {
                Utils.showToast("Enter Tax Type", mContext);
                return;
            }
        }

        if (TextUtils.isEmpty(rate.trim())) {
            Utils.showToast("Enter Tax rate", mContext);
            return;
        }

        if (addTaxButton.getText().toString().equalsIgnoreCase("ADD")) {
            taxData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            taxData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedTaxtoEdit != null) {
                taxData.id = selectedTaxtoEdit.id;
                taxData.create_date = selectedTaxtoEdit.create_date;
                taxData.add_update = selectedTaxtoEdit.add_update;

                if (TextUtils.isEmpty(selectedTaxtoEdit.add_update)) {
                    taxData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedTaxtoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    taxData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        taxData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        taxData.tax_type = taxType;
        taxData.tax_description = desc;
        taxData.tax_rate = rate;
        taxData.admin_id = adminId;
        taxData.status = "1";

        long taxAdded = billMatrixDaoImpl.addTax(taxData);

        if (taxAdded != -1) {
            taxAdapter.addTax(taxData, addTaxButton.getText().toString().equalsIgnoreCase("ADD"));
            taxTypeRecyclerView.smoothScrollToPosition(taxAdapter.getItemCount());

            /*
             * reset all edit texts
             */
            taxTypeSpinner.setSelection(0);
            taxRateEditText.setText("");
            taxDescEditText.setText("");

            if (addTaxButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    ServerUtils.addTaxtoServer(taxData, mContext, billMatrixDaoImpl, adminId);
                } else {
                    /*
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TAXES_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Tax Added successfully", mContext);
                }
            } else {
                if (selectedTaxtoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        ServerUtils.updateTaxtoServer(taxData, mContext, adminId, billMatrixDaoImpl);
                    } else {
                        /*
                         * To show pending sync Icon in database page
                         */
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TAXES_EDITED_OFFLINE, true).apply();
                        Utils.showToast("Tax Updated successfully", mContext);
                    }
                }
            }

            addTaxButton.setText(getString(R.string.add));
            isEditing = false;
            isTaxAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;

        } else {
            Utils.showToast("Tax Type must be unique", mContext);
        }
    }

    public void onBackPressed() {
        if (addTaxButton.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addTaxType();
                    if (isTaxAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                if (isEditing) {
                    Utils.showToast("Save present editing Tax before deleting other Tax", mContext);
                    return;
                }
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Tax Type", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.updateTax("-1", taxAdapter.getItem(position).tax_type);
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(taxAdapter.getItem(position).id)) {
                                ServerUtils.deleteTaxfromServer(taxAdapter.getItem(position), mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /*
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TAXES_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Tax Deleted successfully", mContext);
                        }
                        taxAdapter.deleteTax(position, false);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addTaxButton.setText(getString(R.string.save));

                    if (position != -1) {
                        selectedTaxtoEdit = taxAdapter.getItem(position);
                    }

                    if (selectedTaxtoEdit != null) {
                        Log.e(TAG, "onItemClick: " + selectedTaxtoEdit.tax_type);
                        try {
                            taxTypeSpinner.setSelection(4);
                            otherTaxEditText.setVisibility(View.VISIBLE);
                            otherTaxEditText.setText(selectedTaxtoEdit.tax_type);

                            String[] taxTypeArray = getResources().getStringArray(R.array.tax_type_array);
                            for (String aTaxTypeString : taxTypeArray) {
                                if (aTaxTypeString.equalsIgnoreCase(selectedTaxtoEdit.tax_type)) {
                                    int taxTypeSelectedPosition = taxSpinnerAdapter.getPosition(selectedTaxtoEdit.tax_type);
                                    taxTypeSpinner.setSelection(taxTypeSelectedPosition);
                                    otherTaxEditText.setVisibility(View.GONE);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            taxTypeSpinner.setSelection(0);
                        }
                        taxDescEditText.setText(selectedTaxtoEdit.tax_description);
                        taxRateEditText.setText(selectedTaxtoEdit.tax_rate);
                        Log.e(TAG, "onItemClick: " + selectedTaxtoEdit.tax_description);
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deleteTax(taxAdapter.getItem(position).tax_type);
                        taxAdapter.deleteTax(position, true);
                    }
                } else {
                    Utils.showToast("Save present editing tax before editing other tax", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
