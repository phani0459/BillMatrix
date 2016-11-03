package com.billmatrix.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.adapters.EmployeesAdapter;
import com.billmatrix.models.Employee;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KANDAGATLAs on 23-10-2016.
 */

public class EmployeesActivity extends BaseTabActivity {

    private static final String TAG = EmployeesActivity.class.getSimpleName();
    @BindView(R.id.employees)
    public View employeesLayout;
    @BindView(R.id.employeesList)
    public RecyclerView employeesRecyclerView;
    @BindView(R.id.et_emp_name)
    public EditText empName_EditText;
    @BindView(R.id.et_emp_loginId)
    public EditText empLoginId_EditText;
    @BindView(R.id.et_emp_pwd)
    public EditText empPwd_EditText;
    @BindView(R.id.et_emp_mob)
    public EditText empMobile_EditText;
    @BindView(R.id.sp_emp_status)
    public Spinner empStatusSpinner;

    private EmployeesAdapter employeesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setPageTitle("<span>" + getArrowString() + " Employees </span>");
        addTabButtons(1, "Employees");

        employeesLayout.setVisibility(View.VISIBLE);

        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Employee.EmployeeData> employees = new ArrayList<>();

        employeesAdapter = new EmployeesAdapter(employees);
        employeesRecyclerView.setAdapter(employeesAdapter);

        if (Utils.isInternetAvailable(mContext)) {
            if (!FileUtils.isFileExists(Constants.EMPLOYEES_FILE_NAME, mContext)) {
                getEmployeesFromServer();
            }
        }

        if (FileUtils.isFileExists(Constants.EMPLOYEES_FILE_NAME, mContext)) {
            String employeeString = FileUtils.readFromFile(Constants.EMPLOYEES_FILE_NAME, mContext);
            Employee employee = Constants.getGson().fromJson(employeeString, Employee.class);
            employeesAdapter.addEmployee(employee.data);
        }
    }

    Employee employee;

    public void getEmployeesFromServer() {
        Call<Employee> call = Utils.getBillMatrixAPI(mContext).getEmployees();

        call.enqueue(new Callback<Employee>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.body() != null) {
                    employee = response.body();
                    Log.e("SUCCEESS RESPONSE RAW", employee.employeedata + "");
                    if (employee.status == 200 && employee.employeedata.equalsIgnoreCase("success")) {
                        FileUtils.writeToFile(mContext, Constants.EMPLOYEES_FILE_NAME, Constants.getGson().toJson(employee));
                        employeesAdapter.addEmployee(employee.data);
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    @OnClick(R.id.btn_addEmployee)
    public void addEmployee() {

        Utils.hideSoftKeyboard(empName_EditText);

        Employee.EmployeeData employeeData = new Employee().new EmployeeData();
        String empName = empName_EditText.getText().toString();
        String empId = empLoginId_EditText.getText().toString();
        String empPwd = empPwd_EditText.getText().toString();
        String empMob = empMobile_EditText.getText().toString();
        String empStatus = empStatusSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(empName)) {
            if (!TextUtils.isEmpty(empId)) {
                if (!TextUtils.isEmpty(empPwd)) {
                    if (!TextUtils.isEmpty(empMob)) {
                        if (!TextUtils.isEmpty(empStatus)) {
                            employeeData.create_date = Constants.getSimpleDateFormat().format(System.currentTimeMillis());
                            employeeData.update_date = Constants.getSimpleDateFormat().format(System.currentTimeMillis());
                            employeeData.email = empId;
                            employeeData.name = empName;
                            employeeData.password = empPwd;
                            employeeData.number = empMob;
                            employeeData.status = empStatus;

                            employeesAdapter.addEmployee(employeeData);
                            employeesRecyclerView.smoothScrollToPosition(employeesAdapter.getItemCount());

                            /**
                             * reset all edit texts
                             */
                            empName_EditText.setText("");
                            empLoginId_EditText.setText("");
                            empPwd_EditText.setText("");
                            empMobile_EditText.setText("");
                            empStatusSpinner.setSelection(0);

                        } else {
                            showToast("Select Employee Status");
                        }
                    } else {
                        showToast("Enter Employee Mobile Number");
                    }
                } else {
                    showToast("Enter Employee Password");
                }
            } else {
                showToast("Enter Employee Id");
            }
        } else {
            showToast("Enter Employee Name");
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in Employees
         */
    }
}
