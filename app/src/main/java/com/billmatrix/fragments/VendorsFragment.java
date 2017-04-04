package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.VendorsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
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
public class VendorsFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {


    private static final String TAG = VendorsFragment.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.et_vendorAdd)
    public EditText vendorAdd_EditText;
    @BindView(R.id.et_vendorEmail)
    public EditText vendorEmail_EditText;
    @BindView(R.id.et_vendorName)
    public EditText vendorName_EditText;
    @BindView(R.id.et_vendorPhone)
    public EditText vendorPhone_EditText;
    @BindView(R.id.et_vendorSince)
    public EditText vendorSince_EditText;
    @BindView(R.id.vendorsList)
    public RecyclerView vendorsRecyclerView;
    private VendorsAdapter vendorsAdapter;
    @BindView(R.id.btn_addVendor)
    public Button addVendorButton;
    @BindView(R.id.tv_vendros_no_results)
    public TextView noResultsTextView;

    BillMatrixDaoImpl billMatrixDaoImpl;
    private Vendor.VendorData selectedVendortoEdit;
    private String adminId;
    public boolean isEditing;

    public VendorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vendors, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        if (savedInstanceState != null) {
            selectedVendortoEdit = (Vendor.VendorData) savedInstanceState.getSerializable("EDIT_VENDOR");
            if (selectedVendortoEdit != null) {
                isEditing = false;
                onItemClick(2, -1);
            }
        }

        vendorsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Vendor.VendorData> vendors = new ArrayList<>();

        vendorsAdapter = new VendorsAdapter(vendors, this);
        vendorsRecyclerView.setAdapter(vendorsAdapter);
        vendorSince_EditText.setInputType(InputType.TYPE_NULL);

        vendorPhone_EditText.setFilters(Utils.getInputFilter(10));

        vendorSince_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, vendorSince_EditText, true, false);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        vendors = billMatrixDaoImpl.getVendors();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                if (!vendorData.status.equalsIgnoreCase("-1")) {
                    vendorsAdapter.addVendor(vendorData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getVendorsFromServer(adminId);
                }
            }
        }

        return v;
    }

    public void onBackPressed() {
        if (addVendorButton.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addVendor();
                    if (isVendroAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    private void getVendorsFromServer(String adminId) {
        Log.e(TAG, "getVendorsFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getVendorsFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                if (!vendorData.status.equalsIgnoreCase("-1")) {
                    vendorsAdapter.addVendor(vendorData);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing && selectedVendortoEdit != null) {
            outState.putSerializable("EDIT_VENDOR", selectedVendortoEdit);
        }
    }

    private boolean isVendroAdded;

    @OnClick(R.id.btn_addVendor)
    public void addVendor() {
        Utils.hideSoftKeyboard(vendorAdd_EditText);
        isVendroAdded = false;

        Vendor.VendorData vendorData = new Vendor().new VendorData();
        Vendor.VendorData vendorFromServer = new Vendor().new VendorData();
        String vendorEmail = vendorEmail_EditText.getText().toString();
        String vendorAdd = vendorAdd_EditText.getText().toString();
        String vendorName = vendorName_EditText.getText().toString();
        String vendorMob = vendorPhone_EditText.getText().toString();
        String vendorSince = vendorSince_EditText.getText().toString();

        if (TextUtils.isEmpty(vendorName.trim())) {
            Utils.showToast("Enter Vendor Name", mContext);
            return;
        }

        if (TextUtils.isEmpty(vendorAdd.trim())) {
            Utils.showToast("Enter Vendor Address", mContext);
            return;
        }

        if (TextUtils.isEmpty(vendorMob.trim())) {
            Utils.showToast("Enter Vendor Mobile Number", mContext);
            return;
        }

        if (!Utils.isPhoneValid(vendorMob)) {
            Utils.showToast("Enter Valid Vendor Mobile Number", mContext);
            return;
        }

        if (TextUtils.isEmpty(vendorSince.trim())) {
            Utils.showToast("Enter Vendor Since", mContext);
            return;
        }

        if (!TextUtils.isEmpty(vendorEmail.trim()) && !Utils.isEmailValid(vendorEmail)) {
            Utils.showToast("Enter Valid Vendor Email", mContext);
            return;
        }

        if (addVendorButton.getText().toString().equalsIgnoreCase("ADD")) {
            vendorData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            vendorData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedVendortoEdit != null) {
                vendorData.id = selectedVendortoEdit.id;
                vendorData.create_date = selectedVendortoEdit.create_date;
                vendorData.add_update = selectedVendortoEdit.add_update;

                if (TextUtils.isEmpty(selectedVendortoEdit.add_update)) {
                    vendorData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedVendortoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    vendorData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        vendorData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        vendorData.email = vendorEmail;
        vendorData.name = vendorName;
        vendorData.address = vendorAdd;
        vendorData.phone = vendorMob;
        vendorData.since = vendorSince;
        vendorData.admin_id = adminId;
        vendorData.status = "1";

        long vendorAdded = billMatrixDaoImpl.addVendor(vendorData);

        if (vendorAdded != -1) {
            /**
             * reset all edit texts
             */
            vendorEmail_EditText.setText("");
            vendorSince_EditText.setText("");
            vendorPhone_EditText.setText("");
            vendorName_EditText.setText("");
            vendorAdd_EditText.setText("");

            if (addVendorButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    vendorFromServer = ServerUtils.addVendortoServer(vendorData, mContext, adminId, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_VENDORS_EDITED_OFFLINE, true).apply();
                    vendorFromServer = vendorData;
                    Utils.showToast("Vendor Added successfully", mContext);
                }
            } else {
                if (selectedVendortoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        vendorFromServer = ServerUtils.updateVendortoServer(vendorData, mContext, billMatrixDaoImpl);
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_VENDORS_EDITED_OFFLINE, true).apply();
                        vendorFromServer = vendorData;
                        Utils.showToast("Vendor Updated successfully", mContext);
                    }

                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, true).apply();
                    billMatrixDaoImpl.updateInventoryOffline(DBConstants.ADD_UPDATE, Constants.UPDATE_OFFLINE, !TextUtils.isEmpty(selectedVendortoEdit.id) ? selectedVendortoEdit.id : selectedVendortoEdit.name);
                    billMatrixDaoImpl.updateVendorName(DBConstants.INVENTORY_TABLE, DBConstants.VENDOR_NAME, vendorFromServer.name, selectedVendortoEdit.name);
                }
            }

            Log.e(TAG, "vendorFromServer: " + vendorFromServer.add_update );
            vendorsAdapter.addVendor(vendorFromServer);
            vendorsRecyclerView.smoothScrollToPosition(vendorsAdapter.getItemCount());
            isEditing = false;
            addVendorButton.setText(getString(R.string.add));
            ((BaseTabActivity) mContext).ifTabCanChange = true;
            isVendroAdded = true;
        } else {
            Utils.showToast("Vendor Phone must be unique", mContext);
        }
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query);
        if (query.length() > 0) {
            query = query.toLowerCase();
            noResultsTextView.setVisibility(View.GONE);
            vendorsAdapter.removeAllVendors();

            boolean noVendors = false;
            ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();

            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    if (vendorData.name.toLowerCase().contains(query) || ("V" + vendorData.id).toLowerCase().contains(query)) {
                        vendorsAdapter.addVendor(vendorData);
                        noVendors = true;
                    }
                }
            }

            if (!noVendors) {
                noResultsTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");

        noResultsTextView.setVisibility(View.GONE);
        Utils.hideSoftKeyboard(vendorAdd_EditText);
        vendorsAdapter.removeAllVendors();

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                vendorsAdapter.addVendor(vendorData);
            }
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Vendor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Vendor.VendorData selectedVendor = vendorsAdapter.getItem(position);
                        if (selectedVendor.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateVendor(DBConstants.STATUS, "-1", selectedVendor.phone);
                        } else {
                            billMatrixDaoImpl.deleteVendor(selectedVendor.phone);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedVendor.id)) {
                                ServerUtils.deleteVendorfromServer(selectedVendor, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_VENDORS_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Vendor Deleted successfully", mContext);
                        }
                        vendorsAdapter.deleteVendor(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addVendorButton.setText(getString(R.string.save));
                    if (position != -1) {
                        selectedVendortoEdit = vendorsAdapter.getItem(position);
                    }
                    if (selectedVendortoEdit != null) {
                        vendorName_EditText.setText(selectedVendortoEdit.name);
                        vendorSince_EditText.setText(selectedVendortoEdit.since);
                        vendorAdd_EditText.setText(selectedVendortoEdit.address);
                        vendorPhone_EditText.setText(selectedVendortoEdit.phone);
                        vendorEmail_EditText.setText(selectedVendortoEdit.email);
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deleteVendor(vendorsAdapter.getItem(position).phone);
                        vendorsAdapter.deleteVendor(position);
                    }
                } else {
                    Utils.showToast("Save present editing Vendor before editing other vendor", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
