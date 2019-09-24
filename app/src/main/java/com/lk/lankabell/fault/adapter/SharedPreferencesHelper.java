package com.lk.lankabell.fault.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lk.lankabell.fault.config.AppContoller;

/**
 * CREATED BY IFRAS ON 20/09/2018.
 */
public class SharedPreferencesHelper {

    public static void setLocalSharedPreference(final Context con, String localSPKey, String localSPValue) {
        //SharedPreferences localSP = con.getSharedPreferences(SETTINGS, Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE);
        SharedPreferences localSP = con.getSharedPreferences(AppContoller.SETTINGS, Context.MODE_PRIVATE);
        Editor localBackupEditor = localSP.edit();
        localBackupEditor.putString(localSPKey, localSPValue);
        localBackupEditor.commit();
    }

    public String getLocalSharedPreference(final Context con, String localSPKey) {
        SharedPreferences localSP = con.getSharedPreferences(AppContoller.SETTINGS, Context.MODE_PRIVATE);
        return localSP.getString(localSPKey, "").toString();
    }
}
