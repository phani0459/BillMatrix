package com.billmatrix.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicesAdapter extends ArrayAdapter<BluetoothDevice> {
    private ArrayList<BluetoothDevice> devices;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView home;
    }

    public DevicesAdapter(Context context, ArrayList<BluetoothDevice> users) {
        super(context, android.R.layout.simple_list_item_2, users);
        this.devices = users;
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        devices.add(bluetoothDevice);
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public BluetoothDevice getItem(int position) {
        super.getItem(position);
        return devices.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.home = (TextView) convertView.findViewById(android.R.id.text2);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = "BT";
        } else if (deviceName.equals(device.getAddress())) {
            deviceName = "BT";
        }
        viewHolder.name.setText(deviceName);
        viewHolder.home.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}