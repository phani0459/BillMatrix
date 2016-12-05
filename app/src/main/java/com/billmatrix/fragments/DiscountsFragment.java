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
import android.widget.EditText;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.DiscountAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.billmatrix.R.string.rate;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscountsFragment extends Fragment implements OnItemClickListener {

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
    private DiscountAdapter discountAdapter;
    private String adminId;


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

        List<Discount.DiscountData> discounts = new ArrayList<>();

        discountAdapter = new DiscountAdapter(discounts, this);
        discountsRecyclerView.setAdapter(discountAdapter);

        discounts = billMatrixDaoImpl.getDiscount();
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        if (discounts != null && discounts.size() > 0) {
            for (Discount.DiscountData discountData : discounts) {
                if (!discountData.status.equalsIgnoreCase("-1")) {
                    discountAdapter.addDiscount(discountData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
//                    getCustomersFromServer(adminId);
                }
            }
        }

        return v;
    }

    @OnClick(R.id.btn_addDiscountType)
    public void addDiscountType() {
        String description = discountDescEditText.getText().toString();

        if (TextUtils.isEmpty(description)) {
            ((BaseTabActivity) mContext).showToast("Enter Discount Description");
            return;
        }

        String code = discountCodeEditText.getText().toString();

        if (TextUtils.isEmpty(code)) {
            ((BaseTabActivity) mContext).showToast("Enter Discount Code");
            return;
        }

        String value = discountValueEditText.getText().toString();

        if (TextUtils.isEmpty(value)) {
            ((BaseTabActivity) mContext).showToast("Enter Discount Value");
            return;
        }

        Discount.DiscountData discountData = new Discount().new DiscountData();

        discountData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        discountData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        discountData.discount = value;
        discountData.discountDescription = description;
        discountData.discount_code = code;
        discountData.status = "1";

        long discountAdded = billMatrixDaoImpl.addDiscount(discountData);

        if (discountAdded != -1) {
            discountAdapter.addDiscount(discountData);
            discountsRecyclerView.smoothScrollToPosition(discountAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            discountDescEditText.setText("");
            discountValueEditText.setText("");
            discountCodeEditText.setText("");
            isEditing = false;
        } else {
            ((BaseTabActivity) mContext).showToast("Discount Code must be unique");
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
                        billMatrixDaoImpl.deleteDiscount(discountAdapter.getItem(position).discount_code);
                        discountAdapter.deleteDiscount(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                }
                break;
            case 3:
                break;
        }
    }
}
