package com.lk.lankabell.fault.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

        // Setting Dialog Title
        alertDialog.setTitle("ERROR!");

        // Setting Dialog Message
        alertDialog.setMessage("Sorry, no internet connectivity detected. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                _context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
