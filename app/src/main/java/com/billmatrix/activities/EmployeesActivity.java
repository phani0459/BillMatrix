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
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Profile;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KANDAGATLAs on 23-10-2016.
 */

public class EmployeesActivity extends BaseTabActivity implements OnItemClickListener, OnDataFetchListener {

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
    private Employee.EmployeeData selectedEmptoEdit;
    private Profile profilefromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setPageTitle(String.format("<span>%s Employees </span>", getArrowString()));
        if (savedInstanceState != null) {
            selectedEmptoEdit = (Employee.EmployeeData) savedInstanceState.getSerializable("EDIT_EMP");
            if (selectedEmptoEdit != null) {
                isEditing = false;
                onItemClick(2, -1);
            }
        }

        addTabButtons(1, "Employees");

        Utils.loadSpinner(empStatusSpinner, mContext, R.array.employee_status);

        employeesLayout.setVisibility(View.VISIBLE);

        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        empMobile_EditText.setFilters(Utils.getInputFilter(10));
        empLoginId_EditText.setFilters(Utils.getInputFilter(15));
        empPwd_EditText.setFilters(Utils.getInputFilter(15));
        ServerUtils.setIsSync(false);

        List<Employee.EmployeeData> employees = new ArrayList<>();
        try {
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            profilefromFile = Constants.getGson().fromJson(profileString, Profile.class);
            storeAdminEditText.setText(profilefromFile.data.username.toUpperCase());
            locationEditText.setText(profilefromFile.data.location != null ? profilefromFile.data.location.toUpperCase() : "");
            branchAdminEditText.setText(profilefromFile.data.branch != null ? profilefromFile.data.branch.toUpperCase() : "");
        } catch (Exception e) {
            e.printStackTrace();
            storeAdminEditText.setText("ADMIN");
        }

        if (!isEditing) {
            addEmpButton.setText(getString(R.string.add));
        } else {
            addEmpButton.setText(getString(R.string.save));
        }

        employeesAdapter = new EmployeesAdapter(employees, mContext);
        employeesRecyclerView.setAdapter(employeesAdapter);

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        employees = billMatrixDaoImpl.getEmployees();

        if (employees != null && employees.size() > 0) {
            for (Employee.EmployeeData employeeData : employees) {
                if (!employeeData.status.equalsIgnoreCase("-1")) {
                    employeesAdapter.addEmployee(employeeData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getEmployeesFromServer(adminId);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing && selectedEmptoEdit != null) {
            outState.putSerializable("EDIT_EMP", selectedEmptoEdit);
        }
    }

    public void getEmployeesFromServer(String adminId) {
        Log.e(TAG, "getEmployeesFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getEmployeesFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Employee.EmployeeData> employees = billMatrixDaoImpl.getEmployees();

        if (employees != null && employees.size() > 0) {
            for (Employee.EmployeeData employeeData : employees) {
                if (!employeeData.status.equalsIgnoreCase("-1")) {
                    employeesAdapter.addEmployee(employeeData);
                }
            }
        }
    }

    public boolean isEmployeeAdded;

    @OnClick(R.id.btn_addEmployee)
    public void addEmployee() {
        isEmployeeAdded = false;
        Utils.hideSoftKeyboard(empName_EditText);

        Employee.EmployeeData employeeData = new Employee().new EmployeeData();
        Employee.EmployeeData employeeFromServer = new Employee().new EmployeeData();
        String empName = empName_EditText.getText().toString();
        String empId = empLoginId_EditText.getText().toString();
        String empPwd = empPwd_EditText.getText().toString();
        String empMob = empMobile_EditText.getText().toString();
        String empStatus = empStatusSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(empName.trim())) {
            Utils.showToast("Enter Employee Name", mContext);
            return;
        }

        if (TextUtils.isEmpty(empId.trim())) {
            Utils.showToast("Enter Employee Id", mContext);
            return;
        }

        if (profilefromFile != null && profilefromFile.data != null && empId.equalsIgnoreCase(profilefromFile.data.login_id)) {
            Utils.showToast("Employee Id must not be same as admin Id", mContext);
            return;
        }

        if (empId.length() < 6) {
            Utils.showToast("Login Id must be more than 6 characters", mContext);
            return;
        }

        if (TextUtils.isEmpty(empPwd.trim())) {
            Utils.showToast("Enter Employee Password", mContext);
            return;
        }

        if (empPwd.length() < 6) {
            Utils.showToast("Password must be more than 6 characters", mContext);
            return;
        }

        if (!Utils.isPhoneValid(empMob)) {
            Utils.showToast("Enter Valid Employee Mobile Number", mContext);
            return;
        }

        if (TextUtils.isEmpty(empStatus)) {
            Utils.showToast("Select Employee Status", mContext);
            return;
        }

        if (addEmpButton.getText().toString().equalsIgnoreCase("ADD")) {
            employeeData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            employeeData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedEmptoEdit != null) {
                employeeData.id = selectedEmptoEdit.id;
                employeeData.create_date = selectedEmptoEdit.create_date;
                employeeData.add_update = selectedEmptoEdit.add_update;

                if (TextUtils.isEmpty(selectedEmptoEdit.add_update)) {
                    employeeData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedEmptoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    employeeData.add_update = Constants.UPDATE_OFFLINE;
                }
            }
        }
        employeeData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        employeeData.login_id = empId;
        employeeData.username = empName;
        employeeData.password = empPwd;
        employeeData.mobile_number = empMob;
        employeeData.status = empStatus.equalsIgnoreCase("ACTIVE") ? "1" : "0";
        employeeData.imei_number = Utils.getSharedPreferences(mContext).getString(Constants.PREF_LICENECE_KEY, "");
        employeeData.type = "user";
        employeeData.admin_id = adminId;
        employeeData.location = locationEditText.getText().toString();
        employeeData.branch = branchAdminEditText.getText().toString();

        long empAdded = billMatrixDaoImpl.addEmployee(employeeData);

        if (empAdded != -1) {
            employeesRecyclerView.smoothScrollToPosition(employeesAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            empName_EditText.setText("");
            empLoginId_EditText.setText("");
            empPwd_EditText.setText("");
            empMobile_EditText.setText("");
            empStatusSpinner.setSelection(0);

            if (addEmpButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    employeeFromServer = ServerUtils.addEmployeetoServer(employeeData, mContext, billMatrixDaoImpl, adminId);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_EMPLOYEES_EDITED_OFFLINE, true).apply();
                    employeeFromServer = employeeData;
                    Utils.showToast("Employee Added successfully", mContext);
                }
            } else {
                if (selectedEmptoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        employeeFromServer = ServerUtils.updateEmployeetoServer(employeeData, mContext, billMatrixDaoImpl, profilefromFile.data.contact_person);
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_EMPLOYEES_EDITED_OFFLINE, true).apply();
                        employeeFromServer = employeeData;
                        Utils.showToast("Employee Updated successfully", mContext);
                    }
                }
            }

