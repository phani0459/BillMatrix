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
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendorsFragment extends Fragment implements OnItemClickListener {


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
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, vendorSince_EditText, true);
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

    private void getVendorsFromServer(String loginId) {
        Log.e(TAG, "getVendorsFromServer: ");
        Call<Vendor> call = Utils.getBillMatrixAPI(mContext).getAdminVendors(loginId);

        call.enqueue(new Callback<Vendor>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    Vendor vendor = response.body();
                    if (vendor.status == 200 && vendor.Vendordata.equalsIgnoreCase("success")) {
                        for (Vendor.VendorData vendorData : vendor.data) {
                            billMatrixDaoImpl.addVendor(vendorData);
                            vendorsAdapter.addVendor(vendorData);
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    private boolean isVendroAdded;

    @OnClick(R.id.btn_addVendor)
    public void addVendor() {
        Utils.hideSoftKeyboard(vendorAdd_EditText);
        isVendroAdded = false;

        Vendor.VendorData vendorData = new Vendor().new VendorData();
        String vendorEmail = vendorEmail_EditText.getText().toString();
        String vendorAdd = vendorAdd_EditText.getText().toString();
        String vendorName = vendorName_EditText.getText().toString();
        String vendorMob = vendorPhone_EditText.getText().toString();
        String vendorSince = vendorSince_EditText.getText().toString();

        if (TextUtils.isEmpty(vendorName)) {
            ((BaseTabActivity) mContext).showToast("Enter Vendor Name");
            return;
        }

        if (TextUtils.isEmpty(vendorAdd)) {
            ((BaseTabActivity) mContext).showToast("Enter Vendor Address");
            return;
        }

        if (TextUtils.isEmpty(vendorMob)) {
            ((BaseTabActivity) mContext).showToast("Enter Vendor Mobile Number");
            return;
        }

        if (!Utils.isPhoneValid(vendorMob)) {
            ((BaseTabActivity) mContext).showToast("Enter Valid Vendor Mobile Number");
            return;
        }

        if (TextUtils.isEmpty(vendorSince)) {
            ((BaseTabActivity) mContext).showToast("Enter Vendor Since");
            return;
        }

        if (!TextUtils.isEmpty(vendorEmail) && !Utils.isEmailValid(vendorEmail)) {
            ((BaseTabActivity) mContext).showToast("Enter Valid Vendor Email");
            return;
        }

        if (addVendorButton.getText().toString().equalsIgnoreCase("ADD")) {
            vendorData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        } else {
            if (selectedVendortoEdit != null) {
                vendorData.id = selectedVendortoEdit.id;
                vendorData.create_date = selectedVendortoEdit.create_date;
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

            if (addVendorButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    addVendortoServer(vendorData);
                } else {
                    ((BaseTabActivity) mContext).showToast("Vendor Added successfully");
                }
            } else {
                if (selectedVendortoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        updateVendortoServer(vendorData);
                    } else {
                        ((BaseTabActivity) mContext).showToast("Vendor Updated successfully");
                    }
                }
            }
            isEditing = false;
            addVendorButton.setText(getString(R.string.add));
            ((BaseTabActivity) mContext).ifTabCanChange = true;
            isVendroAdded = true;
        } else {
            ((BaseTabActivity) mContext).showToast("Vendor Phone must be unique");
        }
    }

    public void deleteVendorfromServer(String vendorId) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteVendor(vendorId);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> employeeStatus = response.body();
                    if (employeeStatus.get("status").equalsIgnoreCase("200")) {
                        if (employeeStatus.get("delete_vendor").equalsIgnoreCase("success")) {
                            ((BaseTabActivity) mContext).showToast("Vendor Deleted successfully");
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    private void addVendortoServer(Vendor.VendorData vendorData) {
        Log.e(TAG, "addVendortoServer: " + vendorData.toString());
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).addVendor(vendorData.name, vendorData.email, vendorData.phone,
                vendorData.since, vendorData.address, vendorData.status, adminId);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> vendorMap = response.body();
                    if (vendorMap.get("status").equalsIgnoreCase("200")) {
                        if (vendorMap.get("create_vendor").equalsIgnoreCase("success")) {
                            ((BaseTabActivity) mContext).showToast("Vendor Added successfully");
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    private void updateVendortoServer(Vendor.VendorData vendorData) {
        Log.e(TAG, "updateVendortoServer: ");
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).updateVendor(vendorData.id, vendorData.email, vendorData.phone,
                vendorData.since, vendorData.status, vendorData.address, vendorData.name);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> vendorMap = response.body();
                    if (vendorMap.get("status").equalsIgnoreCase("200")) {
                        if (vendorMap.containsKey("update_vendor") && vendorMap.get("update_vendor").equalsIgnoreCase("Successfully Updated")) {
                            ((BaseTabActivity) mContext).showToast("Vendor Updated successfully");
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query);
        if (query.length() > 0) {
            noResultsTextView.setVisibility(View.GONE);
            vendorsAdapter.removeAllVendors();

            boolean noVendors = false;
            ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();

            if (vendors != null && vendors.size() > 0) {
                for (Vendor.VendorData vendorData : vendors) {
                    if (vendorData.name.contains(query) || vendorData.id.contains(query)) {
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
                        billMatrixDaoImpl.updateVendor("-1", vendorsAdapter.getItem(position).phone);
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(vendorsAdapter.getItem(position).id)) {
                                deleteVendorfromServer(vendorsAdapter.getItem(position).id);
                            }
                        } else {
                            ((BaseTabActivity) mContext).showToast("Vendor Deleted successfully");
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
                    selectedVendortoEdit = vendorsAdapter.getItem(position);
                    vendorName_EditText.setText(selectedVendortoEdit.name);
                    vendorSince_EditText.setText(selectedVendortoEdit.since);
                    vendorAdd_EditText.setText(selectedVendortoEdit.address);
                    vendorPhone_EditText.setText(selectedVendortoEdit.phone);
                    vendorEmail_EditText.setText(selectedVendortoEdit.email);
                    billMatrixDaoImpl.deleteVendor(vendorsAdapter.getItem(position).phone);
                    vendorsAdapter.deleteVendor(position);
                } else {
                    ((BaseTabActivity) mContext).showToast("Save present editing Vendor before editing other vendor");
                }
                break;
            case 3:
                break;
        }
    }
}
