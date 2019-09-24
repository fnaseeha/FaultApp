package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.UserProfile;

import java.util.ArrayList;

public class UserProfileDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public UserProfileDAO(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public ArrayList<UserProfile> getUserProfile(){

        ArrayList<UserProfile> list = new ArrayList<>();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cur = null;
        try{
            String select = "SELECT * FROM " + dbHelper.TABLE_USER_PROFILE;
             cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                UserProfile userProfile = new UserProfile();
                userProfile.setEpf(cur.getString(cur.getColumnIndex(dbHelper.U_EPF)));
                userProfile.setEmp(cur.getString(cur.getColumnIndex(dbHelper.U_EMP_NO)));
                userProfile.setPword(cur.getString(cur.getColumnIndex(dbHelper.U_PWORD)));
                userProfile.setSimNo(cur.getString(cur.getColumnIndex(dbHelper.U_SIM_NO)));
                userProfile.setCordinatorNumber(cur.getString(cur.getColumnIndex(dbHelper.U_CORDINATOR_NUMBER)));
                list.add(userProfile);
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
            count = dB.delete(dbHelper.TABLE_USER_PROFILE,null,null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }

    public int updateCordinatorNumber(String cordinator_number,String empNo){

        int count = 0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.U_CORDINATOR_NUMBER,cordinator_number);
            //myDB.update(TableName, cv, "_id="+id, null);
           // dB.execSQL("UPDATE "+dbHelper.TABLE_USER_PROFILE+" SET "+"dbHelper.U_CORDINATOR_NUMBER"+"='"+cordinator_number+"' WHERE "+dbHelper.U_EPF+"='"+empNo+"'");

            count = dB.update(dbHelper.TABLE_USER_PROFILE,  values,dbHelper.U_EMP_NO+" = ?",new String[]{empNo});
            System.out.println("* count >>> "+count);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }
        return count;
    }

    public int insert(String epf,String emp,String password,String simNumber){
        //epf,emp,pword,simNo
        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            ContentValues values = new ContentValues();
            values.put(dbHelper.U_EPF,epf);
            values.put(dbHelper.U_EMP_NO,emp);
            values.put(dbHelper.U_PWORD, password);
            values.put(dbHelper.U_SIM_NO,simNumber);
            values.put(dbHelper.U_CORDINATOR_NUMBER,"0111111111");

            count = (int) dB.insert(dbHelper.TABLE_USER_PROFILE, null, values);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }

        return count;
    }

    }
