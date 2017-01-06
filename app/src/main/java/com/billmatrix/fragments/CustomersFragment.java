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
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.ServerUtils;
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

public class CustomersFragment extends Fragment implements OnItemClickListener {

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

        customerContactEditText.setFilters(Utils.getInputFilter(10));

        customersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        customersAdapter = new CustomersAdapter(customers, this);
        customersRecyclerView.setAdapter(customersAdapter);

        customers = billMatrixDaoImpl.getCustomers();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

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
        Call<Customer> call = Utils.getBillMatrixAPI(mContext).getAdminCustomers(adminId);

        call.enqueue(new Callback<Customer>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    Customer customer = response.body();
                    if (customer.status == 200 && customer.Customerdata.equalsIgnoreCase("success")) {
                        for (Customer.CustomerData customerData : customer.data) {
                            billMatrixDaoImpl.addCustomer(customerData);
                            customersAdapter.addCustomer(customerData);
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
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    public boolean isCustomerAdded;

    @OnClick(R.id.btn_addCustomer)
    public void addCustomer() {
        isCustomerAdded = false;
        Utils.hideSoftKeyboard(customerNameEditText);

        Customer.CustomerData customerData = new Customer().new CustomerData();
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
        } else {
            if (selectedCusttoEdit != null) {
                customerData.id = selectedCusttoEdit.id;
                customerData.create_date = selectedCusttoEdit.create_date;
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

            if (addCustomerBtn.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    ServerUtils.addCustomertoServer(customerData, mContext, adminId);
                } else {
                    Utils.showToast("Customer Added successfully", mContext);
                }
            } else {
                if (selectedCusttoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        ServerUtils.updateCustomertoServer(customerData, mContext);
                    } else {
                        Utils.showToast("Customer Updated successfully", mContext);
                    }
                }
            }
            addCustomerBtn.setText(getString(R.string.add));
            isEditing = false;
            isCustomerAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;
        } else {
            Utils.showToast("Customer Mobile Number must be unique", mContext);
        }
    }

    public void deleteCustomerfromServer(String customerID) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).deleteCustomer(customerID);

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
                        if (employeeStatus.get("delete_customer").equalsIgnoreCase("success")) {
                            Utils.showToast("Customer Deleted successfully", mContext);
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

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.updateCustomer("-1", customersAdapter.getItem(position).mobile_number);
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(customersAdapter.getItem(position).id)) {
                                deleteCustomerfromServer(customersAdapter.getItem(position).id);
                            }
                        } else {
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
                    billMatrixDaoImpl.deleteCustomer(customersAdapter.getItem(position).mobile_number);
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
