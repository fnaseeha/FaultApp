package com.lk.lankabell.fault.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;

import com.onurkaganaldemir.ktoastlib.KToast;

public class ToastManager {

    Activity activity;

    public ToastManager(Context context) {
        this.activity = (Activity) context;
    }


    // Success
    public void success(String sMessage){
        KToast.successToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_AUTO);
    }

    // Info
    public void info(String sMessage){
        KToast.infoToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_SHORT);
    }

    // Warning
    public void warning(String sMessage){
        KToast.warningToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_AUTO);
    }

    // Normal
    public void normal(String sMessage){
        KToast.normalToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_LONG, com.onurkaganaldemir.ktoastlib.R.drawable.ic_info);
    }

    // Error
    public void error(String sMessage){
        KToast.errorToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_LONG);
    }

    // Custom Color
    public void customColor(String sMessage){
        //KToast.customColorToast(activity, "This is a custom color toast.", Gravity.BOTTOM, KToast.LENGTH_AUTO, R.color.fuchsia, R.drawable.ic_infinite_white);
    }

    // Custom Drawable
    public void customBackgroud(String sMessage){
        //KToast.customColorToast(activity, "This is a custom color toast.", Gravity.BOTTOM, KToast.LENGTH_AUTO, R.color.fuchsia, R.drawable.ic_infinite_white);
        KToast.customBackgroudToast(activity, sMessage, Gravity.BOTTOM, KToast.LENGTH_AUTO, com.onurkaganaldemir.ktoastlib.R.drawable.background_custom_toast, null , com.onurkaganaldemir.ktoastlib.R.drawable.ic_info);
    }
}
