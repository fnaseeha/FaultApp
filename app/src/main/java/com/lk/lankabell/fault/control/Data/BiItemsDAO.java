package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.ActionTaken;
import com.lk.lankabell.fault.model.BiItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BiItemsDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public BiItemsDAO(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(BiItems biItems) {
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }


        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.BI_SERVICE_CODE, biItems.getBI_SERVICE_CODE());
            values.put(dbHelper.BI_SERVICE_NAME, biItems.getBI_SERVICE_NAME());


            count = (int) dB.insert(dbHelper.TABLE_BI_ITEMS, null, values);

            // Log.v("INSERTED ACTION TAKEN",""+count);


        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return count;
    }


    public int delete(){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_BI_ITEMS,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }

    public ArrayList<String> getAllActions(){

        ArrayList<String> list = new ArrayList<>();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_BI_ITEMS;

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                list.add(cur.getString(cur.getColumnIndex(dbHelper.BI_SERVICE_NAME)));
            }

            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return list;
    }


    public String getBIServiceCodeByServiceName(final String desc){

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_BI_ITEMS +" WHERE "+dbHelper.BI_SERVICE_NAME +" ='"+desc+"'";
            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                return cur.getString(cur.getColumnIndex(dbHelper.BI_SERVICE_CODE));
            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return "NO DATA";
    }

    public String getBIServiceNameByCode(final String code){

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_BI_ITEMS +" WHERE "+dbHelper.BI_SERVICE_CODE +" ='"+code+"'";
            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                return cur.getString(cur.getColumnIndex(dbHelper.BI_SERVICE_NAME));
            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return "NO DATA";
    }


}
