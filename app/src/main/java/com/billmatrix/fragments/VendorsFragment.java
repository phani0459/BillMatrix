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
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.VendorsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Vendor;
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
public class VendorsFragment extends Fragment implements VendorsAdapter.onClickListener {


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

    public VendorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vendors, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        vendorsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Vendor.VendorData> vendors = new ArrayList<>();

        vendorsAdapter = new VendorsAdapter(vendors, this);
        vendorsRecyclerView.setAdapter(vendorsAdapter);
        vendorSince_EditText.setInputType(InputType.TYPE_NULL);

        vendorSince_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, vendorSince_EditText);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        vendors = billMatrixDaoImpl.getVendors();
        String loginId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                vendorsAdapter.addVendor(vendorData);
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(loginId)) {
//                    getEmployeesFromServer(loginId);
                }
            }
        }

        return v;
    }

    BillMatrixDaoImpl billMatrixDaoImpl;

    @OnClick(R.id.btn_addVendor)
    public void addVendor() {
        Utils.hideSoftKeyboard(vendorAdd_EditText);

        Vendor.VendorData vendorData = new Vendor().new VendorData();
        String vendorEmail = vendorEmail_EditText.getText().toString();
        String vendorAdd = vendorAdd_EditText.getText().toString();
        String vendorName = vendorName_EditText.getText().toString();
        String vendorMob = vendorPhone_EditText.getText().toString();
        String vendorSince = vendorSince_EditText.getText().toString();

        if (!TextUtils.isEmpty(vendorName)) {
            if (!TextUtils.isEmpty(vendorEmail)) {
                if (!TextUtils.isEmpty(vendorAdd)) {
                    if (!TextUtils.isEmpty(vendorMob)) {
                        if (!TextUtils.isEmpty(vendorSince)) {
                            vendorData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            vendorData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            vendorData.email = vendorEmail;
                            vendorData.name = vendorName;
                            vendorData.address = vendorAdd;
                            vendorData.phone = vendorMob;
                            vendorData.since = vendorSince;

                            long vendorAdded = billMatrixDaoImpl.addVendor(vendorData);

                            if (vendorAdded != -1) {
                                vendorsAdapter.addVendor(vendorData);
                                vendorsRecyclerView.smoothScrollToPosition(vendorsAdapter.getItemCount());

                                /**
                                 * reset all edit texts
                                 */
                                vendorEmail_EditText.setText("");
                                vendorSince_EditText.setText("");
                                vendorPhone_EditText.setText("");
                                vendorName_EditText.setText("");
                                vendorAdd_EditText.setText("");
                            } else {
                                ((BaseTabActivity) mContext).showToast("Vendor Email / Phone must be unique");
                            }
                        } else {
                            ((BaseTabActivity) mContext).showToast("Enter Vendor Since");
                        }
                    } else {
                        ((BaseTabActivity) mContext).showToast("Enter Vendor Mobile Number");
                    }
                } else {
                    ((BaseTabActivity) mContext).showToast("Enter Vendor Address");
                }
            } else {
                ((BaseTabActivity) mContext).showToast("Enter Vendor Email");
            }
        } else {
            ((BaseTabActivity) mContext).showToast("Enter Vendor Name");
        }
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query);
        if (query.length() > 0) {
            vendorsAdapter.removeAllVendors();

            ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();

            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    if (vendorData.name.contains(query)) {
                        vendorsAdapter.addVendor(vendorData);
                    }
                }
            }
        }
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");

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
                        billMatrixDaoImpl.deleteVendor(vendorsAdapter.getItem(position).phone);
                        vendorsAdapter.deleteVendor(position);
                    }
                });
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
