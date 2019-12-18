package com.freezer.remotepcclient.socket_remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.freezer.remotepcclient.AboutDialog;
import com.freezer.remotepcclient.R;
import com.freezer.remotepcclient.socket_prompt.SocketServer;
import com.freezer.remotepcclient.socket_remote.ui.keyboard.KeyboardFragment;
import com.freezer.remotepcclient.socket_remote.ui.navigation.NavigationFragment;
import com.freezer.remotepcclient.socket_remote.ui.touchpad.TouchPadFragment;
import com.google.android.material.navigation.NavigationView;

public class SocketRemoteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SocketServer server;
    private SocketRemoteService socketRemoteService;
    private boolean binded = false;

    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = "SocketRemote Activity";

    public void sendMessage(String message) {
        socketRemoteService.sendCommand(message);
        Log.d(TAG, "Sended command : " + message);
    }


    ServiceConnection socketServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SocketRemoteService.LocalSocketRemoteServiceBinder binder = (SocketRemoteService.LocalSocketRemoteServiceBinder) iBinder;
            socketRemoteService = binder.getService();
            binded = true;
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

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
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

        Bundle extras = getIntent().getExtras();
        server = getIntent().getExtras().getParcelable("socketServer");
        Intent socketServiceIntent = new Intent(this, SocketRemoteService.class);
        socketServiceIntent.putExtra("socketServer", server);
        this.bindService(socketServiceIntent, socketServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(socketServiceConnection);
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
                AboutDialog.showDialog(SocketRemoteActivity.this);
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
            case R.id.nav_touchpad :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, TouchPadFragment.newInstance()).addToBackStack(null).commit();
                setActionBarTitile(R.string.menu_touchpad);
                break;
            }
            case R.id.nav_keyboard :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, KeyboardFragment.newInstance()).addToBackStack(null).commit();
                setActionBarTitile(R.string.menu_keyboard);
                break;
            }
            case R.id.nav_navigation :{
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, NavigationFragment.newInstance()).addToBackStack(null).commit();
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
