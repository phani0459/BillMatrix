package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DuesAdapter extends RecyclerView.Adapter<DuesAdapter.TransactionHolder> {

    public List<Transaction> transactions;
    OnItemClickListener onItemClickListener;

    public class TransactionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_due_billNO)
        TextView billNoTextView;
        @BindView(R.id.tv_item_due_date)
        TextView dateTextView;
        @BindView(R.id.tv_item_due_amtPaid)
        TextView amtPaidTextView;
        @BindView(R.id.tv_item_due_billAmount)
        TextView totalBillTextView;
        @BindView(R.id.tv_item_due_totalDue)
        TextView dueTextView;

        public TransactionHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteTransaction(int position) {
        transactions.remove(position);
        notifyDataSetChanged();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        notifyDataSetChanged();
    }

    public DuesAdapter(List<Transaction> transactions, OnItemClickListener onItemClickListener) {
        this.transactions = transactions;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllTransactions() {
        transactions = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_due, parent, false);

        return new TransactionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, final int position) {
        Transaction transaction = transactions.get(position);

        holder.billNoTextView.setText(transaction.billNumber);
        holder.dateTextView.setText(transaction.date);
        holder.totalBillTextView.setText(transaction.totalAmount);
        holder.amtPaidTextView.setText(transaction.amountPaid);
        holder.dueTextView.setText(transaction.amountDue);
    }

    public Transaction getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public float getTotal(String qty, String price) {
        float total = 0.0f;

        try {
            total = Float.parseFloat(qty) * Float.parseFloat(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return total;
    }
}