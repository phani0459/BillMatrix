package com.billmatrix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class POSInventoryAdapter extends RecyclerView.Adapter<POSInventoryAdapter.POSInventoryHolder> {

    private List<Inventory.InventoryData> inventories;
    OnItemClickListener onItemClickListener;

    public class POSInventoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_item_pos_inventory)
        ImageView inventoryImageView;
        @BindView(R.id.tv_item_pos_inventoryName)
        TextView nameTextView;

        public POSInventoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void addInventory(Inventory.InventoryData inventoryData) {
        inventories.add(inventoryData);
        notifyDataSetChanged();
    }

    public POSInventoryAdapter(List<Inventory.InventoryData> inventories, OnItemClickListener onClickListener, Context mContext) {
        this.inventories = inventories;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllInventories() {
        inventories = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public POSInventoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pos_inventory, parent, false);

        return new POSInventoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(POSInventoryHolder holder, final int position) {
        Inventory.InventoryData inventoryData = inventories.get(position);

        holder.nameTextView.setText(inventoryData.item_name);

        ((View) holder.inventoryImageView.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });
    }

    public Inventory.InventoryData getItem(int position) {
        return inventories.get(position);
    }

    @Override
    public int getItemCount() {
        return inventories.size();
    }


}