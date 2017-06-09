package com.billmatrix.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Transaction;
import com.billmatrix.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesReportsAdapter extends RecyclerView.Adapter<SalesReportsAdapter.ReportHolder> {

    private final BillMatrixDaoImpl billMatrixDaoImpl;
    private List<Transaction> reports;
    private Fragment fragment;
    private ArrayList<Inventory.InventoryData> totalInventories;
    private String salesType;
    private String salesItemType;

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public void setSalesItemType(String salesItemType) {
        this.salesItemType = salesItemType;
    }

    public class ReportHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sale_rep_item_sno)
        TextView snoTextView;
        @BindView(R.id.tv_sale_rep_item_h3)
        TextView itemNameTextView;
        @BindView(R.id.tv_sale_rep_item_h4)
        TextView vendorTextView;
        @BindView(R.id.tv_sale_rep_item_date)
        TextView dateTextView;
        @BindView(R.id.tv_sale_rep_item_h5)
        TextView costTextView;
        @BindView(R.id.tv_sale_rep_item_h6)
        TextView qtyTextView;

        public ReportHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteReport(int position) {
        reports.remove(position);
        notifyDataSetChanged();
    }

    public void addAllReports(ArrayList<Transaction> transactions) {
        reports.addAll(transactions);
        fetchInventoriesFromTransactions();
        notifyDataSetChanged();
    }

    public void addAllInventories(ArrayList<Inventory.InventoryData> transactions) {
        totalInventories.addAll(transactions);
        if (totalInventories != null && totalInventories.size() > 0) {
            Collections.sort(totalInventories, getDateComparator());
        }

        notifyDataSetChanged();
    }

    public void addReport(Transaction transaction) {
        reports.add(transaction);
        fetchInventoriesFromTransactions();
        notifyDataSetChanged();
    }

    private void fetchInventoriesFromTransactions() {
        totalInventories = new ArrayList<>();
        for (int i = 0; i < reports.size(); i++) {
            ArrayList<Inventory.InventoryData> transInventories = Constants.getGson().fromJson(reports.get(i).inventoryJson, Constants.inventoryDatasMapType);
            if (transInventories.size() > 0) {
                for (int j = 0; j < transInventories.size(); j++) {
                    transInventories.get(j).transactionDate = reports.get(i).date;
                    totalInventories.add(transInventories.get(j));
                }
            }
        }

        if (!TextUtils.isEmpty(salesType)) {
            switch (salesType) {
                case "SALE BY ITEM":
                    if (!TextUtils.isEmpty(salesItemType)) {
                        ArrayList<Inventory.InventoryData> localInventories = new ArrayList<>();
                        if (totalInventories.size() > 0) {
                            for (int i = 0; i < totalInventories.size(); i++) {
                                if (totalInventories.get(i).item_name.equalsIgnoreCase(salesItemType)) {
                                    localInventories.add(totalInventories.get(i));
                                }
                            }
                        }
                        totalInventories = new ArrayList<>();
                        totalInventories = localInventories;
                    }
                    break;
                case "SALE BY VENDOR":
                    if (!TextUtils.isEmpty(salesItemType)) {
                        ArrayList<Inventory.InventoryData> localInventories = new ArrayList<>();
                        if (totalInventories.size() > 0) {
                            for (int i = 0; i < totalInventories.size(); i++) {
                                if (totalInventories.get(i).vendor.equalsIgnoreCase(salesItemType)) {
                                    localInventories.add(totalInventories.get(i));
                                }
                            }
                        }
                        totalInventories = new ArrayList<>();
                        totalInventories = localInventories;
                        getReportsByVendor();
                    }
                    break;
                case "SALE PROFIT":
                    getReportsByProfit();
                    break;
                case "SALE BY DISCOUNT":
                    getReportsByDiscount();
                    break;
            }
        }

        if (totalInventories.size() > 0) {
            Collections.sort(totalInventories, getTransDateComparator());
        }
    }

    private void getReportsByDiscount() {
        ArrayList<Transaction> localReports = new ArrayList<>();
        ArrayList<Transaction> localDateReports = new ArrayList<>();
        String totalAmount = "";
        String totalDiscountedAmt = "";

        if (reports.size() > 0) {
            for (int i = 0; i < reports.size(); i++) {

                if (reports.get(i).discountCodeApplied.equalsIgnoreCase(salesItemType)) {
                    localReports.add(reports.get(i));
                }

                float previousAmt = TextUtils.isEmpty(totalAmount) ? 0 : Float.parseFloat(totalAmount);
                float presentAmt = TextUtils.isEmpty(reports.get(i).totalAmount) ? 0 : Float.parseFloat(reports.get(i).totalAmount);

                if (reports.get(i).discountCodeApplied.equalsIgnoreCase(salesItemType)) {
                    float previousDiscountedAmt = TextUtils.isEmpty(totalDiscountedAmt) ? 0 : Float.parseFloat(totalDiscountedAmt);
                    float presentDiscountedAmt = TextUtils.isEmpty(reports.get(i).totalAmount) ? 0 : Float.parseFloat(reports.get(i).totalAmount);
                    totalDiscountedAmt = String.format(Locale.getDefault(), "%.2f", (previousDiscountedAmt + presentDiscountedAmt));
                }

                totalAmount = String.format(Locale.getDefault(), "%.2f", (previousAmt + presentAmt));
            }
        }

        boolean hasDate = false;
        if (localReports.size() > 0) {
            for (int i = 0; i < localReports.size(); i++) {
                if (localDateReports.size() > 0) {
                    for (int j = 0; j < localDateReports.size(); j++) {
                        if (localDateReports.get(j).date.equalsIgnoreCase(localReports.get(i).date)) {
                            hasDate = true;
                            float presentDiscount = TextUtils.isEmpty(localReports.get(i).totalDiscount) ? 0 : Float.parseFloat(localReports.get(i).totalDiscount);
                            float previousDiscount = TextUtils.isEmpty(localDateReports.get(j).totalDiscount) ? 0 : Float.parseFloat(localDateReports.get(j).totalDiscount);
                            localDateReports.get(j).totalDiscount = String.format(Locale.getDefault(), "%.2f", (previousDiscount + presentDiscount));
                            break;
                        }
                    }
                    if (!hasDate) {
                        localReports.get(i).totalAmount = totalAmount;
                        localReports.get(i).totalDiscountedAmt = totalDiscountedAmt;
                        localDateReports.add(localReports.get(i));
                    }
                } else {
                    localReports.get(i).totalAmount = totalAmount;
                    localReports.get(i).totalDiscountedAmt = totalDiscountedAmt;
                    localDateReports.add(localReports.get(i));
                }
            }
        }

        reports = new ArrayList<>();
        reports = localDateReports;
    }

    private void getReportsByProfit() {
        ArrayList<Inventory.InventoryData> profitInventories = new ArrayList<>();
        if (totalInventories.size() > 0) {
            for (int i = 0; i < totalInventories.size(); i++) {
                boolean hasDate = false;
                if (profitInventories.size() > 0) {
                    for (int j = 0; j < profitInventories.size(); j++) {
                        if (profitInventories.get(j).transactionDate.equalsIgnoreCase(totalInventories.get(i).transactionDate)) {
                            hasDate = true;
                            float itemQty = Float.parseFloat(totalInventories.get(i).selectedQTY);
                            float itemPrice = TextUtils.isEmpty(totalInventories.get(i).price) ? 0 : Float.parseFloat(totalInventories.get(i).price);
                            float itemMyCost = TextUtils.isEmpty(totalInventories.get(i).mycost) ? 0 : Float.parseFloat(totalInventories.get(i).mycost);
                            float discountSelected = TextUtils.isEmpty(totalInventories.get(i).discountValue) ? 0 : Float.parseFloat(totalInventories.get(i).discountValue);
                            totalInventories.get(i).totalSales = (itemQty * itemPrice) - ((itemQty * itemPrice * discountSelected) / 100);
                            totalInventories.get(i).totalProfit = totalInventories.get(i).totalSales - (itemMyCost * itemQty);
                            profitInventories.get(j).totalSales = profitInventories.get(j).totalSales + totalInventories.get(i).totalSales;
                            profitInventories.get(j).totalProfit = profitInventories.get(j).totalProfit + totalInventories.get(i).totalProfit;
                        }
                    }

                    if (!hasDate) {
                        float itemQty = Float.parseFloat(totalInventories.get(i).selectedQTY);
                        float itemPrice = TextUtils.isEmpty(totalInventories.get(i).price) ? 0 : Float.parseFloat(totalInventories.get(i).price);
                        float itemMyCost = TextUtils.isEmpty(totalInventories.get(i).mycost) ? 0 : Float.parseFloat(totalInventories.get(i).mycost);
                        float discountSelected = TextUtils.isEmpty(totalInventories.get(i).discountValue) ? 0 : Float.parseFloat(totalInventories.get(i).discountValue);
                        totalInventories.get(i).totalSales = (itemQty * itemPrice) - ((itemQty * itemPrice * discountSelected) / 100);
                        totalInventories.get(i).totalProfit = totalInventories.get(i).totalSales - (itemMyCost * itemQty);
                        profitInventories.add(totalInventories.get(i));
                    }
                } else {
                    float itemQty = Float.parseFloat(totalInventories.get(i).selectedQTY);
                    float itemPrice = TextUtils.isEmpty(totalInventories.get(i).price) ? 0 : Float.parseFloat(totalInventories.get(i).price);
                    float itemMyCost = TextUtils.isEmpty(totalInventories.get(i).mycost) ? 0 : Float.parseFloat(totalInventories.get(i).mycost);
                    float discountSelected = TextUtils.isEmpty(totalInventories.get(i).discountValue) ? 0 : Float.parseFloat(totalInventories.get(i).discountValue);
                    totalInventories.get(i).totalSales = (itemQty * itemPrice) - ((itemQty * itemPrice * discountSelected) / 100);
                    totalInventories.get(i).totalProfit = totalInventories.get(i).totalSales - (itemMyCost * itemQty);
                    profitInventories.add(totalInventories.get(i));
                }
            }
        }

        totalInventories = new ArrayList<>();
        totalInventories = profitInventories;
    }

    private void getReportsByVendor() {
        HashSet<String> dates = new HashSet<>();
        ArrayList<Inventory.InventoryData> vendorInventories = new ArrayList<>();
        ArrayList<Inventory.InventoryData> vendorDateInventories = new ArrayList<>();
        if (totalInventories.size() > 0) {
            for (int i = 0; i < totalInventories.size(); i++) {
                if (totalInventories.get(i).vendor.equalsIgnoreCase(salesItemType)) {
                    float oldItemQty = Float.parseFloat(totalInventories.get(i).selectedQTY);
                    float oldItemPrice = TextUtils.isEmpty(totalInventories.get(i).price) ? 0 : Float.parseFloat(totalInventories.get(i).price);
                    totalInventories.get(i).totalSales = oldItemQty * oldItemPrice;
                    vendorInventories.add(totalInventories.get(i));
                }
            }

            if (vendorInventories.size() > 0) {
                for (int i = 0; i < vendorInventories.size(); i++) {
                    dates.add(vendorInventories.get(i).transactionDate);
                }

                for (String date : dates) {
                    boolean hasDate = false;
                    for (int i = 0; i < vendorInventories.size(); i++) {
                        if (date.equalsIgnoreCase(vendorInventories.get(i).transactionDate)) {
                            if (vendorDateInventories.size() <= 0) {
                                vendorDateInventories.add(vendorInventories.get(i));
                            } else {
                                for (int j = 0; j < vendorDateInventories.size(); j++) {
                                    if (vendorDateInventories.get(j).transactionDate.equalsIgnoreCase(vendorInventories.get(i).transactionDate)) {
                                        hasDate = true;
                                        vendorDateInventories.get(j).totalSales = vendorDateInventories.get(j).totalSales + vendorInventories.get(i).totalSales;
                                    }
                                }

                                if (!hasDate) {
                                    vendorDateInventories.add(vendorInventories.get(i));
                                }
                            }
                        }
                    }
                }
            }

            totalInventories = new ArrayList<>();
            totalInventories = vendorDateInventories;
        }
    }

    private Comparator<Inventory.InventoryData> getTransDateComparator() {
        return new Comparator<Inventory.InventoryData>() {
            @Override
            public int compare(Inventory.InventoryData dataOne, Inventory.InventoryData dataTwo) {
                return dataOne.transactionDate.compareTo(dataTwo.transactionDate);
            }
        };
    }

    private Comparator<Inventory.InventoryData> getDateComparator() {
        return new Comparator<Inventory.InventoryData>() {
            @Override
            public int compare(Inventory.InventoryData dataOne, Inventory.InventoryData dataTwo) {
                return dataOne.date.compareTo(dataTwo.date);
            }
        };
    }

    public SalesReportsAdapter(List<Transaction> reports, Fragment fragment, BillMatrixDaoImpl billMatrixDaoImpl) {
        this.reports = reports;
        this.fragment = fragment;
        this.billMatrixDaoImpl = billMatrixDaoImpl;
        totalInventories = new ArrayList<>();
    }

    public void removeAllReports() {
        reports = new ArrayList<>();
        totalInventories = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_report, parent, false);

        return new ReportHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportHolder holder, final int position) {
        Inventory.InventoryData inventoryData = null;
        Transaction transaction = null;
        if (salesType.equalsIgnoreCase("SALE BY DISCOUNT")) {
            transaction = reports.get(position);
        } else {
            inventoryData = totalInventories.get(position);
        }

        holder.itemNameTextView.setVisibility(View.VISIBLE);
        holder.qtyTextView.setVisibility(View.VISIBLE);
        holder.vendorTextView.setVisibility(View.VISIBLE);
        holder.costTextView.setVisibility(View.VISIBLE);

        holder.snoTextView.setText("" + (position + 1));

        switch (salesType) {
            case "PURCHASE TOTAL":
            case "PURCHASE BY VENDOR":
                holder.dateTextView.setText(inventoryData.date);
                break;
            default:
                String dateString = null;
                if (inventoryData != null) {
                    dateString = inventoryData.transactionDate.trim();
                } else if (transaction != null) {
                    dateString = transaction.date.trim();
                }

                if (!TextUtils.isEmpty(dateString)) {
                    try {
                        holder.dateTextView.setText(Constants.getDateFormat().format(Constants.getSQLiteDateFormat().parse(dateString).getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        switch (salesType) {
            case "SALE TOTAL":
            case "SALE BY ITEM":
                if (inventoryData != null) {
                    holder.itemNameTextView.setText(inventoryData.item_name);
                    if (!TextUtils.isEmpty(inventoryData.selectedQTY)) {
                        holder.qtyTextView.setText(inventoryData.selectedQTY);
                    }
                }

                holder.vendorTextView.setVisibility(View.GONE);
                holder.costTextView.setVisibility(View.GONE);
                break;
            case "SALE BY VENDOR":
                if (inventoryData != null) {
                    holder.vendorTextView.setText(billMatrixDaoImpl.getVendorName(inventoryData.vendor));
                    holder.qtyTextView.setText(String.format(Locale.getDefault(), "%.2f", inventoryData.totalSales));
                }

                holder.itemNameTextView.setVisibility(View.GONE);
                holder.costTextView.setVisibility(View.GONE);
                break;
            case "SALE PROFIT":
                if (inventoryData != null) {
                    holder.vendorTextView.setText(String.format(Locale.getDefault(), "%.2f", inventoryData.totalSales));
                    holder.costTextView.setText(String.format(Locale.getDefault(), "%.2f", inventoryData.totalProfit));
                }

                holder.itemNameTextView.setVisibility(View.GONE);
                holder.qtyTextView.setVisibility(View.GONE);
                break;
            case "SALE BY DISCOUNT":
                if (transaction != null) {
                    holder.itemNameTextView.setText(transaction.totalDiscountedAmt);
                    holder.vendorTextView.setText(transaction.totalAmount);
                    holder.costTextView.setText(transaction.totalDiscount);
                    holder.qtyTextView.setText(salesItemType);
                }
                break;
            case "PURCHASE TOTAL":
            case "PURCHASE BY VENDOR":
                if (inventoryData != null) {
                    holder.itemNameTextView.setText(inventoryData.item_name);
                    holder.vendorTextView.setText(billMatrixDaoImpl.getVendorName(inventoryData.vendor));
                    holder.costTextView.setText(inventoryData.price);
                    holder.qtyTextView.setText(inventoryData.qty);
                }
                break;
        }
    }

    public Object getItem(int position) {
        if (!TextUtils.isEmpty(salesType) && salesType.equalsIgnoreCase("SALE BY DISCOUNT")) {
            return reports.get(position);
        }
        return totalInventories.get(position);
    }

    @Override
    public int getItemCount() {
        if (!TextUtils.isEmpty(salesType) && salesType.equalsIgnoreCase("SALE BY DISCOUNT")) {
            return reports.size();
        }
        return totalInventories.size();
    }
}