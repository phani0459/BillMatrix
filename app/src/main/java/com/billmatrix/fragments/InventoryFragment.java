package com.billmatrix.fragments;


import android.app.AlertDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.WorkService;
import com.billmatrix.activities.BaseTabActivity;
import com.billmatrix.adapters.DevicesAdapter;
import com.billmatrix.adapters.InventoryAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.database.DBConstants;
import com.billmatrix.interfaces.OnDataFetchListener;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Vendor;
import com.billmatrix.network.ServerData;
import com.billmatrix.network.ServerUtils;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;
import com.lvrenyang.pos.Cmd;
import com.lvrenyang.utils.DataUtils;

import org.zirco.myprinter.Global;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = InventoryFragment.class.getSimpleName();
    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    private InventoryAdapter inventoryAdapter;
    private static final int BLUETOOTH_ENABLE_REQUEST_ID = 1;

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
    @BindView(R.id.et_print_bar_itemName)
    public EditText printItemName;
    @BindView(R.id.et_print_bar_itemCode)
    public EditText printItemCode;
    @BindView(R.id.et_gen_bar_itemCode)
    public EditText generateItemCode;
    @BindView(R.id.et_gen_bar_itemName)
    public EditText generateItemName;
    @BindView(R.id.tv_generated_barCode)
    public TextView generatedBarCodeTextView;
    @BindView(R.id.et_noOfCodes)
    public EditText noOfCodesEditText;
    @BindView(R.id.tv_inven_no_results)
    public TextView noResultsTextView;
    @BindView(R.id.btn_prnt_bar_code)
    public Button printBarcodeButton;
    @BindView(R.id.btn_gen_bar_code)
    public Button generateBarCodeButton;

    private String adminId;
    private Inventory.InventoryData selectedInventorytoEdit;
    public boolean isEditing;
    private BluetoothAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog connectingProgressDialog;
    private MHandler mHandler;
    private ProgressDialog searchingDialog;
    private ArrayAdapter<String> vendorSpinnerAdapter;
    private Dialog devicesDialog;
    private DevicesAdapter devicesAdapter;
    private ListView devicesListView;

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
        connectingProgressDialog = Utils.getProgressDialog(mContext, "");

        Utils.loadSpinner(unitSpinner, mContext, R.array.units_array);
        generatedBarCodeTextView.setText(getString(R.string.BAR_CODE_GENERATED) + "\n" + "XXXXXXXX");
        barCodeEditText.setFilters(Utils.getInputFilter(12));

        dateEditText.setInputType(InputType.TYPE_NULL);
        final DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, true);

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

        generateBarCodeButton.setEnabled(false);
        printBarcodeButton.setEnabled(false);
        generateBarCodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
        printBarcodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);

        ArrayList<Vendor.VendorData> vendors = billMatrixDaoImpl.getVendors();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Select vendor");

        if (vendors != null && vendors.size() > 0) {
            for (Vendor.VendorData vendorData : vendors) {
                strings.add(vendorData.name);
            }

        }
        vendorSpinnerAdapter = Utils.loadSpinner(vendorSpinner, mContext, strings);

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
        inventoryAdapter = new InventoryAdapter(inventoryDatas, this, mContext);
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
                devicesDialog.dismiss();
            }
        });

        devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
        devicesListView.setAdapter(devicesAdapter);

        return v;
    }

    public void onBackPressed() {
        if (addInventoryButton.getText().toString().equalsIgnoreCase("SAVE")) {
            ((BaseTabActivity) mContext).showAlertDialog("Save and Exit?", "Do you want to save the changes made", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addInventory();
                    if (isInventoryAdded) {
                        ((BaseTabActivity) mContext).finish();
                    }
                }
            });
        } else {
            ((BaseTabActivity) mContext).finish();
        }
    }

    private void getInventoryFromServer(String adminId) {
        Log.e(TAG, "getInventoryFromServer: ");
        ServerData serverData = new ServerData();
        serverData.setBillMatrixDaoImpl(billMatrixDaoImpl);
        serverData.setFromLogin(false);
        serverData.setProgressDialog(null);
        serverData.setContext(mContext);
        serverData.setOnDataFetchListener(this);
        serverData.getInventoryFromServer(adminId);
    }

    @Override
    public void onDataFetch(int dataFetched) {
        ArrayList<Inventory.InventoryData> inventoryDatas = billMatrixDaoImpl.getInventory();

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!inventoryData.status.equalsIgnoreCase("-1")) {
                    inventoryAdapter.addInventory(inventoryData);
                }
            }
        }
    }

    private boolean isInventoryAdded;

    @OnClick(R.id.btnAddInventory)
    public void addInventory() {
        Utils.hideSoftKeyboard(itemCodeEditText);
        isInventoryAdded = false;

        Inventory.InventoryData inventoryData = new Inventory().new InventoryData();
        Inventory.InventoryData inventoryFromServer = new Inventory().new InventoryData();
        String itemCode = itemCodeEditText.getText().toString();

        if (TextUtils.isEmpty(itemCode)) {
            Utils.showToast("Enter Item Code", mContext);
            return;
        }

        String itemName = itemNameeEditText.getText().toString();

        if (TextUtils.isEmpty(itemName)) {
            Utils.showToast("Enter Item Name", mContext);
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(unit)) {
            Utils.showToast("Select Unit", mContext);
            return;
        }

        if (unit.equalsIgnoreCase("Other")) {
            unit = customUnitEditText.getText().toString();
            if (TextUtils.isEmpty(unit)) {
                Utils.showToast("Enter Custom Unit", mContext);
                return;
            }
        }

        String qty = qtyEditText.getText().toString();

        if (TextUtils.isEmpty(qty)) {
            Utils.showToast("Enter Quantity", mContext);
            return;
        }

        String price = priceEditText.getText().toString();

        if (TextUtils.isEmpty(price)) {
            Utils.showToast("Enter Price", mContext);
            return;
        }

        String myCost = myCostEditText.getText().toString();

        if (TextUtils.isEmpty(myCost)) {
            Utils.showToast("Enter Cost", mContext);
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date)) {
            Utils.showToast("Select Date", mContext);
            return;
        }

        String wareHouse = wareHouseEditText.getText().toString();

        String vendor = vendorSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(vendor) || vendor.equalsIgnoreCase("select vendor")) {
            Utils.showToast("Select Vendor", mContext);
            return;
        }

        String barcode = barCodeEditText.getText().toString();

        if (!TextUtils.isEmpty(barcode)) {
            if (barcode.length() < 10) {
                Utils.showToast("Enter valid Barcode", mContext);
                return;
            }
        }

        String photo = photoEditText.getText().toString();

        if (addInventoryButton.getText().toString().equalsIgnoreCase("ADD")) {
            inventoryData.create_date = Constants.getDateTimeFormat().format(System.currentTimeMillis());
            inventoryData.add_update = Constants.ADD_OFFLINE;
        } else {
            if (selectedInventorytoEdit != null) {
                inventoryData.id = selectedInventorytoEdit.id;
                inventoryData.create_date = selectedInventorytoEdit.create_date;
                if (selectedInventorytoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                    inventoryData.add_update = Constants.UPDATE_OFFLINE;
                }
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
                    inventoryFromServer = ServerUtils.addInventorytoServer(inventoryData, mContext, adminId, billMatrixDaoImpl);
                } else {
                    /**
                     * To show pending sync Icon in database page
                     */
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, true).apply();
                    Utils.showToast("Inventory Added successfully", mContext);
                    inventoryFromServer = inventoryData;
                }
            } else {
                if (selectedInventorytoEdit != null) {
                    if (Utils.isInternetAvailable(mContext)) {
                        inventoryFromServer = ServerUtils.updateInventorytoServer(inventoryData, mContext, billMatrixDaoImpl);
                    } else {
                        /**
                         * To show pending sync Icon in database page
                         */
                        inventoryFromServer = inventoryData;
                        Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, true).apply();
                        Utils.showToast("Inventory Updated successfully", mContext);
                    }
                }
            }

            inventoryAdapter.addInventory(inventoryFromServer);
            inventoryRecyclerView.smoothScrollToPosition(inventoryAdapter.getItemCount());
            addInventoryButton.setText(getString(R.string.add));
            isEditing = false;
            ((BaseTabActivity) mContext).ifTabCanChange = true;
            isInventoryAdded = true;
        } else {
            Utils.showToast("Item Code must be unique", mContext);
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                ((BaseTabActivity) mContext).showAlertDialog("Are you sure?", "You want to delete Inventory", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Inventory.InventoryData selectedInventory = inventoryAdapter.getItem(position);
                        if (selectedInventory.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
                            billMatrixDaoImpl.updateInventory(DBConstants.STATUS, "-1", selectedInventory.item_code);
                        } else {
                            billMatrixDaoImpl.deleteInventory(selectedInventory.item_code);
                        }
                        if (Utils.isInternetAvailable(mContext)) {
                            if (!TextUtils.isEmpty(selectedInventory.id)) {
                                ServerUtils.deleteInventoryfromServer(selectedInventory, mContext, billMatrixDaoImpl);
                            }
                        } else {
                            /**
                             * To show pending sync Icon in database page
                             */
                            Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_INVENTORY_EDITED_OFFLINE, true).apply();
                            Utils.showToast("Inventory Deleted successfully", mContext);
                        }
                        inventoryAdapter.deleteInventory(position);
                    }
                });
                break;
            case 2:
                if (!isEditing) {
                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

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
                    try {
                        int vendorSelectedPosition = vendorSpinnerAdapter.getPosition(selectedInventorytoEdit.vendor);
                        vendorSpinner.setSelection(vendorSelectedPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vendorSpinner.setSelection(0);
                    }

                    int unitSelection = getUnitSelection(selectedInventorytoEdit.unit);
                    unitSpinner.setSelection(unitSelection);
                    if (unitSelection == 7) {
                        customUnitEditText.setVisibility(View.VISIBLE);
                        customUnitEditText.setText(selectedInventorytoEdit.unit);
                    } else {
                        customUnitEditText.setVisibility(View.GONE);
                    }

                    billMatrixDaoImpl.deleteInventory(inventoryAdapter.getItem(position).item_code);
                    inventoryAdapter.deleteInventory(position);
                } else {
                    Utils.showToast("Save present editing inventory before editing other inventory", mContext);
                }
                break;
            case 3:
                selectedInventorytoEdit = inventoryAdapter.getItem(position);
                generateItemCode.setText(selectedInventorytoEdit.item_code);
                generateItemName.setText(selectedInventorytoEdit.item_name);
                printItemCode.setText(selectedInventorytoEdit.item_code);
                printItemName.setText(selectedInventorytoEdit.item_name);
                if (!TextUtils.isEmpty(selectedInventorytoEdit.barcode)) {
                    generatedBarCodeTextView.setText(getString(R.string.BAR_CODE_GENERATED) + "\n" + selectedInventorytoEdit.barcode);

                    generateBarCodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
                    generateBarCodeButton.setEnabled(false);
                    printBarcodeButton.setBackgroundResource(R.drawable.barcode_btn_bg);
                    printBarcodeButton.setEnabled(true);
                } else {
                    generatedBarCodeTextView.setText(getString(R.string.BAR_CODE_GENERATED) + "\n" + "XXXXXXXX");

                    generateBarCodeButton.setBackgroundResource(R.drawable.barcode_btn_bg);
                    generateBarCodeButton.setEnabled(true);
                    printBarcodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
                    printBarcodeButton.setEnabled(false);
                }
                noOfCodesEditText.setText("1");
                break;
            case 4:
                selectedInventorytoEdit = null;
                generateItemCode.setText("");
                generateItemName.setText("");
                printItemCode.setText("");
                noOfCodesEditText.setText("");
                printItemName.setText("");
                generatedBarCodeTextView.setText(getString(R.string.BAR_CODE_GENERATED) + "\n" + "XXXXXXXX");

                generateBarCodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
                generateBarCodeButton.setEnabled(false);
                printBarcodeButton.setBackgroundResource(R.drawable.edit_text_disabled_border);
                printBarcodeButton.setEnabled(false);
                break;
        }
    }

    private int getUnitSelection(String unit) {
        if (unit.equalsIgnoreCase("KGs")) {
            return 0;
        } else if (unit.equalsIgnoreCase("GMs")) {
            return 1;
        } else if (unit.equalsIgnoreCase("LTRs")) {
            return 2;
        } else if (unit.equalsIgnoreCase("MLs")) {
            return 3;
        } else if (unit.equalsIgnoreCase("PCs")) {
            return 4;
        } else if (unit.equalsIgnoreCase("NOs")) {
            return 5;
        } else if (unit.equalsIgnoreCase("units")) {
            return 6;
        } else if (unit.equalsIgnoreCase("Other")) {
            return 7;
        }
        return 7;
    }

    public void searchClosed() {
        Log.e(TAG, "searchClosed: ");
        noResultsTextView.setVisibility(View.GONE);

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
            noResultsTextView.setVisibility(View.GONE);
            boolean noInventory = false;
            query = query.toLowerCase();
            inventoryAdapter.removeAllInventories();

            ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory();

            if (inventories != null && inventories.size() > 0) {
                for (Inventory.InventoryData inventoryData : inventories) {
                    if ((!TextUtils.isEmpty(inventoryData.barcode) && inventoryData.barcode.toLowerCase().contains(query)) ||
                            inventoryData.item_name.toLowerCase().contains(query) ||
                            inventoryData.item_code.toLowerCase().contains(query)) {
                        noInventory = true;
                        inventoryAdapter.addInventory(inventoryData);
                    }
                }
            }

            if (!noInventory) {
                noResultsTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Printer Code
     */
    public void printBarcode() {
        if (WorkService.workThread != null && WorkService.workThread.isConnected()) {
            Log.e(TAG, "printBarcode: " + selectedInventorytoEdit.barcode);
            Bundle data = new Bundle();
            data.putString(Global.STRPARA1, selectedInventorytoEdit.barcode);
            data.putInt(Global.INTPARA1, 0);
            data.putInt(Global.INTPARA2, Cmd.Constant.BARCODE_TYPE_UPC_A);
            data.putInt(Global.INTPARA3, 3);
            data.putInt(Global.INTPARA4, 96);
            data.putInt(Global.INTPARA5, 0);
            data.putInt(Global.INTPARA6, 2);
            WorkService.workThread.handleCmd(Global.CMD_POS_SETBARCODE, data);
        } else {
            Utils.showToast(Global.toast_notconnect, mContext);
        }
    }

    /**
     * Printer Code
     *
     * @param v
     */
    @OnClick(R.id.btn_prnt_bar_code)
    public void enableBluetooth(View v) {
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
                printBarcode();
            } else {
                searchingDialog.show();
                startDiscovery();
            }
        }
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

                    devicesAdapter.add(device);
                    devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            connectingProgressDialog.setMessage("Connecting" + " " + devicesAdapter.getItem(position).getAddress());
                            connectingProgressDialog.setIndeterminate(true);
                            connectingProgressDialog.setCancelable(false);
                            connectingProgressDialog.show();

                            WorkService.workThread.connectBt(devicesAdapter.getItem(position).getAddress());

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
            } else if (resultCode == RESULT_CANCELED) {
                Utils.showToast("Switch On Bluetooth to connect Printer and print", mContext);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
        unInitBroadcast();
    }

    private void unInitBroadcast() {
        if (broadcastReceiver != null)
            mContext.unregisterReceiver(broadcastReceiver);
    }
}
