package com.freezer.remotepcclient.list_bluetooth_devices;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.freezer.remotepcclient.R;
import com.freezer.remotepcclient.bluetooth_remote.BluetoothRemoteActivity;
import com.freezer.remotepcclient.bluetooth_remote.BluetoothRemoteService;

import java.util.ArrayList;

public class BluetoothDeviceSelect extends DialogFragment{
    private Context mContext;

    private static final String TAG = "BLSelector";

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BTDevice selectedBluetoothDevice = null;

    private ArrayList<BTDevice> btDevices = new ArrayList<>();
    private BTDeviceArrayAdapter adapter;

    private ListView bluetoothDevicesListView;

    private Button scanButton;
    private ProgressBar bluetoothScanProgressBar;

    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_device_select_dialog, container, false);

        adapter = new BTDeviceArrayAdapter(getActivity(), btDevices);
        bluetoothDevicesListView = (ListView) view.findViewById(R.id.bluetoothDevicesListView);
        bluetoothDevicesListView.setAdapter(adapter);

        bluetoothScanProgressBar = (ProgressBar) view.findViewById(R.id.bluetoothScanProgressBar);
        bluetoothScanProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Show connection dialog
        bluetoothDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.cancelDiscovery();
                    getActivity().unregisterReceiver(receiver);
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Open new Activity of bluetooth remote
                                startBluetoothRemoteService(selectedBluetoothDevice.getDevice());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                selectedBluetoothDevice = btDevices.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to connect to " + selectedBluetoothDevice.getName() + " ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        scanButton = (Button) view.findViewById(R.id.bluetoothScanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Scanning for devices");

                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                    getActivity().unregisterReceiver(receiver);
                }

                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();

                        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                            Toast.makeText(getActivity(), "Scanning...", Toast.LENGTH_SHORT).show();
                            bluetoothScanProgressBar.setVisibility(ProgressBar.VISIBLE);

                        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                            Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();
                            bluetoothScanProgressBar.setVisibility(ProgressBar.GONE);
                        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.d(TAG, "Device found " + device.toString());

                            // Add founded device to ListView
                            BTDevice btDevice = new BTDevice(device.getName(), device.getAddress() , false, device);
                            if(btDevices.contains(btDevice)) {
                                Log.d(TAG, "Duplicated device found " + device.toString());
                            }
                            else {
                                btDevices.add(btDevice);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                };

                IntentFilter filter = new IntentFilter();

                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothDevice.ACTION_FOUND);

                getActivity().registerReceiver(receiver, filter);

                mBluetoothAdapter.startDiscovery();
            }
            // Scanning for devices logic
        });
        return view;
    }

    private void startBluetoothRemoteService(BluetoothDevice btDevice){
//        Intent bluetoothServiceIntent = new Intent(getActivity(), SocketRemoteService.class);
//        bluetoothServiceIntent.putExtra("BTDevice", btDevice);

        Intent bluetoothActivityIntent = new Intent(getActivity(), BluetoothRemoteActivity.class);
        bluetoothActivityIntent.putExtra("BTDevice", btDevice);

//        getActivity().startService(bluetoothServiceIntent);
        getActivity().startActivity(bluetoothActivityIntent);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInPutListener = (OnInputListener) getActivity();
            mContext = getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.80),(int) (height * 0.6));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

}
