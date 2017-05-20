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
import com.billmatrix.models.GeneratedReport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneratedReportsAdapter extends RecyclerView.Adapter<GeneratedReportsAdapter.ReportHolder> {

    private List<GeneratedReport.ReportData> reports;
    OnItemClickListener onItemClickListener;

    public class ReportHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_report_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_report_item_name)
        TextView itemNameTextView;
        @BindView(R.id.tv_item_report_vendor)
        TextView vendorTextView;
        @BindView(R.id.tv_item_report_date)
        TextView dateTextView;
        @BindView(R.id.tv_item_report_cost)
        TextView costTextView;
        @BindView(R.id.tv_item_report_qty)
        TextView qtyTextView;
        @BindView(R.id.tv_item_report_discount)
        TextView discountTextView;
        @BindView(R.id.tv_item_report_total)
        TextView totalTextView;

        public ReportHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteReport(int position) {
        reports.remove(position);
        notifyDataSetChanged();
    }

    public void addReport(GeneratedReport.ReportData customerData) {
        reports.add(customerData);
        notifyDataSetChanged();
    }

    public GeneratedReportsAdapter(List<GeneratedReport.ReportData> reports, OnItemClickListener onItemClickListener) {
        this.reports = reports;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllReports() {
        reports = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generated_report, parent, false);

        return new ReportHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportHolder holder, final int position) {
        GeneratedReport.ReportData customerData = reports.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.itemNameTextView.setText(customerData.itemName);
        holder.vendorTextView.setText(customerData.vendor);
        holder.dateTextView.setText(!TextUtils.isEmpty(customerData.date) ? customerData.date.trim() : "");
        holder.costTextView.setText(!TextUtils.isEmpty(customerData.cost) ? customerData.cost.trim() : "");
        holder.discountTextView.setText(!TextUtils.isEmpty(customerData.discount) ? customerData.discount.trim() : "");
        holder.totalTextView.setText(!TextUtils.isEmpty(customerData.total) ? customerData.total.trim() : "");
        if (!TextUtils.isEmpty(customerData.quantity)) {
            holder.qtyTextView.setText(customerData.quantity);
        }
        /*holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }

    public GeneratedReport.ReportData getItem(int position) {
        return reports.get(position);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}