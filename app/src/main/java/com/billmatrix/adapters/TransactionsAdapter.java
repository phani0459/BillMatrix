package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionHolder> {

    private List<Inventory.InventoryData> inventoryDatas;
    OnItemClickListener onItemClickListener;

    public class TransactionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_trans_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_trans_name)
        TextView nameTextView;
        @BindView(R.id.tv_item_trans_qty)
        TextView qtyTextView;
        @BindView(R.id.tv_item_trans_units)
        TextView unitsTextView;
        @BindView(R.id.tv_item_trans_rate)
        TextView rateTextView;
        @BindView(R.id.tv_item_trans_total)
        TextView totalTextView;

        public TransactionHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteTransaction(int position) {
        inventoryDatas.remove(position);
        notifyDataSetChanged();
    }

    public void addTransaction(Inventory.InventoryData transaction) {
        inventoryDatas.add(transaction);
        notifyDataSetChanged();
    }

    public TransactionsAdapter(List<Inventory.InventoryData> inventoryDatas, OnItemClickListener onItemClickListener) {
        this.inventoryDatas = inventoryDatas;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllTransactions() {
        inventoryDatas = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cust_transacs, parent, false);

        return new TransactionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, final int position) {
        Inventory.InventoryData transaction = inventoryDatas.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(transaction.item_name);
        holder.qtyTextView.setText(transaction.selectedQTY);
        holder.unitsTextView.setText(transaction.unit);
        holder.rateTextView.setText(transaction.price);
        holder.totalTextView.setText(String.format(Locale.getDefault(), "%.2f", getTotal(transaction.selectedQTY, transaction.price)));
    }

    public Inventory.InventoryData getItem(int position) {
        return inventoryDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return inventoryDatas.size();
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