package com.billmatrix.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.WorkService;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HardwareFragment extends Fragment {

    private static final String TAG = HardwareFragment.class.getSimpleName();
    private android.content.Context mContext;
    @BindView(R.id.tv_no_devices)
    public TextView noDevicesTextView;
    @BindView(R.id.ll_hardwares)
    public LinearLayout conncetedHardwaresLayout;

    public HardwareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hardware, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        if (WorkService.workThread != null) {
            if (WorkService.workThread.getDeviceAddress() != null) {
                noDevicesTextView.setVisibility(View.GONE);
                LinearLayout layout = getLinearLayout("Connected Printer: " + "\n" + WorkService.workThread.getDeviceName() + ": " + WorkService.workThread.getDeviceAddress());
                conncetedHardwaresLayout.addView(layout);
            }
        }

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_BOOL_IS_BARCODE_SCANNER_ATTACHED, false)) {
            noDevicesTextView.setVisibility(View.GONE);
            LinearLayout layout = getLinearLayout("Barcode Scanner is Attached");
            conncetedHardwaresLayout.addView(layout);
        }

        return v;
    }


    public LinearLayout getLinearLayout(String text) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.item_hardware_text, null);
        TextView textView = (TextView) layout.findViewById(R.id.tv_hardware_text_item);
        textView.setText(text);
        return layout;
    }

}
