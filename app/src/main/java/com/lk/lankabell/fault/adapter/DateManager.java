package com.lk.lankabell.fault.adapter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateManager {

    Date begining, end;

    public String convertToDate(String date) {

        String date1 = date.replace("Date(", "");
        String date2 = date1.replace(")", "");
        // System.out.println("nnn " + Long.parseLong(String.valueOf(date2)));
        // return Long.parseLong(String.valueOf(date2));
        return getMilliToDate(date2);
    }

    public static String getMilliToDate(String milliSecond)
    {
        Date date;
        //  System.out.println("milliSecond");
        //   System.out.println(milliSecond);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        if(!milliSecond.equals("")){


            //long ms = 1538568984000L;
            long ms = Long.valueOf(milliSecond);
            date = new Date(ms);

            System.out.println(dateformat.format(date));

        }else{
            date = new Date(1538568984000L);
        }
        //long milliSecond = Long.valueOf(milliSeconds);
        return dateformat.format(date);
    }

    public static long getDaysCount(String inputString2){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = myFormat.parse(inputString2 );
            Date date2 = myFormat.parse(getCurrentDate());
            long diff = date2.getTime() - date1.getTime();
            //System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getCurrentDate() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date myDate = new Date();
        String today = timeStampFormat.format(myDate);
        return today;
    }

    public Date getLastDateOfMonth() {

        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        end = calendar.getTime();
        return  end;
    }

    public Date getLastDateOfAnyMonth(String dateString) throws ParseException {

        Calendar calendar = getCalendarForDate(dateString);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        end = calendar.getTime();
        return  end;
    }

    public static long todayMillsec(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String curTime = sdf.format(today);
        Date d;
        long todayMill = (long) 0;
        try {
            d = sdf.parse(curTime);
            todayMill = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return todayMill;
    }


    public static long todayMillsecWithDateFormat(String dateFormat){
        Date today = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String curTime = sdf.format(today);
        Date d;
        long todayMill = (long) 0;
        try {
            d = sdf.parse(curTime);
            todayMill = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return todayMill;
    }

    public static Timestamp anyDayTimestampWithDateFormat(String date){
        //date format should be "2007-11-11 12:13:14"
        Timestamp ts = Timestamp.valueOf(date) ;
        return ts;
    }

    public static long anyStringDatetoMills(String mydate, String format){

        //String myDate = "2014/10/29 18:10:45";
        //String format = "yyyy/MM/dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(mydate);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        long millis = date.getTime();
        return millis;

    }

    public Date getFirstDateOfMonth(){
        System.out.println("Get first date of month ");
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        begining = calendar.getTime();

        return begining;
    }

    public Date getFirstDateOfAnyMonth(String dateString) throws ParseException {
        Calendar calendar = getCalendarForDate(dateString);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        begining = calendar.getTime();

        return begining;
    }

    public long getDate(){



            long timeInMilliseconds = 0;
            //String date_ = date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                Date mDate = sdf.parse("2017 11 24");
                timeInMilliseconds = mDate.getTime();
                System.out.println("Date in milli :: " + timeInMilliseconds);

            }
            catch (ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return timeInMilliseconds;
        }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        //Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        //Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }


    private static Calendar getCalendarForDate(String date) throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        //Pattern "dd MM yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.ENGLISH);
        calendar.setTime(sdf.parse(date));


        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }


    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }


    //Date Calculations
    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(yesterday());
    }


    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getDateWithTime(){
        //2017-06-18%2019:00:22
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        //SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date myDate = new Date();
        String dateWithTime = timeStampFormat.format(myDate);
        return dateWithTime;
    }

    public static double getHrsGivenDate(String toyBornTime){

        //String toyBornTime = "2014-06-18 12:56:50";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        try {

            Date oldDate = dateFormat.parse(toyBornTime);

            Date currentDate = new Date();
            long diff = currentDate.getTime() - oldDate.getTime();
            double seconds = diff / 1000.0;
            double minutes = seconds / 60.0;
            double hours = minutes / 60.0;
          //  long days = hours / 24;

            return minutes;

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return 999;
    }


    public static String getDateWithTime2(){
        //2017-06-18%2019:00:22
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date myDate = new Date();
        String dateWithTime = timeStampFormat.format(myDate);
        return dateWithTime;
    }

    public static String changeDateFormat(String formatnew, String date){
        System.out.println("inside method date - "+date);
        Date date1 = null;
        SimpleDateFormat sdf1 = new SimpleDateFormat(formatnew);
        try{
            date1 = sdf1.parse(date);
        }catch(Exception e){

        }
        String da = sdf1.format(date1);
        return da;
    }

    public static String changeDateFormat2(String formatnew, String date){
        System.out.println("inside method date - "+date);
        Date date1 = null;
        long mildate = 0;
        mildate = getMilSecAccordingToDate2(date);
        String s2 = getDateAccordingToMilliSeconds(mildate,formatnew);
        return s2;
    }



    public static String getTodayDateString() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = new Date();
        String today = timeStampFormat.format(myDate);
        return today;
    }
    public static String getPreviousWeekDateString() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Date start = c.getTime();
        return timeStampFormat.format(start);
    }

    public static String getTodayDateString1() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date myDate = new Date();
        String today = timeStampFormat.format(myDate);
        return today;
    }

    public static Date convertToDateFormat(String date){
        Date date1= null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }


    public static String getYearWithMonth() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMM");
        Date myDate = new Date();
        String date = timeStampFormat.format(myDate);
        return date;
    }


    public static String getMonth(){
        String sMonth;
        Calendar cal =  Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String tDate = df.format(cal.getTime());

        if(tDate.equals("01") || tDate.equals("1")){
            cal.add(Calendar.MONTH ,-1);
            sMonth  = new SimpleDateFormat("MMMM").format(cal.getTime());
        }else{
            // cal.add(Calendar.MONTH ,1);
            df = new SimpleDateFormat("MMMM");
            sMonth  =  df.format(cal.getTime());
        }

        return  sMonth;
    }


    public static String getYear() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy");
        Date myDate = new Date();
        String myYear = timeStampFormat.format(myDate);
        return myYear;
    }

    public static String getDateAccordingToMilliSeconds(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public static long getMilSecAccordingToDate(String someDate ){
        System.out.println("inside getMilSecAccordingToDate1");
        //String someDate = "05.10.2011";
        long dateMilSec = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = sdf.parse(someDate);
        } catch (ParseException e) {
            System.out.println("Error at parsing date into milliseconds ");
            e.printStackTrace();
        }
        System.out.println(" ==== Date in Mill sec DateManager: "+ date.getTime());
        dateMilSec =  date.getTime();


        return dateMilSec;
    }

    public static long getMilSecAccordingToDate2(String someDate ){

        System.out.println("getMilSecAccordingToDate2");
        //11/27/2017 10:58:27 AM
        //("yyyy-MM-dd HH:mm:ss.SSS");
        long dateMilSec = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        Date date = null;
        try {
            date = sdf.parse(someDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(" ==== Date in Mill sec DateManager: "+ date.getTime());
        dateMilSec =  date.getTime();

        return dateMilSec;
    }
    
}
