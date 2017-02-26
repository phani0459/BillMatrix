package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.WorkService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HardwareFragment extends Fragment {

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
                TextView textView = getTextView();
                textView.setText("Connected Printer: " + "\n" + WorkService.workThread.getDeviceName() + ": " + WorkService.workThread.getDeviceAddress());
                conncetedHardwaresLayout.addView(textView);
            }
        }

        return v;
    }

    public TextView getTextView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) layoutInflater.inflate(R.layout.item_hardware_text, null);
        return textView;
    }

}
