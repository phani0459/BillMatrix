package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
    private ServerData serverData;

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

        serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);

        ServerUtils.setOnDataChangeListener(null);

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

        syncEmployees(ServerUtils.STATUS_DELETING);
    }

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

        if (deletedCustomers.size() == 0 && updatedCustomers.size() == 0 && addedCustomers.size() == 0) {
            syncVendors(ServerUtils.STATUS_DELETING);
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
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedCustomers.size() > 0) {
                    Observable<ArrayList<Customer.CustomerData>> updatedCustomersObservable = Observable.fromArray(updatedCustomers);
                    syncCustomerswithServer(updatedCustomersObservable, currentStatus);
                } else {
                    syncVendors(ServerUtils.STATUS_DELETING);
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
                        Log.e(TAG, "get all employees: ");
                        billMatrixDaoImpl.deleteAllEmployees();
                        serverData.getEmployeesFromServer(adminId);
                        break;

                }
                Log.e(TAG, "onComplete");
            }
        });
    }

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

        if (deletedEmployees.size() == 0 && updatedEmployees.size() == 0 && addedEmployees.size() == 0) {
            employeesSyncIcon.setImageResource(R.drawable.sync_grey);
            syncCustomers(ServerUtils.STATUS_DELETING);
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
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedEmployees.size() > 0) {
                    Observable<ArrayList<Employee.EmployeeData>> updatedEmployeesObservable = Observable.fromArray(updatedEmployees);
                    syncEmployeeswithServer(updatedEmployeesObservable, currentStatus);
                } else {
                    employeesSyncIcon.setImageResource(R.drawable.sync_grey);
                    syncCustomers(ServerUtils.STATUS_DELETING);
                }
                break;
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
                syncCustomers(ServerUtils.STATUS_DELETING);
                break;
            case 1:
                customersSyncIcon.setImageResource(R.drawable.sync_grey);
                syncVendors(ServerUtils.STATUS_DELETING);
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
            vendorsSyncIcon.setImageResource(R.drawable.sync_grey);
            //TODO Sync inventory
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
                }
                break;
            case ServerUtils.STATUS_UPDATING:
                if (updatedVendors.size() > 0) {
                    Observable<ArrayList<Vendor.VendorData>> updatedVendorsObservable = Observable.fromArray(updatedVendors);
                    syncVendorswithServer(updatedVendorsObservable, currentStatus);
                } else {
                    vendorsSyncIcon.setImageResource(R.drawable.sync_grey);
                    //TODO sync inventory
                }
                break;
        }
    }

    public void syncVendorswithServer(Observable<ArrayList<Vendor.VendorData>> observableVendors, final int status) {

        observableVendors.flatMap(new Function<List<Vendor.VendorData>, ObservableSource<Vendor.VendorData>>() { // flatMap - to return vendors one by one
            @Override
            public ObservableSource<Vendor.VendorData> apply(List<Vendor.VendorData> employeesList) throws Exception {
                return Observable.fromIterable(employeesList); // returning Vendors one by one from VendorsList.
            }
        }).flatMap(new Function<Vendor.VendorData, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(Vendor.VendorData emp) throws Exception {
                // here we get the vendor one by one
                // and does corresponding sync in server
                // for that vendor
                switch (status) {
                    case ServerUtils.STATUS_DELETING:
                        ServerUtils.deleteVendorfromServer(emp, mContext, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_ADDING:
                        ServerUtils.addVendortoServer(emp, mContext, adminId, billMatrixDaoImpl);
                        break;
                    case ServerUtils.STATUS_UPDATING:
                        ServerUtils.updateVendortoServer(emp, mContext, billMatrixDaoImpl);
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

}
