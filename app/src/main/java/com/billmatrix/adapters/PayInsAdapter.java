package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.PayIns;
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayInsAdapter extends RecyclerView.Adapter<PayInsAdapter.VendorHolder> {

    private List<PayIns.PayInData> payIns;
    OnItemClickListener onItemClickListener;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_payIns_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_payIns_custName)
        TextView nameTextView;
        @BindView(R.id.tv_item_payIns_amount)
        TextView amountTextView;
        @BindView(R.id.tv_item_payIns_date)
        TextView dateTextView;
        @BindView(R.id.im_item_payIn_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_payIn_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deletePayIn(int position) {
        payIns.remove(position);
        notifyDataSetChanged();
    }

    public void addPayIn(PayIns.PayInData vendorData) {
        payIns.add(vendorData);
        notifyDataSetChanged();
    }

    public PayInsAdapter(List<PayIns.PayInData> payIns, OnItemClickListener onClickListener) {
        this.payIns = payIns;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllPayInsData() {
        payIns = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_in_item, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        PayIns.PayInData vendorData = payIns.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(vendorData.customername);
        holder.dateTextView.setText(vendorData.date);
        holder.amountTextView.setText(vendorData.amount.trim());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });
    }

    public PayIns.PayInData getItem(int position) {
        return payIns.get(position);
    }

    @Override
    public int getItemCount() {
        return payIns.size();
    }


}