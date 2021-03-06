package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Payments;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenExpensesAdapter extends RecyclerView.Adapter<GenExpensesAdapter.ExpenseHolder> {

    private List<Payments.PaymentData> genExpenses;
    OnItemClickListener onItemClickListener;

    public class ExpenseHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_gen_expense_sno)
        TextView snoTextView;
        @BindView(R.id.tv_item_gen_expense_expenseName)
        TextView nameTextView;
        @BindView(R.id.tv_item_gen_expense_purpose)
        TextView purposeTextView;
        @BindView(R.id.tv_item_gen_expense_amount)
        TextView amountTextView;
        @BindView(R.id.tv_item_gen_expense_date)
        TextView dateTextView;
        @BindView(R.id.im_item_gen_expense_edit)
        ImageView editImageView;
        @BindView(R.id.im_item_gen_expense_del)
        ImageView deleteImageView;

        public ExpenseHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void deleteGenExpense(int position) {
        genExpenses.remove(position);
        notifyDataSetChanged();
    }

    public void addGenExpense(Payments.PaymentData paymentData) {
        genExpenses.add(paymentData);
        notifyDataSetChanged();
    }

    public GenExpensesAdapter(List<Payments.PaymentData> genExpenses, OnItemClickListener onClickListener) {
        this.genExpenses = genExpenses;
        this.onItemClickListener = onClickListener;
    }

    public void removeAllGenExpenses() {
        genExpenses = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ExpenseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gen_expense_item, parent, false);

        return new ExpenseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseHolder holder, final int position) {
        Payments.PaymentData paymentData = genExpenses.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(paymentData.payee_name);
        holder.purposeTextView.setText(paymentData.purpose_of_payment);
        holder.dateTextView.setText(paymentData.date_of_payment);
        holder.amountTextView.setText(paymentData.amount.trim());
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

    public Payments.PaymentData getItem(int position) {
        return genExpenses.get(position);
    }

    @Override
    public int getItemCount() {
        return genExpenses.size();
    }


}