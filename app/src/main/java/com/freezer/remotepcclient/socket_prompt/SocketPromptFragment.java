package com.freezer.remotepcclient.socket_prompt;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class SocketPromptFragment extends DialogFragment {
    private Context context;

    private final String TAG = "SOCKET INPUTING PROMPT";

    private EditText serverAddrText;
    private EditText serverPortText;
    private Button socketConnectButton;
}
