package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.CustomerPriority;

import java.util.ArrayList;

public class CustomerPriorityDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public CustomerPriorityDAO (Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(CustomerPriority customerPriority) {
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.CP_CUSTOMERSTAT, customerPriority.getCP_CUSTOMERSTAT());
            values.put(dbHelper.CP_STAT_LEVEL, customerPriority.getCP_STAT_LEVEL());
            values.put(dbHelper.CP_STAT_LEVEL_TIME, customerPriority.getCP_STAT_LEVEL_TIME());

            count = (int) dB.insert(dbHelper.TABLE_CUSTOMER_PRIORITY, null, values);

       //     Log.v("INSERTED CUS PRIORITY",""+count);


        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return count;
    }


    public String getMinmumRespTime(String CusRatings){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String select = "SELECT "+dbHelper.CP_STAT_LEVEL_TIME
                            +" FROM " + dbHelper.TABLE_CUSTOMER_PRIORITY
                            +" WHERE " + dbHelper.CP_CUSTOMERSTAT +" ='"+CusRatings+"'" ;

            cursor = dB.rawQuery(select, null);

            while(cursor.moveToNext()){

                return cursor.getString(cursor.getColumnIndex(dbHelper.CP_STAT_LEVEL_TIME));

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

    public int delete(){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_CUSTOMER_PRIORITY,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }


    public ArrayList<CustomerPriority> getAllCustomerPriority() {

        ArrayList<CustomerPriority> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER_PRIORITY;

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                CustomerPriority customerPriority = new CustomerPriority();

                customerPriority.setCP_ID(cur.getString(cur.getColumnIndex(dbHelper.CP_ID)));
                customerPriority.setCP_CUSTOMERSTAT(cur.getString(cur.getColumnIndex(dbHelper.CP_CUSTOMERSTAT)));
                customerPriority.setCP_STAT_LEVEL(cur.getString(cur.getColumnIndex(dbHelper.CP_STAT_LEVEL)));
                customerPriority.setCP_STAT_LEVEL_TIME(cur.getString(cur.getColumnIndex(dbHelper.CP_STAT_LEVEL_TIME)));

                list.add(customerPriority);
            }

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }

}
