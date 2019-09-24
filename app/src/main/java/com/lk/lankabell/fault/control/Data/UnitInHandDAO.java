package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.MaterialIssued;
import com.lk.lankabell.fault.model.UnitInHand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnitInHandDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public UnitInHandDAO(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public ArrayList<UnitInHand> getUnitInHand(){

        ArrayList<UnitInHand> list = new ArrayList<>();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;
        try{
            String select = "SELECT * FROM " + dbHelper.TABLE_UNIT_IN_HAND;
             cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                UnitInHand unitInHand = new UnitInHand();
                unitInHand.setSerial(cur.getString(cur.getColumnIndex(dbHelper.Un_SerialNo)));
                unitInHand.setItemCode(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemCode)));
                unitInHand.setItemCategory(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemCategory)));
                unitInHand.setItemDescription(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemDescription)));
                list.add(unitInHand);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cur!= null){
                cur.close();
            }
            dB.close();
        }
        return list;
    }

    public JSONArray getUnitInHandArray(final  ArrayList<UnitInHand> list) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            for(UnitInHand unit : list){
                JSONObject postedJSON = new JSONObject();

/* "empNo":"2453",
         "itemCode":"CDHWCU010100",
         "itemType":"CPHN",
         "serial":"0F799AE1"*/

                postedJSON.put("empNo", AppContoller.getEMPNO(context));
                postedJSON.put("itemCode", unit.getItemCode());
                postedJSON.put("itemType",unit.getItemCategory());
                postedJSON.put("serial",unit.getSerial());

                jsonArray.put(postedJSON);
            }

            Log.v("Data",jsonArray.toString());

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        }finally {
            dB.close();
        }

        return jsonArray;
    }
    public ArrayList<UnitInHand> getUnitInHand(String type){

        ArrayList<UnitInHand> list = new ArrayList<>();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_UNIT_IN_HAND+" WHERE "+dbHelper.Un_ItemCategory+" = '"+type+"'";
             cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                UnitInHand unitInHand = new UnitInHand();
                unitInHand.setSerial(cur.getString(cur.getColumnIndex(dbHelper.Un_SerialNo)));
                unitInHand.setItemCode(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemCode)));
                unitInHand.setItemCategory(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemCategory)));
                unitInHand.setItemDescription(cur.getString(cur.getColumnIndex(dbHelper.Un_ItemDescription)));
                list.add(unitInHand);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cur!= null){
                cur.close();
            }
            dB.close();
        }
        return list;
    }

    public int delete(){

        int count = 0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_UNIT_IN_HAND,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }

    public int insert(UnitInHand unitInHand){
        //epf,emp,pword,simNo
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.Un_SerialNo,unitInHand.getSerial());
            values.put(dbHelper.Un_ItemCode,unitInHand.getItemCode());
            values.put(dbHelper.Un_ItemCategory, unitInHand.getItemCategory());
            values.put(dbHelper.Un_ItemDescription,unitInHand.getItemDescription());

            count = (int) dB.insert(dbHelper.TABLE_UNIT_IN_HAND, null, values);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }

        return count;
    }

    }
