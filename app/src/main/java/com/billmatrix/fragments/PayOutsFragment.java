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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.PayOutsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Vendor;
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
public class PayOutsFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = PayOutsFragment.class.getSimpleName();
    @BindView(R.id.sp_pay_outs_vendor)
    public Spinner vendorSpinner;
    @BindView(R.id.sp_pay_outs_payment)
    public Spinner modePaymentSpinner;
    @BindView(R.id.et_payouts_date)
    public EditText dateEditText;
    @BindView(R.id.et_payment_mode)
    public EditText otherPaymentEditText;
    @BindView(R.id.et_payout_amount)
    public EditText amountEditText;
    @BindView(R.id.payOutsList)
    public RecyclerView payOutsList;
    @BindView(R.id.btn_addPayout)
    public Button addPaymentButton;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public PayOutsAdapter payOutsAdapter;
    private String adminId;
    public boolean isEditing;
    private boolean isPaymentAdded;
    private Payments.PaymentData selectedPaymenttoEdit;
    private ArrayAdapter<String> vendorSpinnerAdapter;
    private ArrayAdapter<CharSequence> modeSpinnerAdapter;

    public PayOutsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_outs, container, false);
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

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }
        }

        vendorSpinnerAdapter = Utils.loadSpinner(vendorSpinner, mContext, strings);
        modeSpinnerAdapter = Utils.loadSpinner(modePaymentSpinner, mContext, R.array.mode_of_pay_array);

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

        modePaymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getAdapter().getItem(position).toString();
                if (selectedMode.equalsIgnoreCase("cash")) {
                    otherPaymentEditText.setVisibility(View.GONE);
                } else {
                    otherPaymentEditText.setVisibility(View.VISIBLE);
                    if (selectedMode.equalsIgnoreCase("card")) {
                        otherPaymentEditText.setHint("Last 4 digits of card");
                    } else if (selectedMode.equalsIgnoreCase("cheque")) {
                        otherPaymentEditText.setHint("Cheque No.");
                    } else if (selectedMode.equalsIgnoreCase("other")) {
                        otherPaymentEditText.setHint("Payments Mode");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payOutsList.setLayoutManager(new LinearLayoutManager(mContext));

        List<Payments.PaymentData> paymentsfromDB = new ArrayList<>();

        payOutsAdapter = new PayOutsAdapter(paymentsfromDB, this);
        payOutsList.setAdapter(payOutsAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYOUT);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData payOutData : paymentsfromDB) {
                if (!payOutData.status.equalsIgnoreCase("-1")) {
                    payOutsAdapter.addPayOut(payOutData);
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
        serverData.setPaymentType(PaymentsActivity.PAYOUT);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getPaymentsFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Payments.PaymentData> paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYOUT);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (Payments.PaymentData payOutData : paymentsfromDB) {
                if (!payOutData.status.equalsIgnoreCase("-1")) {
                    payOutsAdapter.addPayOut(payOutData);
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

    @OnClick(R.id.btn_addPayout)
    public void addPayout() {
        Utils.hideSoftKeyboard(amountEditText);

        Payments.PaymentData payOutData = new Payments().new PaymentData();
        Payments.PaymentData paymentFromServer = new Payments().new PaymentData();

        String vendorName = vendorSpinner.getSelectedItem().toString();

        if (vendorName.equalsIgnoreCase("Select vendor")) {
            Utils.showToast("Select Vendor Name", mContext);
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Enter Date", mContext);
            return;
        }
        String amount = amountEditText.getText().toString();

        if (TextUtils.isEmpty(amount.trim())) {
            Utils.showToast("Enter Amount", mContext);
            return;
        }

        String mode = modePaymentSpinner.getSelectedItem().toString();

        if (mode.equalsIgnoreCase("Other")) {
            mode = otherPaymentEditText.getText().toString();
            if (TextUtils.isEmpty(mode)) {
                Utils.showToast("Enter Other Mode of Payment", mContext);
                return;
            }
        }

        if (addPaymentButton.getText().toString().equalsIgnoreCase("ADD")) {
            payOutData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            payOutData.id = "PM_" + (billMatrixDaoImpl.getPaymentsCount() + 1);
            payOutData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedPaymenttoEdit != null) {
                payOutData.id = selectedPaymenttoEdit.id;
                payOutData.create_date = selectedPaymenttoEdit.create_date;
                payOutData.add_update = selectedPaymenttoEdit.add_update;

                if (TextUtils.isEmpty(selectedPaymenttoEdit.add_update)) {
                    payOutData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedPaymenttoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    payOutData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        payOutData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        payOutData.payee_name = vendorName;
        payOutData.mode_of_payment = mode;
        payOutData.date_of_payment = date;
        payOutData.purpose_of_payment = "";
        payOutData.status = "1";
        payOutData.admin_id = adminId;
        payOutData.amount = amount;
        payOutData.payment_type = PaymentsActivity.PAYOUT;

        billMatrixDaoImpl.addPayment(payOutData);

        /**
         * reset all edit texts
         */
        vendorSpinner.setSelection(0);
        modePaymentSpinner.setSelection(0);
        dateEditText.setText("");
        amountEditText.setText("");

        if (addPaymentButton.getText().toString().equalsIgnoreCase("ADD")) {
            if (Utils.isInternetAvailable(mContext)) {
                paymentFromServer = ServerUtils.addPaymenttoServer(payOutData, mContext, adminId, billMatrixDaoImpl);
            } else {
                /**
                 * To show pending sync Icon in database page
                 */
                Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
                Utils.showToast("Payment Added successfully", mContext);
                paymentFromServer = payOutData;
            }
        } else {
            if (selectedPaymenttoEdit != null) {
                if (Utils.isInternetAvailable(mContext)) {
                    paymentFromServer = ServerUtils.updatePaymenttoServer(payOutData, mContext, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    paymentFromServer = payOutData;
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Payment Updated successfully", mContext);
                }
            }
        }

        payOutsAdapter.addPayOut(paymentFromServer);
        payOutsList.smoothScrollToPosition(payOutsAdapter.getItemCount());
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
                    addPayout();
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
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Pay out?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Payments.PaymentData selectedPayment = payOutsAdapter.getItem(position);
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
                            Utils.showToast("Pay Out Deleted successfully", mContext);
                        }
                        payOutsAdapter.deletePayOut(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addPaymentButton.setText(getString(R.string.save));

                    if (position != -1) {
                        selectedPaymenttoEdit = payOutsAdapter.getItem(position);
                    }

                    if (selectedPaymenttoEdit != null) {
                        try {
                            int vendorSelectedPosition = vendorSpinnerAdapter.getPosition(selectedPaymenttoEdit.payee_name);
                            vendorSpinner.setSelection(vendorSelectedPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                            vendorSpinner.setSelection(0);
                        }


                        try {
                            int modeSelectedPosition = modeSpinnerAdapter.getPosition(selectedPaymenttoEdit.mode_of_payment);
                            modePaymentSpinner.setSelection(modeSelectedPosition);
                            otherPaymentEditText.setVisibility(View.GONE);

                            if (modeSelectedPosition == -1) {
                                modePaymentSpinner.setSelection(3);
                                otherPaymentEditText.setText(selectedPaymenttoEdit.mode_of_payment);
                                otherPaymentEditText.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            modePaymentSpinner.setSelection(0);
                        }

                        dateEditText.setText(selectedPaymenttoEdit.date_of_payment);
                        amountEditText.setText(selectedPaymenttoEdit.amount);
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deletePayment(DBConstants.ID, payOutsAdapter.getItem(position).id);
                        payOutsAdapter.deletePayOut(position);
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
