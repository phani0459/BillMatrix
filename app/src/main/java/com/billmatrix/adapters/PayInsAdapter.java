package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.PayIn;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayInsAdapter extends RecyclerView.Adapter<PayInsAdapter.PayInsHolder> {

    private List<PayIn.PayInData> payIns;
    OnItemClickListener onItemClickListener;

    public class PayInsHolder extends RecyclerView.ViewHolder {
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

        public PayInsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deletePayIn(int position) {
        payIns.remove(position);
        notifyDataSetChanged();
    }

    public void addPayIn(PayIn.PayInData payInData) {
        payIns.add(payInData);
        notifyDataSetChanged();
    }

    public PayInsAdapter(List<PayIn.PayInData> payIns, OnItemClickListener onClickListener) {
        this.payIns = payIns;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllPayIns() {
        payIns = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public PayInsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_in_item, parent, false);

        return new PayInsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayInsHolder holder, final int position) {
        PayIn.PayInData payInData = payIns.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(payInData.name);
        holder.dateTextView.setText(payInData.date);
        holder.amountTextView.setText(payInData.amount.trim());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });
    }

    public PayIn.PayInData getItem(int position) {
        return payIns.get(position);
    }

    @Override
    public int getItemCount() {
        return payIns.size();
    }


}