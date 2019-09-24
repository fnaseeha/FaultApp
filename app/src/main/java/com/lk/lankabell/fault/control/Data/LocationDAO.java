package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.LocationDetails;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

public class LocationDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG = "DAO";

    public LocationDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insert(LocationDetails location){
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            ContentValues cv = new ContentValues();
            cv.put(dbHelper.L_LATITUDE, location.getLATITUDE());
            cv.put(dbHelper.L_LONGITUTE, location.getLONGITUDE());
            cv.put(dbHelper.L_EMP_NO, location.getEMPNO());

            if(location.getLATITUDE() != 0 && location.getLONGITUDE() != 0){
                count = (int) dB.insert(dbHelper.TABLE_USER_LOCATION, null, cv);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }
        return  count;
    }

    public int delete(){
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {
                String deleteQuery = "DELETE FROM " + dbHelper.TABLE_USER_LOCATION + " WHERE " + dbHelper.L_ID +
                        " IN (SELECT " + dbHelper.L_ID + " FROM " + dbHelper.TABLE_USER_LOCATION + " ORDER BY " + dbHelper.L_ID + " LIMIT 15)";

                System.out.println("* delete query " + deleteQuery);
                cursor = dB.rawQuery(deleteQuery, null);
                count = cursor.getCount();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
            dB.close();
        }
        return  count;
    }

    public int getLocationCount(){
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            String selectQuery = "SELECT * FROM "+dbHelper.TABLE_USER_LOCATION;

            System.out.println("* select query "+selectQuery);
            cursor = dB.rawQuery(selectQuery, null);

            count = cursor.getCount();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!= null){
                cursor.close();
            }
            dB.close();
        }
        return  count;
    }

    public LocationDetails getLastLocation(){

        LocationDetails locationDetails = new LocationDetails();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            String selectQuery = "SELECT * FROM "+dbHelper.TABLE_USER_LOCATION+" ORDER BY "+dbHelper.L_ID+" DESC LIMIT 1";

            System.out.println("* select query "+selectQuery);
            cursor = dB.rawQuery(selectQuery, null);

            while(cursor.moveToNext()) {
                locationDetails.setLONGITUDE(cursor.getDouble(cursor.getColumnIndex(dbHelper.L_LONGITUTE)));
                locationDetails.setLATITUDE(cursor.getDouble(cursor.getColumnIndex(dbHelper.L_LATITUDE)));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!= null){
                cursor.close();
            }
            dB.close();
        }
        return  locationDetails;
    }

}
