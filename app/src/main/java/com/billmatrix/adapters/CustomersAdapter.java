package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerHolder> {

    private List<Customer.CustomerData> customers;
    OnItemClickListener onItemClickListener;

    public class CustomerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_customerSno)
        TextView snoTextView;
        @BindView(R.id.tv_item_cust_name)
        TextView nameTextView;
        @BindView(R.id.tv_item_cust_contact)
        TextView contactTextView;
        @BindView(R.id.tv_item_cust_date)
        TextView dateTextView;
        @BindView(R.id.tv_item_cust_location)
        TextView locationTextView;
        @BindView(R.id.tv_item_cust_status)
        TextView statusTextView;
        @BindView(R.id.im_item_customer_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_customer_del)
        ImageView deleteImageView;

        public CustomerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteCustomer(int position) {
        customers.remove(position);
        notifyDataSetChanged();
    }

    public void addCustomer(Customer.CustomerData customerData) {
        customers.add(customerData);
        notifyDataSetChanged();
    }

    public CustomersAdapter(List<Customer.CustomerData> customers, OnItemClickListener onItemClickListener) {
        this.customers = customers;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAllCustomers() {
        customers = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);

        return new CustomerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomerHolder holder, final int position) {
        Customer.CustomerData customerData = customers.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(customerData.username);
        holder.contactTextView.setText(customerData.mobile_number);
        holder.dateTextView.setText(customerData.date.trim());
        holder.locationTextView.setText(customerData.location.trim());
        holder.statusTextView.setText(customerData.status);
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(1, position);
            }
        });
    }

    public Customer.CustomerData getItem(int position) {
        return customers.get(position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }
}