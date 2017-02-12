package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class DatabaseFragment extends Fragment implements OnDataFetchListener, CompoundButton.OnCheckedChangeListener {

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

    @BindView(R.id.cb_inventory_sync)
    public CheckBox inventorySyncCheckbox;
    @BindView(R.id.cb_customers_sync)
    public CheckBox customersSyncCheckbox;
    @BindView(R.id.cb_reports_sync)
    public CheckBox reportsSyncCheckbox;
    @BindView(R.id.cb_purchases_sync)
    public CheckBox purchasesSyncCheckbox;
    @BindView(R.id.cb_sales_sync)
    public CheckBox salesSyncCheckbox;
    @BindView(R.id.cb_employees_sync)
    public CheckBox employeesSyncCheckbox;
    @BindView(R.id.cb_vendors_sync)
    public CheckBox vendorsSyncCheckbox;
    @BindView(R.id.cb_discounts_sync)
    public CheckBox discountSyncCheckbox;
    @BindView(R.id.cb_header_footer_sync)
    public CheckBox headerFooterSyncCheckbox;
    @BindView(R.id.cb_generated_report_sync)
    public CheckBox generatedReportSyncCheckbox;

    @BindView(R.id.btn_sync)
    public Button syncButton;

    private ServerData serverData;

    public DatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (inventorySyncCheckbox.isChecked() || customersSyncCheckbox.isChecked() ||
                reportsSyncCheckbox.isChecked() || purchasesSyncCheckbox.isChecked() ||
                salesSyncCheckbox.isChecked() || employeesSyncCheckbox.isChecked() ||
                vendorsSyncCheckbox.isChecked() || discountSyncCheckbox.isChecked() ||
                headerFooterSyncCheckbox.isChecked() || generatedReportSyncCheckbox.isChecked()) {
            syncButton.setEnabled(true);
            syncButton.setBackgroundResource(R.drawable.green_button);
        } else {
            syncButton.setEnabled(false);
            syncButton.setBackgroundResource(R.drawable.button_disable);
        }
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
                employeesSyncIcon.setImageResource(R.drawable.sync_grey);
                employeesSyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 1:
                customersSyncIcon.setImageResource(R.drawable.sync_grey);
                customersSyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 2:
                vendorsSyncIcon.setImageResource(R.drawable.sync_grey);
                vendorsSyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 3:
                inventorySyncIcon.setImageResource(R.drawable.sync_grey);
                inventorySyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 4:
                //TODO TAX
                break;
            case 5:
                discountSyncIcon.setImageResource(R.drawable.sync_grey);
                discountSyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 6:
                reportsSyncIcon.setImageResource(R.drawable.sync_grey);
                reportsSyncCheckbox.setChecked(false);
                checkNextSyncItem();
                break;
            case 7:
                purchasesSyncCheckbox.setChecked(false);
                purchasesSyncIcon.setImageResource(R.drawable.sync_grey);
                checkNextSyncItem();
                break;
            case 8:
                salesSyncCheckbox.setChecked(false);
                salesSyncIcon.setImageResource(R.drawable.sync_grey);
                checkNextSyncItem();
                break;
            case 9:
                headerFooterSyncCheckbox.setChecked(false);
                headerFooterSyncIcon.setImageResource(R.drawable.sync_grey);
                checkNextSyncItem();
                break;
            case 10:
                generatedReportSyncCheckbox.setChecked(false);
                generatedReportSyncIcon.setImageResource(R.drawable.sync_grey);
                checkNextSyncItem();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_database_new, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);

        inventorySyncCheckbox.setOnCheckedChangeListener(this);
        customersSyncCheckbox.setOnCheckedChangeListener(this);
        reportsSyncCheckbox.setOnCheckedChangeListener(this);
        purchasesSyncCheckbox.setOnCheckedChangeListener(this);
        salesSyncCheckbox.setOnCheckedChangeListener(this);
        employeesSyncCheckbox.setOnCheckedChangeListener(this);
        vendorsSyncCheckbox.setOnCheckedChangeListener(this);
        discountSyncCheckbox.setOnCheckedChangeListener(this);
        headerFooterSyncCheckbox.setOnCheckedChangeListener(this);
        generatedReportSyncCheckbox.setOnCheckedChangeListener(this);


        /**
         * If edited offline true, set pending sync icon
         */

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_EMPLOYEES_EDITED_OFFLINE, false)) {
            employeesSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, false)) {
            inventorySyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, false)) {
            customersSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_REPORTS_EDITED_OFFLINE, false)) {
            reportsSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, false)) {
            purchasesSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_SALES_EDITED_OFFLINE, false)) {
            salesSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_VENDORS_EDITED_OFFLINE, false)) {
            vendorsSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_DISCS_EDITED_OFFLINE, false)) {
            discountSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_HnF_EDITED_OFFLINE, false)) {
            headerFooterSyncIcon.setImageResource(R.drawable.sync_red);
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_GEN_REPORT_EDITED_OFFLINE, false)) {
            generatedReportSyncIcon.setImageResource(R.drawable.sync_red);
        }

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

        checkNextSyncItem();
    }

    public void checkNextSyncItem() {
        if (inventorySyncCheckbox.isChecked()) {
            syncInventory(ServerUtils.STATUS_DELETING);
        } else if (customersSyncCheckbox.isChecked()) {
            syncCustomers(ServerUtils.STATUS_DELETING);
        } else if (reportsSyncCheckbox.isChecked()) {
            //TODO sync Reports
        } else if (purchasesSyncCheckbox.isChecked()) {
            //TODO sync Purchases
        } else if (salesSyncCheckbox.isChecked()) {
            //TODO sync sales
        } else if (employeesSyncCheckbox.isChecked()) {
            syncEmployees(ServerUtils.STATUS_DELETING);
        } else if (vendorsSyncCheckbox.isChecked()) {
            syncVendors(ServerUtils.STATUS_DELETING);
        } else if (discountSyncCheckbox.isChecked()) {
            //TODO sync Discounts
        } else if (headerFooterSyncCheckbox.isChecked()) {
            //TODO sync Header and footer
        } else if (generatedReportSyncCheckbox.isChecked()) {
            //TODO sync Generated Reports
        }
    }

    /*****************************************************
     * ******** Employee SYNC ***************************
     **************************************************/

    private void syncEmployees(int currentStatus) {
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

        if (deletedEmployees.size() <= 0 && updatedEmployees.size() <= 0 && addedEmployees.size() <= 0) {
            billMatrixDaoImpl.deleteAllEmployees();
            serverData.getEmployeesFromServer(adminId);
            return;
        }

        switch (currentStatus) {
            case ServerUtils.STATUS_DELETING:
                if (deletedEmployees.size() > 0) {
                    Observable<ArrayList<Employee.EmployeeData>> deletedEmployeesObservable = Observable.fromArray(deletedEmployees);
                    syncEmployeeswithServer(deletedEmployeesObservable, currentStatus);
                } else if (addedEmployees.size() > 0) {
                    currentStatus = ServerUtils.STATUS_ADDING;
                    Observable<ArrayList<Employee.EmployeeData>> addedEmployeesObservable = Observable.fromArray(addedEmployees);
                    syncEmployeeswithServer(addedEmployeesObservable, currentStatus);
                } else if (updatedEmployees.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Employee.EmployeeData>> updatedEmployeesObservable = Observable.fromArray(updatedEmployees);
                    syncEmployeeswithServer(updatedEmployeesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllEmployees();
                    serverData.getEmployeesFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_ADDING:
                if (addedEmployees.size() > 0) {
                    Observable<ArrayList<Employee.EmployeeData>> addedEmployeesObservable = Observable.fromArray(addedEmployees);
                    syncEmployeeswithServer(addedEmployeesObservable, currentStatus);
                } else if (updatedEmployees.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Employee.EmployeeData>> updatedEmployeesObservable = Observable.fromArray(updatedEmployees);
                    syncEmployeeswithServer(updatedEmployeesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllEmployees();
                    serverData.getEmployeesFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedEmployees.size() > 0) {
                    Observable<ArrayList<Employee.EmployeeData>> updatedEmployeesObservable = Observable.fromArray(updatedEmployees);
                    syncEmployeeswithServer(updatedEmployeesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllEmployees();
                    serverData.getEmployeesFromServer(adminId);
                }
                break;
        }
    }

    public void syncEmployeeswithServer(Observable<ArrayList<Employee.EmployeeData>> observableEmployees, final int status) {

        observableEmployees.flatMap(new Function<List<Employee.EmployeeData>, ObservableSource<Employee.EmployeeData>>() { // flatMap - to return users one by one
            @Override
            public ObservableSource<Employee.EmployeeData> apply(List<Employee.EmployeeData> employeesList) throws Exception {
                return Observable.fromIterable(employeesList); // returning Employees one by one from EmployeesList.
            }
        }).flatMap(new Function<Employee.EmployeeData, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(Employee.EmployeeData emp) throws Exception {
                // here we get the user one by one
                // and does correstponding sync in server
                // for that employee
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        ServerUtils.deleteEmployeefromServer(emp, mContext, billMatrixDaoImpl, false);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        ServerUtils.addEmployeetoServer(emp, mContext, billMatrixDaoImpl, adminId, false);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        ServerUtils.updateEmployeetoServer(emp, mContext, billMatrixDaoImpl, false);
                        break;

                }
                return Observable.fromArray(new ArrayList<String>());
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(ArrayList<String> strings) {
            }

            @Override
            public void onComplete() {
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        syncEmployees(ServerUtils.STATUS_ADDING);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        syncEmployees(ServerUtils.STATUS_UPDATING);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        billMatrixDaoImpl.deleteAllEmployees();
                        serverData.getEmployeesFromServer(adminId);
                        break;

                }
                Log.e(TAG, "onComplete");
            }
        });
    }

    /*****************************************************
     * ******** Customers SYNC ***************************
     **************************************************/

    private void syncCustomers(int currentStatus) {
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

        if (deletedCustomers.size() <= 0 && updatedCustomers.size() <= 0 && addedCustomers.size() <= 0) {
            billMatrixDaoImpl.deleteAllCustomers();
            serverData.getCustomersFromServer(adminId);
            return;
        }

        switch (currentStatus) {
            case ServerUtils.STATUS_DELETING:
                if (deletedCustomers.size() > 0) {
                    Observable<ArrayList<Customer.CustomerData>> deletedCustomersObservable = Observable.fromArray(deletedCustomers);
                    syncCustomerswithServer(deletedCustomersObservable, currentStatus);
                } else if (addedCustomers.size() > 0) {
                    currentStatus = ServerUtils.STATUS_ADDING;
                    Observable<ArrayList<Customer.CustomerData>> addedCustomersObservable = Observable.fromArray(addedCustomers);
                    syncCustomerswithServer(addedCustomersObservable, currentStatus);
                } else if (updatedCustomers.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Customer.CustomerData>> updatedCustomersObservable = Observable.fromArray(updatedCustomers);
                    syncCustomerswithServer(updatedCustomersObservable, currentStatus);
                } else {
                    Log.e(TAG, "get all customers: ");
                    billMatrixDaoImpl.deleteAllCustomers();
                    serverData.getCustomersFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_ADDING:
                if (addedCustomers.size() > 0) {
                    Observable<ArrayList<Customer.CustomerData>> addedCustomersObservable = Observable.fromArray(addedCustomers);
                    syncCustomerswithServer(addedCustomersObservable, currentStatus);
                } else if (updatedCustomers.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Customer.CustomerData>> updatedCustomersObservable = Observable.fromArray(updatedCustomers);
                    syncCustomerswithServer(updatedCustomersObservable, currentStatus);
                } else {
                    Log.e(TAG, "get all customers: ");
                    billMatrixDaoImpl.deleteAllCustomers();
                    serverData.getCustomersFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedCustomers.size() > 0) {
                    Observable<ArrayList<Customer.CustomerData>> updatedCustomersObservable = Observable.fromArray(updatedCustomers);
                    syncCustomerswithServer(updatedCustomersObservable, currentStatus);
                } else {
                    Log.e(TAG, "get all customers: ");
                    billMatrixDaoImpl.deleteAllCustomers();
                    serverData.getCustomersFromServer(adminId);
                }
                break;
        }

    }

    public void syncCustomerswithServer(Observable<ArrayList<Customer.CustomerData>> observableCustomers, final int status) {

        observableCustomers.flatMap(new Function<List<Customer.CustomerData>, ObservableSource<Customer.CustomerData>>() { // flatMap - to return users one by one
            @Override
            public ObservableSource<Customer.CustomerData> apply(List<Customer.CustomerData> employeesList) throws Exception {
                return Observable.fromIterable(employeesList); // returning Customers one by one from CustomerLsit.
            }
        }).flatMap(new Function<Customer.CustomerData, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(Customer.CustomerData emp) throws Exception {
                // here we get the user one by one
                // and does correstponding sync in server
                // for that Customer
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        ServerUtils.deleteCustomerfromServer(emp, mContext, billMatrixDaoImpl, false);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        ServerUtils.addCustomertoServer(emp, mContext, adminId, billMatrixDaoImpl, false);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        ServerUtils.updateCustomertoServer(emp, mContext, billMatrixDaoImpl, false);
                        break;

                }
                return Observable.fromArray(new ArrayList<String>());
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(ArrayList<String> strings) {
            }

            @Override
            public void onComplete() {
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        syncCustomers(ServerUtils.STATUS_ADDING);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        syncCustomers(ServerUtils.STATUS_UPDATING);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        Log.e(TAG, "get all customers: ");
                        billMatrixDaoImpl.deleteAllCustomers();
                        serverData.getCustomersFromServer(adminId);
                        break;

                }
                Log.e(TAG, "onComplete");
            }
        });
    }

    /*****************************************************
     * ******** Vendors SYNC ***************************
     **************************************************/

    private void syncVendors(int currentStatus) {
        vendorsSyncIcon.setImageResource(R.drawable.sync_green);

        ArrayList<Vendor.VendorData> dbVendors = billMatrixDaoImpl.getVendors();
        ArrayList<Vendor.VendorData> deletedVendors = new ArrayList<>();
        ArrayList<Vendor.VendorData> addedVendors = new ArrayList<>();
        ArrayList<Vendor.VendorData> updatedVendors = new ArrayList<>();

        if (dbVendors != null && dbVendors.size() > 0) {
            for (Vendor.VendorData vendor : dbVendors) {
                if (vendor.status.equalsIgnoreCase("-1")) {
                    deletedVendors.add(vendor);
                }

                if (!TextUtils.isEmpty(vendor.add_update)) {
                    if (vendor.add_update.equalsIgnoreCase(Constants.ADD_OFFLINE)) {
                        addedVendors.add(vendor);
                    }

                    if (vendor.add_update.equalsIgnoreCase(Constants.UPDATE_OFFLINE)) {
                        updatedVendors.add(vendor);
                    }
                }
            }
        }

        if (deletedVendors.size() == 0 && updatedVendors.size() == 0 && addedVendors.size() == 0) {
            billMatrixDaoImpl.deleteAllVendors();
            serverData.getVendorsFromServer(adminId);
            return;
        }

        switch (currentStatus) {
            case ServerUtils.STATUS_DELETING:
                if (deletedVendors.size() > 0) {
                    Observable<ArrayList<Vendor.VendorData>> deletedVendorsObservable = Observable.fromArray(deletedVendors);
                    syncVendorswithServer(deletedVendorsObservable, currentStatus);
                } else if (addedVendors.size() > 0) {
                    currentStatus = ServerUtils.STATUS_ADDING;
                    Observable<ArrayList<Vendor.VendorData>> addedVendorsObservable = Observable.fromArray(addedVendors);
                    syncVendorswithServer(addedVendorsObservable, currentStatus);
                } else if (updatedVendors.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Vendor.VendorData>> updatedVendorsObservable = Observable.fromArray(updatedVendors);
                    syncVendorswithServer(updatedVendorsObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllVendors();
                    serverData.getVendorsFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_ADDING:
                if (addedVendors.size() > 0) {
                    Observable<ArrayList<Vendor.VendorData>> addedVendorsObservable = Observable.fromArray(addedVendors);
                    syncVendorswithServer(addedVendorsObservable, currentStatus);
                } else if (updatedVendors.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Vendor.VendorData>> updatedVendorsObservable = Observable.fromArray(updatedVendors);
                    syncVendorswithServer(updatedVendorsObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllVendors();
                    serverData.getVendorsFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedVendors.size() > 0) {
                    Observable<ArrayList<Vendor.VendorData>> updatedVendorsObservable = Observable.fromArray(updatedVendors);
                    syncVendorswithServer(updatedVendorsObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllVendors();
                    serverData.getVendorsFromServer(adminId);
                }
                break;
        }
    }


    public void syncVendorswithServer(Observable<ArrayList<Vendor.VendorData>> observableVendors, final int status) {

        observableVendors.flatMap(new Function<List<Vendor.VendorData>, ObservableSource<Vendor.VendorData>>() { // flatMap - to return vendors one by one
            @Override
            public ObservableSource<Vendor.VendorData> apply(List<Vendor.VendorData> vendorLsit) throws Exception {
                return Observable.fromIterable(vendorLsit); // returning Vendors one by one from VendorsList.
            }
        }).flatMap(new Function<Vendor.VendorData, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(Vendor.VendorData vendorData) throws Exception {
                // here we get the vendor one by one
                // and does corresponding sync in server
                // for that vendor
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        ServerUtils.deleteVendorfromServer(vendorData, mContext, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        ServerUtils.addVendortoServer(vendorData, mContext, adminId, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        ServerUtils.updateVendortoServer(vendorData, mContext, billMatrixDaoImpl);
                        break;

                }
                return Observable.fromArray(new ArrayList<String>());
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(ArrayList<String> strings) {
            }

            @Override
            public void onComplete() {
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        syncVendors(ServerUtils.STATUS_ADDING);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        syncVendors(ServerUtils.STATUS_UPDATING);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        Log.e(TAG, "get all vendors: ");
                        billMatrixDaoImpl.deleteAllVendors();
                        serverData.getVendorsFromServer(adminId);
                        break;

                }
                Log.e(TAG, "Vendors onComplete");
            }
        });
    }

    /*****************************************************
     * ******** Inventory SYNC ***************************
     **************************************************/

    private void syncInventory(int currentStatus) {
        inventorySyncIcon.setImageResource(R.drawable.sync_green);

        ArrayList<Inventory.InventoryData> dbInventories = billMatrixDaoImpl.getInventory();
        ArrayList<Inventory.InventoryData> deletedInventories = new ArrayList<>();
        ArrayList<Inventory.InventoryData> addedInventories = new ArrayList<>();
        ArrayList<Inventory.InventoryData> updatedInventories = new ArrayList<>();

        if (dbInventories != null && dbInventories.size() > 0) {
            for (Inventory.InventoryData inventory : dbInventories) {
                if (inventory.status.equalsIgnoreCase("-1")) {
                    deletedInventories.add(inventory);
                }

                if (!TextUtils.isEmpty(inventory.add_update)) {
                    if (inventory.add_update.equalsIgnoreCase(Constants.ADD_OFFLINE)) {
                        addedInventories.add(inventory);
                    }

                    if (inventory.add_update.equalsIgnoreCase(Constants.UPDATE_OFFLINE)) {
                        updatedInventories.add(inventory);
                    }
                }
            }
        }

        if (deletedInventories.size() == 0 && updatedInventories.size() == 0 && addedInventories.size() == 0) {
            billMatrixDaoImpl.deleteAllInventories();
            serverData.getInventoryFromServer(adminId);
            return;
        }

        switch (currentStatus) {
            case ServerUtils.STATUS_DELETING:
                if (deletedInventories.size() > 0) {
                    Observable<ArrayList<Inventory.InventoryData>> deletedInventoriesObservable = Observable.fromArray(deletedInventories);
                    syncInventorywithServer(deletedInventoriesObservable, currentStatus);
                } else if (addedInventories.size() > 0) {
                    currentStatus = ServerUtils.STATUS_ADDING;
                    Observable<ArrayList<Inventory.InventoryData>> addedInventoriesObservable = Observable.fromArray(addedInventories);
                    syncInventorywithServer(addedInventoriesObservable, currentStatus);
                } else if (updatedInventories.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Inventory.InventoryData>> updatedInventoriesObservable = Observable.fromArray(updatedInventories);
                    syncInventorywithServer(updatedInventoriesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllInventories();
                    serverData.getInventoryFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_ADDING:
                if (addedInventories.size() > 0) {
                    Observable<ArrayList<Inventory.InventoryData>> addedInventoriesObservable = Observable.fromArray(addedInventories);
                    syncInventorywithServer(addedInventoriesObservable, currentStatus);
                } else if (updatedInventories.size() > 0) {
                    currentStatus = ServerUtils.STATUS_UPDATING;
                    Observable<ArrayList<Inventory.InventoryData>> updatedInventoriesObservable = Observable.fromArray(updatedInventories);
                    syncInventorywithServer(updatedInventoriesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllInventories();
                    serverData.getInventoryFromServer(adminId);
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedInventories.size() > 0) {
                    Observable<ArrayList<Inventory.InventoryData>> updatedInventoriesObservable = Observable.fromArray(updatedInventories);
                    syncInventorywithServer(updatedInventoriesObservable, currentStatus);
                } else {
                    billMatrixDaoImpl.deleteAllInventories();
                    serverData.getInventoryFromServer(adminId);
                }
                break;
        }
    }


    public void syncInventorywithServer(Observable<ArrayList<Inventory.InventoryData>> observableVendors, final int status) {

        observableVendors.flatMap(new Function<List<Inventory.InventoryData>, ObservableSource<Inventory.InventoryData>>() { // flatMap - to return vendors one by one
            @Override
            public ObservableSource<Inventory.InventoryData> apply(List<Inventory.InventoryData> inventoryList) throws Exception {
                return Observable.fromIterable(inventoryList); // returning Vendors one by one from VendorsList.
            }
        }).flatMap(new Function<Inventory.InventoryData, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(Inventory.InventoryData inventoryData) throws Exception {
                // here we get the vendor one by one
                // and does corresponding sync in server
                // for that vendor
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        ServerUtils.deleteInventoryfromServer(inventoryData, mContext, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        ServerUtils.addInventorytoServer(inventoryData, mContext, adminId, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        ServerUtils.updateInventorytoServer(inventoryData, mContext, billMatrixDaoImpl);
                        break;

                }
                return Observable.fromArray(new ArrayList<String>());
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(ArrayList<String> strings) {
            }

            @Override
            public void onComplete() {
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        syncInventory(ServerUtils.STATUS_ADDING);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        syncInventory(ServerUtils.STATUS_UPDATING);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        Log.e(TAG, "get all Inventory: ");
                        billMatrixDaoImpl.deleteAllInventories();
                        serverData.getInventoryFromServer(adminId);
                        break;

                }
                Log.e(TAG, "Inventory onComplete");
            }
        });
    }
}
