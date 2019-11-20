package com.freezer.remotepcclient.list_bluetooth_devices;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class BTDevice implements Parcelable {
    @Nullable  String name;
    String MAC_ADDR;
    boolean isBonded;
    BluetoothDevice device;

    public BTDevice(String name, String MAC_ADDR, boolean isBonded, BluetoothDevice device) {
        this.name = name;
        this.MAC_ADDR = MAC_ADDR;
        this.isBonded = isBonded;
        this.device = device;
    }

    protected BTDevice(Parcel in) {
        name = in.readString();
        MAC_ADDR = in.readString();
        isBonded = in.readByte() != 0;
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<BTDevice> CREATOR = new Creator<BTDevice>() {
        @Override
        public BTDevice createFromParcel(Parcel in) {
            return new BTDevice(in);
        }

        @Override
        public BTDevice[] newArray(int size) {
            return new BTDevice[size];
        }
    };

    @Override
    public String toString(){
        return this.name + "\n" + this.isBonded;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean retVal = false;

        if (obj instanceof BTDevice){
            BTDevice ptr = (BTDevice) obj;
            retVal = ptr.MAC_ADDR.equals(this.MAC_ADDR);
        }

        return retVal;
    }

    public String getName() {
        if(name == null)
            return "Unknown";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMAC_ADDR() {
        return MAC_ADDR;
    }

    public void setMAC_ADDR(String MAC_ADDR) {
        this.MAC_ADDR = MAC_ADDR;
    }

    public boolean isBonded() {
        return isBonded;
    }

    public void setBonded(boolean bonded) {
        isBonded = bonded;
    }

    public String getPaired() {
        return this.isBonded ? "Paired" : "";
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(i);
    }
}