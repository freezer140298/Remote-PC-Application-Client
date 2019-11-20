package com.freezer.remotepcclient.bluetooth_remote;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothRemoteService extends Service {
    private static final String TAG = "BTRemote Service";

    final int handlerState = 0;                        //used to identify handler message
    // Handler bluetoothIn;
    private BluetoothDevice btDevice = null;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;

    private boolean stopThread;
    // SPP UUID service - this should work for most devices
    private static final UUID BT_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");


    private StringBuilder recDataString = new StringBuilder();

    public void sendCommand(String command) {
        mConnectedThread.write(command);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SERVICE CREATED");
        stopThread = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SERVICE STARTED");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // bluetoothIn.removeCallbacksAndMessages(null);

        // Send EXIT_CMD to Server
        sendCommand("EXIT_CMD");
        stopThread = true;
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null) {
            mConnectingThread.closeSocket();
        }
        Log.d(TAG, "onDestroy");
    }

    private final IBinder binder = new LocalBluetoothRemoteServiceBinder();

    public class LocalBluetoothRemoteServiceBinder extends Binder {
        public BluetoothRemoteService getService(){
            return BluetoothRemoteService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        btDevice = intent.getParcelableExtra("BTDevice");
        Log.d(TAG, "Device : " + btDevice.getName());
        ConnectingThread connectingThread = new ConnectingThread(btDevice);
        connectingThread.start();

        return this.binder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    // New Class for Connecting Thread
    private class ConnectingThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectingThread(BluetoothDevice device) {
            Log.d(TAG, "IN CONNECTING THREAD");
            mmDevice = device;
            BluetoothSocket temp = null;
            Log.d(TAG, "MAC ADDRESS : " + btDevice.getAddress()); // Get MAC_ADDRESS from a BluetoothDevice object
            Log.d(TAG, "BT UUID : " + BT_UUID);
            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(BT_UUID);
                Log.d(TAG, "SOCKET CREATED : " + temp.toString());
            } catch (IOException e) {
                Log.d(TAG, "SOCKET CREATION FAILED :" + e.toString());
                stopSelf();
            }
            mmSocket = temp;
        }

        @Override
        public void run() {
            super.run();
            Log.d(TAG, "IN CONNECTING THREAD RUN");
            // Establish the Bluetooth socket connection.
            // Cancelling discovery as it may slow down connection
            try {
                mmSocket.connect();
                Log.d(TAG, "BT SOCKET CONNECTED");
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                Log.d(TAG, "CONNECTED THREAD STARTED");
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("TEST_CONNECTION");
            } catch (IOException e) {
                try {
                    Log.d(TAG, "SOCKET CONNECTION FAILED : " + e.toString());
                    mmSocket.close();
                    stopSelf();
                } catch (IOException e2) {
                    Log.d(TAG, "SOCKET CLOSING FAILED :" + e2.toString());
                    stopSelf();
                    //insert code to deal with this
                }
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "Connected thread start failed", Toast.LENGTH_LONG).show();
                stopSelf();
            }
        }

        public void closeSocket() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d(TAG, e2.toString());
                Log.d(TAG, "SOCKET CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }

    // New Class for Connected Thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "IN CONNECTED THREAD");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.d(TAG, "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[128];
            int bytes;

            // Keep looping to listen for received messages
            while (true && !stopThread) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d("DEBUG BT PART", "CONNECTED THREAD " + readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    // bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    Log.d("DEBUG BT", e.toString());
                    Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                    stopSelf();
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            StringBuilder msgBuilder = new StringBuilder(input);
            msgBuilder.append("|");
            msgBuilder.setLength(24);
            String msg = msgBuilder.toString();
            byte[] msgBuffer = msg.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.flush();
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Log.d(TAG, "UNABLE TO READ/WRITE " + e.toString());
                Log.d(TAG, "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }
        }

        public void closeStreams() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream.close();
                mmOutStream.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d(TAG, e2.toString());
                Log.d(TAG, "STREAM CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }
}