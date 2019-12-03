package com.freezer.remotepcclient.socket_prompt;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class PingTask extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "PingTask";
    @Override
    protected Boolean doInBackground(String... params) {
        String url = params[0];
        int port = Integer.parseInt(params[1]);

        boolean isSuccess = false;

        isSuccess = pingURL(url, port);

        return isSuccess;
    }

    public static boolean pingURL(String hostname, int port) {
        boolean isReachable = false;
        try (Socket socket = new Socket(hostname, port)) {
            isReachable = true;
            Log.d(TAG, hostname + ":" + port + " is reachable");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isReachable;
    }
}
