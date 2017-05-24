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
import com.billmatrix.models.Transaction;
import com.billmatrix.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesReportsAdapter extends RecyclerView.Adapter<SalesReportsAdapter.ReportHolder> {

    private List<Transaction> reports;
    OnItemClickListener onItemClickListener;

    public class ReportHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sale_rep_item_sno)
        TextView snoTextView;
        @BindView(R.id.tv_sale_rep_item_h3)
        TextView itemNameTextView;
        @BindView(R.id.tv_sale_rep_item_h4)
        TextView vendorTextView;
        @BindView(R.id.tv_sale_rep_item_date)
        TextView dateTextView;
        @BindView(R.id.tv_sale_rep_item_h5)
        TextView costTextView;
        @BindView(R.id.tv_sale_rep_item_h6)
        TextView qtyTextView;

        public ReportHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteReport(int position) {
        reports.remove(position);
        notifyDataSetChanged();
    }

    public void addReport(Transaction transaction) {
        reports.add(transaction);
        notifyDataSetChanged();
    }

    public SalesReportsAdapter(List<Transaction> reports, OnItemClickListener onItemClickListener) {
        this.reports = reports;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllReports() {
        reports = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_report, parent, false);

        return new ReportHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportHolder holder, final int position) {
        Transaction transaction = reports.get(position);
        Inventory.InventoryData inventoryData = Constants.getGson().fromJson(transaction.inventoryJson, Inventory.InventoryData.class);

        holder.snoTextView.setText("" + (position + 1));
        holder.itemNameTextView.setText(inventoryData.item_name);
        holder.vendorTextView.setText(inventoryData.vendor);
        holder.dateTextView.setText(!TextUtils.isEmpty(transaction.date) ? transaction.date.trim() : "");
        holder.costTextView.setText(!TextUtils.isEmpty(inventoryData.price) ? inventoryData.price.trim() : "");
        if (!TextUtils.isEmpty(inventoryData.selectedQTY)) {
            holder.qtyTextView.setText(inventoryData.selectedQTY);
        }
    }

    public Transaction getItem(int position) {
        return reports.get(position);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}