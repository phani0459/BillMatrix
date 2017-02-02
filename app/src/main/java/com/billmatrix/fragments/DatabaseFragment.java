package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.utils.Constants;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatabaseFragment extends Fragment implements OnDataFetchListener {

    private static final String TAG = DatabaseFragment.class.getSimpleName();
    private Context mContext;
    private String adminId;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.tv_last_sync_date)
    public TextView lastSyncDateTextView;
    @BindView(R.id.im_inventory_sync)
    public ImageView inventorySyncIcon;
    @BindView(R.id.im_customers_sync)
    public ImageView customersSyncIcon;
    @BindView(R.id.im_reports_sync)
    public ImageView reportsSyncIcon;
    @BindView(R.id.im_purchases_sync)
    public ImageView purchasesSyncIcon;
    @BindView(R.id.im_sales_sync)
    public ImageView salesSyncIcon;
    @BindView(R.id.im_employees_sync)
    public ImageView employeesSyncIcon;
    @BindView(R.id.im_vendors_sync)
    public ImageView vendorsSyncIcon;
    @BindView(R.id.im_discounts_sync)
    public ImageView discountSyncIcon;
    @BindView(R.id.im_header_footer_sync)
    public ImageView headerFooterSyncIcon;
    @BindView(R.id.im_generated_report_sync)
    public ImageView generatedReportSyncIcon;

    public DatabaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_database_new, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        return v;
    }

    @OnClick(R.id.btn_sync)
    public void syncWithServer() {
        if (!Utils.isInternetAvailable(mContext)) {
            Utils.showToast("Internet not Available!", mContext);
            return;
        }

        Utils.showToast("Sync Started", mContext);
        ServerUtils.setIsSync(true);

        syncEmployees();

    }

    private void syncCustomers() {
        customersSyncIcon.setImageResource(R.drawable.sync_green);

        ArrayList<Customer.CustomerData> dbCustomers = billMatrixDaoImpl.getCustomers();
        ArrayList<Customer.CustomerData> deletedCustomers = new ArrayList<>();
        ArrayList<Customer.CustomerData> addedCustomers = new ArrayList<>();
        ArrayList<Customer.CustomerData> updatedCustomers = new ArrayList<>();

        if (dbCustomers != null && dbCustomers.size() > 0) {
            for (Customer.CustomerData customer : dbCustomers) {
                if (customer.status.equalsIgnoreCase("-1")) {
                    deletedCustomers.add(customer);
                }

                if (!TextUtils.isEmpty(customer.add_update)) {
                    if (customer.add_update.equalsIgnoreCase(Constants.ADD_OFFLINE)) {
                        addedCustomers.add(customer);
                    }

                    if (customer.add_update.equalsIgnoreCase(Constants.UPDATE_OFFLINE)) {
                        updatedCustomers.add(customer);
                    }
                }
            }
        }

        if (deletedCustomers.size() > 0) {
            for (int i = 0; i < deletedCustomers.size(); i++) {
                Customer.CustomerData deletedCustomer = deletedCustomers.get(i);
                ServerUtils.deleteCustomerfromServer(deletedCustomer, mContext, billMatrixDaoImpl);
            }
        }

        if (addedCustomers.size() > 0) {
            for (int i = 0; i < addedCustomers.size(); i++) {
                Customer.CustomerData addedCustomer = addedCustomers.get(i);
                ServerUtils.addCustomertoServer(addedCustomer, mContext, adminId, billMatrixDaoImpl);
            }
        }

        if (updatedCustomers.size() > 0) {
            for (int i = 0; i < updatedCustomers.size(); i++) {
                Customer.CustomerData updatedCustomer = updatedCustomers.get(i);
                ServerUtils.updateCustomertoServer(updatedCustomer, mContext, billMatrixDaoImpl);
            }
        }

    }

    private void syncEmployees() {

        employeesSyncIcon.setImageResource(R.drawable.sync_green);

        ArrayList<Employee.EmployeeData> dbEmployees = billMatrixDaoImpl.getEmployees();
        ArrayList<Employee.EmployeeData> deletedEmployees = new ArrayList<>();
        ArrayList<Employee.EmployeeData> addedEmployees = new ArrayList<>();
        ArrayList<Employee.EmployeeData> updatedEmployees = new ArrayList<>();

        if (dbEmployees != null && dbEmployees.size() > 0) {
            for (Employee.EmployeeData employee : dbEmployees) {
                if (employee.status.equalsIgnoreCase("-1")) {
                    deletedEmployees.add(employee);
                }

                if (!TextUtils.isEmpty(employee.add_update)) {
                    if (employee.add_update.equalsIgnoreCase(Constants.ADD_OFFLINE)) {
                        addedEmployees.add(employee);
                    }

                    if (employee.add_update.equalsIgnoreCase(Constants.UPDATE_OFFLINE)) {
                        updatedEmployees.add(employee);
                    }
                }
            }
        }

        if (deletedEmployees.size() > 0) {
            for (int i = 0; i < deletedEmployees.size(); i++) {
                Employee.EmployeeData deletedEmp = deletedEmployees.get(i);
                ServerUtils.deleteEmployeefromServer(deletedEmp, mContext, billMatrixDaoImpl);
            }
        }

        if (addedEmployees.size() > 0) {
            for (int i = 0; i < addedEmployees.size(); i++) {
                Employee.EmployeeData addedEmp = addedEmployees.get(i);
                ServerUtils.addEmployeetoServer(addedEmp, mContext, billMatrixDaoImpl, adminId);
            }
        }

        if (updatedEmployees.size() > 0) {
            for (int i = 0; i < updatedEmployees.size(); i++) {
                Employee.EmployeeData updatedEmp = updatedEmployees.get(i);
                ServerUtils.updateEmployeetoServer(updatedEmp, mContext, billMatrixDaoImpl);
            }
        }

        syncCustomers();
    }

    /**
     * Sequence to fetch data from server
     * Employees - 0
     * Customers - 1
     * Vendors - 2
     * Inventory - 3
     * Tax - 4
     * Discounts - 5
     */
    @Override
    public void onDataFetch(int dataFetched) {
        switch (dataFetched) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
    }
}
