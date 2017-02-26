package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.adapters.CustomersAdapter;
import com.billmatrix.adapters.TransactionsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Transaction;
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
public class CustomerTransFragment extends Fragment implements OnItemClickListener {

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.atv_select_customer)
    public AutoCompleteTextView selectCustomerAutoCompleteTV;
    @BindView(R.id.atv_billNumber)
    public AutoCompleteTextView billNumberAutoCompleteTV;
    @BindView(R.id.customersTransactionsList)
    public RecyclerView transactionsRecyclerView;
    @BindView(R.id.tv_trans_TotalPaid)
    public TextView totalPaidTextView;
    @BindView(R.id.tv_trans_TotalBillAmt)
    public TextView totalBillTextView;
    @BindView(R.id.tv_trans_TotalDue)
    public TextView totalDueTextView;
    @BindView(R.id.tv_trans_no_results)
    public TextView noTransTextView;

    private ArrayList<String> customerNames;
    private ArrayList<String> bills;
    private String adminId;
    private TransactionsAdapter transactionsAdapter;

    public CustomerTransFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cust_trans, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        List<Customer.CustomerData> customers = new ArrayList<>();
        customerNames = new ArrayList<>();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customer : customers) {
                customerNames.add(customer.username);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, customerNames);
            selectCustomerAutoCompleteTV.setThreshold(1);//will start working from first character
            selectCustomerAutoCompleteTV.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }

        bills = billMatrixDaoImpl.getBillNumbers();

        if (bills != null && bills.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, bills);
            billNumberAutoCompleteTV.setThreshold(1);//will start working from first character
            billNumberAutoCompleteTV.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }

        ArrayList<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        transactionsAdapter = new TransactionsAdapter(inventoryDatas, this);
        transactionsRecyclerView.setAdapter(transactionsAdapter);

        return v;
    }

    @OnClick(R.id.btn_view_cust_trans)
    public void viewTransactions() {

        Utils.hideSoftKeyboard(selectCustomerAutoCompleteTV);
        transactionsAdapter.removeAllTransactions();

        String selectedCustomer = selectCustomerAutoCompleteTV.getText().toString();
        String selectedBillNumber = billNumberAutoCompleteTV.getText().toString();

        if (!TextUtils.isEmpty(selectedCustomer)) {
            if (customerNames != null && customerNames.size() > 0) {
                if (!customerNames.contains(selectedCustomer)) {
                    Utils.showToast("Enter valid Customer Name", mContext);
                    return;
                }
            } else {
                Utils.showToast("There are no customers to view transactions", mContext);
                return;
            }
        }

        if (TextUtils.isEmpty(selectedBillNumber)) {
            Utils.showToast("Select Bill Number to view transactions", mContext);
            return;
        }

        if (bills != null && bills.size() > 0) {
            if (!bills.contains(selectedBillNumber)) {
                Utils.showToast("Enter valid Bill Number", mContext);
                return;
            }
        } else {
            Utils.showToast("There are no bills at present", mContext);
            return;
        }

        ArrayList<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        Transaction dbTransaction = billMatrixDaoImpl.getCustomerTransaction(selectedCustomer, selectedBillNumber);

        if (dbTransaction != null) {
            if (!TextUtils.isEmpty(dbTransaction.inventoryJson)) {
                inventoryDatas = Constants.getGson().fromJson(dbTransaction.inventoryJson, Constants.inventoryDatasMapType);
            }

            totalPaidTextView.setText(dbTransaction.amountPaid);
            totalDueTextView.setText(dbTransaction.amountDue);
            totalBillTextView.setText(dbTransaction.totalAmount);
        } else {
            noTransTextView.setVisibility(View.VISIBLE);
        }

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            noTransTextView.setVisibility(View.GONE);
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!inventoryData.status.equalsIgnoreCase("-1")) {
                    transactionsAdapter.addTransaction(inventoryData);
                }
            }
        } else {
            noTransTextView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
