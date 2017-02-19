package com.billmatrix.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.DiscountAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
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
public class DiscountsFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = DiscountsFragment.class.getSimpleName();
    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.discountsList)
    public RecyclerView discountsRecyclerView;
    @BindView(R.id.et_disc_desc)
    public EditText discountDescEditText;
    @BindView(R.id.et_disc_code)
    public EditText discountCodeEditText;
    @BindView(R.id.et_disc_value)
    public EditText discountValueEditText;
    @BindView(R.id.btn_addDiscountType)
    public Button addDiscountButton;

    private DiscountAdapter discountAdapter;
    private String adminId;
    private Discount.DiscountData selectedDiscounttoEdit;
    private boolean isDiscountAdded;

    private static DiscountsFragment discountFragment;

    public static DiscountsFragment getInstance() {
        if (discountFragment != null) {
            return discountFragment;
        }

        discountFragment = new DiscountsFragment();
        return discountFragment;
    }

    public DiscountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discounts, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        discountsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        Discount.DiscountData defaultDiscount = new Discount().new DiscountData();

        defaultDiscount.discount_code = Utils.getSharedPreferences(mContext).getString(Constants.PREF_DEFAULT_DISCOUNT_CODE, "");
        defaultDiscount.discount_description = "No Discount";
        defaultDiscount.discount = "0";

        List<Discount.DiscountData> discounts = new ArrayList<>();
        discounts.add(defaultDiscount);

        discountAdapter = new DiscountAdapter(mContext, discounts, this);
        discountsRecyclerView.setAdapter(discountAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        discounts = billMatrixDaoImpl.getDiscount();

        if (discounts != null && discounts.size() > 0) {
            for (Discount.DiscountData discountData : discounts) {
                if (!discountData.status.equalsIgnoreCase("-1")) {
                    discountAdapter.addDiscount(discountData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getDiscountsFromServer(adminId);
                }
            }
        }

        return v;
    }

    private void getDiscountsFromServer(String adminId) {
        Log.e(TAG, "getDiscountsFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getDiscountsFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Discount.DiscountData> discounts = billMatrixDaoImpl.getDiscount();

        if (discounts != null && discounts.size() > 0) {
            for (Discount.DiscountData discountData : discounts) {
                if (!discountData.status.equalsIgnoreCase("-1")) {
                    discountAdapter.addDiscount(discountData);
                }
            }
        }
    }

    public void onBackPressed() {
        if (addDiscountButton.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addDiscountType();
                    if (isDiscountAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    @OnClick(R.id.btn_addDiscountType)
    public void addDiscountType() {
        Utils.hideSoftKeyboard(discountCodeEditText);

        String description = discountDescEditText.getText().toString();

        if (TextUtils.isEmpty(description)) {
            Utils.showToast("Enter Discount Description", mContext);
            return;
        }

        String code = discountCodeEditText.getText().toString();

        if (TextUtils.isEmpty(code)) {
            Utils.showToast("Enter Discount Code", mContext);
            return;
        }

        String value = discountValueEditText.getText().toString();

        if (TextUtils.isEmpty(value)) {
            Utils.showToast("Enter Discount Value", mContext);
            return;
        }

        Discount.DiscountData discountData = new Discount().new DiscountData();
        Discount.DiscountData discountFromServer = new Discount().new DiscountData();

        if (addDiscountButton.getText().toString().equalsIgnoreCase("ADD")) {
            discountData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            discountData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedDiscounttoEdit != null) {
                discountData.id = selectedDiscounttoEdit.id;
                discountData.create_date = selectedDiscounttoEdit.create_date;
                if (TextUtils.isEmpty(selectedDiscounttoEdit.add_update)) {
                    discountData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedDiscounttoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    discountData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }

        discountData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        discountData.discount = value;
        discountData.discount_description = description;
        discountData.discount_code = code;
        discountData.status = "1";
        discountData.admin_id = adminId;

        if (value.equals("0")) {
            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_DEFAULT_DISCOUNT_CODE, code).apply();
            discountAdapter.insertDefaultDiscount(discountData);
            /**
             * reset all edit texts
             */
            discountDescEditText.setText("");
            discountValueEditText.setText("");
            discountCodeEditText.setText("");

            addDiscountButton.setText(getString(R.string.add));
            isEditing = false;
            isDiscountAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;
            return;
        }

        long discountAdded = billMatrixDaoImpl.addDiscount(discountData);

        if (discountAdded != -1) {
            /**
             * reset all edit texts
             */
            discountDescEditText.setText("");
            discountValueEditText.setText("");
            discountCodeEditText.setText("");

            if (addDiscountButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    discountFromServer = ServerUtils.addDiscounttoServer(discountData, adminId, mContext, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    discountFromServer = discountData;
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_DISCS_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Discount Added successfully", mContext);
                }
            } else {
                if (selectedDiscounttoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        discountFromServer = ServerUtils.updateDiscounttoServer(discountData, mContext, adminId, billMatrixDaoImpl);
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        discountFromServer = discountData;
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_DISCS_EDITED_OFFLINE, true).apply();
                        Utils.showToast("Discount Updated successfully", mContext);
                    }
                }
            }

            discountAdapter.addDiscount(discountFromServer);
            discountsRecyclerView.smoothScrollToPosition(discountAdapter.getItemCount());
            addDiscountButton.setText(getString(R.string.add));
            isEditing = false;
            isDiscountAdded = true;
            ((BaseTabActivity) mContext).ifTabCanChange = true;
        } else {
            Utils.showToast("Discount Code must be unique", mContext);
        }
    }

    public boolean isEditing;

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Discount Type", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Discount.DiscountData selectedDiscount = discountAdapter.getItem(position);
                        if (selectedDiscount.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateDiscount(DBConstants.STATUS, "-1", selectedDiscount.discount_code);
                        } else {
                            billMatrixDaoImpl.deleteDiscount(selectedDiscount.discount_code);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedDiscount.id)) {
                                ServerUtils.deleteDiscountfromServer(selectedDiscount, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_DISCS_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Discount Deleted successfully", mContext);
                        }
                        discountAdapter.deleteDiscount(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addDiscountButton.setText(getString(R.string.save));
                    selectedDiscounttoEdit = discountAdapter.getItem(position);

                    discountDescEditText.setText(selectedDiscounttoEdit.discount_description);
                    discountCodeEditText.setText(selectedDiscounttoEdit.discount_code);
                    discountValueEditText.setText(selectedDiscounttoEdit.discount);

                    billMatrixDaoImpl.deleteDiscount(discountAdapter.getItem(position).discount_code);
                    discountAdapter.deleteDiscount(position);
                } else {
                    Utils.showToast("Save present editing discount before editing other discount", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
