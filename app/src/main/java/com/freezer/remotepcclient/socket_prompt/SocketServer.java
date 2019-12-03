package com.freezer.remotepcclient.socket_prompt;


import android.os.Parcel;
import android.os.Parcelable;

public class SocketServer implements Parcelable {
    private String serverAddr;
    private int serverPort;

    public SocketServer(String serverAddr, int serverPort) {
        setServerAddr(serverAddr);
        setServerPort(serverPort);
    }

    protected SocketServer(Parcel in) {
        serverAddr = in.readString();
        serverPort = in.readInt();
    }

    public static final Creator<SocketServer> CREATOR = new Creator<SocketServer>() {
        @Override
        public SocketServer createFromParcel(Parcel in) {
            return new SocketServer(in);
        }

        @Override
        public SocketServer[] newArray(int size) {
            return new SocketServer[size];
        }
    };

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        if(validIP(serverAddr)) {
            this.serverAddr = serverAddr;
        }
        else {
            this.serverAddr = "";
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public static boolean validIP(String ip) {
        if(ip == null || ip.length() < 7 || ip.length() > 15) return false;
        try {
            int x = 0;
            int y = ip.indexOf('.');

            if (y == -1 || ip.charAt(x) == '-' || Integer.parseInt(ip.substring(x, y)) > 255) return false;

            x = ip.indexOf('.', ++y);
            if (x == -1 || ip.charAt(y) == '-' || Integer.parseInt(ip.substring(y, x)) > 255) return false;

            y = ip.indexOf('.', ++x);
            return  !(y == -1 ||
                    ip.charAt(x) == '-' ||
                    Integer.parseInt(ip.substring(x, y)) > 255 ||
                    ip.charAt(++y) == '-' ||
                    Integer.parseInt(ip.substring(y, ip.length())) > 255 ||
                    ip.charAt(ip.length()-1) == '.');

        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(serverAddr);
        parcel.writeInt(serverPort);
    }

}
