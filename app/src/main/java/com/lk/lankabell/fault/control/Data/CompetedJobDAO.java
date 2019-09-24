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
import com.lk.lankabell.fault.model.CompetedJob;
import com.lk.lankabell.fault.model.MaterialIssued;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class CompetedJobDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG="DAO";

    public CompetedJobDAO (Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(CompetedJob competedJob) {

        int count =0;

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }

        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.COMPLETED_JOB_REQUESTED_ID, competedJob.getCOMPLETED_JOB_REQUESTED_ID());
            values.put(dbHelper.COMPLETED_JOB_REQUEST_TO_REFID, competedJob.getCOMPLETED_JOB_REQUEST_TO_REFID());
            values.put(dbHelper.COMPLETED_JOB_IS_MATERIAL_REQUEST, competedJob.getCOMPLETED_JOB_IS_MATERIAL_REQUEST());
            values.put(dbHelper.COMPLETED_JOB_ACTION_TAKEN, competedJob.getCOMPLETED_JOB_ACTION_TAKEN());
            values.put(dbHelper.COMPLETED_JOB_LATITUDE, competedJob.getCOMPLETED_JOB_LATITUDE());
            values.put(dbHelper.COMPLETED_JOB_LONGITUTE, competedJob.getCOMPLETED_JOB_LONGITUTE());
            values.put(dbHelper.COMPLETED_JOB_REMARK, competedJob.getCOMPLETED_JOB_REMARK());
           values.put(dbHelper.COMPLETED_JOB_DATE,  DateManager.getDateWithTime());
            values.put(dbHelper.COMPLETED_JOB_IS_SYNC, "0");
            values.put(dbHelper.COMPLETED_JOB_VISIT_LOG_IS_SYNC, "0");

            count = (int) dB.insert(dbHelper.TABLE_COMPETED_JOB, null, values);

            Log.v("INSERTED COMPETED JOB",""+count);


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
            count = dB.delete(dbHelper.TABLE_COMPETED_JOB,
                    dbHelper.COMPLETED_JOB_REQUESTED_ID + " ='"+requestId+"'",null);

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }


    public JSONArray getAllCompletedUnsunced(){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_COMPETED_JOB +" WHERE "+dbHelper.COMPLETED_JOB_IS_SYNC+"='0'";

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {
//remark
                JSONObject postedJSON = new JSONObject();

                JSONArray materialList = new MaterialIssuedDAO(context) .getAllIssuedMaterial(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));

                postedJSON.put("empNo", AppContoller.getEMPNO(context));
                postedJSON.put("requestId", cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));
                postedJSON.put("actionCode", cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_TAKEN)));
                postedJSON.put("closingRemark",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REMARK)));

                if(materialList.length()>0) {
                    postedJSON.put("availMaterials", "YES");
                }else{
                    postedJSON.put("availMaterials", "NO");
                }
                postedJSON.put("materialList",materialList);

                postedJSON.put("unitDetails", new MaterialIssuedDAO(context)
                        .getUnitDetails(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID))));

                jsonArray.put(postedJSON);

//                list.add(job);
            }

//            Log.v("Insert Query",""+count);

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }
    public JSONArray getCompletedFault(String fault_id){

        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{

            String select = "SELECT * FROM " + dbHelper.TABLE_COMPETED_JOB +" WHERE "+dbHelper.COMPLETED_JOB_REQUESTED_ID+"='"+fault_id+"'";

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                JSONObject postedJSON = new JSONObject();

                JSONArray materialList = new MaterialIssuedDAO(context)
                        .getAllIssuedMaterial(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));

                postedJSON.put("empNo", AppContoller.getEMPNO(context));
                postedJSON.put("requestId", cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));
                postedJSON.put("actionCode",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_TAKEN)));
                postedJSON.put("closingRemark",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REMARK)));


                if(materialList.length()>0) {
                    postedJSON.put("availMaterials", "YES");
                }else{
                    postedJSON.put("availMaterials", "NO");
                }
                postedJSON.put("materialList",materialList);

                postedJSON.put("unitDetails", new MaterialIssuedDAO(context)
                        .getUnitDetails(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID))));

                jsonArray.put(postedJSON);

//                list.add(job);
            }

