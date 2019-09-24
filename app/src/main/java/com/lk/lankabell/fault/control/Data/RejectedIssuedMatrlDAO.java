package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.AcceptedIssuedMatrl;
import com.lk.lankabell.fault.model.IssuedDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RejectedIssuedMatrlDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public RejectedIssuedMatrlDAO(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(IssuedDetails issuedMatrl) {
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try {

            ContentValues values = new ContentValues();
            //values.put(dbHelper.APID_ID, issuedMatrl.getAPID_ID());
            values.put(dbHelper.RPID_ITEMISSUE_NO, issuedMatrl.getPID_ITEMISSUE_NO());
            values.put(dbHelper.RPID_ITEM_ISSUED_DATE, issuedMatrl.getPID_ITEM_ISSUED_DATE());
            values.put(dbHelper.RPID_ITEM_ISSUE_REMARK, issuedMatrl.getPID_ITEM_ISSUE_REMARK());
            values.put(dbHelper.RPID_FROM_LOCATION_TYPE, issuedMatrl.getPID_FROM_LOCATION_TYPE());
            values.put(dbHelper.RPID_FROM_LOCATION, issuedMatrl.getPID_FROM_LOCATION());
            values.put(dbHelper.RPID_ITEM_ISSUE_STATUS, issuedMatrl.getPID_ITEM_ISSUE_STATUS());
            values.put(dbHelper.RPID_STOCK_TYPE, issuedMatrl.getPID_STOCK_TYPE());
            values.put(dbHelper.RPID_TOTAL_UNITS, issuedMatrl.getPID_TOTAL_UNITS());
            values.put(dbHelper.RPID_IS_SYNC, "0");

            count = (int) dB.insert(dbHelper.TABLE_REJECTED_ISSUE_DETAILS, null, values);

          //  Log.v("INSERTED ACCEPTED FAULT",""+count);


        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return count;
    }


    public JSONArray getRejectUnits(){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_REJECTED_ISSUE_DETAILS +" WHERE "+dbHelper.RPID_IS_SYNC+"='0' ";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                JSONObject postedJSON = new JSONObject();
                postedJSON.put("issueNo", cur.getString(cur.getColumnIndex(dbHelper.RPID_ITEMISSUE_NO)));
                postedJSON.put("remark", new AppContoller().getEMPNO(context));
                postedJSON.put("empNo", new AppContoller().getEMPNO(context));
                jsonArray.put(postedJSON);
            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }


    public int deleteByItemIssueNo(String ITEMISSUE_NO){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_REJECTED_ISSUE_DETAILS,
                    dbHelper.RPID_ITEMISSUE_NO + " ='"+ITEMISSUE_NO+"'",null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }


    public JSONArray getRejectedUnits(){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_REJECTED_ISSUE_DETAILS +" WHERE "+dbHelper.RPID_IS_SYNC+"='0' ";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
                JSONObject postedJSON = new JSONObject();
                postedJSON.put("issueNo", cur.getString(cur.getColumnIndex(dbHelper.RPID_ITEMISSUE_NO)));
                postedJSON.put("remark", new AppContoller().getEMPNO(context));
                postedJSON.put("empNo", new AppContoller().getEMPNO(context));
                jsonArray.put(postedJSON);
            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }



    public ArrayList<AcceptedIssuedMatrl> getAllAccepted(){

        ArrayList<AcceptedIssuedMatrl> list = new ArrayList<>();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_REJECTED_ISSUE_DETAILS;

            Cursor cur = dB.rawQuery(select, null);



            while (cur.moveToNext()) {

                AcceptedIssuedMatrl matrl = new AcceptedIssuedMatrl();

                matrl.setAPID_ID(cur.getString(cur.getColumnIndex(dbHelper.APID_ID)));
                matrl.setAPID_ITEMISSUE_NO(cur.getString(cur.getColumnIndex(dbHelper.APID_ITEMISSUE_NO)));
                matrl.setAPID_ITEM_ISSUED_DATE(cur.getString(cur.getColumnIndex(dbHelper.APID_ITEM_ISSUED_DATE)));
                matrl.setAPID_ITEM_ISSUE_REMARK(cur.getString(cur.getColumnIndex(dbHelper.APID_ITEM_ISSUE_REMARK)));
                matrl.setAPID_FROM_LOCATION_TYPE(cur.getString(cur.getColumnIndex(dbHelper.APID_FROM_LOCATION_TYPE)));
                matrl.setAPID_FROM_LOCATION(cur.getString(cur.getColumnIndex(dbHelper.APID_FROM_LOCATION)));
                matrl.setAPID_ITEM_ISSUE_STATUS(cur.getString(cur.getColumnIndex(dbHelper.APID_ITEM_ISSUE_STATUS)));
                matrl.setAPID_STOCK_TYPE(cur.getString(cur.getColumnIndex(dbHelper.APID_STOCK_TYPE)));
                matrl.setAPID_TOTAL_UNITS(cur.getString(cur.getColumnIndex(dbHelper.APID_TOTAL_UNITS)));
                matrl.setAPID_IS_SYNC(cur.getString(cur.getColumnIndex(dbHelper.APID_IS_SYNC)));

                list.add(matrl);
            }

//            Log.v("Insert Query",""+count);

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return list;
    }





    public int updateIsSynced(String APID_ITEMISSUE_NO) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.RPID_IS_SYNC,"1");
            count = dB.update(dbHelper.TABLE_REJECTED_ISSUE_DETAILS, values,dbHelper.RPID_ITEMISSUE_NO + " =?",
                    new String[] { String.valueOf(APID_ITEMISSUE_NO)});

        }catch (Exception e) {

            Log.v(" Exception", e.toString());

        }finally {
            if (cursor !=null) {
                cursor.close();
            }
            dB.close();
        }
        return count;
    }


}