package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Tax;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.VendorHolder> {

    private List<Discount.DiscountData> discounts;
    OnItemClickListener onItemClickListener;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_item_discount_sno)
        CheckBox snoCheckBox;
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

    public void addDiscount(Discount.DiscountData taxData) {
        discounts.add(taxData);
        notifyDataSetChanged();
    }

    public DiscountAdapter(List<Discount.DiscountData> taxes, OnItemClickListener onClickListener) {
        this.discounts = taxes;
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
        Discount.DiscountData discountData = discounts.get(position);

        holder.snoCheckBox.setText("" + (position + 1));
        holder.discDescTextView.setText(discountData.discountDescription);
        holder.discCodeTextView.setText(discountData.discount_code);
        holder.discValueTextView.setText(discountData.discount.trim());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
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