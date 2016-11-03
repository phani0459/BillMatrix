package com.billmatrix.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.models.Employee;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.EmployeeHolder> {

    private List<Employee.EmployeeData> employees;

    public class EmployeeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_emp_sno)
        TextView snoTextView;
        @BindView(R.id.tv_emp_name)
        TextView nameTextView;
        @BindView(R.id.tv_emp_loginId)
        TextView loginIdTextView;
        @BindView(R.id.tv_emp_pwd)
        TextView pwdTextView;
        @BindView(R.id.tv_emp_mob)
        TextView mobileTextView;
        @BindView(R.id.tv_emp_status)
        TextView statusTextView;
        @BindView(R.id.im_emp_edit)
        ImageView editImageView;
        @BindView(R.id.im_emp_del)
        ImageView deleteImageView;

        public EmployeeHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public void addEmployee(Employee.EmployeeData employeeData) {
        employees.add(employeeData);
        notifyDataSetChanged();
    }

    public EmployeesAdapter(List<Employee.EmployeeData> employees) {
        this.employees = employees;
    }


    @Override
    public EmployeeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item, parent, false);

        return new EmployeeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmployeeHolder holder, int position) {
        Employee.EmployeeData employee = employees.get(position);

        holder.snoTextView.setText("" + (position + 1));
        holder.nameTextView.setText(employee.name);
        holder.loginIdTextView.setText(employee.email);
        holder.mobileTextView.setText(employee.number);
        holder.pwdTextView.setText(employee.password);
        holder.statusTextView.setText(employee.status);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }
}