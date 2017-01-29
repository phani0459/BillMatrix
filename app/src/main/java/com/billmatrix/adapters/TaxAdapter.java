package com.billmatrix.adapters;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Tax;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaxAdapter extends RecyclerView.Adapter<TaxAdapter.VendorHolder> {

    private final Context mContext;
    private ArrayMap<String, Float> selectedtaxes;
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

        VendorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteTax(int position, boolean isEdit) {
        if (!isEdit && selectedtaxes.keySet().contains(taxes.get(position).tax_type)) {
            selectedtaxes.remove(taxes.get(position).tax_type);
            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_TAX_JSON, Constants.getGson().toJson(selectedtaxes)).apply();
        }
        taxes.remove(position);
        notifyDataSetChanged();
    }

    public void addTax(Tax.TaxData taxData, boolean isAdd) {
        if (!isAdd && selectedtaxes.keySet().contains(taxData.tax_type)) {
            selectedtaxes.put(taxData.tax_type, Float.parseFloat(taxData.tax_rate));
            Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_TAX_JSON, Constants.getGson().toJson(selectedtaxes)).apply();
        }
        taxes.add(taxData);
        notifyDataSetChanged();
    }

    public TaxAdapter(List<Tax.TaxData> taxes, OnItemClickListener onClickListener, Context mContext) {
        this.taxes = taxes;
        this.onItemClickListener = onClickListener;
        this.mContext = mContext;
        selectedtaxes = new ArrayMap<String, Float>();

        String selectedTaxJSON = Utils.getSharedPreferences(mContext).getString(Constants.PREF_TAX_JSON, "");
        if (!TextUtils.isEmpty(selectedTaxJSON)) {
            selectedtaxes = Constants.getGson().fromJson(selectedTaxJSON, Constants.floatArrayMapType);
        }
    }

    public void removeAllTaxes() {
        taxes = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VendorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tax_item, parent, false);

        return new VendorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VendorHolder holder, final int position) {
        final Tax.TaxData taxData = taxes.get(position);

        holder.snoCheckBox.setText((position + 1) + "");
        holder.taxTypeTextView.setText(taxData.tax_type);
        holder.taxDescTextView.setText(taxData.tax_description);
        holder.taxRateTextView.setText(taxData.tax_rate.trim());
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

        /**
         * to show previously selected tax as selected
         */
        holder.snoCheckBox.setChecked(false);
        for (String selectedTaxType : selectedtaxes.keySet()) {
            if (selectedTaxType.equalsIgnoreCase(taxData.tax_type)) {
                holder.snoCheckBox.setChecked(true);
            }
        }

        holder.snoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedtaxes.keySet().contains(taxData.tax_type)) {
                    selectedtaxes.remove(taxData.tax_type);
                } else {
                    try {
                        selectedtaxes.put(taxData.tax_type, Float.parseFloat(taxData.tax_rate));
                    } catch (NumberFormatException e) {
                        selectedtaxes.put(taxData.tax_type, 0.0f);
                        e.printStackTrace();
                    }
                }
                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_TAX_JSON, Constants.getGson().toJson(selectedtaxes)).apply();
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