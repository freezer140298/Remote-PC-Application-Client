package com.freezer.remotepcclient;

import android.app.AlertDialog;
import android.content.Context;

public class AboutDialog {
    public static void showDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("About")
                .setMessage("Java Programming Class Project\n" +
                        "Nguyen Dinh Duy\n" +
                        "Vu Dinh Dinh\n" +
                        "Truong Huynh Du\n" +
                        "Ta Xuan Quynh\n" +
                        "Nguyen Nhat Chieu").setPositiveButton("OK", null).show();
    }
}
