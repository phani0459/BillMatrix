package com.billmatrix.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.adapters.POSInventoryAdapter;
import com.billmatrix.adapters.POSItemAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Inventory;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class POSActivity extends Activity implements OnItemClickListener, POSItemAdapter.OnItemSelected {

    private static final String TAG = POSActivity.class.getSimpleName();
    @BindView(R.id.sp_pos_customers)
    public Spinner customersSpinner;
    @BindView(R.id.pos_tabs_layout)
    public LinearLayout tabsLayout;
    @BindView(R.id.rl_pos_cust_close)
    public RelativeLayout customerCloseButton;
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
    @BindView(R.id.pos_searchView)
    public EditText posSearchEditText;
    @BindView(R.id.ll_customerDetails)
    public LinearLayout customerDetailsLayout;
    @BindView(R.id.tv_POS_No_Customer)
    public TextView customerNotSelectedTextView;

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private POSItemAdapter posItemAdapter;
    private POSInventoryAdapter posInventoryAdapter;
    private float discountSelected;
    private ArrayList<Customer.CustomerData> dbCustomers;
    private Customer.CustomerData selectedCustomer;
    private ArrayAdapter<String> customerSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_pos);
        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        dbCustomers = new ArrayList<>();
        ArrayList<String> customerNames = new ArrayList<>();

        customerNames.add("SELECT CUSTOMER");
        dbCustomers = billMatrixDaoImpl.getCustomers();

        if (dbCustomers != null && dbCustomers.size() > 0) {
            for (Customer.CustomerData customer : dbCustomers) {
                customerNames.add(customer.username.toUpperCase());
            }
        }
        customerNames.add("GUEST CUSTOMER 1");
        customerSpinnerAdapter = Utils.loadSpinner(customersSpinner, mContext, customerNames);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout buttonLayout = (RelativeLayout) layoutInflater.inflate(R.layout.tab_button, null);
        final Button button = (Button) buttonLayout.findViewById(R.id.btn_tab);
        final Button tab_bottom_view = (Button) buttonLayout.findViewById(R.id.tab_bottom_view);

        button.setText(getString(R.string.INVENTORY));

        button.getBackground().setLevel(0);
        button.setTextColor(getResources().getColor(android.R.color.black));
        tab_bottom_view.setBackgroundColor(getResources().getColor(R.color.tabButtonBG));

        posInventoryList.setLayoutManager(new GridLayoutManager(mContext, 2));

        List<Inventory.InventoryData> inventory = billMatrixDaoImpl.getInventory();
        posInventoryAdapter = new POSInventoryAdapter(inventory, this);
        posInventoryList.setAdapter(posInventoryAdapter);

        List<Inventory.InventoryData> itemsInventory = new ArrayList<>();
        posItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        posItemAdapter = new POSItemAdapter(itemsInventory, this);
        posItemsRecyclerView.setAdapter(posItemAdapter);

        discountSelected = Utils.getSharedPreferences(mContext).getFloat(Constants.PREF_DISCOUNT_VALUE, 0.0f);
        discountCodeEditText.setText(Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, ""));

        tabsLayout.addView(buttonLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selecteCustomerName = (String) adapterView.getAdapter().getItem(i);
                if (!selecteCustomerName.equalsIgnoreCase("SELECT CUSTOMER") && !selecteCustomerName.contains("GUEST CUSTOMER")) {
                    for (Customer.CustomerData customer : dbCustomers) {
                        if (customer.username.equalsIgnoreCase(selecteCustomerName)) {
                            selectedCustomer = customer;
                            loadCustomerDetails();
                        }
                    }
                } else if (selecteCustomerName.equalsIgnoreCase("SELECT CUSTOMER")) {
                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(getString(R.string.select_pos_customer));
                } else if (selecteCustomerName.contains("GUEST CUSTOMER")) {
                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(getString(R.string.add_guest_as_customer));
                }
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
                        return false;
                    }
                }
                v.clearFocus();
                return false;
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
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: ");
        if (query.length() > 0) {
            query = query.toLowerCase();

            if (dbCustomers != null && dbCustomers.size() > 0) {
                for (Customer.CustomerData customerData : dbCustomers) {
                    if (customerData.username.toLowerCase().contains(query)) {
                        Log.e(TAG, "query Searched: " + query);
                        selectedCustomer = customerData;

                        try {
                            int customerSelectedPosition = customerSpinnerAdapter.getPosition(customerData.username.toUpperCase());
                            Log.e(TAG, "searchClicked: " + customerSelectedPosition);
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
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");
    }

    public void loadCustomerDetails() {
        if (selectedCustomer != null) {
            customerLocationTextView.setText(selectedCustomer.location.toUpperCase());
            customerContactTextView.setText(selectedCustomer.mobile_number.toUpperCase());
            customerNameTextView.setText(selectedCustomer.username.toUpperCase());

            customerNotSelectedTextView.setVisibility(View.GONE);
            customerDetailsLayout.setVisibility(View.VISIBLE);

        }
    }

    public void removePreferences() {
        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.IS_LOGGED_IN, false).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_USER_TYPE, null).apply();
        Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_EMP_LOGIN_ID, "").apply();
        FileUtils.deleteFile(mContext, Constants.EMPLOYEE_FILE_NAME);
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

    public void showToast(String msg) {
        Toast.makeText(mContext, msg + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(int caseInt, int position) {
        Inventory.InventoryData selectedInventory = posInventoryAdapter.getItem(position);
        if (posItemAdapter.inventories != null && posItemAdapter.inventories.size() > 0) {
            if (posItemAdapter.inventories.contains(selectedInventory)) {
                int inventoryQty = Integer.parseInt(selectedInventory.selectedQTY);
                if (TextUtils.isDigitsOnly(selectedInventory.qty)) {
                    if (Integer.parseInt(selectedInventory.qty) > inventoryQty) {
                        inventoryQty = inventoryQty + 1;
                        selectedInventory.selectedQTY = inventoryQty + "";
                        posItemAdapter.changeInventory(selectedInventory);
                    } else {
                        showToast("Items not available");
                    }
                }
                return;
            } else {
                selectedInventory.selectedQTY = "1";
            }
        } else {
            selectedInventory.selectedQTY = "1";
        }
        posItemAdapter.addInventory(selectedInventory);
        totalCartItemsTextView.setText(posItemAdapter.getItemCount() + " " + getString(R.string.ITEMS));
    }

    private String getSubTotal() {
        Float subTotal = 0f;
        for (int i = 0; i < posItemAdapter.getItemTotals().size(); i++) {
            subTotal = subTotal + posItemAdapter.getItemTotals().get(i);
        }
        return String.format(Locale.getDefault(), "%.2f", subTotal) + "";
    }

    @Override
    public void itemSelected(int caseInt, final int position) {
        switch (caseInt) {
            case 0:
                subTotalTextView.setText(getString(R.string.sub_total) + " " + getSubTotal() + "/-");
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }
}
