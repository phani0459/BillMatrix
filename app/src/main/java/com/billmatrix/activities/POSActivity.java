package com.billmatrix.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.WorkService;
import com.billmatrix.adapters.DevicesAdapter;
import com.billmatrix.adapters.POSInventoryAdapter;
import com.billmatrix.adapters.POSItemAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Profile;
import com.billmatrix.models.Transaction;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.ConnectivityReceiver;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lvrenyang.utils.DataUtils;

import org.zirco.myprinter.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class POSActivity extends Activity implements OnItemClickListener, POSItemAdapter.OnItemSelected, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = POSActivity.class.getSimpleName();
    @BindView(R.id.sp_pos_customers)
    public Spinner customersSpinner;
    @BindView(R.id.pos_tabs_layout)
    public LinearLayout tabsLayout;
    @BindView(R.id.rl_pos_cust_close)
    public RelativeLayout customerCloseButton;
    @BindView(R.id.cb_zbill)
    public CheckBox zBillCheckBox;
    @BindView(R.id.rl_pos_cust_edit)
    public RelativeLayout customerEditButton;
    @BindView(R.id.posInventoryList)
    public RecyclerView posInventoryList;
    @BindView(R.id.pos_items_list)
    public RecyclerView posItemsRecyclerView;
    @BindView(R.id.tv_total_cart_items)
    public TextView totalCartItemsTextView;
    @BindView(R.id.tv_sub_total)
    public TextView subTotalTextView;
    @BindView(R.id.et_pos_discount_code)
    public EditText discountCodeEditText;
    @BindView(R.id.tv_pos_customer_Name)
    public TextView customerNameTextView;
    @BindView(R.id.tv_pos_customer_location)
    public TextView customerLocationTextView;
    @BindView(R.id.tv_pos_customer_contact)
    public TextView customerContactTextView;
    @BindView(R.id.dra_pos_customer_banner)
    public SimpleDraweeView customerBannerDraweeView;
    @BindView(R.id.dra_pos_customer_photo)
    public SimpleDraweeView headerLogoDraweeView;
    @BindView(R.id.pos_customer_searchView)
    public EditText posSearchEditText;
    @BindView(R.id.et_pos_items_searchView)
    public EditText posItemsSearchEditText;
    @BindView(R.id.ll_customerDetails)
    public LinearLayout customerDetailsLayout;
    @BindView(R.id.tv_POS_No_Customer)
    public TextView customerNotSelectedTextView;
    @BindView(R.id.tv_discount_cal)
    public TextView discountCalTextView;
    @BindView(R.id.tv_totalValue)
    public TextView totalValueTextView;
    @BindView(R.id.tv_POS_item_No_Customer)
    public TextView noCustomerItemTextView;
    @BindView(R.id.im_pos_edit_customer)
    public ImageView editCustomerImageView;
    @BindView(R.id.tv_pos_taxValue)
    public TextView taxValueTextView;
    @BindView(R.id.btn_cash_or_card)
    public Button cashCardButton;
    @BindView(R.id.btn_credit)
    public Button creditButton;
    @BindView(R.id.tv_pos_inven_no_results)
    public TextView noResultsTextView;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private POSItemAdapter posItemAdapter;
    private POSInventoryAdapter posInventoryAdapter;
    private float discountSelected;
    private ArrayList<Customer.CustomerData> dbCustomers;
    private Customer.CustomerData selectedCustomer;
    private String selectedGuestCustomerName;
    private ArrayAdapter<String> customerSpinnerAdapter;
    private boolean isGuestCustomerSelected;
    private boolean isZBillChecked;
    private boolean isAdmin;
    private String adminId;
    private ArrayMap<String, Float> selectedtaxes;
    private int guestCustomerCount;

    private BluetoothAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog connectingProgressDialog;
    private MHandler mHandler;
    private ProgressDialog searchingDialog;
    private static final int BLUETOOTH_ENABLE_REQUEST_ID = 1;
    private Dialog devicesDialog;
    private DevicesAdapter devicesAdapter;
    private ListView devicesListView;
    private Dialog paymentsDialog;
    private TextView totalPaidTextView;
    private TextView balanceTextView;
    private CountDownTimer timer;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_pos);
        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        dbCustomers = new ArrayList<>();
        guestCustomerCount = 1;
        connectingProgressDialog = Utils.getProgressDialog(mContext, "");

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);
        String userType = Utils.getSharedPreferences(mContext).getString(Constants.PREF_USER_TYPE, null);

        isAdmin = false;

        if (!TextUtils.isEmpty(userType)) {
            if (userType.equalsIgnoreCase("admin")) {
                isAdmin = false;
            }
        }

        /**
         * Fetch profile to set printer headers
         */
        if (FileUtils.isFileExists(Constants.PROFILE_FILE_NAME, mContext)) {
            Log.e(TAG, "Profile is from file");
            String profileString = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);
            profile = Constants.getGson().fromJson(profileString, Profile.class);
        }

        ArrayList<String> customerNames = new ArrayList<>();

        customerNames.add("SELECT CUSTOMER");
        customerNames.add("GUEST CUSTOMER " + guestCustomerCount);

        dbCustomers = billMatrixDaoImpl.getCustomers();

        if (dbCustomers != null && dbCustomers.size() > 0) {
            for (Customer.CustomerData customer : dbCustomers) {
                /**
                 * Show only Customers who are active
                 */
                if (customer.status.equalsIgnoreCase("1")) {
                    customerNames.add(customer.username.toUpperCase());
                }
            }
        }
        customerSpinnerAdapter = Utils.loadSpinner(customersSpinner, mContext, customerNames);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout buttonLayout = (RelativeLayout) layoutInflater.inflate(R.layout.tab_button, null);
        final Button button = (Button) buttonLayout.findViewById(R.id.btn_tab);
        final Button tab_bottom_view = (Button) buttonLayout.findViewById(R.id.tab_bottom_view);

        button.setText(getString(R.string.INVENTORY));

        button.getBackground().setLevel(0);
        button.setTextColor(getResources().getColor(android.R.color.black));
        tab_bottom_view.setBackgroundColor(getResources().getColor(R.color.tabButtonBG));

        List<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        posInventoryList.setLayoutManager(new GridLayoutManager(mContext, 2));
        posInventoryAdapter = new POSInventoryAdapter(inventoryDatas, this, mContext);
        posInventoryList.setAdapter(posInventoryAdapter);

        inventoryDatas = billMatrixDaoImpl.getInventory();

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!inventoryData.status.equalsIgnoreCase("-1")) {
                    posInventoryAdapter.addInventory(inventoryData);
                }
            }
        }

        List<Inventory.InventoryData> itemsInventory = new ArrayList<>();
        posItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        posItemAdapter = new POSItemAdapter(itemsInventory, this, mContext);
        posItemsRecyclerView.setAdapter(posItemAdapter);

        discountSelected = Utils.getSharedPreferences(mContext).getFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, 0.0f);
        discountCodeEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, ""));
        selectedtaxes = new ArrayMap<>();

        String selectedTaxJSON = Utils.getSharedPreferences(mContext).getString(Constants.PREF_TAX_JSON, "");
        if (!TextUtils.isEmpty(selectedTaxJSON)) {
            selectedtaxes = Constants.getGson().fromJson(selectedTaxJSON, Constants.floatArrayMapType);
        }

        /**
         * set texts defaults
         */
        totalCartItemsTextView.setText("0" + " " + getString(R.string.ITEMS));
        subTotalTextView.setText(getString(R.string.sub_total) + " " + "0.00" + "/-");
        discountCalTextView.setText(getString(R.string.discount) + ": " + "0.00" + "/-");
        totalValueTextView.setText(" " + "0.00" + "/-");
        taxValueTextView.setText(" " + "0.00" + "/-");

        tabsLayout.addView(buttonLayout);

        /**
         * Printer Code
         */

        mHandler = new MHandler();
        WorkService.addHandler(mHandler);
        initBroadcast();

        if (null == WorkService.workThread) {
            Intent intent = new Intent(mContext, WorkService.class);
            mContext.startService(intent);
        }

        /**
         * Initiate devices Dialog
         */
        devicesDialog = new Dialog(mContext);
        devicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        devicesDialog.setCancelable(false);
        devicesDialog.setContentView(R.layout.dialog_printer_devices);
        devicesListView = (ListView) devicesDialog.findViewById(R.id.lv_printers);
        Button close = (Button) devicesDialog.findViewById(R.id.btn_close_printers_dialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * to remove all the printers previously searched
                 */
                devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
                devicesListView.setAdapter(devicesAdapter);

                devicesDialog.dismiss();

                if (adapter != null && adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
            }
        });

        Button continueButton = (Button) devicesDialog.findViewById(R.id.btn_cont_without_printer);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * to remove all the printers previously searched
                 */
                devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
                devicesListView.setAdapter(devicesAdapter);

                devicesDialog.dismiss();

                if (adapter != null && adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }

                resetCustomerBill(true);
            }
        });

        devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
        devicesListView.setAdapter(devicesAdapter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            Utils.showToast("Barcode Scanner detected. Please turn OFF Hardware/Physical keyboard to enable softkeyboard to function.", mContext);
        }
    }

    /*
    Show transport diaolog
     */
    @OnClick(R.id.im_pos_transport)
    public void showTransportDialog(View v) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pos_transport);
        dialog.setCancelable(false);

        final EditText dispatchDate = (EditText) dialog.findViewById(R.id.et_pos_dispatch_date);
        final TextView customerName = (TextView) dialog.findViewById(R.id.tv_pos_transport_cust_name);
        Button saveTransportButton = (Button) dialog.findViewById(R.id.btn_save_transport);
        Button close = (Button) dialog.findViewById(R.id.btn_close_transport_dialog);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        saveTransportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dispatchDate.setInputType(InputType.TYPE_NULL);
        final DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dispatchDate, false, true);

        dispatchDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!datePickerDialog.isShowing()) {
                    Utils.hideSoftKeyboard(dispatchDate);
                    datePickerDialog.show();
                }
                return false;
            }
        });

        if (selectedCustomer != null) {
            customerName.setText(selectedCustomer.username);
        } else {
            Utils.showToast("Select Customer", mContext);
            return;
        }

        dialog.show();
    }

    @OnClick(R.id.rl_pos_cust_edit)
    public void showCustomerDetailsDialog() {

        if (!isAdmin) {
            Utils.showToast("Employee cannot add / edit customer", mContext);
            return;
        }

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pos_customer_details);
        dialog.setCancelable(false);

        final String previousCustomerName;
        final EditText customerUserName = (EditText) dialog.findViewById(R.id.et_pos_cust_name);
        final EditText customerMobile = (EditText) dialog.findViewById(R.id.et_pos_cust_mobile);
        final EditText customerDate = (EditText) dialog.findViewById(R.id.et_pos_cust_date);
        final EditText customerLocation = (EditText) dialog.findViewById(R.id.et_pos_cust_location);
        final TextView customerTitle = (TextView) dialog.findViewById(R.id.dialog_customer_title);

        customerMobile.setFilters(Utils.getInputFilter(10));

        Button editCustomerButton = (Button) dialog.findViewById(R.id.btn_edit_add_customer);
        Button close = (Button) dialog.findViewById(R.id.btn_close_cust_details_dialog);

        customerDate.setInputType(InputType.TYPE_NULL);
        final DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, customerDate, true, false);

        customerDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!datePickerDialog.isShowing()) {
                    Utils.hideSoftKeyboard(customerDate);
                    datePickerDialog.show();
                }
                return false;
            }
        });

        if (selectedCustomer != null) {
            customerDate.setText(selectedCustomer.date);
            customerLocation.setText(selectedCustomer.location);
            customerUserName.setText(selectedCustomer.username);
            customerMobile.setText(selectedCustomer.mobile_number);
            previousCustomerName = selectedCustomer.username;
            customerTitle.setText("EDIT CUSTOMER");
            editCustomerButton.setText(getString(R.string.save));
        } else {
            customerTitle.setText(getString(R.string.add_new_customer));
            previousCustomerName = "";
            editCustomerButton.setText(getString(R.string.add));

            if (isGuestCustomerSelected) {
                customerTitle.setText("ADD GUEST CUSTOMER");
            }
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        editCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Customer.CustomerData editedCustomerData = new Customer().new CustomerData();
                if (selectedCustomer != null) {
                    editedCustomerData.status = selectedCustomer.status;
                    editedCustomerData.admin_id = selectedCustomer.admin_id;
                    editedCustomerData.id = selectedCustomer.id;
                    editedCustomerData.date = selectedCustomer.date;
                    editedCustomerData.create_date = selectedCustomer.create_date;
                    editedCustomerData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                    if (selectedCustomer.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                        editedCustomerData.add_update = Constants.UPDATE_OFFLINE;
                    }
                } else {
                    editedCustomerData.status = "1";
                    editedCustomerData.admin_id = adminId;
                    editedCustomerData.date = Constants.getDateFormat().format(System.currentTimeMillis());
                    editedCustomerData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                    editedCustomerData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                    editedCustomerData.add_update = Constants.ADD_OFFLINE;
                }

                String customerName = customerUserName.getText().toString();

                if (TextUtils.isEmpty(customerName)) {
                    Utils.showToast("Enter Customer Name", mContext);
                    return;
                }

                String customerContact = customerMobile.getText().toString();
                if (TextUtils.isEmpty(customerContact)) {
                    Utils.showToast("Enter Customer Mobile Number", mContext);
                    return;
                }

                if (!Utils.isPhoneValid(customerContact)) {
                    Utils.showToast("Enter Valid Customer Mobile Number", mContext);
                    return;
                }

                String customerLocationS = customerLocation.getText().toString();
                if (TextUtils.isEmpty(customerLocationS)) {
                    Utils.showToast("Enter Customer Location", mContext);
                    return;
                }

                editedCustomerData.date = customerDate.getText().toString();
                editedCustomerData.location = customerLocationS;
                editedCustomerData.username = customerName;
                editedCustomerData.mobile_number = customerContact;

                Log.e(TAG, "onClick: " + editedCustomerData.toString());

                if (selectedCustomer != null) {
                    billMatrixDaoImpl.deleteCustomer(DBConstants.ID, selectedCustomer.id);
                }

                addCustomerinDB(editedCustomerData, dialog, previousCustomerName);
            }
        });

        dialog.show();
    }

    public void addCustomerinDB(Customer.CustomerData customerData, Dialog dialog, String previousCustomerName) {
        long customerAdded = billMatrixDaoImpl.addCustomer(customerData);

        if (customerAdded != -1) {
            if (TextUtils.isEmpty(customerData.id)) {
                if (Utils.isInternetAvailable(mContext)) {
                    ServerUtils.addCustomertoServer(customerData, mContext, adminId, billMatrixDaoImpl);
                } else {
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Customer Added successfully", mContext);
                }
            } else {
                if (Utils.isInternetAvailable(mContext)) {
                    ServerUtils.updateCustomertoServer(customerData, mContext, billMatrixDaoImpl);
                } else {
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_CUSTOMERS_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Customer Updated successfully", mContext);
                }
            }

            /**
             * If there are dues for edited customer, and name has been changed, update transactions and pos table
             * and if there are payins of customer, change customer name
             */
            billMatrixDaoImpl.updateCustomerName(DBConstants.POS_ITEMS_TABLE, DBConstants.CUSTOMER_NAME, customerData.username, previousCustomerName);
            billMatrixDaoImpl.updateCustomerName(DBConstants.CUSTOMER_TRANSACTIONS_TABLE, DBConstants.CUSTOMER_NAME, customerData.username, previousCustomerName);

            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_PURCS_EDITED_OFFLINE, true).apply();
            billMatrixDaoImpl.updatePaymentOffline(DBConstants.ADD_UPDATE, Constants.UPDATE_OFFLINE, previousCustomerName);
            billMatrixDaoImpl.updateCustomerName(DBConstants.PAYMENTS_TABLE, DBConstants.PAYEE_NAME, customerData.username, previousCustomerName);

            /**
             * If Customer Name is updated, remove previous name and add new Name to spinner
             */
            if (!TextUtils.isEmpty(previousCustomerName)) {
                customerSpinnerAdapter.remove(previousCustomerName.toUpperCase());
                customerSpinnerAdapter.add(customerData.username.toUpperCase());
            }

            dialog.dismiss();
            selectedCustomer = customerData;
            isGuestCustomerSelected = false;

            customerSpinnerAdapter.add(customerData.username.toUpperCase());
            editCustomerImageView.setImageResource(R.drawable.edit_icon);
            try {
                int customerSelectedPosition = customerSpinnerAdapter.getPosition(customerData.username.toUpperCase());
                customersSpinner.setSelection(customerSelectedPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadCustomerDetails();
        } else {
            Utils.showToast("Customer Mobile Number must be unique", mContext);
        }
    }

    @OnClick(R.id.ll_pos_pay)
    public void pay() {
        if (posItemAdapter.getItemCount() <= 0) {
            return;
        }
        if (isPaymentTypeClicked) {
            paymentTypeDialog();
        } else {
            Utils.showToast("Select Payment type", mContext);
        }
    }

    public boolean isPaymentTypeClicked;
    String paymentType;

    @OnClick(R.id.btn_credit)
    public void credit(View v) {
        if (posItemAdapter.getItemCount() <= 0) {
            return;
        }
        if (isGuestCustomerSelected) {
            Utils.showToast("Add Guest Customer before allowing for credit", mContext);
            return;
        }
        if (selectedCustomer != null) {
        } else {
            return;
        }
        isPaymentTypeClicked = true;
        cashCardButton.setBackgroundResource(R.drawable.button_enable);
        v.setBackgroundResource(R.drawable.credit_button);
        creditButtonClicked = true;
        paymentType = "Credit";
    }

    @OnClick(R.id.btn_cash_or_card)
    public void cashOrCard(View v) {
        if (posItemAdapter.getItemCount() <= 0) {
            return;
        }
        if (selectedCustomer != null) {
        } else if (isGuestCustomerSelected) {
        } else {
            return;
        }
        creditButtonClicked = false;
        isPaymentTypeClicked = true;
        creditButton.setBackgroundResource(R.drawable.button_enable);
        v.setBackgroundResource(R.drawable.green_button);
        paymentType = "Cash";
    }

    boolean creditButtonClicked = false;

    public void paymentTypeDialog() {
        String username = "";
        if (selectedCustomer != null) {
            username = selectedCustomer.username;
        } else if (isGuestCustomerSelected) {
            username = customerNotSelectedTextView.getText().toString();
        } else {
            return;
        }
        paymentsDialog = new Dialog(mContext);
        paymentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentsDialog.setCancelable(false);
        paymentsDialog.setContentView(R.layout.dialog_pos_cash_card);

        TextView title = (TextView) paymentsDialog.findViewById(R.id.tv_payments_title);
        TextView discount = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_discount);
        TextView tax = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_tax);
        final TextView total = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_total);
        totalPaidTextView = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_total_paid);
        balanceTextView = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_balance);
        final TextView changeTextView = (TextView) paymentsDialog.findViewById(R.id.tv_dialog_change);
        final EditText cashEditText = (EditText) paymentsDialog.findViewById(R.id.et_dialog_cash);
        final EditText cardEditText = (EditText) paymentsDialog.findViewById(R.id.et_dialog_card);
        final LinearLayout cardLayout = (LinearLayout) paymentsDialog.findViewById(R.id.ll_dialog_card);

        Button close = (Button) paymentsDialog.findViewById(R.id.btn_close_cash_card_dialog);
        final Button cashButton = (Button) paymentsDialog.findViewById(R.id.btn_dialog_cash);
        final Button cardButton = (Button) paymentsDialog.findViewById(R.id.btn_dialog_card);
        final Button creditButton = (Button) paymentsDialog.findViewById(R.id.btn_dialog_credit);
        final Button payButton = (Button) paymentsDialog.findViewById(R.id.btn_dialog_pay);

        title.setText(getString(R.string.payments) + " - " + username);

        if (creditButtonClicked) {
            cashButton.setBackgroundResource(R.drawable.button_disable);
            creditButton.setBackgroundResource(R.drawable.orange_button);
            cardButton.setBackgroundResource(R.drawable.button_disable);
            cardLayout.setVisibility(View.VISIBLE);
        }

        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestCustomerSelected) {
                    Utils.showToast("Add Guest Customer before allowing for credit", mContext);
                    return;
                }
                creditButtonClicked = true;
                cardLayout.setVisibility(View.VISIBLE);
                cashButton.setBackgroundResource(R.drawable.button_disable);
                creditButton.setBackgroundResource(R.drawable.orange_button);
                cardButton.setBackgroundResource(R.drawable.button_disable);
                paymentType = "Credit";
            }
        });

        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardLayout.setVisibility(View.VISIBLE);
                cardButton.setBackgroundResource(R.drawable.button_enable);
                cashButton.setBackgroundResource(R.drawable.button_disable);
                creditButton.setBackgroundResource(R.drawable.button_disable);
                creditButtonClicked = false;
                paymentType = "Card";
            }
        });

        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardLayout.setVisibility(View.GONE);
                cardEditText.setText("");
                cashButton.setBackgroundResource(R.drawable.button_enable);
                cardButton.setBackgroundResource(R.drawable.button_disable);
                creditButton.setBackgroundResource(R.drawable.button_disable);
                creditButtonClicked = false;
                paymentType = "Cash";
            }
        });

        String discountString = discountCalTextView.getText().toString().replace(getString(R.string.discount) + ": ", "");
        String totalString = totalValueTextView.getText().toString().replace(getString(R.string.sub_total) + " ", "");
        String taxString = taxValueTextView.getText().toString().replace(" ", "");

        discount.setText(discountString.replace("/-", ""));
        total.setText(totalString.replace("/-", ""));
        tax.setText(taxString.replace("/-", ""));
        balanceTextView.setText(total.getText().toString());

        totalPaidTextView.setText(getString(R.string.zero));
        changeTextView.setText(getString(R.string.zero));

        final float billTotalValue = Float.parseFloat(total.getText().toString().equalsIgnoreCase("") ? getString(R.string.zero) : total.getText().toString());

        cardEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String cardString = cardEditText.getText().toString();
                /**
                 * to enter only 2 values after decimal point
                 */
                if (cardString.contains(".") && charSequence.charAt(charSequence.length() - 1) != '.') {
                    if (cardString.indexOf(".") + 3 <= cardString.length() - 1) {
                        String formatted = cardString.substring(0, cardString.indexOf(".") + 3);
                        cardEditText.setText(formatted);
                        cardEditText.setSelection(formatted.length());
                    }
                }

                float moneyPaidFromCard = Float.parseFloat(cardEditText.getText().toString().equalsIgnoreCase("") ? "0.00" : cardEditText.getText().toString());
                float moneyPaidFromCash = Float.parseFloat(cashEditText.getText().toString().equalsIgnoreCase("") ? "0.00" : cashEditText.getText().toString());

                float changeValue = (moneyPaidFromCard + moneyPaidFromCash) - billTotalValue;

                /**
                 * if change is negative it is balanceTextView
                 */
                if (changeValue == 0) {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    balanceTextView.setText(getString(R.string.zero));
                    changeTextView.setText(getString(R.string.zero));
                } else if (changeValue < 0) {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", (moneyPaidFromCard + moneyPaidFromCash)));
                    balanceTextView.setText(String.format(Locale.getDefault(), "%.2f", -changeValue));
                    changeTextView.setText(getString(R.string.zero));
                } else {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    changeTextView.setText(String.format(Locale.getDefault(), "%.2f", changeValue));
                    balanceTextView.setText(getString(R.string.zero));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cashEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String cashString = cashEditText.getText().toString();
                /**
                 * to enter only 2 values after decimal point
                 */
                if (cashString.contains(".") && charSequence.charAt(charSequence.length() - 1) != '.') {
                    if (cashString.indexOf(".") + 3 <= cashString.length() - 1) {
                        String formatted = cashString.substring(0, cashString.indexOf(".") + 3);
                        cashEditText.setText(formatted);
                        cashEditText.setSelection(formatted.length());
                    }
                }

                float moneyPaidFromCard = Float.parseFloat(cardEditText.getText().toString().equalsIgnoreCase("") ? "0.00" : cardEditText.getText().toString());
                float moneyPaidFromCash = Float.parseFloat(cashEditText.getText().toString().equalsIgnoreCase("") ? "0.00" : cashEditText.getText().toString());

                float changeValue = (moneyPaidFromCard + moneyPaidFromCash) - billTotalValue;

                /**
                 * if change is negative it is balanceTextView
                 */
                if (changeValue == 0) {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    balanceTextView.setText(getString(R.string.zero));
                    changeTextView.setText(getString(R.string.zero));
                } else if (changeValue < 0) {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", (moneyPaidFromCard + moneyPaidFromCash)));
                    balanceTextView.setText(String.format(Locale.getDefault(), "%.2f", -changeValue));
                    changeTextView.setText(getString(R.string.zero));
                } else {
                    totalPaidTextView.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    changeTextView.setText(String.format(Locale.getDefault(), "%.2f", changeValue));
                    balanceTextView.setText(getString(R.string.zero));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentsDialog.dismiss();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float balanceRemaining = Float.parseFloat(balanceTextView.getText().toString().equalsIgnoreCase("") ? "0.00" : balanceTextView.getText().toString());
                float totalPaidMoney = Float.parseFloat(totalPaidTextView.getText().toString());
                if (totalPaidMoney <= 0 && !creditButtonClicked) {
                    Utils.showToast("Please enter cash paid", mContext);
                    return;
                }
                if (balanceRemaining <= 0) {
                    printBillDialog("Want to Print Bill?");
                } else {
                    if (!isGuestCustomerSelected) {
                        cashButton.setBackgroundResource(R.drawable.button_disable);
                        creditButton.setBackgroundResource(R.drawable.orange_button);
                        cardButton.setBackgroundResource(R.drawable.button_disable);

                        printBillDialog("Want to Print Bill?");

                    } else {
                        Utils.showToast("Add Guest Customer before allowing for credit", mContext);
                    }
                }
            }
        });

        paymentsDialog.show();
    }

    public void resetCustomerBill(boolean isTransactionSuccess) {

        /**
         * save transaction if transaction is successfull
         */
        if (isTransactionSuccess) {
            saveTransaction();
        }

        /**
         * Remove customer bill items to close the bill
         */
        if (posItemAdapter.getItemCount() > 0) {
            posItemAdapter.removeAllItems();
        }
        billMatrixDaoImpl.deleteAllCustomerItems(selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
        if (isGuestCustomerSelected) {
            int guestCustInt = TextUtils.isEmpty(selectedGuestCustomerName) ? 1 : Integer.parseInt(selectedGuestCustomerName.replace("GUEST CUSTOMER ", ""));
            if (guestCustInt == guestCustomerCount && guestCustInt != 1) {
                guestCustomerCount = guestCustomerCount - 1;
                customerSpinnerAdapter.remove(selectedGuestCustomerName);
            }
        }
        customersSpinner.setSelection(0);

        /**
         * dismiss payments dialog
         */
        if (paymentsDialog != null && paymentsDialog.isShowing()) {
            paymentsDialog.dismiss();
        }
    }

    private void saveTransaction() {
        Transaction transaction = new Transaction();

        List<Inventory.InventoryData> itemsPurchased = posItemAdapter.inventories;

        String inventoryJson = Constants.getGson().toJson(itemsPurchased);

        transaction.add_update = Constants.ADD_OFFLINE;
        transaction.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        transaction.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());

        transaction.date = Constants.getDateFormat().format(System.currentTimeMillis());
        transaction.inventoryJson = inventoryJson;
        transaction.customerName = selectedCustomer != null ? selectedCustomer.username : selectedGuestCustomerName;
        transaction.amountPaid = totalPaidTextView.getText().toString();
        transaction.amountDue = balanceTextView.getText().toString().trim();
        transaction.totalAmount = totalValueTextView.getText().toString().replace("/-", "").trim();
        transaction.admin_id = adminId;
        transaction.billNumber = generateBillNumber();
        transaction.id = transaction.billNumber;
        transaction.isZbillChecked = zBillCheckBox.isChecked();
        transaction.status = "1";

        Log.e(TAG, "saveTransaction: " + transaction.toString());

        billMatrixDaoImpl.addTransaction(transaction);
    }

    private String generateBillNumber() {
        String billCounter = (billMatrixDaoImpl.getTransactionsCount(Constants.getDateFormat().format(System.currentTimeMillis())) + 1) + "";
        String billCounterNumber = billCounter.length() == 1 ? "00" + billCounter : (billCounter.length() == 2 ? "0" + billCounter : billCounter);
        StringBuilder billNumber = new StringBuilder("");
        billNumber.append(isGuestCustomerSelected ? "GB" : "B").append(Constants.getBillDateFormat().format(System.currentTimeMillis()))
                .append(billCounterNumber);
        return billNumber.toString();
    }

    public void printBillDialog(String title) {
        AlertDialog devicesDialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setPositiveButton(WorkService.workThread.isConnected() ? "YES" : "Search Printer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enableBluetooth();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetCustomerBill(true);
                    }
                }).create();

        devicesDialog.show();
    }

    @OnClick(R.id.rl_pos_cust_close)
    public void closeCurrentBill() {
        String username = "";
        if (selectedCustomer != null) {
            username = selectedCustomer.username;
        } else if (isGuestCustomerSelected) {
            username = customerNotSelectedTextView.getText().toString();
        } else {
            return;
        }

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pos_cancel_bill);
        dialog.setCancelable(false);

        TextView forCustomer = (TextView) dialog.findViewById(R.id.tv_for_customer);
        forCustomer.setText(getString(R.string.for_customer, username));

        Button yes = (Button) dialog.findViewById(R.id.btn_cancel_bill_yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCustomerBill(false);
                dialog.dismiss();
            }
        });

        Button close = (Button) dialog.findViewById(R.id.btn_close_cancel_dialog);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    String scannedBarcode = "";

    /**
     * to listen to the barcode scanner value
     *
     * @param e event triggered by scanner
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() != KeyEvent.ACTION_DOWN) {
            char pressedKey = (char) e.getUnicodeChar();
            scannedBarcode += pressedKey;
            if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (noCustomerItemTextView.getVisibility() == View.GONE) {
                    if (!TextUtils.isEmpty(scannedBarcode)) {
                        fetchInventoryByBarcode(scannedBarcode);
                    }
                } else {
                    scannedBarcode = "";
                    Utils.showToast("Select Customer before adding items to cart", mContext);
                }
            }
        }
        return super.dispatchKeyEvent(e);
    }

    private void fetchInventoryByBarcode(String barcodeValue) {
        Utils.showToast(barcodeValue, mContext);
        Inventory.InventoryData barcodeInventoryData;
        if (posInventoryAdapter != null) {
            barcodeInventoryData = posInventoryAdapter.getInventoryonByBarcode("333333333333");
            if (barcodeInventoryData != null) {
                loadInventoryToTransaction(barcodeInventoryData);
            }
        }
        scannedBarcode = "";
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityReceiver.connectivityReceiverListener = this;

        /**
         * if barcode is scanned, edit text will change focus to next edit text, to remove we again change focus to same edit text
         */
        posItemsSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_TAB) {
                    // handleInputScan();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (posItemsSearchEditText != null) {
                                posItemsSearchEditText.requestFocus();
                            }
                        }
                    }, 10); // Remove this Delay Handler IF requestFocus(); works just fine without delay
                    return true;
                }
                return false;
            }
        });

        customersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posItemAdapter.removeAllItems();
                creditButton.setBackgroundResource(R.drawable.button_enable);
                cashCardButton.setBackgroundResource(R.drawable.button_enable);
                isPaymentTypeClicked = false;
                isZBillChecked = false;

                String selecteCustomerName = (String) adapterView.getAdapter().getItem(i);
                if (!selecteCustomerName.equalsIgnoreCase("SELECT CUSTOMER") && !selecteCustomerName.contains("GUEST CUSTOMER")) {
                    for (Customer.CustomerData customer : dbCustomers) {
                        if (customer.username.equalsIgnoreCase(selecteCustomerName)) {
                            selectedGuestCustomerName = null;
                            selectedCustomer = customer;
                            isGuestCustomerSelected = false;
                            loadCustomerDetails();
                            loadPreviousItems(selecteCustomerName);
                        }
                    }
                    editCustomerImageView.setImageResource(R.drawable.edit_icon);
                } else if (selecteCustomerName.equalsIgnoreCase("SELECT CUSTOMER")) {
                    selectedCustomer = null;
                    selectedGuestCustomerName = null;
                    isGuestCustomerSelected = false;
                    noCustomerItemTextView.setVisibility(View.VISIBLE);
                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(getString(R.string.select_pos_customer));
                    editCustomerImageView.setImageResource(R.drawable.add_customer);
                } else if (selecteCustomerName.contains("GUEST CUSTOMER")) {
                    selectedCustomer = null;
                    isGuestCustomerSelected = true;
                    selectedGuestCustomerName = selecteCustomerName;
                    loadPreviousItems(selecteCustomerName);

                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    noCustomerItemTextView.setVisibility(View.GONE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(selecteCustomerName);
                    editCustomerImageView.setImageResource(R.drawable.add_customer);
                }

                loadFooterValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        posSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    posSearchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
                } else {
                    posSearchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        zBillCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isZBillChecked = b;
                billMatrixDaoImpl.updatePOSZBill(zBillCheckBox.isChecked(), selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
            }
        });

        posSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideSoftKeyboard(posSearchEditText);
                    searchClicked(posSearchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });


        /**
         * POS items search
         */
        posItemsSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    posItemsSearchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
                } else {
                    posItemsSearchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        posSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (posSearchEditText.getRight() - posSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (posSearchEditText.getText().toString().length() > 0) {
                            posSearchEditText.setText("");
                            searchClosed();
                        }
                        return true;
                    }
                }
                v.clearFocus();
                return false;
            }
        });

        posItemsSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (posItemsSearchEditText.getRight() - posItemsSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Log.e(TAG, "onTouch: ");
                        if (posItemsSearchEditText.getText().toString().length() > 0) {
                            posItemsSearchEditText.setText("");
                            searchItemsClosed();
                        }
                        return false;
                    }
                }
                v.clearFocus();
                return false;
            }
        });

        posItemsSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideSoftKeyboard(posItemsSearchEditText);
                    searchItemsClicked(posItemsSearchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });


    }

    private void loadPreviousItems(String selecteCustomerName) {
        ArrayList<Inventory.InventoryData> previousItems = billMatrixDaoImpl.getPOSItem(selecteCustomerName);
        if (previousItems != null && previousItems.size() > 0) {
            /**
             * apply discount selected for previous customer
             */
            discountSelected = Float.parseFloat(previousItems.get(0).discountValue);
            discountCodeEditText.setText(previousItems.get(0).discountCode);
            zBillCheckBox.setChecked(previousItems.get(0).isZbillChecked);

            for (Inventory.InventoryData inventoryData : previousItems) {
                posItemAdapter.addInventory(inventoryData);
            }
        }
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: ");
        if (query.length() > 0) {
            query = query.toLowerCase();
            selectedCustomer = null;

            if (dbCustomers != null && dbCustomers.size() > 0) {
                for (Customer.CustomerData customerData : dbCustomers) {
                    /**
                     * Show customers who are active only
                     */
                    if (customerData.status.equalsIgnoreCase("1")) {
                        if (customerData.username.toLowerCase().contains(query)) {
                            Log.e(TAG, "query Searched: " + query);
                            selectedCustomer = customerData;

                            posSearchEditText.setText("");
                            posSearchEditText.clearFocus();

                            try {
                                int customerSelectedPosition = customerSpinnerAdapter.getPosition(customerData.username.toUpperCase());
                                customersSpinner.setSelection(customerSelectedPosition);
                            } catch (Exception e) {
                                e.printStackTrace();
                                customersSpinner.setSelection(0);
                            }

                            loadCustomerDetails();

                        }
                    }
                }
            }

            if (selectedCustomer == null) {
                customersSpinner.setSelection(0);
                Utils.showToast("Not able to search Customer", mContext);
                posSearchEditText.clearFocus();
            }
        }
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");
    }

    @OnClick(R.id.btn_discountSelected)
    public void validateDiscount() {
        Utils.hideSoftKeyboard(discountCodeEditText);
        boolean isDiscountValidated = false;
        ArrayList<Discount.DiscountData> discounts = billMatrixDaoImpl.getDiscount();
        String discountToBeValidated = discountCodeEditText.getText().toString();

        if (discounts != null && discounts.size() > 0) {
            for (Discount.DiscountData dbDiscountData : discounts) {
                if (dbDiscountData.discount_code.equals(discountToBeValidated)) {
                    isDiscountValidated = true;
                    try {
                        discountSelected = Float.parseFloat(dbDiscountData.discount);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        discountSelected = 0.0f;
                    }
                }
            }
        }

        if (isDiscountValidated) {
            Utils.showToast("Discount Applied", mContext);
        } else {
            Utils.showToast("Enter Valid Discount Code", mContext);
            discountSelected = Utils.getSharedPreferences(mContext).getFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, 0.0f);
            discountCodeEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, ""));
        }

        billMatrixDaoImpl.updatePOSDiscount(discountCodeEditText.getText().toString(), discountSelected + "",
                selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
        loadFooterValues();
    }

    public void searchItemsClicked(String query) {
        Log.e(TAG, "searchItemsClicked: ");
        if (query.length() > 0) {
            query = query.toLowerCase();
            noResultsTextView.setVisibility(View.GONE);
            boolean noInventory = false;
            query = query.toLowerCase();
            posInventoryAdapter.removeAllInventories();

            ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();

            if (inventories != null && inventories.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventories) {
                    if ((!TextUtils.isEmpty(inventoryData.barcode) && inventoryData.barcode.toLowerCase().contains(query)) ||
                            inventoryData.item_name.toLowerCase().contains(query) ||
                            inventoryData.item_code.toLowerCase().contains(query)) {
                        noInventory = true;
                        posInventoryAdapter.addInventory(inventoryData);
                    }
                }
            }

            if (!noInventory) {
                noResultsTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void searchItemsClosed() {
        noResultsTextView.setVisibility(View.GONE);

        posInventoryAdapter.removeAllInventories();

        ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();

        if (inventories != null && inventories.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventories) {
                posInventoryAdapter.addInventory(inventoryData);
            }
        }

        Log.e(TAG, "searchItemsClosed: ");
    }

    public void loadCustomerDetails() {
        if (selectedCustomer != null) {
            customerLocationTextView.setText(selectedCustomer.location.toUpperCase());
            customerContactTextView.setText(selectedCustomer.mobile_number.toUpperCase());
            customerNameTextView.setText(selectedCustomer.username.toUpperCase());

            customerNotSelectedTextView.setVisibility(View.GONE);
            customerDetailsLayout.setVisibility(View.VISIBLE);
            noCustomerItemTextView.setVisibility(View.GONE);

        }
    }

    public void removePreferences() {
        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, null).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, "").apply();
        FileUtils.deleteFile(mContext, Constants.EMPLOYEE_FILE_NAME);
        billMatrixDaoImpl.deleteAllPOSItems();
    }

    @OnClick(R.id.btn_pos_logout)
    public void logout() {
        showAlertDialog(getString(R.string.logout), getString(R.string.wanna_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removePreferences();
                // to remove any activities that are in stack
                ActivityCompat.finishAffinity(POSActivity.this);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @OnTouch(R.id.tv_pos_reports)
    public boolean reports(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, ReportsActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    @OnClick(R.id.im_pos_billmatrix_logo)
    public void logoClicked() {
        Intent intent = new Intent(mContext, ControlPanelActivity.class);
        startActivity(intent);
        finish();
    }

    @OnTouch(R.id.tv_pos_settings)
    public boolean settings(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    public void showAlertDialog(String title, String msg, DialogInterface.OnClickListener okayClickListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(android.R.string.yes), okayClickListner);
        builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void loadInventoryToTransaction(Inventory.InventoryData selectedInventory) {
        /**
         * to add next guest customer in customer spinner if selected customer is guest and there are no more guest customers in spinner
         */
        if (isGuestCustomerSelected) {
            int guestCustInt = TextUtils.isEmpty(selectedGuestCustomerName) ? 1 : Integer.parseInt(selectedGuestCustomerName.replace("GUEST CUSTOMER ", ""));
            if (guestCustInt == guestCustomerCount) {
                guestCustomerCount = guestCustomerCount + 1;
                customerSpinnerAdapter.insert("GUEST CUSTOMER " + guestCustomerCount, guestCustomerCount);
            }
        }
        if (posItemAdapter.inventoryIDs != null && posItemAdapter.inventoryIDs.size() > 0) {
            if (posItemAdapter.inventoryIDs.contains(selectedInventory.id)) {
                Inventory.InventoryData inventoryFromAdapter = posItemAdapter.getInventoryonID(selectedInventory.id);
                if (inventoryFromAdapter != null) {
                    int inventoryQty = Integer.parseInt(inventoryFromAdapter.selectedQTY);
                    if (TextUtils.isDigitsOnly(selectedInventory.qty)) {
                        if (Integer.parseInt(selectedInventory.qty) > inventoryQty) {
                            inventoryQty = inventoryQty + 1;
                            inventoryFromAdapter.selectedQTY = inventoryQty + "";
                            posItemAdapter.changeInventory(inventoryFromAdapter);
                        } else {
                            Utils.showToast("This item is out of stock", mContext);
                        }
                    }
                    billMatrixDaoImpl.updatePOSItem(inventoryFromAdapter.selectedQTY, inventoryFromAdapter.item_code,
                            selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
                    loadFooterValues();
                }
                return;
            } else {
                selectedInventory.selectedQTY = "1";
            }
        } else {
            selectedInventory.selectedQTY = "1";
        }
        /**
         * Discount code added to customer
         */
        selectedInventory.discountCode = discountCodeEditText.getText().toString();
        selectedInventory.discountValue = discountSelected + "";
        selectedInventory.isZbillChecked = zBillCheckBox.isChecked();

        posItemAdapter.addInventory(selectedInventory);
        posItemsRecyclerView.smoothScrollToPosition(posItemAdapter.getItemCount());
        billMatrixDaoImpl.addPOSItem(selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName, selectedInventory);
        billMatrixDaoImpl.updatePOSZBill(zBillCheckBox.isChecked(), selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
        loadFooterValues();
    }

    @Override
    public void onItemClick(int caseInt, int position) {
        Inventory.InventoryData selectedInventory = posInventoryAdapter.getItem(position);
        if (noCustomerItemTextView.getVisibility() == View.GONE) {
            loadInventoryToTransaction(selectedInventory);
        } else {
            Utils.showToast("Select Customer before adding items to cart", mContext);
        }
    }

    private Float getSubTotal() {
        Float subTotal = 0f;
        for (int i = 0; i < posItemAdapter.inventories.size(); i++) {
            int qty = Integer.parseInt(posItemAdapter.inventories.get(i).selectedQTY);
            float price = Float.parseFloat(posItemAdapter.inventories.get(i).price);
            subTotal = subTotal + (qty * price);
        }
        return subTotal; //String.format(Locale.getDefault(), "%.2f", subTotal) + "";
    }

    public Float getDiscountCalculated(Float subTotal) {
        Float discountCalculated = 0.00f;
        if (discountSelected != 0) {
            discountCalculated = ((subTotal * discountSelected) / 100);
        }
        return discountCalculated; //String.format(Locale.getDefault(), "%.2f", discountCalculated) + "";
    }

    /**
     * Calculate tax after deducting discount on subtotal
     *
     * @param total
     * @return
     */
    public Float getTaxCalculated(Float total) {
        Float taxCalculated = 0.00f;
        Float totalTax = 0.00f;
        if (selectedtaxes != null && selectedtaxes.size() > 0) {
            for (String selectedTaxType : selectedtaxes.keySet()) {
                if (selectedtaxes.get(selectedTaxType) != 0) {
                    totalTax = totalTax + selectedtaxes.get(selectedTaxType);
                }
            }
        }
        if (totalTax != 0) {
            taxCalculated = ((total * totalTax) / 100);
        }
        return taxCalculated; //String.format(Locale.getDefault(), "%.2f", discountCalculated) + "";
    }

    public void loadFooterValues() {
        float subTotal = getSubTotal();
        float discount = getDiscountCalculated(subTotal);
        float tax = getTaxCalculated(subTotal - discount);
        subTotalTextView.setText(getString(R.string.sub_total) + " " + String.format(Locale.getDefault(), "%.2f", subTotal) + "/-");
        discountCalTextView.setText(getString(R.string.discount) + ": " + String.format(Locale.getDefault(), "%.2f", discount) + "/-");
        totalValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", (subTotal - discount + tax)) + "/-");
        taxValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", tax) + "/-");
        totalCartItemsTextView.setText(posItemAdapter.getItemCount() + " " + getString(R.string.ITEMS));
        zBillCheckBox.setChecked(isZBillChecked);
    }

    @Override
    public void itemSelected(int caseInt, final int position) {
        switch (caseInt) {
            case 0:
                loadFooterValues();
                billMatrixDaoImpl.updatePOSItem(posItemAdapter.getItem(position).selectedQTY, posItemAdapter.getItem(position).item_code,
                        selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
                break;
            case 1:
                showAlertDialog("Are you sure?", "You want to remove Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.deletePOSItem(posItemAdapter.getItem(position).item_code, selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
                        posItemAdapter.removeItem(position);
                        float subTotal = getSubTotal();
                        float discount = getDiscountCalculated(subTotal);
                        float tax = getTaxCalculated(subTotal - discount);

                        subTotalTextView.setText(getString(R.string.sub_total) + " " + String.format(Locale.getDefault(), "%.2f", subTotal) + "/-");
                        discountCalTextView.setText(getString(R.string.discount) + ": " + String.format(Locale.getDefault(), "%.2f", discount) + "/-");
                        totalValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", (subTotal - discount + tax)) + "/-");
                        taxValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", tax) + "/-");
                        totalCartItemsTextView.setText(posItemAdapter.getItemCount() + " " + getString(R.string.ITEMS));
                    }
                });
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }


    /*************************************************************
     * **********PRINTER CODE *************************************
     *************************************************************/

    public void enableBluetooth() {
        searchingDialog = Utils.getProgressDialog(mContext, "Searching...");
        searchingDialog.setCancelable(false);

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }

        if (!adapter.isEnabled()) {
            searchingDialog.dismiss();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_ID);
        } else {
            if (WorkService.workThread != null && WorkService.workThread.isConnected()) {
                printBill();
            } else {
                searchingDialog.show();
                startDiscovery();

                startDiscoveryTimer();
            }
        }
    }

    public void startDiscoveryTimer() {
        if (timer != null) {
            return;
        }
        timer = new CountDownTimer(Constants.PRINTER_DISCOVERY_TIME, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (adapter != null && adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                Utils.showToast("Printer not available", mContext);
                timer = null;
            }
        };
        timer.start();
    }

    private void printBill() {
        View parent = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bill, null);
        View view = parent.findViewById(R.id.ll_bill_Layout);

        setBill(view);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap mBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.draw(canvas);

        int nPaperWidth = 384;

        if (WorkService.workThread.isConnected()) {
            Bundle data = new Bundle();
            data.putParcelable(Global.PARCE1, mBitmap);
            data.putInt(Global.INTPARA1, nPaperWidth);
            data.putInt(Global.INTPARA2, 0);
            data.putInt(Global.INTPARA3, 3);
            WorkService.workThread.handleCmd(Global.CMD_POS_PRINTPICTURE, data);

            resetCustomerBill(true);

        } else {
            Utils.showToast(Global.toast_notconnect, mContext);
        }
    }

    public TextView storeNameTextView;
    public TextView addOneTextView;
    public TextView addTwoTextView;
    public TextView phoneTextView;
    public TextView tinTextView;
    public TextView dateTextView;
    public TextView timeTextView;
    public TextView userNameTextView;
    public TextView billNoTextView;
    public TextView grandTotalTextView;
    public TextView billTotalItemTextView, footerTextView, cstNoTextView;
    public TextView billTotalQTYTextView, paymentTypeTextView, savedMoneyTextView;

    private void setBill(View view) {
        storeNameTextView = (TextView) view.findViewById(R.id.tv_storeName);
        addOneTextView = (TextView) view.findViewById(R.id.tv_add_1);
        addTwoTextView = (TextView) view.findViewById(R.id.tv_add_2);
        phoneTextView = (TextView) view.findViewById(R.id.tv_phone);
        tinTextView = (TextView) view.findViewById(R.id.tv_TIN);
        dateTextView = (TextView) view.findViewById(R.id.tv_date);
        billNoTextView = (TextView) view.findViewById(R.id.tv_BillNo);
        cstNoTextView = (TextView) view.findViewById(R.id.tv_CSTNo);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        userNameTextView = (TextView) view.findViewById(R.id.tv_userName);
        grandTotalTextView = (TextView) view.findViewById(R.id.tv_grandtotal_value);
        billTotalItemTextView = (TextView) view.findViewById(R.id.tv_totalItems);
        billTotalQTYTextView = (TextView) view.findViewById(R.id.tv_totalQTY);
        paymentTypeTextView = (TextView) view.findViewById(R.id.tv_paymentType);
        savedMoneyTextView = (TextView) view.findViewById(R.id.tv_savedMoney);
        footerTextView = (TextView) view.findViewById(R.id.tv_bill_footer);

        if (profile != null && profile.data != null) {
            storeNameTextView.setText(profile.data.store_name);
            addOneTextView.setText(profile.data.address_one);
            addTwoTextView.setText(profile.data.address_two);
            phoneTextView.setText(profile.data.mobile_number);
            tinTextView.setText(!TextUtils.isEmpty(profile.data.vat_tin) ? "TIN: " + profile.data.vat_tin : "");
            cstNoTextView.setText(!TextUtils.isEmpty(profile.data.cst_no) ? "CST: " + profile.data.cst_no : "");
        }

        LinearLayout billItemsLayout = (LinearLayout) view.findViewById(R.id.ll_bill_items_layout);
        LinearLayout billTaxesLayout = (LinearLayout) view.findViewById(R.id.ll_bill_taxes);
        int totalItemsCount = 0;

        ArrayList<Inventory.InventoryData> previousItems = billMatrixDaoImpl.getPOSItem(selectedCustomer != null ? selectedCustomer.username.toUpperCase() : selectedGuestCustomerName);
        if (previousItems != null && previousItems.size() > 0) {
            for (Inventory.InventoryData inventoryData : previousItems) {
                View billItemLayout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_bill_layout, null);
                TextView itemNameTextView = (TextView) billItemLayout.findViewById(R.id.tv_BillItemName);
                TextView qtyTextView = (TextView) billItemLayout.findViewById(R.id.tv_BillItemQTY);
                TextView mrpTextView = (TextView) billItemLayout.findViewById(R.id.tv_BillItemMRP);
                TextView priceTextView = (TextView) billItemLayout.findViewById(R.id.tv_BillItemPRICE);
                TextView totalTextView = (TextView) billItemLayout.findViewById(R.id.tv_BillItemTOTAL);

                float totalPrice = Float.parseFloat(inventoryData.price) * Float.parseFloat(inventoryData.selectedQTY);

                itemNameTextView.setText(inventoryData.item_name);
                qtyTextView.setText(inventoryData.selectedQTY);
                mrpTextView.setText(inventoryData.price);
                priceTextView.setText(inventoryData.price);
                totalTextView.setText(String.format(Locale.getDefault(), "%.1f", totalPrice));
                totalItemsCount = totalItemsCount + Integer.parseInt(inventoryData.selectedQTY);

                billItemsLayout.addView(billItemLayout);
            }
        }

        userNameTextView.setText("USER: " + (selectedCustomer != null ? selectedCustomer.username : selectedGuestCustomerName));
        dateTextView.setText(Constants.getDateFormat().format(System.currentTimeMillis()));
        timeTextView.setText(Constants.getTimeFormat().format(System.currentTimeMillis()));
        savedMoneyTextView.setText(discountCalTextView.getText().toString().replace("/-", "").replace(getString(R.string.discount) + ": ", ""));
        billTotalItemTextView.setText(posItemAdapter.getItemCount() + "");
        billTotalQTYTextView.setText(totalItemsCount + "");
        billNoTextView.setText("Bill No.: " + generateBillNumber());
        paymentTypeTextView.setText(paymentType);
        grandTotalTextView.setText(totalValueTextView.getText().toString().replace("/-", ""));

        if (selectedtaxes != null && selectedtaxes.size() > 0) {
            Float taxableAmount = 0.0f;
            try {
                Float subTotal = Float.parseFloat(subTotalTextView.getText().toString().replace(getString(R.string.sub_total) + " ", "").replace("/-", ""));
                Float discount = Float.parseFloat(savedMoneyTextView.getText().toString());
                taxableAmount = subTotal - discount;
            } catch (NumberFormatException e) {
                taxableAmount = 0.0f;
            }
            for (String selectedTaxType : selectedtaxes.keySet()) {
                View billTaxLayout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_bill_tax, null);
                TextView taxDescTextView = (TextView) billTaxLayout.findViewById(R.id.tv_bill_tax);
                TextView taxableAmtTextView = (TextView) billTaxLayout.findViewById(R.id.tv_total_taxable_amt);
                TextView taxValueTextView = (TextView) billTaxLayout.findViewById(R.id.tv_bill_taxValue);

                taxDescTextView.setText(selectedTaxType + " @ " + selectedtaxes.get(selectedTaxType) + "% on: ");
                taxableAmtTextView.setText(String.format(Locale.getDefault(), "%.1f", taxableAmount));
                taxValueTextView.setText(String.format(Locale.getDefault(), "%.1f", ((taxableAmount * selectedtaxes.get(selectedTaxType)) / 100)));

                billTaxesLayout.addView(billTaxLayout);
            }
        }

        String footer = Utils.getSharedPreferences(mContext).getString(Constants.PREF_FOOTER_TEXT, null);

        if (TextUtils.isEmpty(footer)) {
            footer = "GET " + Utils.getSharedPreferences(mContext).getFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, 0.0f) + "% USING "
                    + Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, "No Discount");
        }

        footerTextView.setText(footer);
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;

                    /**
                     * dismiss search
                     */
                    if (searchingDialog != null && searchingDialog.isShowing())
                        searchingDialog.dismiss();

                    if (timer != null) {
                        timer.cancel();
                    }

                    devicesAdapter.addDevice(device);
                    devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            connectingProgressDialog.setMessage("Connecting" + " " + devicesAdapter.getItem(position).getAddress());
                            connectingProgressDialog.setIndeterminate(true);
                            connectingProgressDialog.setCancelable(false);
                            connectingProgressDialog.show();
                            adapter.cancelDiscovery();

                            WorkService.workThread.connectBt(devicesAdapter.getItem(position).getAddress());
                            WorkService.workThread.setDeviceAddress(devicesAdapter.getItem(position).getAddress());
                            WorkService.workThread.setDeviceName(devicesAdapter.getItem(position).getName());

                            devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
                            devicesListView.setAdapter(devicesAdapter);
                            devicesDialog.dismiss();
                        }
                    });

                    devicesDialog.show();

                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if (searchingDialog != null && searchingDialog.isShowing())
                        searchingDialog.dismiss();
                }

            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            showAlertDialog("You are Connected to Internet", "Do you want to sync with Server", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    class MHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * DrawerService of onStartCommand Will send this message
                 */
                case Global.MSG_ALLTHREAD_READY: {
                    if (WorkService.workThread.isConnected()) {
                        connectingProgressDialog.cancel();
                    } else {
                        connectingProgressDialog.cancel();
                    }
                    break;
                }

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(mContext, (result == 1) ? "Printer Connected" : "Connecting Printer failed. Try Again!", Toast.LENGTH_SHORT).show();
                    connectingProgressDialog.cancel();
                    break;
                }

                case Global.CMD_POS_SETBARCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(mContext, (result == 1) ? Global.toast_success : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "Result: " + result);
                    break;
                }

                case Global.CMD_POS_PRINTPICTURERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(mContext, (result == 1) ? Global.toast_success : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "Result: " + result);
                    break;
                }

                case com.lvrenyang.utils.Constant.MSG_BTHEARTBEATTHREAD_UPDATESTATUS:
                case com.lvrenyang.utils.Constant.MSG_NETHEARTBEATTHREAD_UPDATESTATUS: {
                    int statusOK = msg.arg1;
                    int status = msg.arg2;
                    Log.e("TAG", "statusOK: " + statusOK + " status: " + DataUtils.byteToStr((byte) status));
                    if (statusOK == 1) {
                        Log.e("SSSSSSSSS" + statusOK, "statusOK");
                    } else {
                        Log.e("NOOOO" + statusOK, "statusOKstatusOK");
                    }
                    connectingProgressDialog.cancel();
                    break;
                }
                case Global.CMD_POS_WRITE_BT_FLOWCONTROL_RESULT: {
                    int result = msg.arg1;
                    Log.e("TAG", "Result: " + result);
                    connectingProgressDialog.cancel();
                    break;
                }
            }
        }
    }

    public void startDiscovery() {
        adapter.cancelDiscovery();
        adapter.startDiscovery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                searchingDialog.show();
                startDiscovery();
                startDiscoveryTimer();
            } else if (resultCode == RESULT_CANCELED) {
                Utils.showToast("Switch On Bluetooth to connect Printer and print", mContext);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectivityReceiver.connectivityReceiverListener = null;
        WorkService.delHandler(mHandler);
        mHandler = null;
        unInitBroadcast();
        if (searchingDialog != null && searchingDialog.isShowing()) {
            searchingDialog.dismiss();
        }
        if (connectingProgressDialog != null && connectingProgressDialog.isShowing()) {
            connectingProgressDialog.dismiss();
        }
    }

    private void unInitBroadcast() {
        if (broadcastReceiver != null)
            mContext.unregisterReceiver(broadcastReceiver);
    }

}
