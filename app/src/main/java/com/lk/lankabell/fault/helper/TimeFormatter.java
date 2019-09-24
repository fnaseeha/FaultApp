package com.lk.lankabell.fault.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeFormatter {
    public static String convertStringTimetoMilliseconds(long timeX, String formatX) {

        //long timeInMillis = 0;

        SimpleDateFormat formatter = new SimpleDateFormat(formatX);

        // Create a calendar object that will convert the date and addedTime value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(new Date().getAddedTime());
        calendar.setTimeInMillis(timeX);

        return formatter.format(calendar.getTime());
        //paymentUpdate.setPaymentDate(formatter.format(calendar.getAddedTime()));

        //return timeInMillis;
    }
}
