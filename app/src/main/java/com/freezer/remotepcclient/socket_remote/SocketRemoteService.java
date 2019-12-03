package com.freezer.remotepcclient.socket_remote;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.freezer.remotepcclient.socket_prompt.SocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketRemoteService extends Service {
    private static final String TAG = "SocketRemote Service";

    final int handlerState = 0;                        //used to identify handler message

    private SocketServer server;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;

    private boolean stopThread;

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

    private final IBinder binder = new LocalSocketRemoteServiceBinder();

    public class LocalSocketRemoteServiceBinder extends Binder {
        public SocketRemoteService getService(){
            return SocketRemoteService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        server = intent.getParcelableExtra("socketServer");

        Log.d(TAG, "Server at : " + server.getServerAddr() + ":" + server.getServerPort());
        mConnectingThread = new ConnectingThread(server);
        mConnectingThread.start();

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
        private Socket mmSocket = null;
        private SocketServer server;

        public ConnectingThread(SocketServer server) {
            Log.d(TAG, "IN CONNECTING THREAD");
            this.server = server;
        }

        @Override
        public void run() {
            //super.start();
            Log.d(TAG, "IN CONNECTING THREAD RUN");

            try {
                mmSocket = new Socket(server.getServerAddr(), server.getServerPort());
                Log.d(TAG, "SOCKET CREATED AND CONNECTED : " + mmSocket.toString());

                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();

                Log.d(TAG, "CONNECTED THREAD STARTED");
                // I send a character when resuming.beginning transmission to check device is connected
                // If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("TEST_CONNECTION");
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "Connected thread start failed", Toast.LENGTH_LONG).show();
                stopSelf();
            } catch (UnknownHostException e) {
                Log.d(TAG, "SOCKET CREATION FAILED :" + e.toString());
                e.printStackTrace();
                stopSelf();
            } catch (IOException e2) {
                Log.d(TAG, "SOCKET CREATION FAILED :" + e2.toString());
                e2.printStackTrace();
                stopSelf();
            }
        }

        public void closeSocket() {
            try {
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
        private Socket socket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread (Socket socket) {
            Log.d(TAG, "IN CONNECTED THREAD");
            this.socket = socket;

        }

        public void run() {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                // Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.d(TAG, "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[128];
            int bytes;

            // Keep looping to listen for received messages
            while (!stopThread) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d("DEBUG SOCKET PART", "CONNECTED THREAD " + readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    // bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    Log.d("DEBUG SOCKET", e.toString());
                    Log.d(TAG, "UNABLE TO READ/WRITE, STOPPING SERVICE");
                    stopSelf();
                    break;
                }
            }
        }

        // Send message to server
        public void write(String input) {
            SendCommand sendCommand = new SendCommand(mmOutStream, input);
            sendCommand.start();
        }

        public void closeStreams() {
            try {
                //Don't leave Socket open when leaving activity
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

    // Class for sending command to Server
    private class SendCommand extends Thread{
        private OutputStream mmOutStream;
        private String command;

        public SendCommand(OutputStream outputStream, String command) {
            this.mmOutStream = outputStream;
            this.command = command;
        }

        @Override
        public void run() {
            StringBuilder msgBuilder = new StringBuilder(command);
            msgBuilder.append("|");
            msgBuilder.setLength(24);
            String msg = msgBuilder.toString();
            byte[] msgBuffer = msg.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.flush();
                mmOutStream.write(msgBuffer);                //write bytes over Socket connection via outstream
            } catch (IOException e) {
                // If you cannot write, close the application
                Log.d(TAG, "UNABLE TO READ/WRITE " + e.toString());
                Log.d(TAG, "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            } catch (NullPointerException e1) {
                Log.d(TAG, "UNABLE TO READ/WRITE " + e1.toString());
            }
        }
    }
}