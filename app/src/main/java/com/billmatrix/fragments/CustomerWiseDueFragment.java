package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.adapters.DuesAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Transaction;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerWiseDueFragment extends Fragment implements OnItemClickListener {

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.atv_select_customer_due)
    public AutoCompleteTextView selectCustomerAutoCompleteTV;
    @BindView(R.id.customersDuesList)
    public RecyclerView duesRecyclerView;
    @BindView(R.id.tv_dues_no_results)
    public TextView noReusltsTextView;
    @BindView(R.id.tv_due_TotalDueAmt)
    public TextView dueAmountTextView;
    private ArrayList<String> customerNames;
    private DuesAdapter duesAdapter;


    public CustomerWiseDueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cust_due, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        List<Customer.CustomerData> customers = new ArrayList<>();
        customerNames = new ArrayList<>();

        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customer : customers) {
                customerNames.add(customer.username);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, customerNames);
            selectCustomerAutoCompleteTV.setThreshold(1);//will start working from first character
            selectCustomerAutoCompleteTV.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }

        ArrayList<Transaction> transactions = new ArrayList<>();
        duesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        duesAdapter = new DuesAdapter(transactions, this);
        duesRecyclerView.setAdapter(duesAdapter);

        return v;
    }

    @OnClick(R.id.btn_view_cust_dues)
    public void viewDues() {
        Utils.hideSoftKeyboard(selectCustomerAutoCompleteTV);
        duesAdapter.removeAllTransactions();

        String selectedCustomer = selectCustomerAutoCompleteTV.getText().toString();

        if (TextUtils.isEmpty(selectedCustomer)) {
            Utils.showToast("Select Customer to view transactions", mContext);
            return;
        }

        if (customerNames != null && customerNames.size() > 0) {
            if (!customerNames.contains(selectedCustomer)) {
                Utils.showToast("Enter valid Customer Name", mContext);
                return;
            }
        } else {
            Utils.showToast("There are no customers to view transactions", mContext);
            return;
        }

        ArrayList<Transaction> transactions = billMatrixDaoImpl.getCustomerTransactions(selectedCustomer);

        if (transactions != null && transactions.size() > 0) {
            noReusltsTextView.setVisibility(View.GONE);
            for (Transaction transaction : transactions) {
                duesAdapter.addTransaction(transaction);
            }
        } else {
            noReusltsTextView.setVisibility(View.VISIBLE);
        }

        selectCustomerAutoCompleteTV.setText("");

        dueAmountTextView.setText(calculateTotalDue());
    }

    private String calculateTotalDue() {
        float totalDueAmt = 0.0f;
        if (duesAdapter != null && duesAdapter.transactions != null && duesAdapter.transactions.size() > 0) {
            for (Transaction transaction : duesAdapter.transactions) {
                try {
                    totalDueAmt = totalDueAmt + Float.parseFloat(transaction.amountDue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return String.format(Locale.getDefault(), "%.2f", totalDueAmt);
    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
