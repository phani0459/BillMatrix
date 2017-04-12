package com.billmatrix.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.billmatrix.adapters.TransportAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Transport;
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
public class TransportFragment extends Fragment implements OnItemClickListener {


    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.sp_cust_names)
    public Spinner customersSpinner;
    @BindView(R.id.sp_bill_numbers)
    public Spinner billsSpinner;
    @BindView(R.id.sp_transport_name)
    public Spinner transportSpinner;
    @BindView(R.id.transportList)
    public RecyclerView transportsRecyclerView;
    @BindView(R.id.et_transportName)
    public EditText transportNameEditText;
    @BindView(R.id.et_transportContact)
    public EditText contactEditText;
    @BindView(R.id.et_transportLocation)
    public EditText locationEditText;
    @BindView(R.id.btn_addTransport)
    public Button addTranportButton;


    private ArrayAdapter<String> billsSpinnerAdapter;
    private ArrayAdapter<String> transportsSpinnerAdapter;
    private TransportAdapter transportAdapter;
    private Transport.TransportData selectedTransporttoEdit;
    private String adminId;
    public boolean isEditing;
    private boolean isTransportAdded;

    public TransportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transport, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        ArrayList<String> bills = new ArrayList<>();
        ArrayList<String> transports = new ArrayList<>();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        ArrayList<String> customerNames = new ArrayList<>();

        customerNames.add("SELECT CUSTOMER");
        bills.add("BILL NUMBER");
        transports.add("TRANSPORT NAME");

        contactEditText.setFilters(Utils.getInputFilter(10));

        ArrayList<Customer.CustomerData> dbCustomers = billMatrixDaoImpl.getCustomers(adminId);

        if (dbCustomers != null && dbCustomers.size() > 0) {
            for (Customer.CustomerData customer : dbCustomers) {
                /**
                 * Show only Customers who are active
                 */
                if (customer.status.equalsIgnoreCase("1")) {
                    customerNames.add(customer.username.toUpperCase());
                }
            }
        }

        ArrayAdapter<String> customerSpinnerAdapter = Utils.loadSpinner(customersSpinner, mContext, customerNames);
        billsSpinnerAdapter = Utils.loadSpinner(billsSpinner, mContext, bills);

        transportsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        List<Transport.TransportData> transportDatas = new ArrayList<>();

        transportAdapter = new TransportAdapter(transportDatas, this);
        transportsRecyclerView.setAdapter(transportAdapter);

        transportDatas = billMatrixDaoImpl.getTransports();

        if (transportDatas != null && transportDatas.size() > 0) {
            for (Transport.TransportData transportData : transportDatas) {
                if (!transportData.status.equalsIgnoreCase("-1")) {
                    transportAdapter.addTransport(transportData);
                    transports.add(transportData.transportName);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
//                    getCustomersFromServer(adminId);
                }
            }
        }

        transportsSpinnerAdapter = Utils.loadSpinner(transportSpinner, mContext, transports);

        return v;
    }

    @OnClick(R.id.btn_addTransport)
    public void addTransport(View view) {
        Utils.hideSoftKeyboard(transportNameEditText);

        String name = transportNameEditText.getText().toString();

        if (TextUtils.isEmpty(name.trim())) {
            Utils.showToast("Enter Transport Name", mContext);
            return;
        }

        String phone = contactEditText.getText().toString();

        if (TextUtils.isEmpty(phone.trim())) {
            Utils.showToast("Enter Phone Number", mContext);
            return;
        }

        String location = locationEditText.getText().toString();

        if (TextUtils.isEmpty(location.trim())) {
            Utils.showToast("Enter Location", mContext);
            return;
        }

        Transport.TransportData transportData = new Transport().new TransportData();
        Transport.TransportData transportFromServer = new Transport().new TransportData();

        if (addTranportButton.getText().toString().equalsIgnoreCase("ADD")) {
            transportData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            transportData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedTransporttoEdit != null) {
                transportData.id = selectedTransporttoEdit.id;
                transportData.create_date = selectedTransporttoEdit.create_date;
                transportData.add_update = selectedTransporttoEdit.add_update;

                if (TextUtils.isEmpty(selectedTransporttoEdit.add_update)) {
                    transportData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedTransporttoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    transportData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        transportData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        transportData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());

        transportData.location = location;
        transportData.phone = phone;
        transportData.transportName = name;
        transportData.admin_id = adminId;
        transportData.status = "1";

        long transportAdded = billMatrixDaoImpl.addTransport(transportData);

        if (transportAdded != -1) {

            transportNameEditText.setText("");
            contactEditText.setText("");
            locationEditText.setText("");


            if (addTranportButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
//                    transportFromServer = ServerUtils.addCustomertoServer(transportData, mContext, adminId, billMatrixDaoImpl);
                    transportFromServer = transportData;
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TRANSPORT_EDITED_OFFLINE, true).apply();
                    transportFromServer = transportData;
                    Utils.showToast("Transport Added successfully", mContext);
                }
            } else {
                if (selectedTransporttoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
//                        transportFromServer = ServerUtils.updateCustomertoServer(transportData, mContext, billMatrixDaoImpl);
                        transportFromServer = transportData;
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TRANSPORT_EDITED_OFFLINE, true).apply();
                        transportFromServer = transportData;
                        Utils.showToast("Transport Updated successfully", mContext);
                    }
                }
            }

            transportAdapter.addTransport(transportFromServer);
            transportsRecyclerView.smoothScrollToPosition(transportAdapter.getItemCount());
            transportsSpinnerAdapter.add(transportFromServer.transportName);

            addTranportButton.setText(getString(R.string.add));
            isEditing = false;
            isTransportAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;

        } else {
            Utils.showToast("Transport Mobile Number must be unique", mContext);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        customersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCustomer = adapterView.getSelectedItem().toString();
                ArrayList<String> bills = new ArrayList<String>();
                bills.add("BILL NUMBER");
                ArrayList<String> strings = billMatrixDaoImpl.getBillNumbers(selectedCustomer);
                if (strings != null) {
                    bills.addAll(strings);
                }

                billsSpinnerAdapter.clear();
                billsSpinnerAdapter.addAll(bills);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                if (isEditing) {
                    Utils.showToast("Save present editing Transport before deleting other Transport", mContext);
                    return;
                }
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Transport", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Transport.TransportData selectedTransport = transportAdapter.getItem(position);

                        if (selectedTransport.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateTransport(DBConstants.STATUS, "-1", selectedTransport.phone);
                        } else {
                            billMatrixDaoImpl.deleteTransport(DBConstants.PHONE, selectedTransport.phone);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedTransport.id)) {
//                                ServerUtils.deleteCustomerfromServer(selectedTransport, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_TRANSPORT_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Transport Deleted successfully", mContext);
                        }
                        transportAdapter.deleteTransport(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addTranportButton.setText(getString(R.string.save));

                    if (position != -1) {
                        selectedTransporttoEdit = transportAdapter.getItem(position);
                    }

                    if (selectedTransporttoEdit != null) {
                        transportNameEditText.setText(selectedTransporttoEdit.transportName);
                        contactEditText.setText(selectedTransporttoEdit.phone);
                        locationEditText.setText(selectedTransporttoEdit.location);
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deleteTransport(DBConstants.PHONE, transportAdapter.getItem(position).phone);
                        transportAdapter.deleteTransport(position);
                    }
                } else {
                    Utils.showToast("Save present editing Transport before editing other Transport", mContext);
                }
                break;
        }
    }
}
