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
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.CustomersAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomersFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = CustomersFragment.class.getSimpleName();

    @BindView(R.id.sp_cust_status)
    public Spinner custStatusSpinner;
    private Context mContext;
    @BindView(R.id.et_custDate)
    public EditText customerDate_EditText;
    @BindView(R.id.et_customerName)
    public EditText customerNameEditText;
    @BindView(R.id.et_custLocation)
    public EditText customerLocationEditText;
    @BindView(R.id.et_customerContact)
    public EditText customerContactEditText;
    @BindView(R.id.customersList)
    public RecyclerView customersRecyclerView;
    @BindView(R.id.btn_addCustomer)
    public Button addCustomerBtn;

    BillMatrixDaoImpl billMatrixDaoImpl;
    private CustomersAdapter customersAdapter;
    private String adminId;
    private Customer.CustomerData selectedCusttoEdit;
    public boolean isEditing;

    public CustomersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customers, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        Utils.loadSpinner(custStatusSpinner, mContext, R.array.employee_status);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        List<Customer.CustomerData> customers = new ArrayList<>();
        ServerUtils.setIsSync(false);

        customerContactEditText.setFilters(Utils.getInputFilter(10));

        customersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        customersAdapter = new CustomersAdapter(customers, this);
        customersRecyclerView.setAdapter(customersAdapter);

        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customerData : customers) {
                if (!customerData.status.equalsIgnoreCase("-1")) {
                    customersAdapter.addCustomer(customerData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getCustomersFromServer(adminId);
                }
            }
        }

        customerDate_EditText.setInputType(InputType.TYPE_NULL);
        customerDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Utils.hideSoftKeyboard(customerNameEditText);
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, customerDate_EditText, true);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        return v;
    }

    public void onBackPressed() {
        if (addCustomerBtn.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addCustomer();
                    if (isCustomerAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    private void getCustomersFromServer(String adminId) {
        Log.e(TAG, "getCustomersFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getCustomersFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Customer.CustomerData> customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customerData : customers) {
                if (!customerData.status.equalsIgnoreCase("-1")) {
                    customersAdapter.addCustomer(customerData);
                }
            }
        }
    }

    public boolean isCustomerAdded;

    @OnClick(R.id.btn_addCustomer)
    public void addCustomer() {
        isCustomerAdded = false;
        Utils.hideSoftKeyboard(customerNameEditText);

        Customer.CustomerData customerData = new Customer().new CustomerData();
        Customer.CustomerData customerFromServer = new Customer().new CustomerData();
        String customerName = customerNameEditText.getText().toString();
        String customerContact = customerContactEditText.getText().toString();
        String customerDate = customerDate_EditText.getText().toString();
        String customerLocation = customerLocationEditText.getText().toString();
        String customerStatus = custStatusSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(customerName)) {
            Utils.showToast("Enter Customer Name", mContext);
            return;
        }

        if (TextUtils.isEmpty(customerContact)) {
            Utils.showToast("Enter Customer Mobile Number", mContext);
            return;
        }

        if (!Utils.isPhoneValid(customerContact)) {
            Utils.showToast("Enter Valid Customer Mobile Number", mContext);
            return;
        }

        if (TextUtils.isEmpty(customerDate)) {
            Utils.showToast("Select Customer Date", mContext);
            return;
        }

        if (TextUtils.isEmpty(customerLocation)) {
            Utils.showToast("Enter Customer Location", mContext);
            return;
        }

        if (TextUtils.isEmpty(customerStatus)) {
            Utils.showToast("Select Customer Status", mContext);
            return;
        }

        if (addCustomerBtn.getText().toString().equalsIgnoreCase("ADD")) {
            customerData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            customerData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedCusttoEdit != null) {
                customerData.id = selectedCusttoEdit.id;
                customerData.create_date = selectedCusttoEdit.create_date;
                if (selectedCusttoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    customerData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        customerData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        customerData.username = customerName;
        customerData.mobile_number = customerContact;
        customerData.date = customerDate;
        customerData.location = customerLocation;
        customerData.status = customerStatus.equalsIgnoreCase("ACTIVE") ? "1" : "0";
        customerData.admin_id = adminId;

        long customerAdded = billMatrixDaoImpl.addCustomer(customerData);

        if (customerAdded != -1) {
            /**
             * reset all edit texts
             */
            customerNameEditText.setText("");
            customerContactEditText.setText("");
            customerDate_EditText.setText("");
            customerLocationEditText.setText("");
            custStatusSpinner.setSelection(0);

            if (addCustomerBtn.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    customerFromServer = ServerUtils.addCustomertoServer(customerData, mContext, adminId, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, true).apply();
                    customerFromServer = customerData;
                    Utils.showToast("Customer Added successfully", mContext);
                }
            } else {
                if (selectedCusttoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        customerFromServer = ServerUtils.updateCustomertoServer(customerData, mContext, billMatrixDaoImpl);
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, true).apply();
                        customerFromServer = customerData;
                        Utils.showToast("Customer Updated successfully", mContext);
                    }

                    /**
                     * If there are dues for edited customer, and name has been changed, update transactions and pos table
                     */
                    billMatrixDaoImpl.updateCustomerName(DBConstants.POS_ITEMS_TABLE, customerName, selectedCusttoEdit.username);
                    billMatrixDaoImpl.updateCustomerName(DBConstants.CUSTOMER_TRANSACTIONS_TABLE, customerName, selectedCusttoEdit.username);

                }
            }

            customersAdapter.addCustomer(customerFromServer);
            customersRecyclerView.smoothScrollToPosition(customersAdapter.getItemCount());

            addCustomerBtn.setText(getString(R.string.add));
            isEditing = false;
            isCustomerAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;
        } else {
            Utils.showToast("Customer Name / Mobile Number must be unique", mContext);
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Customer.CustomerData selectedCustomer = customersAdapter.getItem(position);

                        /**
                         * If there are any dues for the customer, customer cannot be deleted
                         */
                        if (billMatrixDaoImpl.getCustomerTotalDue(selectedCustomer.username) != 0.0f) {
                            Utils.showToast(selectedCustomer.username + " can not be deleted.\nThere are dues for this customer", mContext);
                            return;
                        }

                        if (selectedCustomer.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateCustomer(DBConstants.STATUS, "-1", selectedCustomer.mobile_number);
                        } else {
                            billMatrixDaoImpl.deleteCustomer(DBConstants.CUSTOMER_CONTACT, selectedCustomer.mobile_number);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedCustomer.id)) {
                                ServerUtils.deleteCustomerfromServer(selectedCustomer, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Customer Deleted successfully", mContext);
                        }
                        customersAdapter.deleteCustomer(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addCustomerBtn.setText(getString(R.string.save));
                    selectedCusttoEdit = customersAdapter.getItem(position);
                    customerNameEditText.setText(selectedCusttoEdit.username);
                    customerDate_EditText.setText(selectedCusttoEdit.date);
                    customerLocationEditText.setText(selectedCusttoEdit.location);
                    customerContactEditText.setText(selectedCusttoEdit.mobile_number);
                    if (selectedCusttoEdit.status.equalsIgnoreCase("ACTIVE") || selectedCusttoEdit.status.equalsIgnoreCase("1")) {
                        custStatusSpinner.setSelection(0);
                    } else {
                        custStatusSpinner.setSelection(1);
                    }
                    billMatrixDaoImpl.deleteCustomer(DBConstants.CUSTOMER_CONTACT, customersAdapter.getItem(position).mobile_number);
                    customersAdapter.deleteCustomer(position);
                } else {
                    Utils.showToast("Save present editing customer before editing other customer", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
