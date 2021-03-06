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
import com.billmatrix.models.Vendor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorsAdapter extends RecyclerView.Adapter<VendorsAdapter.VendorHolder> {

    private List<Vendor.VendorData> vendors;
    OnItemClickListener onItemClickListener;

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

    public VendorsAdapter(List<Vendor.VendorData> vendors, OnItemClickListener onItemClickListener) {
        this.vendors = vendors;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllVendors() {
        vendors = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VendorHolder holder, int position) {
        Vendor.VendorData vendorData = vendors.get(position);

        holder.snoTextView.setText("" + (holder.getAdapterPosition() + 1));
        holder.nameTextView.setText(vendorData.name);
        holder.vendorIdTextView.setText("V" + (!TextUtils.isEmpty(vendorData.id) ? vendorData.id : "_" + (holder.getAdapterPosition() + 1)));
        holder.mobileTextView.setText(!TextUtils.isEmpty(vendorData.phone) ? vendorData.phone.trim() : "-");
        holder.addTextView.setText(!TextUtils.isEmpty(vendorData.address) ? vendorData.address.trim() : "-");
        holder.sinceTextView.setText(vendorData.since);
        holder.emailTextView.setText(!TextUtils.isEmpty(vendorData.email) ? vendorData.email : "--");
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, holder.getAdapterPosition());
            }
        });

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(2, holder.getAdapterPosition());
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
}