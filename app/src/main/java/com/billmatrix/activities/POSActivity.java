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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.adapters.GenExpensesAdapter;
import com.billmatrix.adapters.POSInventoryAdapter;
import com.billmatrix.adapters.POSItemAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.GeneralExpense;
import com.billmatrix.models.Inventory;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.FileUtils;
import com.billmatrix.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class POSActivity extends Activity implements OnItemClickListener {

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

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private POSItemAdapter posItemAdapter;
    private POSInventoryAdapter posInventoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        ButterKnife.bind(this);

        mContext = this;
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        ArrayList<Customer.CustomerData> customers = new ArrayList<>();
        ArrayList<String> customerNames = new ArrayList<>();

        customerNames.add("SELECT CUSTOMER");
        customers = billMatrixDaoImpl.getCustomers();

        if (customers != null && customers.size() > 0) {
            for (Customer.CustomerData customer : customers) {
                customerNames.add(customer.username.toUpperCase());
            }
        }
        customerNames.add("NEW CUSTOMER");
        Utils.loadSpinner(customersSpinner, mContext, customerNames);

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

        tabsLayout.addView(buttonLayout);
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
        posItemAdapter.addInventory(selectedInventory);
        totalCartItemsTextView.setText(posItemAdapter.getItemCount() + " " + getString(R.string.ITEMS));
        subTotalTextView.setText(getString(R.string.sub_total) + " " + "");
    }
}
