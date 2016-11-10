package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorsAdapter extends RecyclerView.Adapter<VendorsAdapter.VendorHolder> {

    private List<Vendor.VendorData> vendors;
    onClickListener onClickListener;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_vendorSno)
        TextView snoTextView;
        @BindView(R.id.tv_item_vendorName)
        TextView nameTextView;
        @BindView(R.id.tv_item_vendorId)
        TextView vendorIdTextView;
        @BindView(R.id.tv_item_vendorAddress)
        TextView addTextView;
        @BindView(R.id.tv_item_vendorPhone)
        TextView mobileTextView;
        @BindView(R.id.tv_item_vendorSince)
        TextView sinceTextView;
        @BindView(R.id.tv_item_vendorEmail)
        TextView emailTextView;
        @BindView(R.id.im_item_vendor_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_vendor_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteVendor(int position) {
        vendors.remove(position);
        notifyDataSetChanged();
    }

    public void addVendor(Vendor.VendorData vendorData) {
        vendors.add(vendorData);
        notifyDataSetChanged();
    }

    public VendorsAdapter(List<Vendor.VendorData> vendors, onClickListener onClickListener) {
        this.vendors = vendors;
        this.onClickListener = onClickListener;
    }

    public void removeAllVendors() {
        vendors = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_item, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        Vendor.VendorData vendorData = vendors.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(vendorData.name);
        holder.vendorIdTextView.setText(TextUtils.isEmpty(vendorData.id) ? "" + (position + 1) : vendorData.id);
        holder.mobileTextView.setText(vendorData.phone.trim());
        holder.addTextView.setText(vendorData.address.trim());
        holder.sinceTextView.setText(vendorData.since);
        holder.emailTextView.setText(vendorData.email);
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(1, position);
            }
        });
    }

    public Vendor.VendorData getItem(int position) {
        return vendors.get(position);
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }

    public interface onClickListener {
        public void onItemClick(int caseInt, int position);
    }
}