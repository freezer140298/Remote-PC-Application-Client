package com.freezer.remotepcclient.socket_prompt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.freezer.remotepcclient.R;
import com.freezer.remotepcclient.socket_remote.SocketRemoteActivity;


public class SocketPrompt extends DialogFragment {
    private Context mContext;
    private SocketServer server;

    private final String TAG = "SOCKET INPUTING PROMPT";

    private TextView greetingTextView;
    private EditText serverAddrText;
    private EditText serverPortText;
    private Button socketConnectButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.socket_input_prompt, container, false);

        greetingTextView = view.findViewById(R.id.socketGreetingTextView);
        serverAddrText = view.findViewById(R.id.socketAddrEditText);
        serverPortText = view.findViewById(R.id.socketPortEditText);
        socketConnectButton = view.findViewById(R.id.socketConnectButton);

        socketConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSocketRemoteService();
            }
        });

        return view;
    }

    private void startSocketRemoteService() {
        Intent socketActivityIntent = new Intent(getActivity(), SocketRemoteActivity.class);
        Log.d(TAG, serverAddrText.getText().toString());
        server = new SocketServer(serverAddrText.getText().toString(), Integer.parseInt(serverPortText.getText().toString()));

        socketActivityIntent.putExtra("socketServer", server);

        startActivity(socketActivityIntent);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
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

        greetingTextView.setHeight((int) (height * 0.08));
        serverAddrText.setHeight((int) (height * 0.07));
        serverPortText.setHeight((int) (height * 0.07));

        window.setLayout((int) (width * 0.80),(int) (height * 0.6));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
