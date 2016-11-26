package com.billmatrix.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.InventoryAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = InventoryFragment.class.getSimpleName();
    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private InventoryAdapter inventoryAdapter;

    @BindView(R.id.inventoryList)
    public RecyclerView inventoryRecyclerView;
    @BindView(R.id.et_inven_item_code)
    public EditText itemCodeEditText;
    @BindView(R.id.et_inven_item_name)
    public EditText itemNameeEditText;
    @BindView(R.id.sp_inven_unit)
    public Spinner unitSpinner;
    @BindView(R.id.et_custom_unit)
    public EditText customUnitEditText;
    @BindView(R.id.et_inven_qty)
    public EditText qtyEditText;
    @BindView(R.id.et_inven_price)
    public EditText priceEditText;
    @BindView(R.id.et_inven_my_cost)
    public EditText myCostEditText;
    @BindView(R.id.et_inven_date)
    public EditText dateEditText;
    @BindView(R.id.et_inven_ware_house)
    public EditText wareHouseEditText;
    @BindView(R.id.sp_inven_vendor)
    public Spinner vendorSpinner;
    @BindView(R.id.et_inven_bar_code)
    public EditText barCodeEditText;
    @BindView(R.id.et_inven_photo)
    public EditText photoEditText;
    @BindView(R.id.btnAddInventory)
    public Button addInventoryButton;

    private String adminId;
    private Inventory.InventoryData selectedInventorytoEdit;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);
        adminId = Utils.getSharedPreferences(mContext).getString(Constants.PREF_ADMIN_ID, null);

        Utils.loadSpinner(unitSpinner, mContext, R.array.units_array);

        dateEditText.setInputType(InputType.TYPE_NULL);
        final DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, false);

        dateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!datePickerDialog.isShowing()) {
                    Utils.hideSoftKeyboard(itemCodeEditText);
                    datePickerDialog.show();
                }
                return false;
            }
        });

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }

        }
        Utils.loadSpinner(vendorSpinner, mContext, strings);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItem(position).toString().equalsIgnoreCase("other")) {
                    customUnitEditText.setVisibility(View.VISIBLE);
                } else {
                    customUnitEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        List<Inventory.InventoryData> inventoryDatas = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(inventoryDatas, this);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        inventoryDatas = billMatrixDaoImpl.getInventory();

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!inventoryData.status.equalsIgnoreCase("-1")) {
                    inventoryAdapter.addInventory(inventoryData);
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getInventoryFromServer(adminId);
                }
            }
        }

        return v;
    }

    private void getInventoryFromServer(String adminId) {
        Log.e(TAG, "getInventoryFromServer: ");
        Call<Inventory> call = Utils.getBillMatrixAPI(mContext).getAdminInventory(adminId);

        call.enqueue(new Callback<Inventory>() {

            /**
             * Successful HTTP response.
             * @param call server call
             * @param response server response
             */
            @Override
            public void onResponse(Call<Inventory> call, Response<Inventory> response) {
                Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    Inventory inventory = response.body();
                    if (inventory.status == 200 && inventory.InventoryData.equalsIgnoreCase("success")) {
                        for (Inventory.InventoryData inventoryData : inventory.data) {
                            billMatrixDaoImpl.addInventory(inventoryData);
                            inventoryAdapter.addInventory(inventoryData);
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
            public void onFailure(Call<Inventory> call, Throwable t) {
                Log.e(TAG, "FAILURE RESPONSE" + t.getMessage());
            }
        });
    }

    @OnClick(R.id.btnAddInventory)
    public void addInventory() {
        Utils.hideSoftKeyboard(itemCodeEditText);

        Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
        String itemCode = itemCodeEditText.getText().toString();

        if (TextUtils.isEmpty(itemCode)) {
            ((BaseTabActivity) mContext).showToast("Enter Item Code");
            return;
        }

        String itemName = itemNameeEditText.getText().toString();

        if (TextUtils.isEmpty(itemName)) {
            ((BaseTabActivity) mContext).showToast("Enter Item Name");
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(unit)) {
            ((BaseTabActivity) mContext).showToast("Select Unit");
            return;
        }

        if (unit.equalsIgnoreCase("Other")) {
            unit = customUnitEditText.getText().toString();
            if (TextUtils.isEmpty(unit)) {
                ((BaseTabActivity) mContext).showToast("Enter Custom Unit");
                return;
            }
        }

        String qty = qtyEditText.getText().toString();

        if (TextUtils.isEmpty(qty)) {
            ((BaseTabActivity) mContext).showToast("Enter Quantity");
            return;
        }

        String price = priceEditText.getText().toString();

        if (TextUtils.isEmpty(price)) {
            ((BaseTabActivity) mContext).showToast("Enter Price");
            return;
        }

        String myCost = myCostEditText.getText().toString();

        if (TextUtils.isEmpty(myCost)) {
            ((BaseTabActivity) mContext).showToast("Enter Cost");
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date)) {
            ((BaseTabActivity) mContext).showToast("Select Date");
            return;
        }

        String wareHouse = wareHouseEditText.getText().toString();

        String vendor = vendorSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(vendor) || vendor.equalsIgnoreCase("select vendor")) {
            ((BaseTabActivity) mContext).showToast("Select Vendor");
            return;
        }

        String barcode = barCodeEditText.getText().toString();

        if (TextUtils.isEmpty(barcode)) {
            ((BaseTabActivity) mContext).showToast("Scan Item For Barcode");
            return;
        }

        String photo = photoEditText.getText().toString();

        if (addInventoryButton.getText().toString().equalsIgnoreCase("ADD")) {
            inventoryData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        } else {
            if (selectedInventorytoEdit != null) {
                inventoryData.id = selectedInventorytoEdit.id;
                inventoryData.create_date = selectedInventorytoEdit.create_date;
            }
        }

        inventoryData.update_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
        inventoryData.admin_id = adminId;
        inventoryData.item_code = itemCode;
        inventoryData.item_name = itemName;
        inventoryData.unit = unit;
        inventoryData.qty = qty;
        inventoryData.price = price;
        inventoryData.mycost = myCost;
        inventoryData.date = date;
        inventoryData.warehouse = wareHouse;
        inventoryData.vendor = vendor;
        inventoryData.barcode = barcode;
        inventoryData.photo = photo;
        inventoryData.status = "1";

        long inventoryAdded = billMatrixDaoImpl.addInventory(inventoryData);

        if (inventoryAdded != -1) {
            inventoryAdapter.addInventory(inventoryData);
            inventoryRecyclerView.smoothScrollToPosition(inventoryAdapter.getItemCount());

            /**
             * reset all edit texts
             */
            itemCodeEditText.setText("");
            itemNameeEditText.setText("");
            unitSpinner.setSelection(0);
            customUnitEditText.setText("");
            qtyEditText.setText("");
            priceEditText.setText("");
            myCostEditText.setText("");
            dateEditText.setText("");
            wareHouseEditText.setText("");
            vendorSpinner.setSelection(0);
            barCodeEditText.setText("");
            photoEditText.setText("");

            if (addInventoryButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    addInventorytoServer(inventoryData);
                } else {
                    ((BaseTabActivity) mContext).showToast("Inventory Added successfully");
                }
            } else {
                if (selectedInventorytoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        updateInventorytoServer(inventoryData);
                    } else {
                        ((BaseTabActivity) mContext).showToast("Inventory Updated successfully");
                    }
                }
            }

        }
        addInventoryButton.setText(getString(R.string.add));
    }

    private void updateInventorytoServer(Inventory.InventoryData inventoryData) {
        Log.e(TAG, "updateCustomertoServer: ");
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).updateInventory(inventoryData.id, inventoryData.item_code, inventoryData.item_name,
                inventoryData.unit, inventoryData.qty, inventoryData.price, inventoryData.mycost, inventoryData.date, inventoryData.warehouse,
                inventoryData.vendor, inventoryData.barcode, inventoryData.photo, inventoryData.status);

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
                    HashMap<String, String> customerMap = response.body();
                    if (customerMap.get("status").equalsIgnoreCase("200")) {
                        if (customerMap.containsKey("update_inventory") && customerMap.get("update_inventory").equalsIgnoreCase("Successfully Updated")) {
                            ((BaseTabActivity) mContext).showToast("Inventory Updated successfully");
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

    private void addInventorytoServer(Inventory.InventoryData inventoryData) {
        Log.e(TAG, "addInventorytoServer: ");
        Call<HashMap<String, String>> call = Utils.getBillMatrixAPI(mContext).addInventory(adminId, inventoryData.item_code, inventoryData.item_name,
                inventoryData.unit, inventoryData.qty, inventoryData.price, inventoryData.mycost, inventoryData.date, inventoryData.warehouse, inventoryData.vendor,
                inventoryData.barcode, inventoryData.photo, "1");

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
                    HashMap<String, String> customerStatus = response.body();
                    if (customerStatus.get("status").equalsIgnoreCase("200")) {
                        if (customerStatus.get("create_inventory").equalsIgnoreCase("success")) {
                            ((BaseTabActivity) mContext).showToast("Inventory Added successfully");
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
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Inventory", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billMatrixDaoImpl.updateInventory("-1", inventoryAdapter.getItem(position).item_code);
                        inventoryAdapter.deleteInventory(position);
                    }
                });
                break;
            case 2:
                addInventoryButton.setText(getString(R.string.save));
                selectedInventorytoEdit = inventoryAdapter.getItem(position);

                itemCodeEditText.setText(selectedInventorytoEdit.item_code);
                itemNameeEditText.setText(selectedInventorytoEdit.item_name);
                qtyEditText.setText(selectedInventorytoEdit.qty);
                priceEditText.setText(selectedInventorytoEdit.price);
                myCostEditText.setText(selectedInventorytoEdit.mycost);
                dateEditText.setText(selectedInventorytoEdit.date);
                wareHouseEditText.setText(selectedInventorytoEdit.warehouse);
                barCodeEditText.setText(selectedInventorytoEdit.barcode);
                photoEditText.setText(selectedInventorytoEdit.photo);
                vendorSpinner.setSelection(0);

                int unitSelection = getUnitSelection(selectedInventorytoEdit.unit);
                unitSpinner.setSelection(unitSelection);
                if (unitSelection == 6) {
                    customUnitEditText.setVisibility(View.VISIBLE);
                    customUnitEditText.setText(selectedInventorytoEdit.unit);
                } else {
                    customUnitEditText.setVisibility(View.GONE);
                }

                billMatrixDaoImpl.deleteInventory(inventoryAdapter.getItem(position).item_code);
                inventoryAdapter.deleteInventory(position);
                break;
            case 3:
                selectedInventorytoEdit = inventoryAdapter.getItem(position);

                break;
        }
    }

    private int getUnitSelection(String unit) {
        if (unit.equalsIgnoreCase("KG's")) {
            return 0;
        } else if (unit.equalsIgnoreCase("GM's")) {
            return 1;
        } else if (unit.equalsIgnoreCase("LTR's")) {
            return 2;
        } else if (unit.equalsIgnoreCase("ML's")) {
            return 3;
        } else if (unit.equalsIgnoreCase("PC's")) {
            return 4;
        } else if (unit.equalsIgnoreCase("NO's")) {
            return 5;
        } else if (unit.equalsIgnoreCase("Other")) {
            return 6;
        }
        return 0;
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");

        inventoryAdapter.removeAllInventories();

        ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();

        if (inventories != null && inventories.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventories) {
                inventoryAdapter.addInventory(inventoryData);
            }
        }
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query);
        if (query.length() > 0) {
            query = query.toLowerCase();
            inventoryAdapter.removeAllInventories();

            ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();

            if (inventories != null && inventories.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventories) {
                    if (inventoryData.barcode.toLowerCase().contains(query) || inventoryData.item_name.toLowerCase().contains(query) ||
                            inventoryData.item_code.toLowerCase().contains(query)) {
                        inventoryAdapter.addInventory(inventoryData);
                    }
                }
            }
        }
    }
}