            employeeFromServer.location = locationEditText.getText().toString();
            employeeFromServer.branch = branchAdminEditText.getText().toString();

            employeesAdapter.addEmployee(employeeFromServer);
            addEmpButton.setText(getString(R.string.add));
            isEditing = false;
            isEmployeeAdded = true;
        } else {
            Utils.showToast("Employee Login Id must be unique", mContext);
        }
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /***
         * There is only one tab in Employees
         */
    }

    @Override
    public void onBackPressed() {
        if (addEmpButton.getText().toString().equalsIgnoreCase("SAVE") || isEditing) {
            showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addEmployee();
                    if (isEmployeeAdded) {
                        finish();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    public boolean isEditing;

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                if (isEditing) {
                    Utils.showToast("Save present editing employee before deleting other employee", mContext);
                    return;
                }
                showAlertDialog("Are you sure?", "You want to delete employee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Employee.EmployeeData selectedEmp = employeesAdapter.getItem(position);
                        if (selectedEmp.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateEmployee(DBConstants.STATUS, "-1", selectedEmp.id);
                        } else {
                            billMatrixDaoImpl.deleteEmployee(selectedEmp.login_id);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedEmp.id)) {
                                ServerUtils.deleteEmployeefromServer(selectedEmp, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_EMPLOYEES_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Employee Deleted successfully", mContext);
                        }
                        employeesAdapter.deleteEmployee(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    addEmpButton.setText(getString(R.string.save));

                    if (position != -1) {
                        selectedEmptoEdit = employeesAdapter.getItem(position);
                    }

                    if (selectedEmptoEdit != null) {
                        empName_EditText.setText(selectedEmptoEdit.username);
                        empLoginId_EditText.setText(selectedEmptoEdit.login_id);
                        empPwd_EditText.setText(selectedEmptoEdit.password);
                        empMobile_EditText.setText(selectedEmptoEdit.mobile_number);
                        if (selectedEmptoEdit.status.equalsIgnoreCase("ACTIVE") || selectedEmptoEdit.status.equalsIgnoreCase("1")) {
                            empStatusSpinner.setSelection(0);
                        } else {
                            empStatusSpinner.setSelection(1);
                        }
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deleteEmployee(employeesAdapter.getItem(position).login_id);
                        employeesAdapter.deleteEmployee(position);
                    }
                } else {
                    Utils.showToast("Save present editing employee before editing other employee", mContext);
                }
                break;
            case 3:
                break;
        }
    }
}