//            Log.v("Insert Query",""+count);

        }catch (Exception e) {
            Log.e(TAG+" Exception", e.toString());
        }finally {
            dB.close();
        }
        return jsonArray;
    }

    public JSONArray getVisitLogArray(String requestId){
        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            String select = "SELECT * FROM " + dbHelper.TABLE_COMPETED_JOB +" WHERE "+dbHelper.COMPLETED_JOB_REQUESTED_ID+" = '"+requestId+"' AND "+
                    dbHelper.COMPLETED_JOB_VISIT_LOG_IS_SYNC+" = '0'";

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                JSONObject postedJSON = new JSONObject();
                postedJSON.put("faultId",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));
                postedJSON.put("empNo",AppContoller.getEMPNO(context));
                //getActionNameByCode //cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_TAKEN))
                //cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_DESCRIPTION_OTHER))
                String remark = cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REMARK));
                String action_name = new ActionTakenDAO(context).getActionNameByCode(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_TAKEN)));

                if(action_name.equals("OTHERS") && !remark.equals("")){
                    postedJSON.put("remark",remark);
                }else if(!action_name.equals("OTHERS")){
                    postedJSON.put("remark",action_name);
                }else{
                    //rare case
                    postedJSON.put("remark",action_name);
                }

                postedJSON.put("longtitute",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_LONGITUTE)));
                postedJSON.put("latitute",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_LATITUDE)));
                postedJSON.put("completedDate",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_DATE)));

                jsonArray.put(postedJSON);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }
        return jsonArray;
    }
    /*{
      "data": [{
          "faultId": "7540396",
          "requestedBy": "121086",
          "materialDetails": [{
              "hardwareItemCode": "LTCUH010105",
              "itemQty": "1",
              "serial": "",
              "itemType": "RB"
          }, {
              "hardwareItemCode": "LTCUH010105",
              "itemQty": "1",
              "serial": "12345678",
              "itemType": "NW"
          }]},
          {
          "faultId": "7540397",
          "requestedBy": "121086",
          "materialDetails": [{
              "hardwareItemCode": "LTCUH010105",
              "itemQty": "1",
              "serial": "",
              "itemType": "RB"
          }, {
              "hardwareItemCode": "LTCUH010105",
              "itemQty": "1",
              "serial": "12345678",
              "itemType": "NW"
          }]

      }]
  }
  */
    public JSONArray getReturnUnitDetails(final String requestedID, ArrayList<MaterialIssued> selectedM) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

               // JSONArray materialList = new MaterialIssuedDAO(context).getReturnMaterial(requestedID);
                JSONArray materialList = new MaterialIssuedDAO(context).getReturnMaterialArray(selectedM);
                JSONObject postedJSON = new JSONObject();

                postedJSON.put("faultId", requestedID);
                postedJSON.put("requestedBy", AppContoller.getEMPNO(context));
                postedJSON.put("materialDetails", materialList);

                jsonArray.put(postedJSON);


                Log.v("Data",jsonArray.toString());

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        }finally {
            dB.close();
        }

        return jsonArray;
    }


    public JSONArray getAllVisitLogArrayUnSync(){
        JSONArray jsonArray = new JSONArray();

        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            /*
               {"faultId":"7539798",
                "empNo":"121086", .
                "longtitute":"6.0",
                "latitute":"7.0",
                "completedDate":"06/17/2019 16:31:48"
                }
            */
            String select = "SELECT * FROM " + dbHelper.TABLE_COMPETED_JOB +" WHERE "+dbHelper.COMPLETED_JOB_VISIT_LOG_IS_SYNC+" = '0'";

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                JSONObject postedJSON = new JSONObject();
                postedJSON.put("faultId",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REQUESTED_ID)));
                postedJSON.put("empNo",AppContoller.getEMPNO(context));

                String remark = cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_REMARK));
                String action_name = new ActionTakenDAO(context).getActionNameByCode(cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_ACTION_TAKEN)));

                if(action_name.equals("OTHERS") && !remark.equals("")){
                    postedJSON.put("remark",remark);
                }else if(!action_name.equals("OTHERS")){
                    postedJSON.put("remark",action_name);
                }else{
                    //worse rare case
                    postedJSON.put("remark",action_name);
                }

                postedJSON.put("longtitute",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_LONGITUTE)));
                postedJSON.put("latitute",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_LATITUDE)));
                postedJSON.put("completedDate",cur.getString(cur.getColumnIndex(dbHelper.COMPLETED_JOB_DATE)));

                jsonArray.put(postedJSON);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }
        return jsonArray;
    }



    public int updateIsSynced(String requestId) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;
        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.COMPLETED_JOB_IS_SYNC , requestId);
            count = dB.update(dbHelper.TABLE_COMPETED_JOB, values,dbHelper.COMPLETED_JOB_REQUESTED_ID + " =?",
                    new String[] {requestId});

        } catch (Exception e) {
            Log.v(" Exception", e.toString());
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
            dB.close();
        }
        return count;
    }

    public int updateIsVisitLogSynced(String requestId) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{

            String updateQuery = "UPDATE "+dbHelper.TABLE_COMPETED_JOB+" SET "+dbHelper.COMPLETED_JOB_VISIT_LOG_IS_SYNC+" = '"+requestId+"'"
                    +" WHERE "+dbHelper.COMPLETED_JOB_REQUESTED_ID+" = '"+requestId+"'";

            System.out.println("* update query "+updateQuery);

            Cursor cur = dB.rawQuery(updateQuery, null);

            count = cur.getCount();


        } catch (Exception e) {
            Log.v(" Exception", e.toString());
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
            dB.close();
        }
        return count;
    }
    public int deleteFault(String requestId) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{

            String deleteQuery = "DELETE "+dbHelper.TABLE_COMPETED_JOB+" FROM "+dbHelper.TABLE_COMPETED_JOB
                    +" WHERE "+dbHelper.COMPLETED_JOB_REQUESTED_ID+" = '"+requestId+"'";

            System.out.println("* delete query "+deleteQuery);

            Cursor cur = dB.rawQuery(deleteQuery, null);

            count = cur.getCount();


        } catch (Exception e) {
            Log.v(" Exception", e.toString());
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
            dB.close();
        }
        return count;
    }

}
