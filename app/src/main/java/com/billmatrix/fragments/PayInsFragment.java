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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.PayInsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.PayIn;
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
public class PayInsFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.et_payins_date)
    public EditText dateEditText;
    @BindView(R.id.atv_payIns_custName)
    public AutoCompleteTextView customerNameAutoCompleteTextView;
    @BindView(R.id.payInsList)
    public RecyclerView payInsRecyclerView;
    @BindView(R.id.et_payIn_amt)
    public EditText amountEditText;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public PayInsAdapter payInsAdapter;
    private String adminId;
    private ArrayList<PayIn.PayInData> paymentsfromDB;

    public PayInsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_ins, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        List<Customer.CustomerData> customers = new ArrayList<>();
        List<String> customerNames = new ArrayList<>();

        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customer : customers) {
                customerNames.add(customer.username);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, customerNames);
            customerNameAutoCompleteTextView.setThreshold(1);//will start working from first character
            customerNameAutoCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }

        dateEditText.setInputType(InputType.TYPE_NULL);

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, true);
                    datePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        payInsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<PayIn.PayInData> payIns = new ArrayList<>();

        payInsAdapter = new PayInsAdapter(payIns, this);
        payInsRecyclerView.setAdapter(payInsAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYIN);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (PayIn.PayInData payInData : paymentsfromDB) {
                if (!payInData.status.equalsIgnoreCase("-1")) {
                    payInsAdapter.addPayIn(payInData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
//                    getDiscountsFromServer(adminId);
                }
            }
        }

        return v;
    }

    @OnClick(R.id.btn_addPayIn)
    public void addPayIn() {
        Utils.hideSoftKeyboard(amountEditText);

        PayIn.PayInData payInData = new PayIn().new PayInData();
        String customerName = customerNameAutoCompleteTextView.getText().toString();
        String date = dateEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        if (TextUtils.isEmpty(customerName)) {
            Utils.showToast("Enter Customer Name", mContext);
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Enter Date", mContext);
            return;
        }

        if (TextUtils.isEmpty(amount)) {
            Utils.showToast("Enter Amount", mContext);
            return;
        }

        payInData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        payInData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        payInData.payee_name = customerName;
        payInData.date_of_payment = date;
        payInData.amount = amount;
        payInData.status = "1";
        payInData.payment_type = PaymentsActivity.PAYIN;

        long paymentAdded = billMatrixDaoImpl.addPayment(payInData);

        if (paymentAdded != -1) {
            payInsAdapter.addPayIn(payInData);
            payInsRecyclerView.smoothScrollToPosition(payInsAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            customerNameAutoCompleteTextView.setText("");
            dateEditText.setText("");
            amountEditText.setText("");
        } else {
            Utils.showToast("Payment must be unique", mContext);
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete PayIn?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        payInsAdapter.deletePayIn(position);
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
