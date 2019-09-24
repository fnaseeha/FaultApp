package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.IssuedDetails;

import java.util.ArrayList;
import java.util.List;

public class IssuedDetailsDAO {

    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG = "DAO";

    public IssuedDetailsDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(IssuedDetails issuedDetails) {
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String sql = "SELECT * FROM "+dbHelper.TABLE_ISSUE_DETAILS
                                + " WHERE "+dbHelper.PID_ITEMISSUE_NO+" = '"+issuedDetails.getPID_ITEMISSUE_NO()+"'";
        Cursor cur = dB.rawQuery(sql,null);

        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.PID_ITEMISSUE_NO, issuedDetails.getPID_ITEMISSUE_NO());
            values.put(dbHelper.PID_ITEM_ISSUED_DATE, issuedDetails.getPID_ITEM_ISSUED_DATE());
            values.put(dbHelper.PID_ITEM_ISSUE_REMARK, issuedDetails.getPID_ITEM_ISSUE_REMARK());
            values.put(dbHelper.PID_FROM_LOCATION_TYPE, issuedDetails.getPID_FROM_LOCATION_TYPE());
            values.put(dbHelper.PID_FROM_LOCATION, issuedDetails.getPID_FROM_LOCATION());
            values.put(dbHelper.PID_ITEM_ISSUE_STATUS, issuedDetails.getPID_ITEM_ISSUE_STATUS());
            values.put(dbHelper.PID_STOCK_TYPE, issuedDetails.getPID_STOCK_TYPE());
                values.put(dbHelper.PID_TOTAL_UNITS, issuedDetails.getPID_TOTAL_UNITS());



            if(cur.getCount()>0){
                count = dB.update(dbHelper.TABLE_ISSUE_DETAILS, values ,
                        dbHelper.PID_ITEMISSUE_NO + " =?", new String[] { issuedDetails.getPID_ITEMISSUE_NO()});
            }else {
                count = (int) dB.insert(dbHelper.TABLE_ISSUE_DETAILS, null, values);
            }

         //   Log.v("INSERTED ACCEPTED FAULT", "" + count);
        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return count;
    }


    public String getCountMRAcceptPending(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
//            String select = "SELECT COUNT(*) as count "
//                    +"FROM "+ dbHelper.TABLE_ISSUE_DETAILS
//                    +" where "+ dbHelper.PID_ITEMISSUE_NO
//                    +" NOT IN (Select "+dbHelper.APID_ITEMISSUE_NO +" FROM " + dbHelper.TABLE_ACCEPTED_ISSUE_DETAILS + ")";
//

            String select = "SELECT COUNT(*) as count  FROM " + dbHelper.TABLE_ISSUE_DETAILS
                    +" WHERE "+dbHelper.PID_ITEMISSUE_NO
                    +" NOT IN (Select "+dbHelper.APID_ITEMISSUE_NO +" FROM " + dbHelper.TABLE_ACCEPTED_ISSUE_DETAILS
                    +" UNION Select "+dbHelper.RPID_ITEMISSUE_NO +" FROM " + dbHelper.TABLE_REJECTED_ISSUE_DETAILS+" )";


            Log.v("MR - ACCEPT PENDING",select);

            cursor = dB.rawQuery(select, null);

            while(cursor.moveToNext()){

                return cursor.getString(cursor.getColumnIndex("count"));

            }
        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            if (cursor !=null) {
                cursor.close();
            }
            dB.close();
        }

        return "";
    }

    public ArrayList<IssuedDetails> getAlItemIssue() {

        ArrayList<IssuedDetails> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_ISSUE_DETAILS;

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                IssuedDetails issuedDetails = new IssuedDetails();

                issuedDetails.setPID_ITEMISSUE_NO(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEMISSUE_NO)));
                issuedDetails.setPID_ITEM_ISSUED_DATE(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUED_DATE)));
                issuedDetails.setPID_ITEM_ISSUE_REMARK(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUE_REMARK)));
                issuedDetails.setPID_FROM_LOCATION_TYPE(cur.getString(cur.getColumnIndex(dbHelper.PID_FROM_LOCATION_TYPE)));
                issuedDetails.setPID_FROM_LOCATION(cur.getString(cur.getColumnIndex(dbHelper.PID_FROM_LOCATION)));
                issuedDetails.setPID_ITEM_ISSUE_STATUS(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUE_STATUS)));
                issuedDetails.setPID_STOCK_TYPE(cur.getString(cur.getColumnIndex(dbHelper.PID_STOCK_TYPE)));
                issuedDetails.setPID_TOTAL_UNITS(cur.getString(cur.getColumnIndex(dbHelper.PID_TOTAL_UNITS)));

                list.add(issuedDetails);
            }

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }


    public List<IssuedDetails> getAllIssuedDetailsByNotAccepted() {

        List<IssuedDetails> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_ISSUE_DETAILS
                    +" WHERE "+dbHelper.PID_ITEMISSUE_NO
                    +" NOT IN (Select "+dbHelper.APID_ITEMISSUE_NO +" FROM " + dbHelper.TABLE_ACCEPTED_ISSUE_DETAILS
                                +" UNION Select "+dbHelper.RPID_ITEMISSUE_NO +" FROM " + dbHelper.TABLE_REJECTED_ISSUE_DETAILS+" )";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                IssuedDetails issuedDetails = new IssuedDetails();

                issuedDetails.setPID_ITEMISSUE_NO(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEMISSUE_NO)));
                issuedDetails.setPID_ITEM_ISSUED_DATE(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUED_DATE)));
                issuedDetails.setPID_ITEM_ISSUE_REMARK(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUE_REMARK)));
                issuedDetails.setPID_FROM_LOCATION_TYPE(cur.getString(cur.getColumnIndex(dbHelper.PID_FROM_LOCATION_TYPE)));
                issuedDetails.setPID_FROM_LOCATION(cur.getString(cur.getColumnIndex(dbHelper.PID_FROM_LOCATION)));
                issuedDetails.setPID_ITEM_ISSUE_STATUS(cur.getString(cur.getColumnIndex(dbHelper.PID_ITEM_ISSUE_STATUS)));
                issuedDetails.setPID_STOCK_TYPE(cur.getString(cur.getColumnIndex(dbHelper.PID_STOCK_TYPE)));
                issuedDetails.setPID_TOTAL_UNITS(cur.getString(cur.getColumnIndex(dbHelper.PID_TOTAL_UNITS)));


                list.add(issuedDetails);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }

    public int deleteAll(){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_ISSUE_DETAILS,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }
}