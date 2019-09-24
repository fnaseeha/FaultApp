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

public class AcceptFaultDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public AcceptFaultDAO (Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(PendingFaults pendingFaults,String issync) {
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        String sql = "SELECT * FROM "+dbHelper.TABLE_ACCEPTED_FAULT
                + " WHERE "+dbHelper.AF_REQUESTID+" = '"+pendingFaults.getPF_REQUESTID()+"'";

        Cursor cur = dB.rawQuery(sql,null);

        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.AF_REQUESTID, pendingFaults.getPF_REQUESTID());
            values.put(dbHelper.AF_REQUESTBATCHID, pendingFaults.getPF_REQUESTBATCHID());
            values.put(dbHelper.AF_REQUESTTOREFID, pendingFaults.getPF_REQUESTTOREFID());
            values.put(dbHelper.AF_REQUESTTONAME, pendingFaults.getPF_REQUESTTONAME());
            values.put(dbHelper.AF_REQUESTTOCONTACT, pendingFaults.getPF_REQUESTTOCONTACT());
            values.put(dbHelper.AF_REQUESTTOADD1, pendingFaults.getPF_REQUESTTOADD1());
            values.put(dbHelper.AF_REQUESTTOADD2, pendingFaults.getPF_REQUESTTOADD2());
            values.put(dbHelper.AF_REQUESTTOADD3, pendingFaults.getPF_REQUESTTOADD3());
            values.put(dbHelper.AF_REQUESTASSIGNEDTO, pendingFaults.getPF_REQUESTASSIGNEDTO());
            values.put(dbHelper.AF_REQUESTASSIGNEDDATE, pendingFaults.getPF_REQUESTASSIGNEDDATE());
            values.put(dbHelper.AF_STATUS, pendingFaults.getPF_STATUS());
            values.put(dbHelper.AF_PRIORITY, pendingFaults.getPF_PRIORITY());
            values.put(dbHelper.AF_REQUESTTYPE, pendingFaults.getPF_REQUESTTYPE());
            values.put(dbHelper.AF_REQUESTSUBTYPE, pendingFaults.getPF_REQUESTSUBTYPE());
            values.put(dbHelper.AF_REQUESTCATEGORY, pendingFaults.getPF_REQUESTCATEGORY());
            values.put(dbHelper.AF_ISACCEPT, pendingFaults.getPF_ISACCEPT());
            values.put(dbHelper.AF_REQUESTTOLOCATION, pendingFaults.getPF_REQUESTTOLOCATION());
            values.put(dbHelper.AF_SERVICETYPE, pendingFaults.getPF_SERVICETYPE());
            values.put(dbHelper.AF_CUSTOMERCATEGORY, pendingFaults.getPF_CUSTOMERCATEGORY());
            values.put(dbHelper.AF_CUSTOMERRATINGS, pendingFaults.getPF_CUSTOMERRATINGS());
            values.put(dbHelper.AF_CUSTOMERREMARKS, pendingFaults.getPF_CUSTOMERREMARKS());
            values.put(dbHelper.AF_DIRECTION, pendingFaults.getPF_DIRECTION());

            values.put(dbHelper.AF_IS_SYCN,issync);
            values.put(dbHelper.AF_EMPNO,new AppContoller().getEMPNO(context));
            values.put(dbHelper.AF_ACCEPTEDO_DATE, new DateManager().getDateWithTime());

            values.put(dbHelper.AF_LTE_DAY,pendingFaults.getPF_LTE_DAY());
            values.put(dbHelper.AF_LTE_NIGHT,pendingFaults.getPF_LTE_NIGHT());

            if(cur.getCount()>0){
                count = dB.update(dbHelper.TABLE_ACCEPTED_FAULT, values ,
                        dbHelper.AF_REQUESTID + " =?", new String[] { pendingFaults.getPF_REQUESTID()});
            }else{
                count = (int) dB.insert(dbHelper.TABLE_ACCEPTED_FAULT, null, values);
            }

         //   Log.v("INSERTED ACCEPTED FAULT",""+count);


        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return count;
    }


    public int deleteByrequestId(String requestId){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_ACCEPTED_FAULT,dbHelper.AF_REQUESTID + " ='"+requestId+"'",null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }

    public ArrayList<PendingFaults> getAllAcceptedToSync(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_ACCEPTED_FAULT +" WHERE "+dbHelper.AF_IS_SYCN+"='0' ";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.AF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.AF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.AF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.AF_DIRECTION)));
                //ACCEPTED..
                faults.setAF_EMPNO(cur.getString(cur.getColumnIndex(dbHelper.AF_EMPNO)));//AF
                faults.setAF_ACCEPTEDO_DATE(cur.getString(cur.getColumnIndex(dbHelper.AF_ACCEPTEDO_DATE)));//AF

                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_DAY)));
                faults.setPF_LTE_NIGHT(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_NIGHT)));

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


    public JSONArray getAcceptedFaults(){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_ACCEPTED_FAULT +" WHERE "+dbHelper.AF_IS_SYCN+"='0' ";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                JSONObject postedJSON = new JSONObject();
                postedJSON.put("acceptedDate", cur.getString(cur.getColumnIndex(dbHelper.AF_ACCEPTEDO_DATE)));
                postedJSON.put("empNo", cur.getString(cur.getColumnIndex(dbHelper.AF_EMPNO)));
                postedJSON.put("requestId", cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTID)));
                postedJSON.put("status", "ACCEPTED");

                jsonArray.put(postedJSON);

            }

