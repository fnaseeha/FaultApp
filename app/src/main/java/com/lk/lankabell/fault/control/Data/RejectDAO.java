package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.PendingFaults;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RejectDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public RejectDAO (Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(PendingFaults pendingFaults) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.RF_REQUESTID, pendingFaults.getPF_REQUESTID());
            values.put(dbHelper.RF_REQUESTBATCHID, pendingFaults.getPF_REQUESTBATCHID());
            values.put(dbHelper.RF_REQUESTTOREFID, pendingFaults.getPF_REQUESTTOREFID());
            values.put(dbHelper.RF_REQUESTTONAME, pendingFaults.getPF_REQUESTTONAME());
            values.put(dbHelper.RF_REQUESTTOCONTACT, pendingFaults.getPF_REQUESTTOCONTACT());
            values.put(dbHelper.RF_REQUESTTOADD1, pendingFaults.getPF_REQUESTTOADD1());
            values.put(dbHelper.RF_REQUESTTOADD2, pendingFaults.getPF_REQUESTTOADD2());
            values.put(dbHelper.RF_REQUESTTOADD3, pendingFaults.getPF_REQUESTTOADD3());
            values.put(dbHelper.RF_REQUESTASSIGNEDTO, pendingFaults.getPF_REQUESTASSIGNEDTO());
            values.put(dbHelper.RF_REQUESTASSIGNEDDATE, pendingFaults.getPF_REQUESTASSIGNEDDATE());
            values.put(dbHelper.RF_STATUS, pendingFaults.getPF_STATUS());
            values.put(dbHelper.RF_PRIORITY, pendingFaults.getPF_PRIORITY());
            values.put(dbHelper.RF_REQUESTTYPE, pendingFaults.getPF_REQUESTTYPE());
            values.put(dbHelper.RF_REQUESTSUBTYPE, pendingFaults.getPF_REQUESTSUBTYPE());
            values.put(dbHelper.RF_REQUESTCATEGORY, pendingFaults.getPF_REQUESTCATEGORY());
            values.put(dbHelper.RF_ISACCEPT, pendingFaults.getPF_ISACCEPT());
            values.put(dbHelper.RF_REQUESTTOLOCATION, pendingFaults.getPF_REQUESTTOLOCATION());
            values.put(dbHelper.RF_SERVICETYPE, pendingFaults.getPF_SERVICETYPE());
            values.put(dbHelper.RF_CUSTOMERCATEGORY, pendingFaults.getPF_CUSTOMERCATEGORY());
            values.put(dbHelper.RF_CUSTOMERRATINGS, pendingFaults.getPF_CUSTOMERRATINGS());
            values.put(dbHelper.RF_CUSTOMERREMARKS, pendingFaults.getPF_CUSTOMERREMARKS());
            values.put(dbHelper.RF_DIRECTION, pendingFaults.getPF_DIRECTION());

            values.put(dbHelper.RF_EMPNO,new AppContoller().getEMPNO(context));
            values.put(dbHelper.RF_REJECTED_DATE, new DateManager().getDateWithTime());
            values.put(dbHelper.RF_IS_SYCN, "0");
            count = (int) dB.insert(dbHelper.TABLE_REJECT_FAULT, null, values);

          //  Log.v("INSERTED REJECT FAULT",""+count);


        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return count;
    }


    public JSONArray getRejectedFaults(){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_REJECT_FAULT +" WHERE "+dbHelper.RF_IS_SYCN+"='0' ";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                JSONObject postedJSON = new JSONObject();
                postedJSON.put("requestId", cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTID)));
                //values.put(dbHelper.RF_REQUESTID, pendingFaults.getPF_REQUESTID());
                jsonArray.put(postedJSON);

            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }

    public ArrayList<PendingFaults> getAllRejected(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_REJECT_FAULT +" WHERE "+dbHelper.RF_IS_SYCN+"='0'";
            Cursor cur = dB.rawQuery(select, null);



            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.RF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.RF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.RF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.RF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.RF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.RF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.RF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.RF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.RF_DIRECTION)));

                list.add(faults);

            }

//            Log.v("Insert Query",""+count);

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return list;
    }


    public int updateIsSynced(String requestID) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.RF_IS_SYCN,"1");

            count = dB.update(dbHelper.TABLE_REJECT_FAULT, values,dbHelper.RF_REQUESTID + " =?",
                    new String[] { String.valueOf(requestID)});

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
