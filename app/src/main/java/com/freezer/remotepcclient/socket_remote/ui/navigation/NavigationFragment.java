package com.freezer.remotepcclient.socket_remote.ui.navigation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.freezer.remotepcclient.R;
import com.freezer.remotepcclient.socket_remote.SocketRemoteActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements View.OnClickListener  {

    private Button downArrowButton, upArrowButton, f5Button, leftArrowButton, rightArrowButton;

    public NavigationFragment() {
        // Required empty public constructor
    }
    public static NavigationFragment newInstance() {
        NavigationFragment fragment = new NavigationFragment();
        return fragment;
    }

    private void sendMessage(String command) {
        ((SocketRemoteActivity) getActivity()).sendMessage(command);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        downArrowButton = (Button) rootView.findViewById(R.id.downArrowButton);
        upArrowButton = (Button) rootView.findViewById(R.id.upArrowButton);
        leftArrowButton = (Button) rootView.findViewById(R.id.leftArrowButton);
        rightArrowButton = (Button) rootView.findViewById(R.id.rightArrowButton);
        f5Button = (Button) rootView.findViewById(R.id.f5Button);
        downArrowButton.setOnClickListener(this);
        leftArrowButton.setOnClickListener(this);
        upArrowButton.setOnClickListener(this);
        rightArrowButton.setOnClickListener(this);
        f5Button.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.navigation_fragment));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String action = null;
        switch (id) {
            case R.id.downArrowButton:
                action = "DOWN_ARROW_KEY";
                break;
            case R.id.leftArrowButton:
                action = "LEFT_ARROW_KEY";
                break;
            case R.id.upArrowButton:
                action = "UP_ARROW_KEY";
                break;
            case R.id.rightArrowButton:
                action = "RIGHT_ARROW_KEY";
                break;
            case R.id.f5Button:
                action = "F5_KEY";
                break;
        }
        sendMessage(action);
    }
}