package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Transport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.VendorHolder> {

    private List<Transport.TransportData> transportDatas;
    OnItemClickListener onItemClickListener;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_transport_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_transport_name)
        TextView transportNameTextView;
        @BindView(R.id.tv_item_transport_phone)
        TextView phoneTextView;
        @BindView(R.id.tv_item_transport_location)
        TextView locationTextView;
        @BindView(R.id.im_item_transport_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_transport_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteTransport(int position) {
        transportDatas.remove(position);
        notifyDataSetChanged();
    }

    public void addTransport(Transport.TransportData discountData) {
        transportDatas.add(discountData);
        notifyDataSetChanged();
    }

    public TransportAdapter(List<Transport.TransportData> transportDatas, OnItemClickListener onClickListener) {
        this.transportDatas = transportDatas;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllTransports() {
        transportDatas = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transport, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        final Transport.TransportData transportData = transportDatas.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.transportNameTextView.setText(transportData.transportName.toUpperCase());
        holder.phoneTextView.setText(transportData.phone.toUpperCase());
        holder.locationTextView.setText(transportData.location.trim().toUpperCase());
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

    public Transport.TransportData getItem(int position) {
        return transportDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return transportDatas.size();
    }


}