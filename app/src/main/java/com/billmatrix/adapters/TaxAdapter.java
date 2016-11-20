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
import com.billmatrix.models.PayIns;
import com.billmatrix.models.Tax;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaxAdapter extends RecyclerView.Adapter<TaxAdapter.VendorHolder> {

    private List<Tax.TaxData> taxes;
    OnItemClickListener onItemClickListener;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_item_tax_sno)
        CheckBox snoCheckBox;
        @BindView(R.id.tv_item_tax_type)
        TextView taxTypeTextView;
        @BindView(R.id.tv_item_tax_desc)
        TextView taxDescTextView;
        @BindView(R.id.tv_item_tax_rate)
        TextView taxRateTextView;
        @BindView(R.id.im_item_tax_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_tax_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteTax(int position) {
        taxes.remove(position);
        notifyDataSetChanged();
    }

    public void addTax(Tax.TaxData taxData) {
        taxes.add(taxData);
        notifyDataSetChanged();
    }

    public TaxAdapter(List<Tax.TaxData> taxes, OnItemClickListener onClickListener) {
        this.taxes = taxes;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllPayInsData() {
        taxes = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tax_item, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        Tax.TaxData taxData = taxes.get(position);

        holder.snoCheckBox.setText("" + (position + 1));
        holder.taxTypeTextView.setText(taxData.taxType);
        holder.taxDescTextView.setText(taxData.taxDescription);
        holder.taxRateTextView.setText(taxData.taxRate.trim());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });
    }

    public Tax.TaxData getItem(int position) {
        return taxes.get(position);
    }

    @Override
    public int getItemCount() {
        return taxes.size();
    }


}