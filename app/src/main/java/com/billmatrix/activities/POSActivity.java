package com.billmatrix.activities;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.adapters.POSInventoryAdapter;
import com.billmatrix.adapters.POSItemAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Inventory;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.ServerUtils;
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

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private POSItemAdapter posItemAdapter;
    private POSInventoryAdapter posInventoryAdapter;
    private float discountSelected;
    private ArrayList<Customer.CustomerData> dbCustomers;
    private Customer.CustomerData selectedCustomer;
    private ArrayAdapter<String> customerSpinnerAdapter;
    private boolean isGuestCustomerSelected;
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_pos);
        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        dbCustomers = new ArrayList<>();

        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        ArrayList<String> customerNames = new ArrayList<>();

        customerNames.add("SELECT CUSTOMER");
        customerNames.add("GUEST CUSTOMER 1");

        dbCustomers = billMatrixDaoImpl.getCustomers();

        if (dbCustomers != null && dbCustomers.size() > 0) {
            for (Customer.CustomerData customer : dbCustomers) {
                customerNames.add(customer.username.toUpperCase());
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

        /**
         * set texts defaults
         */
        totalCartItemsTextView.setText("0" + " " + getString(R.string.ITEMS));
        subTotalTextView.setText(getString(R.string.sub_total) + " " + "0.00" + "/-");
        discountCalTextView.setText(getString(R.string.discount) + ": " + "0.00" + "/-");
        totalValueTextView.setText(" " + "0.00" + "/-");
        taxValueTextView.setText(" " + "0.00" + "/-");

        tabsLayout.addView(buttonLayout);
    }

    @OnClick(R.id.rl_pos_cust_edit)
    public void showCustomerDetailsDialog() {
        if (selectedCustomer == null) {
            if (!isGuestCustomerSelected) {
                return;
            }
        }

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pos_customer_details);
        dialog.setCancelable(false);

        final EditText customerUserName = (EditText) dialog.findViewById(R.id.et_pos_cust_name);
        final EditText customerMobile = (EditText) dialog.findViewById(R.id.et_pos_cust_mobile);
        EditText customerEmail = (EditText) dialog.findViewById(R.id.et_pos_cust_email);
        final EditText customerLocation = (EditText) dialog.findViewById(R.id.et_pos_cust_location);

        Button editCustomerButton = (Button) dialog.findViewById(R.id.btn_edit_add_customer);
        Button close = (Button) dialog.findViewById(R.id.btn_close_cust_details_dialog);

        if (selectedCustomer != null) {
            customerEmail.setText("");
            customerLocation.setText(selectedCustomer.location);
            customerUserName.setText(selectedCustomer.username);
            customerMobile.setText(selectedCustomer.mobile_number);
            editCustomerButton.setText(getString(R.string.save));
        } else {
            editCustomerButton.setText(getString(R.string.add));
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
                } else {
                    editedCustomerData.status = "1";
                    editedCustomerData.admin_id = adminId;
                    editedCustomerData.date = Constants.getDateFormat().format(System.currentTimeMillis());
                    editedCustomerData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
                    editedCustomerData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
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

                editedCustomerData.location = customerLocationS;
                editedCustomerData.username = customerName;
                editedCustomerData.mobile_number = customerContact;

                if (selectedCustomer != null) {
                    updateCustomerinDB(editedCustomerData, dialog);
                } else {
                    addCustomerinDB(editedCustomerData, dialog);
                }
            }
        });

        dialog.show();
    }

    public void updateCustomerinDB(Customer.CustomerData customerData, Dialog dialog) {
        boolean isCustomerUpdated = billMatrixDaoImpl.updateCustomer(customerData);

        if (isCustomerUpdated) {
            if (Utils.isInternetAvailable(mContext)) {
                ServerUtils.updateCustomertoServer(customerData, mContext);
            } else {
                Utils.showToast("Customer Updated successfully", mContext);
            }
            dialog.dismiss();
            selectedCustomer = customerData;
            isGuestCustomerSelected = false;
            loadCustomerDetails();
        } else {
            Utils.showToast("Customer Mobile Number must be unique", mContext);
        }
    }

    public void addCustomerinDB(Customer.CustomerData customerData, Dialog dialog) {
        long customerAdded = billMatrixDaoImpl.addCustomer(customerData);

        if (customerAdded != -1) {
            if (Utils.isInternetAvailable(mContext)) {
                ServerUtils.addCustomertoServer(customerData, mContext, adminId);
            } else {
                Utils.showToast("Customer Updated successfully", mContext);
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

    }

    @OnClick(R.id.btn_credit)
    public void credit() {
        paymentTypeDialog(true);
    }

    @OnClick(R.id.btn_cash_or_card)
    public void cashOrCard() {
        paymentTypeDialog(false);
    }

    public void paymentTypeDialog(boolean isCredit) {
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
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_pos_cash_card);

        TextView title = (TextView) dialog.findViewById(R.id.tv_payments_title);
        TextView discount = (TextView) dialog.findViewById(R.id.tv_dialog_discount);
        TextView tax = (TextView) dialog.findViewById(R.id.tv_dialog_tax);
        final TextView total = (TextView) dialog.findViewById(R.id.tv_dialog_total);
        final TextView totalPaid = (TextView) dialog.findViewById(R.id.tv_dialog_total_paid);
        final TextView balance = (TextView) dialog.findViewById(R.id.tv_dialog_balance);
        final TextView changeTextView = (TextView) dialog.findViewById(R.id.tv_dialog_change);
        final EditText cashEditText = (EditText) dialog.findViewById(R.id.et_dialog_cash);
        final EditText cardEditText = (EditText) dialog.findViewById(R.id.et_dialog_card);
        final LinearLayout cardLayout = (LinearLayout) dialog.findViewById(R.id.ll_dialog_card);

        Button close = (Button) dialog.findViewById(R.id.btn_close_cash_card_dialog);
        final Button cashButton = (Button) dialog.findViewById(R.id.btn_dialog_cash);
        final Button cardButton = (Button) dialog.findViewById(R.id.btn_dialog_card);
        final Button creditButton = (Button) dialog.findViewById(R.id.btn_dialog_credit);

        title.setText(getString(R.string.payments) + " - " + username);

        if (isCredit) {
            cashButton.setBackgroundResource(R.drawable.button_disable);
            creditButton.setBackgroundResource(R.drawable.orange_button);
            cardButton.setBackgroundResource(R.drawable.button_disable);
            cardLayout.setVisibility(View.VISIBLE);
        }

        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardLayout.setVisibility(View.VISIBLE);
                cashButton.setBackgroundResource(R.drawable.button_disable);
                creditButton.setBackgroundResource(R.drawable.orange_button);
                cardButton.setBackgroundResource(R.drawable.button_disable);
            }
        });

        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardLayout.setVisibility(View.VISIBLE);
                cardButton.setBackgroundResource(R.drawable.button_enable);
                cashButton.setBackgroundResource(R.drawable.button_disable);
                creditButton.setBackgroundResource(R.drawable.button_disable);
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
            }
        });

        String discountString = discountCalTextView.getText().toString().replace(getString(R.string.discount) + ": ", "");
        String totalString = totalValueTextView.getText().toString().replace(getString(R.string.sub_total) + " ", "");
        String taxString = taxValueTextView.getText().toString().replace(" ", "");

        discount.setText(discountString.replace("/-", ""));
        total.setText(totalString.replace("/-", ""));
        tax.setText(taxString.replace("/-", ""));

        totalPaid.setText(getString(R.string.zero));
        changeTextView.setText(getString(R.string.zero));
        balance.setText(getString(R.string.zero));

        final float billTotalValue = Float.parseFloat(total.getText().toString().equalsIgnoreCase("") ? "0.00" : total.getText().toString());

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
                 * if change is negative it is balance
                 */
                if (changeValue == 0) {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    balance.setText(getString(R.string.zero));
                    changeTextView.setText(getString(R.string.zero));
                } else if (changeValue < 0) {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", (moneyPaidFromCard + moneyPaidFromCash)));
                    balance.setText(String.format(Locale.getDefault(), "%.2f", -changeValue));
                    changeTextView.setText(getString(R.string.zero));
                } else {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    changeTextView.setText(String.format(Locale.getDefault(), "%.2f", changeValue));
                    balance.setText(getString(R.string.zero));
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
                 * if change is negative it is balance
                 */
                if (changeValue == 0) {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    balance.setText(getString(R.string.zero));
                    changeTextView.setText(getString(R.string.zero));
                } else if (changeValue < 0) {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", (moneyPaidFromCard + moneyPaidFromCash)));
                    balance.setText(String.format(Locale.getDefault(), "%.2f", -changeValue));
                    changeTextView.setText(getString(R.string.zero));
                } else {
                    totalPaid.setText(String.format(Locale.getDefault(), "%.2f", billTotalValue));
                    changeTextView.setText(String.format(Locale.getDefault(), "%.2f", changeValue));
                    balance.setText(getString(R.string.zero));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                if (posItemAdapter.getItemCount() > 0) {
                    posItemAdapter.removeAllItems();
                }
                customersSpinner.setSelection(0);
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
                            isGuestCustomerSelected = false;
                            loadCustomerDetails();
                        }
                    }
                    editCustomerImageView.setImageResource(R.drawable.edit_icon);
                } else if (selecteCustomerName.equalsIgnoreCase("SELECT CUSTOMER")) {
                    selectedCustomer = null;
                    isGuestCustomerSelected = false;
                    noCustomerItemTextView.setVisibility(View.VISIBLE);
                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(getString(R.string.select_pos_customer));
                    editCustomerImageView.setImageResource(R.drawable.edit_icon);
                } else if (selecteCustomerName.contains("GUEST CUSTOMER")) {
                    selectedCustomer = null;
                    isGuestCustomerSelected = true;
                    customerNotSelectedTextView.setVisibility(View.VISIBLE);
                    noCustomerItemTextView.setVisibility(View.GONE);
                    customerDetailsLayout.setVisibility(View.GONE);
                    customerNotSelectedTextView.setText(selecteCustomerName);
                    editCustomerImageView.setImageResource(R.drawable.add_customer);
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
            selectedCustomer = null;

            if (dbCustomers != null && dbCustomers.size() > 0) {
                for (Customer.CustomerData customerData : dbCustomers) {
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

    @Override
    public void onItemClick(int caseInt, int position) {
        Inventory.InventoryData selectedInventory = posInventoryAdapter.getItem(position);
        if (noCustomerItemTextView.getVisibility() == View.GONE) {
            if (posItemAdapter.inventories != null && posItemAdapter.inventories.size() > 0) {
                if (posItemAdapter.inventories.contains(selectedInventory)) {
                    int inventoryQty = Integer.parseInt(selectedInventory.selectedQTY);
                    if (TextUtils.isDigitsOnly(selectedInventory.qty)) {
                        if (Integer.parseInt(selectedInventory.qty) > inventoryQty) {
                            inventoryQty = inventoryQty + 1;
                            selectedInventory.selectedQTY = inventoryQty + "";
                            posItemAdapter.changeInventory(selectedInventory);
                        } else {
                            Utils.showToast("Items not available", mContext);
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


    @Override
    public void itemSelected(int caseInt, final int position) {
        switch (caseInt) {
            case 0:
                Float subTotal = getSubTotal();
                Float discount = getDiscountCalculated(subTotal);
                subTotalTextView.setText(getString(R.string.sub_total) + " " + String.format(Locale.getDefault(), "%.2f", subTotal) + "/-");
                discountCalTextView.setText(getString(R.string.discount) + ": " + String.format(Locale.getDefault(), "%.2f", discount) + "/-");
                totalValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", (subTotal - discount)) + "/-");
                break;
            case 1:
                showAlertDialog("Are you sure?", "You want to remove Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        posItemAdapter.removeItem(position);
                        Float subTotal = getSubTotal();
                        Float discount = getDiscountCalculated(subTotal);
                        subTotalTextView.setText(getString(R.string.sub_total) + " " + String.format(Locale.getDefault(), "%.2f", subTotal) + "/-");
                        discountCalTextView.setText(getString(R.string.discount) + ": " + String.format(Locale.getDefault(), "%.2f", discount) + "/-");
                        totalValueTextView.setText(" " + String.format(Locale.getDefault(), "%.2f", (subTotal - discount)) + "/-");
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
}
