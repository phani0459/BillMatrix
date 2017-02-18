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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.PayInsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
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
public class PayInsFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = PayInsFragment.class.getSimpleName();
    @BindView(R.id.et_payins_date)
    public EditText dateEditText;
    @BindView(R.id.atv_payIns_custName)
    public AutoCompleteTextView customerNameAutoCompleteTextView;
    @BindView(R.id.payInsList)
    public RecyclerView payInsRecyclerView;
    @BindView(R.id.et_payIn_amt)
    public EditText amountEditText;
    @BindView(R.id.btn_addPayIn)
    public Button addPaymentBtn;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public PayInsAdapter payInsAdapter;
    private String adminId;
    private boolean isEditing;
    private boolean isPaymentAdded;
    private Payments.PaymentData selectedPaymenttoEdit;
    private ArrayList<String> customerNames;

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
        customerNames = new ArrayList<>();

        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customer : customers) {
                if (!customer.status.equalsIgnoreCase("-1")) {
                    customerNames.add(customer.username);
                }
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

        List<Payments.PaymentData> paymentsfromDB = new ArrayList<>();

        payInsAdapter = new PayInsAdapter(paymentsfromDB, this);
        payInsRecyclerView.setAdapter(payInsAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYIN);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData paymentData : paymentsfromDB) {
                if (!paymentData.status.equalsIgnoreCase("-1")) {
                    payInsAdapter.addPayIn(paymentData);
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
        serverData.setPaymentType(PaymentsActivity.PAYIN);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getPaymentsFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Payments.PaymentData> paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYIN);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData paymentData : paymentsfromDB) {
                if (!paymentData.status.equalsIgnoreCase("-1")) {
                    payInsAdapter.addPayIn(paymentData);
                }
            }
        }
    }

    public void onBackPressed() {
        if (addPaymentBtn.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addPayIn();
                    if (isPaymentAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    @OnClick(R.id.btn_addPayIn)
    public void addPayIn() {
        Utils.hideSoftKeyboard(amountEditText);

        Payments.PaymentData paymentData = new Payments().new PaymentData();
        Payments.PaymentData paymentFromServer = new Payments().new PaymentData();
        String customerName = customerNameAutoCompleteTextView.getText().toString();
        String date = dateEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        if (TextUtils.isEmpty(customerName)) {
            Utils.showToast("Enter Customer Name", mContext);
            return;
        }

        if (customerNames != null && customerNames.size() > 0) {
            if (!customerNames.contains(customerName)) {
                Utils.showToast("Enter Valid Customer Name", mContext);
                return;
            }
        }

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Enter Date", mContext);
            return;
        }

        if (TextUtils.isEmpty(amount)) {
            Utils.showToast("Enter Amount", mContext);
            return;
        }

        if (addPaymentBtn.getText().toString().equalsIgnoreCase("ADD")) {
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
        paymentData.payee_name = customerName;
        paymentData.date_of_payment = date;
        paymentData.purpose_of_payment = "";
        paymentData.mode_of_payment = "";
        paymentData.amount = amount;
        paymentData.status = "1";
        paymentData.admin_id = adminId;
        paymentData.payment_type = PaymentsActivity.PAYIN;

        billMatrixDaoImpl.addPayment(paymentData);

        /**
         * reset all edit texts
         */
        customerNameAutoCompleteTextView.setText("");
        dateEditText.setText("");
        amountEditText.setText("");

        if (addPaymentBtn.getText().toString().equalsIgnoreCase("ADD")) {
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

        payInsAdapter.addPayIn(paymentFromServer);
        payInsRecyclerView.smoothScrollToPosition(payInsAdapter.getItemCount());

        addPaymentBtn.setText(getString(R.string.add));
        isEditing = false;
        isPaymentAdded = true;
        ((BaseTabActivity) mContext).ifTabCanChange = true;
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete this Payment?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Payments.PaymentData selectedPayment = payInsAdapter.getItem(position);
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
                            Utils.showToast("PayIn Deleted successfully", mContext);
                        }
                        payInsAdapter.deletePayIn(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addPaymentBtn.setText(getString(R.string.save));
                    selectedPaymenttoEdit = payInsAdapter.getItem(position);

                    customerNameAutoCompleteTextView.setText(selectedPaymenttoEdit.payee_name);
                    dateEditText.setText(selectedPaymenttoEdit.date_of_payment);
                    amountEditText.setText(selectedPaymenttoEdit.amount);

                    billMatrixDaoImpl.deletePayment(DBConstants.ID, payInsAdapter.getItem(position).id);
                    payInsAdapter.deletePayIn(position);
                } else {
                    Utils.showToast("Save present editing Payment before editing other payment", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
