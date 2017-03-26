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
import com.billmatrix.fragments.InventoryFragment;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.VendorHolder> {

    private final Context mContext;
    private final BillMatrixDaoImpl billMatrixDaoImpl;
    private List<Inventory.InventoryData> inventoryDatas;
    OnItemClickListener onItemClickListener;
    InventoryFragment inventoryFragment;
    private View lastChecked = null;
    private int lastCheckedPos = -1;

    public class VendorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_inven_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_inven_name)
        TextView itemNameTextView;
        @BindView(R.id.tv_item_inven_itemcode)
        TextView itemCodeTextView;
        @BindView(R.id.tv_item_inven_unit)
        TextView unitTextView;
        @BindView(R.id.tv_item_inven_qty)
        TextView qtyTextView;
        @BindView(R.id.tv_item_inven_price)
        TextView priceTextView;
        @BindView(R.id.tv_item_inven_my_cost)
        TextView myCostTextView;
        @BindView(R.id.tv_item_inven_date)
        TextView dateTextView;
        @BindView(R.id.tv_item_inven_ware_house)
        TextView wareHouseTextView;
        @BindView(R.id.tv_item_inven_vendor)
        TextView vendorTextView;
        @BindView(R.id.tv_item_inven_bar_code)
        TextView barCodeTextView;
        @BindView(R.id.tv_item_inven_photo)
        TextView photoTextView;
        @BindView(R.id.im_item_Inven_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_Inven_del)
        ImageView deleteImageView;

        public VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteInventory(int position) {
        inventoryDatas.remove(position);
        notifyDataSetChanged();
    }

    public void addInventory(Inventory.InventoryData inventoryData) {
        inventoryDatas.add(inventoryData);
        notifyDataSetChanged();
    }

    public InventoryAdapter(List<Inventory.InventoryData> inventoryDatas, InventoryFragment inventoryFragment, Context mContext) {
        this.inventoryDatas = inventoryDatas;
        this.onItemClickListener = (OnItemClickListener) inventoryFragment;
        this.inventoryFragment = inventoryFragment;
        this.mContext = mContext;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorHolder holder, final int position) {
        Inventory.InventoryData inventoryData = inventoryDatas.get(position);

        final View itemView = (View) holder.snoTextView.getParent();

        holder.snoTextView.setText("" + (position + 1));
        holder.itemCodeTextView.setText(inventoryData.item_code);
        holder.itemNameTextView.setText(inventoryData.item_name);
        holder.unitTextView.setText(inventoryData.unit);
        holder.qtyTextView.setText(inventoryData.qty);
        holder.priceTextView.setText(inventoryData.price);
        holder.myCostTextView.setText(inventoryData.mycost);
        holder.dateTextView.setText(inventoryData.date);
        holder.wareHouseTextView.setText(inventoryData.warehouse);
        holder.vendorTextView.setText(billMatrixDaoImpl.getVendorName(inventoryData.vendor));
        holder.barCodeTextView.setText(inventoryData.barcode);
        holder.photoTextView.setText(inventoryData.photo);

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastChecked != null) {
                    lastCheckedPos = -1;
                    lastChecked.setSelected(false);
                }
                onItemClickListener.onItemClick(1, position);
            }
        });

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastChecked != null) {
                    lastCheckedPos = -1;
                    lastChecked.setSelected(false);
                }
                onItemClickListener.onItemClick(2, position);
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inventoryFragment.isEditing) {
                    if (lastCheckedPos != position) {

                        if (lastChecked != null) {
                            lastChecked.setSelected(false);
                        }

                        v.setSelected(true);
                        lastCheckedPos = position;
                        lastChecked = v;
                        onItemClickListener.onItemClick(3, position);
                    } else {
                        if (lastChecked != null) {
                            lastCheckedPos = -1;
                            lastChecked.setSelected(false);
                            onItemClickListener.onItemClick(4, position);
                        }
                    }
                } else {
                    Utils.showToast("Please edit selected inventory before selecting other inventory", mContext);
                }
            }
        });
    }

    public Inventory.InventoryData getItem(int position) {
        return inventoryDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return inventoryDatas.size();
    }


}