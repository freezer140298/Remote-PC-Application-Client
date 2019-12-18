package com.freezer.remotepcclient;

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
import android.widget.Toast;

import com.freezer.remotepcclient.list_bluetooth_devices.BluetoothDeviceSelect;
import com.freezer.remotepcclient.socket_prompt.SocketPrompt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private boolean isWifiApEnable(WifiManager wifiManager) {
        Method method = null;
        try {
            method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void socketConnect(View v){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if(wifiManager.isWifiEnabled() || isWifiApEnable(wifiManager)) {
            Log.d(TAG, "Open Socket Server input Dialog");

            FragmentManager fragmentManager = getSupportFragmentManager();
            SocketPrompt socketPrompt = new SocketPrompt();

            socketPrompt.show(fragmentManager, "Socket Prompt");
        }
        else {
            Toast.makeText(getApplicationContext(), "WiFi or Hotspot is not enabled", Toast.LENGTH_LONG).show();
        }
    }

}
