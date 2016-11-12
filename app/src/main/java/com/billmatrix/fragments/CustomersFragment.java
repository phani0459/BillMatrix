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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.CustomersAdapter;
import com.billmatrix.adapters.VendorsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Customer;
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
public class CustomersFragment extends Fragment implements CustomersAdapter.OnItemClickListener {

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

    BillMatrixDaoImpl billMatrixDaoImpl;
    private CustomersAdapter customersAdapter;
    private String adminId;

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

        customersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        customersAdapter = new CustomersAdapter(customers, this);
        customersRecyclerView.setAdapter(customersAdapter);

        customers = billMatrixDaoImpl.getCustomers();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customerData : customers) {
                customersAdapter.addCustomer(customerData);
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
//                    getEmployeesFromServer(loginId);
                }
            }
        }

        customerDate_EditText.setInputType(InputType.TYPE_NULL);
        customerDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, customerDate_EditText);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        return v;
    }

    @OnClick(R.id.btn_addCustomer)
    public void addCustomer() {
        Utils.hideSoftKeyboard(customerNameEditText);

        Customer.CustomerData customerData = new Customer().new CustomerData();
        String customerName = customerNameEditText.getText().toString();
        String customerContact = customerContactEditText.getText().toString();
        String customerDate = customerDate_EditText.getText().toString();
        String customerLocation = customerLocationEditText.getText().toString();
        String customerStatus = custStatusSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(customerName)) {
            if (!TextUtils.isEmpty(customerContact)) {
                if (!TextUtils.isEmpty(customerDate)) {
                    if (!TextUtils.isEmpty(customerLocation)) {
                        if (!TextUtils.isEmpty(customerStatus)) {
                            customerData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            customerData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            customerData.username = customerName;
                            customerData.mobile_number = customerContact;
                            customerData.date = customerDate;
                            customerData.location = customerLocation;
                            customerData.status = customerStatus;
                            customerData.admin_id = adminId;

                            long vendorAdded = billMatrixDaoImpl.addCustomer(customerData);

                            if (vendorAdded != -1) {
                                customersAdapter.addCustomer(customerData);
                                customersRecyclerView.smoothScrollToPosition(customersAdapter.getItemCount());

                                /**
                                 * reset all edit texts
                                 */
                                customerNameEditText.setText("");
                                customerContactEditText.setText("");
                                customerDate_EditText.setText("");
                                customerLocationEditText.setText("");
                                custStatusSpinner.setSelection(0);
                            } else {
                                ((BaseTabActivity) mContext).showToast("Customer Mobile Number must be unique");
                            }
                        } else {
                            ((BaseTabActivity) mContext).showToast("Select Customer Status");
                        }
                    } else {
                        ((BaseTabActivity) mContext).showToast("Enter Customer Location");
                    }
                } else {
                    ((BaseTabActivity) mContext).showToast("Select Customer Date");
                }
            } else {
                ((BaseTabActivity) mContext).showToast("Enter Customer Mobile Number");
            }
        } else {
            ((BaseTabActivity) mContext).showToast("Enter Customer Name");
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.deleteCustomer(customersAdapter.getItem(position).mobile_number);
                        customersAdapter.deleteCustomer(position);
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
