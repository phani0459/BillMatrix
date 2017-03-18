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

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.GenExpensesAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Payments;
import com.billmatrix.network.ServerData;
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
public class GenExpensesFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = GenExpensesFragment.class.getSimpleName();
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
    @BindView(R.id.btn_addGenExpense)
    public Button addPaymentButton;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public GenExpensesAdapter expensesAdapter;
    private String adminId;
    private boolean isPaymentAdded;
    public boolean isEditing;
    private Payments.PaymentData selectedPaymenttoEdit;

    public GenExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gen_expns, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        if (savedInstanceState != null) {
            selectedPaymenttoEdit = (Payments.PaymentData) savedInstanceState.getSerializable("EDIT_PAY");
            if (selectedPaymenttoEdit != null) {
                Log.e(TAG, "onCreateView: " + selectedPaymenttoEdit.date_of_payment);
                isEditing = false;
                onItemClick(2, -1);
            }
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

        genExpensesList.setLayoutManager(new LinearLayoutManager(mContext));

        List<Payments.PaymentData> paymentsfromDB = new ArrayList<>();

        expensesAdapter = new GenExpensesAdapter(paymentsfromDB, this);
        genExpensesList.setAdapter(expensesAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.GEN_EXPENSE);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData paymentData : paymentsfromDB) {
                if (!paymentData.status.equalsIgnoreCase("-1")) {
                    expensesAdapter.addGenExpense(paymentData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getPaymentsFromServer(adminId);
                }
            }
        }

        return v;
    }

    private void getPaymentsFromServer(String adminId) {
        Log.e(TAG, "getPaymentsFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setPaymentType(PaymentsActivity.GEN_EXPENSE);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getPaymentsFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Payments.PaymentData> paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.GEN_EXPENSE);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData paymentData : paymentsfromDB) {
                if (!paymentData.status.equalsIgnoreCase("-1")) {
                    expensesAdapter.addGenExpense(paymentData);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing && selectedPaymenttoEdit != null) {
            Log.e(TAG, "onSaveInstanceState: ");
            outState.putSerializable("EDIT_PAY", selectedPaymenttoEdit);
        }
    }

    @OnClick(R.id.btn_addGenExpense)
    public void addExpense() {
        Utils.hideSoftKeyboard(amountEditText);

        Payments.PaymentData paymentData = new Payments().new PaymentData();
        Payments.PaymentData paymentFromServer = new Payments().new PaymentData();
        String expenseName = expenseNameEditText.getText().toString();

        if (TextUtils.isEmpty(expenseName.trim())) {
            Utils.showToast("Enter Expense Name", mContext);
            return;
        }

        String purpose = purposeEditText.getText().toString();

        if (TextUtils.isEmpty(purpose.trim())) {
            Utils.showToast("Enter Expense Purpose", mContext);
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Enter Expense Date", mContext);
            return;
        }

        String amount = amountEditText.getText().toString();

        if (TextUtils.isEmpty(amount.trim())) {
            Utils.showToast("Enter Expense Amount", mContext);
            return;
        }

        if (addPaymentButton.getText().toString().equalsIgnoreCase("ADD")) {
            paymentData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            paymentData.id = "PM_" + (billMatrixDaoImpl.getPaymentsCount() + 1);
            paymentData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedPaymenttoEdit != null) {
                paymentData.id = selectedPaymenttoEdit.id;
                paymentData.create_date = selectedPaymenttoEdit.create_date;
                if (selectedPaymenttoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    paymentData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        paymentData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        paymentData.payee_name = expenseName;
        paymentData.purpose_of_payment = purpose;
        paymentData.mode_of_payment = "";
        paymentData.date_of_payment = date;
        paymentData.amount = amount;
        paymentData.status = "1";
        paymentData.admin_id = adminId;
        paymentData.payment_type = PaymentsActivity.GEN_EXPENSE;

        billMatrixDaoImpl.addPayment(paymentData);

        /**
         * reset all edit texts
         */
        expenseNameEditText.setText("");
        purposeEditText.setText("");
        dateEditText.setText("");
        amountEditText.setText("");

        if (addPaymentButton.getText().toString().equalsIgnoreCase("ADD")) {
            if (Utils.isInternetAvailable(mContext)) {
                paymentFromServer = ServerUtils.addPaymenttoServer(paymentData, mContext, adminId, billMatrixDaoImpl);
            } else {
                /**
                 * To show pending sync Icon in database page
                 */
                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
                Utils.showToast("Payment Added successfully", mContext);
                paymentFromServer = paymentData;
            }
        } else {
            if (selectedPaymenttoEdit != null) {
                if (Utils.isInternetAvailable(mContext)) {
                    paymentFromServer = ServerUtils.updatePaymenttoServer(paymentData, mContext, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    paymentFromServer = paymentData;
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Payment Updated successfully", mContext);
                }
            }
        }

        expensesAdapter.addGenExpense(paymentFromServer);
        genExpensesList.smoothScrollToPosition(expensesAdapter.getItemCount());
        addPaymentButton.setText(getString(R.string.add));
        isEditing = false;
        isPaymentAdded = true;
        ((BaseTabActivity) mContext).ifTabCanChange = true;

    }

    public void onBackPressed() {
        if (addPaymentButton.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addExpense();
                    if (isPaymentAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete General Expense?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Payments.PaymentData selectedPayment = expensesAdapter.getItem(position);
                        if (selectedPayment.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updatePaymentStatus(DBConstants.STATUS, "-1", selectedPayment.id);
                        } else {
                            billMatrixDaoImpl.deletePayment(DBConstants.ID, selectedPayment.id);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedPayment.id)) {
                                ServerUtils.deletePaymentfromServer(selectedPayment, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
                            Utils.showToast("General Expense Deleted successfully", mContext);
                        }
                        expensesAdapter.deleteGenExpense(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addPaymentButton.setText(getString(R.string.save));
                    if (position != -1) {
                        selectedPaymenttoEdit = expensesAdapter.getItem(position);
                    }

                    if (selectedPaymenttoEdit != null) {
                        expenseNameEditText.setText(selectedPaymenttoEdit.payee_name);
                        purposeEditText.setText(selectedPaymenttoEdit.purpose_of_payment);
                        dateEditText.setText(selectedPaymenttoEdit.date_of_payment);
                        amountEditText.setText(selectedPaymenttoEdit.amount);
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deletePayment(DBConstants.ID, expensesAdapter.getItem(position).id);
                        expensesAdapter.deleteGenExpense(position);
                    }
                } else {
                    Utils.showToast("Save present editing Payment before editing other payment", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
