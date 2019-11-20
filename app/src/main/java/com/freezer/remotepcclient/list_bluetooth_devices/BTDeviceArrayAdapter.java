package com.freezer.remotepcclient.list_bluetooth_devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freezer.remotepcclient.R;

import java.util.ArrayList;

public class BTDeviceArrayAdapter extends ArrayAdapter<BTDevice> {

    public BTDeviceArrayAdapter(@NonNull Context context, ArrayList<BTDevice> btDevices) {
        super(context, 0, btDevices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BTDevice btDevice = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetoothdevice, parent, false);
        }

        TextView btDeviceNameTextView = (TextView) convertView.findViewById(R.id.btDeviceNameTextView);
        TextView btDeviceMACADDRTextView = (TextView) convertView.findViewById(R.id.btDeviceMACADDRTextView);
        TextView btDeviceIsPairedTextView = (TextView) convertView.findViewById(R.id.btDeviceIsPairedTextView);

        btDeviceNameTextView.setText(btDevice.getName());
        btDeviceMACADDRTextView.setText(btDevice.getMAC_ADDR());
        btDeviceIsPairedTextView.setText(btDevice.getPaired());

//        return super.getView(position, convertView, parent);
        return convertView;
    }
}
