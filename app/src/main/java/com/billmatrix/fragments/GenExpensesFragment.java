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

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.GenExpensesAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
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
public class GenExpensesFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.et_gen_exp_date)
    public EditText dateEditText;
    @BindView(R.id.genExpensesList)
    public RecyclerView genExpensesList;
    @BindView(R.id.et_expenseName)
    public EditText expenseNameEditText;
    @BindView(R.id.et_expensePurpose)
    public EditText purposeEditText;
    @BindView(R.id.et_expenseAmt)
    public EditText amountEditText;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public GenExpensesAdapter expensesAdapter;
    private String adminId;
    private ArrayList<PayIn.PayInData> paymentsfromDB;

    public GenExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gen_expns, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

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

        genExpensesList.setLayoutManager(new LinearLayoutManager(mContext));

        List<PayIn.PayInData> genExpenses = new ArrayList<>();

        expensesAdapter = new GenExpensesAdapter(genExpenses, this);
        genExpensesList.setAdapter(expensesAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.GEN_EXPENSE);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (PayIn.PayInData payInData : paymentsfromDB) {
                if (!payInData.status.equalsIgnoreCase("-1")) {
                    expensesAdapter.addGenExpense(payInData);
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

    @OnClick(R.id.btn_addGenExpense)
    public void addExpense() {
        String expenseName = expenseNameEditText.getText().toString();

        if (TextUtils.isEmpty(expenseName)) {
            Utils.showToast("Enter Expense Name", mContext);
            return;
        }

        String purpose = purposeEditText.getText().toString();

        if (TextUtils.isEmpty(purpose)) {
            Utils.showToast("Enter Expense Purpose", mContext);
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Enter Expense Date", mContext);
            return;
        }

        String amount = amountEditText.getText().toString();

        if (TextUtils.isEmpty(amount)) {
            Utils.showToast("Enter Expense Amount", mContext);
            return;
        }

        PayIn.PayInData paymentData = new PayIn().new PayInData();
        paymentData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        paymentData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        paymentData.payee_name = expenseName;
        paymentData.purpose_of_payment = purpose;
        paymentData.date_of_payment = date;
        paymentData.amount = amount;
        paymentData.status = "1";
        paymentData.payment_type = PaymentsActivity.GEN_EXPENSE;

        long vendorAdded = billMatrixDaoImpl.addPayment(paymentData);

        if (vendorAdded != -1) {
            expensesAdapter.addGenExpense(paymentData);
            genExpensesList.smoothScrollToPosition(expensesAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            expenseNameEditText.setText("");
            purposeEditText.setText("");
            dateEditText.setText("");
            amountEditText.setText("");
        } else {
            Utils.showToast("Vendor Email / Phone must be unique", mContext);
        }

    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete General Expense?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        billMatrixDaoImpl.deleteVendor(payInsAdapter.getItem(position).customername);
                        expensesAdapter.deleteGenExpense(position);
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
