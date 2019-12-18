package com.freezer.remotepcclient.bluetooth_remote;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.freezer.remotepcclient.AboutDialog;
import com.freezer.remotepcclient.R;

import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.freezer.remotepcclient.bluetooth_remote.ui.keyboard.KeyboardFragment;
import com.freezer.remotepcclient.bluetooth_remote.ui.navigation.NavigationFragment;
import com.freezer.remotepcclient.bluetooth_remote.ui.touchpad.TouchPadFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

public class BluetoothRemoteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private BluetoothDevice btDevice = null;
    private BluetoothRemoteService bluetoothRemoteService;
    private boolean binded = false;

    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = "BTRemote Activity";

    public void sendMessage(String message) {
        bluetoothRemoteService.sendCommand(message);
        Log.d(TAG, "Sended command : " + message);
    }


    ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothRemoteService.LocalBluetoothRemoteServiceBinder binder = (BluetoothRemoteService.LocalBluetoothRemoteServiceBinder) iBinder;
            bluetoothRemoteService = binder.getService();
            binded = false;
            Log.d(TAG, "Started");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_touchpad, R.id.nav_keyboard)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, TouchPadFragment.newInstance()).addToBackStack(null).commit();

        navigationView.setNavigationItemSelectedListener(this);

        btDevice = getIntent().getParcelableExtra("BTDevice");
        Intent bluetoothServiceIntent = new Intent(this, BluetoothRemoteService.class);
        bluetoothServiceIntent.putExtra("BTDevice", btDevice);
        this.bindService(bluetoothServiceIntent,bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(bluetoothServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_about : {
                AboutDialog.showDialog(BluetoothRemoteActivity.this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            default: case R.id.nav_touchpad :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, TouchPadFragment.newInstance()).commit();
                setActionBarTitile(R.string.menu_touchpad);
                break;
            }
            case R.id.nav_keyboard :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, KeyboardFragment.newInstance()).commit();
                setActionBarTitile(R.string.menu_keyboard);
                break;
            }
            case R.id.nav_navigation :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, NavigationFragment.newInstance()).commit();
                setActionBarTitile(R.string.menu_navigation);
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setActionBarTitile(int title){
        getSupportActionBar().setTitle(title);
    }
}
