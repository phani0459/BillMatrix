package com.billmatrix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.VendorHolder> {

    private final Context mContext;
    private List<Discount.DiscountData> discounts;
    OnItemClickListener onItemClickListener;
    private RadioButton lastChecked = null;
    private int lastCheckedPos = -1;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rbtn_item_discount_sno)
        RadioButton snoRadioButton;
        @BindView(R.id.tv_item_discount_desc)
        TextView discDescTextView;
        @BindView(R.id.tv_item_discount_code)
        TextView discCodeTextView;
        @BindView(R.id.tv_item_discount_value)
        TextView discValueTextView;
        @BindView(R.id.im_item_discount_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_discount_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteDiscount(int position) {
        discounts.remove(position);
        notifyDataSetChanged();
    }

    public void insertDefaultDiscount(Discount.DiscountData discountData) {
        discounts.add(0, discountData);
        notifyDataSetChanged();
    }

    public void addDiscount(Discount.DiscountData discountData) {
        discounts.add(discountData);
        notifyDataSetChanged();
    }

    public DiscountAdapter(Context mContext, List<Discount.DiscountData> discountDatas, OnItemClickListener onClickListener) {
        this.mContext = mContext;
        this.discounts = discountDatas;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllDiscounts() {
        discounts = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_item, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        final Discount.DiscountData discountData = discounts.get(position);

        holder.snoRadioButton.setText("" + (position + 1));
        holder.discDescTextView.setText(discountData.discount_description);
        holder.discCodeTextView.setText(discountData.discount_code);
        holder.discValueTextView.setText(discountData.discount.trim());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(2, position);
            }
        });

        holder.snoRadioButton.setChecked(false);

        if (position == 0) {
            holder.deleteImageView.setVisibility(View.INVISIBLE);
            holder.editImageView.setVisibility(View.VISIBLE);
            holder.snoRadioButton.setChecked(true);
            lastCheckedPos = 0;
            lastChecked = holder.snoRadioButton;
        }

        /**
         * to show previously selected discount as selected
         */
        String selectedDiscountCode = Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, "");

        if (!TextUtils.isEmpty(selectedDiscountCode)) {
            if (selectedDiscountCode.equalsIgnoreCase(discountData.discount_code)) {
                holder.snoRadioButton.setChecked(true);
            } else {
                holder.snoRadioButton.setChecked(false);
            }
        }

        holder.snoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton compoundButton = (RadioButton) view;
                if (lastCheckedPos != position) {

                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                    }

                    compoundButton.setChecked(true);
                    lastCheckedPos = position;
                    lastChecked = (RadioButton) compoundButton;
                    onItemClickListener.onItemClick(3, position);
                } else {
                    if (lastChecked != null) {
                        lastCheckedPos = -1;
                        lastChecked.setChecked(false);
                        onItemClickListener.onItemClick(4, position);
                    }
                }

                try {
                    Utils.getSharedPreferences(mContext).edit().putFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, Float.parseFloat(discountData.discount)).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_DISCOUNT_CODE, discountData.discount_code).apply();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Utils.getSharedPreferences(mContext).edit().putFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, 0.0f).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_DISCOUNT_CODE, "").apply();
                }
            }
        });
    }

    public Discount.DiscountData getItem(int position) {
        return discounts.get(position);
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }


}