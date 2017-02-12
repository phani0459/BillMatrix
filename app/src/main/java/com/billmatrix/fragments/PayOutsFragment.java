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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.activities.PaymentsActivity;
import com.billmatrix.adapters.PayInsAdapter;
import com.billmatrix.adapters.PayOutsAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.PayIn;
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
public class PayOutsFragment extends Fragment implements OnItemClickListener {

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

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    public PayOutsAdapter payOutsAdapter;
    private String adminId;
    private ArrayList<PayIn.PayInData> paymentsfromDB;

    public PayOutsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_outs, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }

        }
        Utils.loadSpinner(vendorSpinner, mContext, strings);
        Utils.loadSpinner(modePaymentSpinner, mContext, R.array.mode_of_pay_array);

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
                        otherPaymentEditText.setHint("PayIn Mode");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payOutsList.setLayoutManager(new LinearLayoutManager(mContext));

        List<PayIn.PayInData> payOuts = new ArrayList<>();

        payOutsAdapter = new PayOutsAdapter(payOuts, this);
        payOutsList.setAdapter(payOutsAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        paymentsfromDB = billMatrixDaoImpl.getPayments(PaymentsActivity.PAYOUT);

        if (paymentsfromDB != null && paymentsfromDB.size() > 0) {
            for (PayIn.PayInData payOutData : paymentsfromDB) {
                if (!payOutData.status.equalsIgnoreCase("-1")) {
                    payOutsAdapter.addPayOut(payOutData);
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

    @OnClick(R.id.btn_addPayout)
    public void addPayout() {
        Utils.hideSoftKeyboard(amountEditText);

        PayIn.PayInData payOutData = new PayIn().new PayInData();
        String vendorName = vendorSpinner.getSelectedItem().toString();
        String mode = modePaymentSpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        if (vendorName.equalsIgnoreCase("Select vendor")) {
            Utils.showToast("Select Vendor Name", mContext);
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

        payOutData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        payOutData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        payOutData.payee_name = vendorName;
        payOutData.mode_of_payment = mode;
        payOutData.date_of_payment = date;
        payOutData.status = "1";
        payOutData.amount = amount;
        payOutData.payment_type = PaymentsActivity.PAYOUT;

        long paymentAdded = billMatrixDaoImpl.addPayment(payOutData);

        if (paymentAdded != -1) {
            payOutsAdapter.addPayOut(payOutData);
            payOutsList.smoothScrollToPosition(payOutsAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            vendorSpinner.setSelection(0);
            modePaymentSpinner.setSelection(0);
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
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Pay out?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        billMatrixDaoImpl.deleteVendor(payInsAdapter.getItem(position).customername);
                        payOutsAdapter.deletePayOut(position);
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