//            Log.v("Insert Query",""+count);

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }



    public String getCountPending(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String select = "SELECT count(*) as count FROM " + dbHelper.TABLE_ACCEPTED_FAULT +" WHERE "+ dbHelper.PF_REQUESTID
                    +" NOT IN (Select "+dbHelper.COMPLETED_JOB_REQUESTED_ID +" FROM " + dbHelper.TABLE_COMPETED_JOB +" )";


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

    public ArrayList<PendingFaults> getLteAccepted(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_ACCEPTED_FAULT +" WHERE "+ dbHelper.AF_REQUESTID
                    +" NOT IN (Select "+dbHelper.COMPLETED_JOB_REQUESTED_ID +" FROM " + dbHelper.TABLE_COMPETED_JOB +" )" +
                    "AND "+dbHelper.AF_REQUESTTYPE+" = 'L'";
            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.AF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.AF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.AF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.AF_DIRECTION)));
                //ACCEPTED..
                faults.setAF_EMPNO(cur.getString(cur.getColumnIndex(dbHelper.AF_EMPNO)));//AF
                faults.setAF_ACCEPTEDO_DATE(cur.getString(cur.getColumnIndex(dbHelper.AF_ACCEPTEDO_DATE)));//AF

                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_DAY)));
                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_NIGHT)));

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

    public ArrayList<PendingFaults> getAllAccepted(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_ACCEPTED_FAULT +" WHERE "+ dbHelper.AF_REQUESTID
                    +" NOT IN (Select "+dbHelper.COMPLETED_JOB_REQUESTED_ID +" FROM " + dbHelper.TABLE_COMPETED_JOB +" )";

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.AF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.AF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.AF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.AF_DIRECTION)));
                //ACCEPTED..
                faults.setAF_EMPNO(cur.getString(cur.getColumnIndex(dbHelper.AF_EMPNO)));//AF
                faults.setAF_ACCEPTEDO_DATE(cur.getString(cur.getColumnIndex(dbHelper.AF_ACCEPTEDO_DATE)));//AF

                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_DAY)));
                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.AF_LTE_NIGHT)));

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
            values.put(dbHelper.AF_IS_SYCN,"1");
            count = dB.update(dbHelper.TABLE_ACCEPTED_FAULT, values,dbHelper.AF_REQUESTID + " =?",
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

    public int updateQuota_a(String DAY,String NIGHT,String FAULD_ID) {

        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;

        try {

            ContentValues values = new ContentValues();

            values.put(dbHelper.AF_LTE_DAY, DAY);
            values.put(dbHelper.AF_LTE_NIGHT, NIGHT);

            count = dB.update(dbHelper.TABLE_ACCEPTED_FAULT, values ,
                    dbHelper.AF_REQUESTID + " =?", new String[] { FAULD_ID});

        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG+" Exception", e.toString());
        }finally {
            if(cur!= null){
                cur.close();
            }
            dB.close();
        }
        return count;
    }


    public ArrayList<PendingFaults> getAcceptedData(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_ACCEPTED_FAULT;

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.AF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.AF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.AF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.AF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.AF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.AF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.AF_DIRECTION)));
                //ACCEPTED..
                faults.setAF_EMPNO(cur.getString(cur.getColumnIndex(dbHelper.AF_EMPNO)));//AF
                faults.setAF_ACCEPTEDO_DATE(cur.getString(cur.getColumnIndex(dbHelper.AF_ACCEPTEDO_DATE)));//AF

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
}
