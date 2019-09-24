package com.lk.lankabell.fault.control;

import android.view.View;

public interface ClickListener {
    void onPositionClicked(int position, View view);

    void onPositionClickedLong(int position, View view);
}