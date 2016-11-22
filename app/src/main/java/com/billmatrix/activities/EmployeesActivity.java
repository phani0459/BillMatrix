package com.billmatrix.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.billmatrix.R;
import com.billmatrix.adapters.EmployeesAdapter;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Profile;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
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

public class EmployeesActivity extends BaseTabActivity implements OnItemClickListener {

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
    @BindView(R.id.et_emp_storeAdmin)
    public EditText storeAdminEditText;
    @BindView(R.id.et_emp_branch)
    public EditText branchAdminEditText;
    @BindView(R.id.btn_addEmployee)
    public Button addEmpButton;

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
        try {
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            Profile profilefromFile = Constants.getGson().fromJson(profileString, Profile.class);
            storeAdminEditText.setText(profilefromFile.data.username.toUpperCase());
            locationEditText.setText(profilefromFile.data.location != null ? profilefromFile.data.location.toUpperCase() : "");
            branchAdminEditText.setText(profilefromFile.data.branch != null ? profilefromFile.data.branch.toUpperCase() : "");
        } catch (Exception e) {
            e.printStackTrace();
            storeAdminEditText.setText("ADMIN");
        }

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
        Log.e(TAG, "getEmployeesFromServer: ");
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
                t.printStackTrace();
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    @OnClick(R.id.btn_addEmployee)
    public void addEmployee() {
        addEmpButton.setText("ADD");
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
                            employeeData.username = empName;
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

//                                addEmployeetoServer(employeeData);
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
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).addEmployee(employeeData.username, employeeData.password, employeeData.mobile_number, adminId);

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
                addEmpButton.setText("SAVE");
                Employee.EmployeeData selectedEmp = employeesAdapter.getItem(position);
                empName_EditText.setText(selectedEmp.username);
                empLoginId_EditText.setText(selectedEmp.email);
                empPwd_EditText.setText(selectedEmp.password);
                empMobile_EditText.setText(selectedEmp.mobile_number);
                if (selectedEmp.status.equalsIgnoreCase("ACTIVE") || selectedEmp.status.equalsIgnoreCase("1")) {
                    empStatusSpinner.setSelection(0);
                } else {
                    empStatusSpinner.setSelection(1);
                }
                billMatrixDaoImpl.deleteEmployee(employeesAdapter.getItem(position).email);
                employeesAdapter.deleteEmployee(position);
                break;
            case 3:
                break;
        }
    }
}
