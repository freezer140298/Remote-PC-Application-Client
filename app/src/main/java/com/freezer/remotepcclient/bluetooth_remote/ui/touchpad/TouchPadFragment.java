package com.freezer.remotepcclient.bluetooth_remote.ui.touchpad;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.freezer.remotepcclient.R;
import com.freezer.remotepcclient.bluetooth_remote.BluetoothRemoteActivity;


public class TouchPadFragment extends Fragment{
    private Button leftClickButton, rightClickButton;
    private TextView touchPadTextView;
    private int initX, initY, disX, disY;
    boolean mouseMoved = false, moultiTouch = false;

    public static TouchPadFragment newInstance() {
        TouchPadFragment fragment = new TouchPadFragment();
        return fragment;
    }

    public TouchPadFragment() {
        // Required empty public constructor
    }

    private void sendCommand(String command) {
        ((BluetoothRemoteActivity) getActivity()).sendMessage(command);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_touch_pad, container, false);
        leftClickButton = (Button) rootView.findViewById(R.id.leftClickButton);
        rightClickButton = (Button) rootView.findViewById(R.id.rightClickButton);
        touchPadTextView = (TextView) rootView.findViewById(R.id.touchPadTextView);
        leftClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateLeftClick();
            }
        });
        rightClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateRightClick();
            }
        });

        touchPadTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_DOWN:
                            //save X and Y positions when user touches the TextView
                            initX = (int) event.getX();
                            initY = (int) event.getY();
                            mouseMoved = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(moultiTouch == false) {
                                disX = (int) event.getX()- initX; //Mouse movement in x direction
                                disY = (int) event.getY()- initY; //Mouse movement in y direction
                                /*set init to new position so that continuous mouse movement
                                is captured*/
                                initX = (int) event.getX();
                                initY = (int) event.getY();
                                if (disX != 0 || disY != 0) {
                                    //send mouse movement to server
                                    sendCommand("MOUSE_MOVE:" + disX + "," + disY);
                                    mouseMoved=true;
                                }
                            }
                            else {
                                disY = (int) event.getY()- initY; //Mouse movement in y direction
                                disY = (int) disY / 2;//to scroll by less amount
                                initY = (int) event.getY();
                                if(disY != 0) {
                                    sendCommand("MOUSE_WHEEL:" + disY);
                                    mouseMoved=true;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            //consider a tap only if user did not move mouse after ACTION_DOWN
                            if(!mouseMoved){
                                sendCommand("LEFT_CLICK");
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            initY = (int) event.getY();
                            mouseMoved = false;
                            moultiTouch = true;
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            if(!mouseMoved) {
                                sendCommand("LEFT_CLICK");
                            }
                            moultiTouch = false;
                            break;
                    }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.touchpad));
    }

    private void simulateLeftClick() {
        String message = "LEFT_CLICK";
        sendCommand(message);
    }

    private void simulateRightClick() {
        String message = "RIGHT_CLICK";
        sendCommand(message);
    }

}