package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.PendingFaults;

import java.util.ArrayList;

public class PendingFaultDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="Reason DAO";

    public PendingFaultDAO (Context context){
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

        String sql = "SELECT * FROM "+dbHelper.TABLE_PENDING_FAULTS
                + " WHERE "+dbHelper.PF_REQUESTID+" = '"+pendingFaults.getPF_REQUESTID()+"'";

        Cursor cur = dB.rawQuery(sql,null);

        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.PF_REQUESTID, pendingFaults.getPF_REQUESTID());
            values.put(dbHelper.PF_REQUESTBATCHID, pendingFaults.getPF_REQUESTBATCHID());
            values.put(dbHelper.PF_REQUESTTOREFID, pendingFaults.getPF_REQUESTTOREFID());
            values.put(dbHelper.PF_REQUESTTONAME, pendingFaults.getPF_REQUESTTONAME());
            values.put(dbHelper.PF_REQUESTTOCONTACT, pendingFaults.getPF_REQUESTTOCONTACT());
            values.put(dbHelper.PF_REQUESTTOADD1, pendingFaults.getPF_REQUESTTOADD1());
            values.put(dbHelper.PF_REQUESTTOADD2, pendingFaults.getPF_REQUESTTOADD2());
            values.put(dbHelper.PF_REQUESTTOADD3, pendingFaults.getPF_REQUESTTOADD3());
            values.put(dbHelper.PF_REQUESTASSIGNEDTO, pendingFaults.getPF_REQUESTASSIGNEDTO());
            values.put(dbHelper.PF_REQUESTASSIGNEDDATE, pendingFaults.getPF_REQUESTASSIGNEDDATE());
            values.put(dbHelper.PF_STATUS, pendingFaults.getPF_STATUS());
            values.put(dbHelper.PF_PRIORITY, pendingFaults.getPF_PRIORITY());
            values.put(dbHelper.PF_REQUESTTYPE, pendingFaults.getPF_REQUESTTYPE());
            values.put(dbHelper.PF_REQUESTSUBTYPE, pendingFaults.getPF_REQUESTSUBTYPE());
            values.put(dbHelper.PF_REQUESTCATEGORY, pendingFaults.getPF_REQUESTCATEGORY());
            values.put(dbHelper.PF_ISACCEPT, pendingFaults.getPF_ISACCEPT());
            values.put(dbHelper.PF_REQUESTTOLOCATION, pendingFaults.getPF_REQUESTTOLOCATION());
            values.put(dbHelper.PF_SERVICETYPE, pendingFaults.getPF_SERVICETYPE());
            values.put(dbHelper.PF_CUSTOMERCATEGORY, pendingFaults.getPF_CUSTOMERCATEGORY());
            values.put(dbHelper.PF_CUSTOMERRATINGS, pendingFaults.getPF_CUSTOMERRATINGS());
            values.put(dbHelper.PF_CUSTOMERREMARKS, pendingFaults.getPF_CUSTOMERREMARKS());
            values.put(dbHelper.PF_DIRECTION, pendingFaults.getPF_DIRECTION());

            if(cur.getCount()>0){
                count = dB.update(dbHelper.TABLE_PENDING_FAULTS, values ,
                        dbHelper.PF_REQUESTID + " =?", new String[] { pendingFaults.getPF_REQUESTID()});
            }else {
                count = (int) dB.insert(dbHelper.TABLE_PENDING_FAULTS, null, values);
            }

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
           dB.close();
        }
        return count;
    }
    public int updateQuota(String DAY,String NIGHT,String FAULD_ID) {

        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;

        try {

            ContentValues values = new ContentValues();

            values.put(dbHelper.PF_LTE_DAY, DAY);
            values.put(dbHelper.PF_LTE_NIGHT, NIGHT);

            count = dB.update(dbHelper.TABLE_PENDING_FAULTS, values ,
                    dbHelper.PF_REQUESTID + " =?", new String[] { FAULD_ID});

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


    public int deleteAll(){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_PENDING_FAULTS,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }


    public String getCount(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String selectQuery = "SELECT count(*) as count FROM " + dbHelper.TABLE_PENDING_FAULTS +" WHERE "+dbHelper.PF_REQUESTID
                    +" NOT IN ( Select "+dbHelper.PF_REQUESTID +" FROM " + dbHelper.TABLE_REJECT_FAULT+" ) AND "+dbHelper.PF_REQUESTID
                    +" NOT IN ( Select "+dbHelper.COMPLETED_JOB_REQUESTED_ID +" FROM " + dbHelper.TABLE_COMPETED_JOB+" )" ;

            cursor = dB.rawQuery(selectQuery, null);
            Log.e("QUERY",selectQuery);
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
    public String getPendingCountOnly(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String selectQuery = "SELECT count(*) as count FROM " + dbHelper.TABLE_PENDING_FAULTS +" WHERE "+dbHelper.PF_STATUS+" = 'NEW' AND "+dbHelper.PF_REQUESTID
                    +" NOT IN ( Select "+dbHelper.PF_REQUESTID +" FROM " + dbHelper.TABLE_REJECT_FAULT+" )";

            cursor = dB.rawQuery(selectQuery, null);

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

        return "0";
    }

    public String getCountAcceptPending(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String select = "SELECT COUNT(*) as count "
                    +"FROM "+ dbHelper.TABLE_PENDING_FAULTS
                    +" where "+ dbHelper.PF_REQUESTID
                    +" NOT IN (Select "+dbHelper.AF_REQUESTID +" FROM " + dbHelper.TABLE_ACCEPTED_FAULT
                    +" UNION Select "+dbHelper.AF_REQUESTID +" FROM " + dbHelper.TABLE_REJECT_FAULT+" )";

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


    public ArrayList<PendingFaults> getAllPendingFaults(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            String select = "SELECT * "
                    +"FROM "+ dbHelper.TABLE_PENDING_FAULTS
                    +" where "+ dbHelper.PF_REQUESTID
                    +" NOT IN (Select "+dbHelper.AF_REQUESTID +" FROM " + dbHelper.TABLE_ACCEPTED_FAULT
                            +" UNION Select "+dbHelper.AF_REQUESTID +" FROM " + dbHelper.TABLE_REJECT_FAULT+" )";

            Cursor cur = dB.rawQuery(select, null);

            Log.v("Query",select);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.PF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.PF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.PF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.PF_DIRECTION)));

                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.PF_LTE_DAY)));
                faults.setPF_LTE_NIGHT(cur.getString(cur.getColumnIndex(dbHelper.PF_LTE_NIGHT)));

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

    public ArrayList<PendingFaults> getLtePendingFaults(){
        ArrayList<PendingFaults> list = new ArrayList<>();
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            String select = "SELECT * "
                    +"FROM "+ dbHelper.TABLE_PENDING_FAULTS + " WHERE "+dbHelper.PF_REQUESTTYPE+" = 'L'";

            Cursor cur = dB.rawQuery(select, null);

            Log.v("Query",select);

            while (cur.moveToNext()) {

                PendingFaults faults = new PendingFaults();

                faults.setPF_REQUESTID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTID)));
                faults.setPF_REQUESTBATCHID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTBATCHID)));
                faults.setPF_REQUESTTOREFID(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOREFID)));
                faults.setPF_REQUESTTONAME(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTONAME)));
                faults.setPF_REQUESTTOCONTACT(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOCONTACT)));
                faults.setPF_REQUESTTOADD1(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD1)));
                faults.setPF_REQUESTTOADD2(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD2)));
                faults.setPF_REQUESTTOADD3(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOADD3)));
                faults.setPF_REQUESTASSIGNEDTO(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTASSIGNEDTO)));
                faults.setPF_REQUESTASSIGNEDDATE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTASSIGNEDDATE)));
                faults.setPF_STATUS(cur.getString(cur.getColumnIndex(dbHelper.PF_STATUS)));
                faults.setPF_PRIORITY(cur.getString(cur.getColumnIndex(dbHelper.PF_PRIORITY)));
                faults.setPF_REQUESTTYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTYPE)));
                faults.setPF_REQUESTSUBTYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTSUBTYPE)));
                faults.setPF_REQUESTCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTCATEGORY)));
                faults.setPF_ISACCEPT(cur.getString(cur.getColumnIndex(dbHelper.PF_ISACCEPT)));
                faults.setPF_REQUESTTOLOCATION(cur.getString(cur.getColumnIndex(dbHelper.PF_REQUESTTOLOCATION)));
                faults.setPF_SERVICETYPE(cur.getString(cur.getColumnIndex(dbHelper.PF_SERVICETYPE)));
                faults.setPF_CUSTOMERCATEGORY(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERCATEGORY)));
                faults.setPF_CUSTOMERRATINGS(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERRATINGS)));
                faults.setPF_CUSTOMERREMARKS(cur.getString(cur.getColumnIndex(dbHelper.PF_CUSTOMERREMARKS)));
                faults.setPF_DIRECTION(cur.getString(cur.getColumnIndex(dbHelper.PF_DIRECTION)));

                faults.setPF_LTE_DAY(cur.getString(cur.getColumnIndex(dbHelper.PF_LTE_DAY)));
                faults.setPF_LTE_NIGHT(cur.getString(cur.getColumnIndex(dbHelper.PF_LTE_NIGHT)));

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
