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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.DuesAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Transaction;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
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
    @BindView(R.id.ll_dues_custDetails)
    public LinearLayout custDetailsLayout;
    @BindView(R.id.tv_dues_customerName)
    public TextView custNameTextView;

    private ArrayList<String> customerNames;
    private DuesAdapter duesAdapter;
    private String adminId;


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
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        customers = billMatrixDaoImpl.getCustomers(adminId);

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
        ArrayList<Payments.PaymentData> paymentsfromDB = billMatrixDaoImpl.getCustomerPayments(PaymentsActivity.PAYIN, selectedCustomer);

        custDetailsLayout.setVisibility(View.VISIBLE);

        custNameTextView.setText(selectedCustomer);

        if (transactions != null && transactions.size() > 0) {
            noReusltsTextView.setVisibility(View.GONE);
            for (Transaction transaction : transactions) {
                duesAdapter.addTransaction(transaction);
            }
        }

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData paymentData : paymentsfromDB) {
                Transaction transaction = new Transaction();

                transaction.add_update = Constants.ADD_OFFLINE;
                transaction.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                transaction.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());

                transaction.date = paymentData.date_of_payment;
                transaction.inventoryJson = "";
                transaction.customerName = paymentData.payee_name;
                transaction.amountPaid = paymentData.amount;
                transaction.amountDue = getString(R.string.zero);
                transaction.totalAmount = getString(R.string.zero);
                transaction.admin_id = paymentData.admin_id;
                transaction.billNumber = generatePayInBillNumber(paymentData.id, paymentData.date_of_payment);
                transaction.id = transaction.billNumber;
                transaction.isZbillChecked = false;
                transaction.status = "1";

                duesAdapter.addTransaction(transaction);
            }
        }

        if (duesAdapter.getItemCount() <= 0) {
            noReusltsTextView.setVisibility(View.VISIBLE);
        }

        selectCustomerAutoCompleteTV.setText("");

        dueAmountTextView.setText(calculateTotalDue());
    }

    public String generatePayInBillNumber(String payInID, String dateOFPay) {
        String payBillNumber = payInID;

        if (!TextUtils.isEmpty(payInID)) {
            payBillNumber = "PAY" + dateOFPay.replace("-", "") + payInID;
        }

        return payBillNumber;
    }

    private String calculateTotalDue() {
        float totalBillAmt = 0.0f;
        float totalAmtPaid = 0.0f;
        if (duesAdapter != null && duesAdapter.transactions != null && duesAdapter.transactions.size() > 0) {
            for (Transaction transaction : duesAdapter.transactions) {
                try {
                    totalBillAmt = totalBillAmt + Float.parseFloat(transaction.totalAmount);
                    totalAmtPaid = totalAmtPaid + Float.parseFloat(transaction.amountPaid);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return String.format(Locale.getDefault(), "%.2f", (totalBillAmt - totalAmtPaid));
    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
