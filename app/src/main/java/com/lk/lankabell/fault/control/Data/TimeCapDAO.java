package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.TimeCap;
import com.lk.lankabell.fault.model.UserProfile;

import java.util.ArrayList;

public class TimeCapDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public TimeCapDAO(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public ArrayList<TimeCap> getTimeCap(String raw_name){

        ArrayList<TimeCap> timeCaps = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;
        try{
             String select = "SELECT * FROM " + dbHelper.TABLE_TIME_CAP+" WHERE "+dbHelper.T_RAW_NAME+" = '"+raw_name+"'";
             cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                TimeCap timeCap = new TimeCap();

                timeCap.setDate(cur.getString(cur.getColumnIndex(dbHelper.T_DATE)));
                timeCap.setRawName(cur.getString(cur.getColumnIndex(dbHelper.T_RAW_NAME)));

                timeCaps.add(timeCap);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cur!= null){
                cur.close();
            }
            dB.close();
        }
        return timeCaps;
    }

    public int delete(){

        int count = 0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_TIME_CAP,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }



    public int insertOrUpdate(TimeCap timeCap){
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur =null;
        try{
            String sql = "SELECT * FROM "+dbHelper.TABLE_TIME_CAP
                    + " WHERE "+dbHelper.T_RAW_NAME+" = '"+timeCap.getRawName()+"'";

             cur = dB.rawQuery(sql,null);

            ContentValues values = new ContentValues();
            values.put(dbHelper.T_RAW_NAME,timeCap.getRawName());
            values.put(dbHelper.T_DATE, timeCap.getDate());

        if(cur.getCount()==0){
            count = (int) dB.insert(dbHelper.TABLE_TIME_CAP, null, values);
        }else{
            count = dB.update(dbHelper.TABLE_TIME_CAP, values ,
                    dbHelper.T_RAW_NAME + " =?", new String[] { timeCap.getRawName()});
        }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }

        return count;
    }

    }
