package com.billmatrix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Inventory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemReportAdapter extends RecyclerView.Adapter<ItemReportAdapter.VendorHolder> {

    private final BillMatrixDaoImpl billMatrixDaoImpl;
    private List<Inventory.InventoryData> inventoryDatas;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_item_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_item_item_name)
        TextView itemNameTextView;
        @BindView(R.id.tv_item_item_avial_qty)
        TextView qtyTextView;
        @BindView(R.id.tv_item_item_mycost)
        TextView myCostTextView;
        @BindView(R.id.tv_item_item_date)
        TextView dateTextView;
        @BindView(R.id.tv_item_item_vendor)
        TextView vendorTextView;

        public VendorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void deleteInventory(int position) {
        inventoryDatas.remove(position);
        notifyDataSetChanged();
    }

    public void addAllInventory(ArrayList<Inventory.InventoryData> inventorys) {
        inventoryDatas.addAll(inventorys);
        notifyDataSetChanged();
    }

    public ItemReportAdapter(List<Inventory.InventoryData> inventoryDatas, Context mContext) {
        this.inventoryDatas = inventoryDatas;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
    }

    public void removeAllInventories() {
        inventoryDatas = new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean containsInventory(String barcode) {
        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!TextUtils.isEmpty(inventoryData.barcode) && inventoryData.barcode.equalsIgnoreCase(barcode)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_report, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VendorHolder holder, int position) {
        Inventory.InventoryData inventoryData = inventoryDatas.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.itemNameTextView.setText(inventoryData.item_name);
        holder.qtyTextView.setText(inventoryData.selectedQTY);
        holder.myCostTextView.setText(inventoryData.mycost);
        holder.dateTextView.setText(inventoryData.date);
        holder.vendorTextView.setText(billMatrixDaoImpl.getVendorName(inventoryData.vendor));
    }

    public Inventory.InventoryData getItem(int position) {
        return inventoryDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return inventoryDatas.size();
    }


}