package com.billmatrix.fragments;


import android.Manifest;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.WorkService;
import com.billmatrix.activities.BarcodeScannerActivity;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lvrenyang.utils.DataUtils;

import org.zirco.myprinter.Global;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/*
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment implements OnItemClickListener, OnDataFetchListener {

    private static final String TAG = InventoryFragment.class.getSimpleName();
    private static final int BARCODE_CAMERA_PERMISSION = 200;
    private static final int BARCODE_REQUEST_ID = 100;
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
    @BindView(R.id.btn_scanBarcode)
    public Button scanBarCodeButton;

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
    private CountDownTimer timer;

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

        if (savedInstanceState != null) {
            selectedInventorytoEdit = (Inventory.InventoryData) savedInstanceState.getSerializable("EDIT_INVENTORY");
            if (selectedInventorytoEdit != null) {
                isEditing = false;
                onItemClick(2, -1);
            }
        }

        dateEditText.setInputType(InputType.TYPE_NULL);
        final DatePickerDialog datePickerDialog = Utils.dateDialog(mContext, dateEditText, true, false);

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
                strings.add(vendorData.name.toUpperCase());
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

        inventoryDatas = billMatrixDaoImpl.getInventory(null);

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            if (inventoryDatas.size() > Constants.DEFAULT_INVENTORY_SHOW_SIZE) {
                for (int i = 0; i < Constants.DEFAULT_INVENTORY_SHOW_SIZE; i++) {
                    Inventory.InventoryData inventoryData = inventoryDatas.get(i);
                    if (!inventoryData.status.equalsIgnoreCase("-1")) {
                        inventoryAdapter.addInventory(inventoryData);
                    }
                }
            } else {
                for (Inventory.InventoryData inventoryData : inventoryDatas) {
                    if (!inventoryData.status.equalsIgnoreCase("-1")) {
                        inventoryAdapter.addInventory(inventoryData);
                    }
                }
            }
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                if (!TextUtils.isEmpty(adminId)) {
                    getInventoryFromServer(adminId);
                }
            }
        }

        /*
         * Printer Code
         */

        mHandler = new MHandler();
        WorkService.addHandler(mHandler);
        initBroadcast();

        if (null == WorkService.workThread) {
            Intent intent = new Intent(mContext, WorkService.class);
            mContext.startService(intent);
        }

        /*
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
                /*
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
                /*
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

        devicesAdapter = new DevicesAdapter(mContext, new ArrayList<BluetoothDevice>());
        devicesListView.setAdapter(devicesAdapter);

        return v;
    }

    @OnClick(R.id.btn_scanBarcode)
    public void checkCameraPermission() {
        /*
         * Reset bottom layout fields
         */
        onItemClick(4, 0);

        if (checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, BARCODE_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(mContext, BarcodeScannerActivity.class);
            startActivityForResult(intent, BARCODE_REQUEST_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing && selectedInventorytoEdit != null) {
            outState.putSerializable("EDIT_INVENTORY", selectedInventorytoEdit);
        }
    }

    @OnClick(R.id.btn_barcode_go)
    public void barcodeGo() {

        if (isEditing) {
            Utils.showToast("Save present editing Inventory before scanning other Inventory", mContext);
            return;
        }

        if (scanBarCodeButton.getText().toString().equalsIgnoreCase(getString(R.string.BAR_CODE))) {
            Utils.showToast("Scan Barcode to add Item", mContext);
            return;
        }

        barCodeEditText.setText(scanBarCodeButton.getText().toString());

        Utils.showToast("Add all Item Details to save Inventory", mContext);

        scanBarCodeButton.setText(getString(R.string.BAR_CODE));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case BARCODE_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mContext, BarcodeScannerActivity.class);
                    startActivityForResult(intent, BARCODE_REQUEST_ID);
                } else {
                    Utils.showToast("Please grant camera permission to use the QR Scanner", mContext);
                }
                return;
        }
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
        ArrayList<Inventory.InventoryData> inventoryDatas = billMatrixDaoImpl.getInventory(null);

        if (inventoryDatas != null && inventoryDatas.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventoryDatas) {
                if (!inventoryData.status.equalsIgnoreCase("-1")) {
                    inventoryAdapter.addInventory(inventoryData);
                }
            }
        }
    }

    public void fetchInventoryByBarcode(String barcodeValue) {
        Utils.showToast(barcodeValue, mContext);
        Inventory.InventoryData barcodeInventoryData;
        barcodeInventoryData = billMatrixDaoImpl.getInventoryonByBarcode(DBConstants.BARCODE, "365214");
        if (barcodeInventoryData != null && !inventoryAdapter.containsInventory(barcodeInventoryData.barcode)) {
            inventoryAdapter.addInventory(barcodeInventoryData);
            inventoryRecyclerView.smoothScrollToPosition(inventoryAdapter.getItemCount());
        } else {
            Utils.showToast("Inventory already added", mContext);
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

        if (TextUtils.isEmpty(itemCode.trim())) {
            Utils.showToast("Enter Item Code", mContext);
            return;
        }

        String itemName = itemNameeEditText.getText().toString();

        if (TextUtils.isEmpty(itemName.trim())) {
            Utils.showToast("Enter Item Name", mContext);
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(unit.trim())) {
            Utils.showToast("Select Unit", mContext);
            return;
        }

        if (unit.equalsIgnoreCase("Other")) {
            unit = customUnitEditText.getText().toString();
            if (TextUtils.isEmpty(unit.trim())) {
                Utils.showToast("Enter Custom Unit", mContext);
                return;
            }
        }

        String qty = qtyEditText.getText().toString();

        if (TextUtils.isEmpty(qty.trim())) {
            Utils.showToast("Enter Quantity", mContext);
            return;
        }

        String price = priceEditText.getText().toString();

        if (TextUtils.isEmpty(price.trim())) {
            Utils.showToast("Enter Price", mContext);
            return;
        }

        String myCost = myCostEditText.getText().toString();

        if (TextUtils.isEmpty(myCost.trim())) {
            Utils.showToast("Enter Cost", mContext);
            return;
        }

        String date = dateEditText.getText().toString();

        if (TextUtils.isEmpty(date.trim())) {
            Utils.showToast("Select Date", mContext);
            return;
        }

        String wareHouse = wareHouseEditText.getText().toString();

        String vendor = vendorSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(vendor.trim()) || vendor.equalsIgnoreCase("select vendor")) {
            Utils.showToast("Select Vendor", mContext);
            return;
        }

        String barcode = barCodeEditText.getText().toString();

        if (!TextUtils.isEmpty(barcode.trim())) {
            if (barcode.length() < 13) {
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
                inventoryData.add_update = selectedInventorytoEdit.add_update;

                if (TextUtils.isEmpty(selectedInventorytoEdit.add_update)) {
                    inventoryData.add_update = Constants.ADD_OFFLINE;
                } else if (selectedInventorytoEdit.add_update.equalsIgnoreCase(Constants.DATA_FROM_SERVER)) {
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
            /*
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

            Log.e(TAG, "addInventory: "  + inventoryData.toString() );

            if (addInventoryButton.getText().toString().equalsIgnoreCase("ADD")) {
                if (Utils.isInternetAvailable(mContext)) {
                    inventoryFromServer = ServerUtils.addInventorytoServer(inventoryData, mContext, adminId, billMatrixDaoImpl);
                } else {
                    /*
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
                        /*
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
            Utils.showToast("Barcode / Item Code must be unique", mContext);
        }
    }

    @Override
    public void onItemClick(int caseInt, final int position) {
        switch (caseInt) {
            case 1:
                if (isEditing) {
                    Utils.showToast("Save present editing Inventory before deleting other Inventory", mContext);
                    return;
                }

                /*
                 * Reset bottom layout fields
                 */
                resetBottomLayout();

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
                            /*
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
                    /*
                     * Reset bottom layout fields
                     */
                    resetBottomLayout();

                    isEditing = true;
                    ((BaseTabActivity) mContext).ifTabCanChange = false;

                    addInventoryButton.setText(getString(R.string.save));
                    if (position != -1) {
                        selectedInventorytoEdit = inventoryAdapter.getItem(position);
                    }

                    if (selectedInventorytoEdit != null) {
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
                            String vendorName = selectedInventorytoEdit.vendor;
                            if (TextUtils.isDigitsOnly(selectedInventorytoEdit.vendor)) {
                                vendorName = billMatrixDaoImpl.getVendorName(selectedInventorytoEdit.vendor);
                            }
                            int vendorSelectedPosition = vendorSpinnerAdapter.getPosition(vendorName.toUpperCase());
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
                    }

                    if (position != -1) {
                        billMatrixDaoImpl.deleteInventory(inventoryAdapter.getItem(position).item_code);
                        inventoryAdapter.deleteInventory(position);
                    }
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
                resetBottomLayout();
                break;
        }
    }

    public void resetBottomLayout() {
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

        ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory(null);

        if (inventories != null && inventories.size() > 0) {
            for (Inventory.InventoryData inventoryData : inventories) {
                inventoryAdapter.addInventory(inventoryData);
            }
        }
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query);
        /*
         * Reset bottom layout fields
         */
        onItemClick(4, 0);

        if (query.length() > 0) {
            query = query.toLowerCase();

            noResultsTextView.setVisibility(View.GONE);
            boolean noInventory = false;
            query = query.toLowerCase();
            inventoryAdapter.removeAllInventories();

            ArrayList<Inventory.InventoryData> inventories = billMatrixDaoImpl.getInventory(null);

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

    /*
     * Printer Code
     */
    public void printBarcode() {
        /*if (WorkService.workThread != null && WorkService.workThread.isConnected()) {
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
        }*/
        printBarcodeImage(selectedInventorytoEdit);
    }

    /*
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

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;

                    /*
                     * dismiss search
                     */
                    if (searchingDialog != null && searchingDialog.isShowing())
                        searchingDialog.dismiss();

                    if (timer != null) {
                        timer.cancel();
                    }

                    devicesAdapter.add(device);
                    devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            connectingProgressDialog.setMessage("Connecting" + " " + devicesAdapter.getItem(position).getAddress());
                            connectingProgressDialog.setIndeterminate(true);
                            connectingProgressDialog.setCancelable(false);
                            connectingProgressDialog.show();
                            adapter.cancelDiscovery();

                            WorkService.workThread.connectBt(devicesAdapter.getItem(position).getAddress());
                            /*WorkService.workThread.setDeviceAddress(devicesAdapter.getItem(position).getAddress());
                            WorkService.workThread.setDeviceName(devicesAdapter.getItem(position).getName());*/

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

    class MHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /*
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
        } else if (requestCode == BARCODE_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                scanBarCodeButton.setText(data.getStringExtra("BARCODE"));
            } else if (resultCode == RESULT_CANCELED) {
                Utils.showToast("Unable to scan barcode", mContext);
            }
        }
    }

    private void printBarcodeImage(Inventory.InventoryData inventoryData) {
        View view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.barcode, null);

        TextView storeNameTextView = (TextView) view.findViewById(R.id.tv_barcode_storeName);
        TextView itemNameTextView = (TextView) view.findViewById(R.id.tv_barcode_itemName);
        TextView valueTextView = (TextView) view.findViewById(R.id.tv_barcode_value);
        TextView priceTextView = (TextView) view.findViewById(R.id.tv_barcode_price);
        ImageView barcodeImageView = (ImageView) view.findViewById(R.id.im_barcode);

        priceTextView.setText("Item Price: " + "Rs." + inventoryData.price + "/-");
        valueTextView.setText(inventoryData.barcode);
        itemNameTextView.setText(inventoryData.item_name);

        try {
            Bitmap bitmap = encodeAsBitmap(inventoryData.barcode, BarcodeFormat.UPC_A, 360, 64);
            barcodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
        } else {
            Utils.showToast(Global.toast_notconnect, mContext);
        }

    }

    /*************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * <p>
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
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
