package com.freezer.remotepcclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.freezer.remotepcclient.list_bluetooth_devices.BluetoothDeviceSelect;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button bluetoothButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }


    public void bluetoothConnect(View v) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "Open Bluetooth selector");

            FragmentManager fragmentManager = getSupportFragmentManager();
            BluetoothDeviceSelect bluetoothDeviceSelect = new BluetoothDeviceSelect();

            bluetoothDeviceSelect.show(fragmentManager, "Select bluetooth device");
        }
        else
        {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }
    }

    public void socketConnect(View v){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            Log.d(TAG, "Open Socket Server input Dialog");
            // TODO : Show Socket Server input prompt

            }
        else {
            Intent enableWifi = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            startActivityForResult(enableWifi, 1);
        }
    }

}
