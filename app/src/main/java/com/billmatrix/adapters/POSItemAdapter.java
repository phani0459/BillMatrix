package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.models.Inventory;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class POSItemAdapter extends RecyclerView.Adapter<POSItemAdapter.POSInventoryHolder> {

    public List<Inventory.InventoryData> inventories;
    public List<String> inventoryIDs;
    OnItemSelected onItemSelected;
    private View lastChecked = null;
    private int lastCheckedPos = -1;

    public class POSInventoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_pos_item_del)
        ImageView deleteImageView;
        @BindView(R.id.tv_pos_item_item_code)
        TextView itemCodeTextView;
        @BindView(R.id.tv_pos_item_item_name)
        TextView itemNameTextView;
        @BindView(R.id.tv_pos_item_unit)
        TextView unitTextView;
        @BindView(R.id.enbtn_pos_item_qty)
        ElegantNumberButton qtyNumberButton;
        @BindView(R.id.tv_pos_item_price)
        TextView priceTextView;
        @BindView(R.id.tv_pos_item_total)
        TextView totalTextView;

        public POSInventoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public POSItemAdapter(List<Inventory.InventoryData> inventories, OnItemSelected onItemSelected) {
        this.inventories = inventories;
        this.onItemSelected = onItemSelected;
        inventoryIDs = new ArrayList<>();
    }

    public void removeItem(int position) {
        inventoryIDs.remove(inventories.get(position).id);
        inventories.remove(position);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        inventories = new ArrayList<>();
        inventoryIDs = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addInventory(Inventory.InventoryData inventoryData) {
        inventories.add(inventoryData);
        inventoryIDs.add(inventoryData.id);
        notifyItemChanged(inventories.size());
    }

    public Inventory.InventoryData getInventoryonID(String id) {
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).id.equals(id)) {
                return inventories.get(i);
            }
        }
        return null;
    }

    public void changeInventory(Inventory.InventoryData inventoryData) {
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).id.equalsIgnoreCase(inventoryData.id)) {
                inventories.set(i, inventoryData);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public POSInventoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_items_item, parent, false);

        return new POSInventoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final POSInventoryHolder holder, final int position) {
        final Inventory.InventoryData inventoryData = inventories.get(position);
        String unit = !TextUtils.isEmpty(inventoryData.unit) ? inventoryData.unit : "";
        final String price = !TextUtils.isEmpty(inventoryData.price) ? inventoryData.price : "1";

        if (unit.length() > 0) {
            unit = unit.substring(0, unit.length() - 1);
        }

        final View itemView = (View) holder.priceTextView.getParent();

        holder.qtyNumberButton.setNumber(inventoryData.selectedQTY, true);
        holder.qtyNumberButton.setRange(1, Integer.parseInt(inventoryData.qty));

        holder.itemCodeTextView.setText(inventoryData.item_code.toUpperCase());
        holder.itemNameTextView.setText(inventoryData.item_name.toUpperCase());
        holder.unitTextView.setText(unit.toUpperCase() + "s");
        holder.priceTextView.setText(inventoryData.price);

        final int quantity = Integer.parseInt(holder.qtyNumberButton.getNumber());
        float totalPrice = Float.parseFloat(price) * quantity;

        holder.totalTextView.setText(String.format(Locale.getDefault(), "%.2f", totalPrice));

        holder.qtyNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                float totalPrice = Float.parseFloat(price) * newValue;
                inventoryData.selectedQTY = newValue + "";
                holder.totalTextView.setText(String.format(Locale.getDefault(), "%.2f", totalPrice));
                onItemSelected.itemSelected(0, position);
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemSelected.itemSelected(1, position);
            }
        });

        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastCheckedPos != position) {

                    if (lastChecked != null) {
                        lastChecked.setSelected(false);
                    }

                    v.setSelected(true);
                    lastCheckedPos = position;
                    lastChecked = v;
                    onItemSelected.itemSelected(3, position);
                } else {
                    if (lastChecked != null) {
                        lastCheckedPos = -1;
                        lastChecked.setSelected(false);
                        onItemSelected.itemSelected(4, position);
                    }
                }
            }
        });*/
    }

    public Inventory.InventoryData getItem(int position) {
        return inventories.get(position);
    }

    @Override
    public int getItemCount() {
        return inventories.size();
    }

    public interface OnItemSelected {
        void itemSelected(int caseInt, int position);
    }


}