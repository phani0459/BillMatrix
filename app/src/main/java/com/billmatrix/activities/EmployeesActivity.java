package com.billmatrix.activities;

import android.content.DialogInterface;
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
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KANDAGATLAs on 23-10-2016.
 */

public class EmployeesActivity extends BaseTabActivity implements EmployeesAdapter.onClickListener {

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
    @BindView(R.id.et_emp_location)
    public EditText locationEditText;

    private EmployeesAdapter employeesAdapter;
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setPageTitle(String.format("<span>%s Employees </span>", getArrowString()));
        addTabButtons(1, "Employees");

        Utils.loadSpinner(empStatusSpinner, mContext, R.array.employee_status);

        employeesLayout.setVisibility(View.VISIBLE);

        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Employee.EmployeeData> employees = new ArrayList<>();

        employeesAdapter = new EmployeesAdapter(employees, mContext);
        employeesRecyclerView.setAdapter(employeesAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        employees = billMatrixDaoImpl.getEmployees();

        if (employees != null && employees.size() > 0) {
            for (Employee.EmployeeData employeeData : employees) {
                employeesAdapter.addEmployee(employeeData);
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getEmployeesFromServer(adminId);
                }
            }
        }
    }

    public void getEmployeesFromServer(String loginId) {
        Call<Employee> call = Utils.getBillMatrixAPI(mContext).getAdminEmployees(loginId);

        call.enqueue(new Callback<Employee>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    Employee employee = response.body();
                    if (employee.status == 200 && employee.employeedata.equalsIgnoreCase("success")) {
                        for (Employee.EmployeeData employeeData : employee.data) {
                            billMatrixDaoImpl.addEmployee(employeeData);
                            employeesAdapter.addEmployee(employeeData);
                        }
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
                            employeeData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            employeeData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                            employeeData.email = empId;
                            employeeData.name = empName;
                            employeeData.password = empPwd;
                            employeeData.mobile_number = empMob;
                            employeeData.status = empStatus;

                            long empAdded = billMatrixDaoImpl.addEmployee(employeeData);

                            if (empAdded != -1) {
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

                                addEmployeetoServer(employeeData);
                            } else {
                                showToast("Employee Login Id must be unique");
                            }
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

    private void addEmployeetoServer(Employee.EmployeeData employeeData) {
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).addEmployee(employeeData.name, employeeData.password, employeeData.mobile_number, adminId);

        call.enqueue(new Callback<HashMap<String, String>>() {


            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Log.e("SUCCEESS RESPONSE RAW", "" + response.raw());
                if (response.body() != null) {
                    HashMap<String, String> employeeStatus = response.body();
                    Log.e("SUCCEESS RESPONSE RAW", employeeStatus + "");
                    if (employeeStatus.get("status").equalsIgnoreCase("200")) {
                        if (employeeStatus.get("create_employee").equalsIgnoreCase("success")) {
                            showToast("Employee Added successfully");
                        }
                    }
                }
            }

            /**
             *  Invoked when a network or unexpected exception occurred during the HTTP request.
             * @param call server call
             * @param t error
             */
            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in Employees
         */
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                showAlertDialog("Are you sure?", "You want to delete employee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.deleteEmployee(employeesAdapter.getItem(position).email);
                        employeesAdapter.deleteEmployee(position);
                    }
                });
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
