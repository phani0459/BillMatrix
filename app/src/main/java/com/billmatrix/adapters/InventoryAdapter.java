package com.billmatrix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
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
    private InventoryFragment inventoryFragment;
    private View lastChecked = null;
    private int lastCheckedPos = -1;

    // Start with first item selected
    private int focusedItem = -1;

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

        public VendorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Handle item click and set the selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inventoryFragment.isEditing) {
                        // Redraw the old selection and the new
                        if (focusedItem != getLayoutPosition()) {
                            notifyItemChanged(focusedItem);
                            onItemClickListener.onItemClick(4, focusedItem);

                            focusedItem = getLayoutPosition();
                            notifyItemChanged(focusedItem);
                            onItemClickListener.onItemClick(3, focusedItem);
                        } else {
                            focusedItem = -1;
                            notifyItemChanged(getLayoutPosition());
                            onItemClickListener.onItemClick(4, getLayoutPosition());
                        }
                    } else {
                        Utils.showToast("Please edit selected inventory before selecting other inventory", mContext);
                    }
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return tryMoveSelection(lm, 1);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return tryMoveSelection(lm, -1);
                    }
                }

                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int tryFocusItem = focusedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            notifyItemChanged(focusedItem);
            focusedItem = tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);
            return true;
        }

        return false;
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
    public void onBindViewHolder(final VendorHolder holder, int position) {
        Inventory.InventoryData inventoryData = inventoryDatas.get(position);

        // Set selected state; use a state list drawable to style the view
        holder.itemView.setSelected(focusedItem == position);

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
                focusedItem = -1;
                holder.itemView.setSelected(false);
                onItemClickListener.onItemClick(1, holder.getAdapterPosition());
            }
        });

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusedItem = -1;
                holder.itemView.setSelected(false);
                onItemClickListener.onItemClick(2, holder.getAdapterPosition());
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