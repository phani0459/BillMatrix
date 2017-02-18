package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Payments;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayOutsAdapter extends RecyclerView.Adapter<PayOutsAdapter.PayOutsHolder> {

    private List<Payments.PaymentData> payIns;
    OnItemClickListener onItemClickListener;

    public class PayOutsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_payOuts_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_payOuts_vendorName)
        TextView nameTextView;
        @BindView(R.id.tv_item_payOuts_amount)
        TextView amountTextView;
        @BindView(R.id.tv_item_payOuts_mode)
        TextView modeTextView;
        @BindView(R.id.tv_item_payOuts_date)
        TextView dateTextView;
        @BindView(R.id.im_item_payOut_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_payOut_del)
        ImageView deleteImageView;

        public PayOutsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deletePayOut(int position) {
        payIns.remove(position);
        notifyDataSetChanged();
    }

    public void addPayOut(Payments.PaymentData paymentData) {
        payIns.add(paymentData);
        notifyDataSetChanged();
    }

    public PayOutsAdapter(List<Payments.PaymentData> payIns, OnItemClickListener onClickListener) {
        this.payIns = payIns;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllPayOuts() {
        payIns = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public PayOutsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_out_item, parent, false);

        return new PayOutsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayOutsHolder holder, final int position) {
        Payments.PaymentData paymentData = payIns.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(paymentData.payee_name);
        holder.modeTextView.setText(paymentData.mode_of_payment);
        holder.dateTextView.setText(paymentData.date_of_payment);
        holder.amountTextView.setText(paymentData.amount.trim());
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
    }

    public Payments.PaymentData getItem(int position) {
        return payIns.get(position);
    }

    @Override
    public int getItemCount() {
        return payIns.size();
    }


}