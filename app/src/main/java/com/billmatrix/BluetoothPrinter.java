package com.billmatrix;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.billmatrix.utils.Global;
import com.lvrenyang.pos.Cmd;
import com.lvrenyang.utils.DataUtils;


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class BluetoothPrinter extends AppCompatActivity {

    private static final int BLUETOOTH_ENABLE_REQUEST_ID = 1;
    private BroadcastReceiver broadcastReceiver;
    private static Handler mHandler = null;
    private LinearLayout linearlayoutdevices;
    private ProgressDialog dialog;
    private BluetoothAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_printer);
        linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);
        dialog = new ProgressDialog(this);

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
        initBroadcast();

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }
    }

    public void enableBluetooth(View v) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            finish();
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_ID);
        } else {
            adapter.cancelDiscovery();
            linearlayoutdevices.removeAllViews();
            adapter.startDiscovery();
        }
    }

    public void printBarcode(View v) {
        if (WorkService.workThread.isConnected()) {
            Bundle data = new Bundle();
            data.putString(Global.STRPARA1, "PHANI KUMAR");
            data.putInt(Global.INTPARA1, 0);
            data.putInt(Global.INTPARA2, Cmd.Constant.BARCODE_TYPE_UPC_A + 0);
            data.putInt(Global.INTPARA3, 3);
            data.putInt(Global.INTPARA4, 96);
            data.putInt(Global.INTPARA5, 0);
            data.putInt(Global.INTPARA6, 2);
            WorkService.workThread.handleCmd(Global.CMD_POS_SETBARCODE,
                    data);
        } else {
            Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
        }
    }

    public void printPicture(View v) {
        Bitmap mBitmap = getImageFromAssetsFile("img.jpg");

        int nPaperWidth = 384;

        if (mBitmap != null) {
            if (WorkService.workThread.isConnected()) {
                Bundle data = new Bundle();
                //data.putParcelable(Global.OBJECT1, mBitmap);
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, nPaperWidth);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            } else {
                Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
        unInitBroadcast();
    }

    private void unInitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;
                    final String address = device.getAddress();
                    String name = device.getName();
                    if (name == null) {
                        name = "BT";
                    } else if (name.equals(address)) {
                        name = "BT";
                    }
                    Button button = new Button(context);
                    button.setText(name + ": " + address);
                    button.setGravity(android.view.Gravity.CENTER_VERTICAL | Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            dialog.setMessage("connecting" + " " + address);
                            dialog.setIndeterminate(true);
                            dialog.setCancelable(false);
                            dialog.show();
                            WorkService.workThread.connectBt(address);
                        }
                    });
                    button.getBackground().setAlpha(100);
                    linearlayoutdevices.addView(button);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    //TODO: disable progress bar
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //TODO: disable progress bar
                }

            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                adapter.cancelDiscovery();
                linearlayoutdevices.removeAllViews();
                adapter.startDiscovery();
            } else if (resultCode == RESULT_CANCELED) {
                //TODO: show toast
            }
        }
    }

    static class MHandler extends Handler {

        WeakReference<BluetoothPrinter> mActivity;

        MHandler(BluetoothPrinter activity) {
            mActivity = new WeakReference<BluetoothPrinter>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothPrinter theActivity = mActivity.get();
            switch (msg.what) {
                /**
                 * DrawerService of onStartCommand Will send this message
                 */
                case Global.MSG_ALLTHREAD_READY: {
                    Log.v("MHandler", "MSG_ALLTHREAD_READY");
                    if (WorkService.workThread.isConnected()) {
                        Log.e("isConnected", "isConnectedisConnected");
                        theActivity.dialog.cancel();
                    } else {
                        Log.e("NOOOO", "isConnectedisConnected");
                        theActivity.dialog.cancel();
                    }
                    break;
                }

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? Global.toast_success
                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Connect Result: " + result);
                    theActivity.dialog.cancel();
                    break;
                }

                case Global.CMD_POS_SETBARCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "Result: " + result);
                    break;
                }

                case Global.CMD_POS_PRINTPICTURERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "Result: " + result);
                    break;
                }

                case com.lvrenyang.utils.Constant.MSG_BTHEARTBEATTHREAD_UPDATESTATUS:
                case com.lvrenyang.utils.Constant.MSG_NETHEARTBEATTHREAD_UPDATESTATUS: {
                    int statusOK = msg.arg1;
                    int status = msg.arg2;
                    Log.e("TAG",
                            "statusOK: " + statusOK + " status: "
                                    + DataUtils.byteToStr((byte) status));
//                    theActivity.progressBar.setIndeterminate(false);
                    if (statusOK == 1)
                        Log.e("SSSSSSSSS" + statusOK, "statusOK");
                    else
                        Log.e("NOOOO" + statusOK, "statusOKstatusOK");
                    theActivity.dialog.cancel();
                    break;
                }
                case Global.CMD_POS_WRITE_BT_FLOWCONTROL_RESULT: {
                    int result = msg.arg1;
                    Log.e("TAG", "Result: " + result);
                    theActivity.dialog.cancel();
                    break;
                }
            }
        }
    }
}
