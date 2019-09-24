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
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.MaterialIssued;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class MaterialIssuedDAO {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG = "DAO";

    public MaterialIssuedDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(MaterialIssued issued) {
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;

        try {

            String selectQuery =
                            "SELECT * " +
                            "FROM " + dbHelper.TABLE_MATERIAL_ISSUED +
                            " WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +"= '"+issued.getMATERIAL_ISSUED_REQUESTED_ID() +
                                    "' AND "+dbHelper.MATERIAL_ISSUED_ISSUE_NO +"= '"+issued.getMATERIAL_ISSUED_ISSUE_NO()+
                                    "' AND "+dbHelper.MATERIAL_ISSUED_TYPE +"= '"+issued.getMATERIAL_ISSUED_TYPE()+"'";

            System.out.println("* selectQuery "+selectQuery);

            cursor = dB.rawQuery(selectQuery, null);

            ContentValues values = new ContentValues();

            values.put(dbHelper.MATERIAL_ISSUED_ITEM_NO, issued.getMATERIAL_ISSUED_ITEM_NO());//MATERIAL_ISSUED_ITEM_NO
            values.put(dbHelper.MATERIAL_ISSUED_PIM_ID, issued.getMATERIAL_ISSUED_PIM_ID());//MATERIAL_ISSUED_ITEM_NO
            values.put(dbHelper.MATERIAL_ISSUED_REQUESTED_ID, issued.getMATERIAL_ISSUED_REQUESTED_ID());
            values.put(dbHelper.MATERIAL_ISSUED_ISSUE_NO, issued.getMATERIAL_ISSUED_ISSUE_NO());
            values.put(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN, issued.getMATERIAL_ISSUED_GIVEN_IMEI_ESN());
            values.put(dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN, issued.getMATERIAL_ISSUED_RETURN_IMEI_ESN());
            values.put(dbHelper.MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN,issued.getMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN());
            values.put(dbHelper.MATERIAL_ISSUED_RETURN_STATUS, issued.getMATERIAL_ISSUED_RETURN_STATUS());
            values.put(dbHelper.MATERIAL_ISSUED_DATE, new DateManager().getDateWithTime());
            values.put(dbHelper.MATERIAL_ISSUED_TYPE, issued.getMATERIAL_ISSUED_TYPE());
            values.put(dbHelper.MATERIAL_ISSUED_IS_ASR, issued.getMATERIAL_ISSUED_IS_ASR());
            values.put(dbHelper.MATERIAL_ISSUED_IS_RETURN, issued.getMATERIAL_ISSUED_IS_RETURN());
            values.put(dbHelper.MATERIAL_ISSUED_TYPE_OTHER, issued.getMATERIAL_ISSUED_TYPE_OTHER());
            values.put(dbHelper.MATERIAL_ISSUED_EXISTING_ITEM_CODE, issued.getMATERIAL_ISSUED_EXISTING_ITEM_CODE());
            values.put(dbHelper.MATERIAL_ISSUED_IS_SYNC, "0");

            int cn = cursor.getCount();

            System.out.println("* cn MaterialIssuedDAO  "+cn);
            System.out.println("* issued.getMATERIAL_ISSUED_TYPE_OTHER() "+issued.getMATERIAL_ISSUED_TYPE_OTHER());

            if(cn>0 && !issued.getMATERIAL_ISSUED_TYPE_OTHER().equals("OTHER")){
                System.out.println("* LLL update");
                count = dB.update(dbHelper.TABLE_MATERIAL_ISSUED, values ,null,null);// ADD WHERE CLAUSE CONDITION
                Log.v("Update Query",""+count);
            }else{
                if(cn>0 && issued.getMATERIAL_ISSUED_TYPE_OTHER().equals("OTHER") ){
                    if(issued.getMATERIAL_ISSUED_IS_RETURN().equals("1")){
                        //return -> insert
                        count = (int) dB.insert(dbHelper.TABLE_MATERIAL_ISSUED, null, values);
                        Log.v("Insert Query",""+count);
                    }else{
                        // no return -> update
                        count = dB.update(dbHelper.TABLE_MATERIAL_ISSUED, values ,null,null);// ADD WHERE CLAUSE CONDITION
                        Log.v("Update Query",""+count);
                    }
                }else {
                    System.out.println("* LLL insert");
                    count = (int) dB.insert(dbHelper.TABLE_MATERIAL_ISSUED, null, values);
                    Log.v("Insert Query", "" + count);
                }
            }
            System.out.println("* count  "+count);

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }

        return count;

    }

    public int insert(MaterialIssued issued) {
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;

        try {

            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.MATERIAL_ISSUED_ITEM_NO, issued.getMATERIAL_ISSUED_ITEM_NO());//MATERIAL_ISSUED_ITEM_NO
            values.put(DatabaseHelper.MATERIAL_ISSUED_PIM_ID, issued.getMATERIAL_ISSUED_PIM_ID());//MATERIAL_ISSUED_ITEM_NO
            values.put(DatabaseHelper.MATERIAL_ISSUED_REQUESTED_ID, issued.getMATERIAL_ISSUED_REQUESTED_ID());
            values.put(DatabaseHelper.MATERIAL_ISSUED_ISSUE_NO, issued.getMATERIAL_ISSUED_ISSUE_NO());
            values.put(DatabaseHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN, issued.getMATERIAL_ISSUED_GIVEN_IMEI_ESN());
            values.put(DatabaseHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN, issued.getMATERIAL_ISSUED_RETURN_IMEI_ESN());
            values.put(DatabaseHelper.MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN,issued.getMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN());
            values.put(DatabaseHelper.MATERIAL_ISSUED_RETURN_STATUS, issued.getMATERIAL_ISSUED_RETURN_STATUS());
            values.put(DatabaseHelper.MATERIAL_ISSUED_DATE, DateManager.getDateWithTime());
            values.put(DatabaseHelper.MATERIAL_ISSUED_TYPE, issued.getMATERIAL_ISSUED_TYPE());
            values.put(DatabaseHelper.MATERIAL_ISSUED_IS_ASR, issued.getMATERIAL_ISSUED_IS_ASR());
            values.put(DatabaseHelper.MATERIAL_ISSUED_IS_RETURN, issued.getMATERIAL_ISSUED_IS_RETURN());
            values.put(DatabaseHelper.MATERIAL_ISSUED_TYPE_OTHER, issued.getMATERIAL_ISSUED_TYPE_OTHER());
            values.put(DatabaseHelper.MATERIAL_ISSUED_EXISTING_ITEM_CODE, issued.getMATERIAL_ISSUED_EXISTING_ITEM_CODE());
            values.put(DatabaseHelper.MATERIAL_ISSUED_IS_SYNC, "0");

            count = (int) dB.insert(DatabaseHelper.TABLE_MATERIAL_ISSUED, null, values);

            System.out.println("* count  "+count);

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }

        return count;

    }


    public JSONArray getAllIssuedMaterial(final String requestedID) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                                    +" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +"='"+requestedID+"'";

            Log.v("Query >>",select);

            Cursor cur = dB.rawQuery(select, null);

            System.out.println("* All meterial count "+cur.getCount());
            while (cur.moveToNext()) {

               if(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE_OTHER)) != null) {
                    JSONObject postedJSON1 = new JSONObject();
                    postedJSON1.put("itemCode", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO)));
                    postedJSON1.put("quantity", "1");
                    postedJSON1.put("transactionType", "ISS");
                    postedJSON1.put("itemType", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE)));
                    postedJSON1.put("serial", "");
                    postedJSON1.put("type", "");
                    postedJSON1.put("category", "");

                    jsonArray.put(postedJSON1);

                    if (cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_IS_RETURN)).equals("1")) {
                        //not return
                        //2 obj
                        JSONObject postedJSON = new JSONObject();
                        postedJSON.put("itemCode", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO)));
                        postedJSON.put("quantity", "1");
                        postedJSON.put("transactionType", "RET");
                        postedJSON.put("itemType", "TS");
                        postedJSON.put("serial", "");
                        postedJSON.put("type", "");
                        postedJSON.put("category", "");

                        jsonArray.put(postedJSON);
                    }

                }else if(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE_OTHER))== null) {
                    //unit details
                   String ReturnImei =  cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN)); //exiting from service
                   String GivenImei =  cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN));
                   String mainType  =   cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE));
                   String Category  =   "";
                   String oldSrl = "";
                   String newSrl = "";
                   String type = "";

                 //  if(mainType.equals("CPHN") ||mainType.equals("LTES")  ){
                       oldSrl = ReturnImei;
                       newSrl = GivenImei;
                       type = "DEFECTIVE";
                 //  }
                     if(mainType.equals("LTEU") ||mainType.equals("LTES")  ){
                         Category= mainType;
                   }

//LTES - sim LTEU- lte CPHN - cdma
                   JSONObject postedJSON1 = new JSONObject();
                   String itemtype =  getItemType(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN))); //MATERIAL_ISSUED_GIVEN_IMEI_ESN

                   if(itemtype.equalsIgnoreCase("REFERBISH")){
                       itemtype = "RB";
                   }else if(itemtype.equalsIgnoreCase("NEW")){
                       itemtype = "NW";
                   }

                   String exitingItemCode = getExitingCode(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN)));

                   if(exitingItemCode.equals("")||exitingItemCode == null|| exitingItemCode.equals("1111")){
                       exitingItemCode = cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO));
                   }

                   String oldSerial = getOldItemCode(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN)));

                   postedJSON1.put("itemCode", oldSerial);
                   postedJSON1.put("quantity", "1");
                   postedJSON1.put("transactionType", "ISS");
                   postedJSON1.put("itemType", itemtype);
                   postedJSON1.put("serial", newSrl);
                   postedJSON1.put("type", type);
                   postedJSON1.put("category",Category);

                   jsonArray.put(postedJSON1);

                   if (cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_IS_RETURN)).equals("1")) {
                       //not return
                       //2 obj
                       JSONObject postedJSON = new JSONObject();
                       postedJSON.put("itemCode",exitingItemCode);
                       postedJSON.put("quantity", "1");
                       postedJSON.put("transactionType", "RET");
                       postedJSON.put("itemType", "TS");
                       postedJSON.put("serial", oldSrl);
                       postedJSON.put("type", type);
                       postedJSON.put("category", Category);

                       jsonArray.put(postedJSON);
                   }
                }
           }

            Log.v("AllIssuedMaterial Data",jsonArray.toString());

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        }finally {
            dB.close();
        }

        return jsonArray;
    }

    private String getExitingCode(String Serial) {
        String code = "";
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cur = null;
        try {
            String select = "SELECT * FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                    +" WHERE "+dbHelper.PIM_SERIAL +"='"+Serial+"'";

            Log.v("Query >>",select);

             cur = dB.rawQuery(select, null);

            while(cur.moveToNext()) {
                code = cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE));
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(cur!= null){
                cur.close();
            }
            dB.close();
        }
        System.out.println("* type "+code);
        return code;
    }
    private String getOldItemCode(String Serial) {
        String code = "";
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {
            String select = "SELECT * FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                    +" WHERE "+dbHelper.PIM_SERIAL +"='"+Serial+"'";

            Log.v("Query >>",select);

            Cursor cur = dB.rawQuery(select, null);

            while(cur.moveToNext()) {
                code = cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCODE));
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            dB.close();
        }
        System.out.println("* type "+code);
        return code;
    }

    private String getItemType(String serial) {
        String type = "";
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cur = null;
        try {
            String select = "SELECT * FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                    +" WHERE "+dbHelper.PIM_SERIAL +"='"+serial+"'";

            Log.v("Query >>",select);

             cur = dB.rawQuery(select, null);

            while(cur.moveToNext()) {
                type = cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMTYPE));
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            dB.close();
            if(cur!= null){
                cur.close();
            }
        }
        System.out.println("* type "+type);
        return type;
    }


    public JSONArray getUnitDetails(final String requestedID) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                    +" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +"='"+requestedID+"'";

            Log.v("Query >>",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {


                if(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE_OTHER))== null) {

                    JSONObject postedJSON = new JSONObject();
                    String ReturnImei =  cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN)); //exiting from service
                    String GivenImei =  cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN));
                    String EnteredImei =  cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN));

//                    if(!EnteredImei.equals("")){
//                        ReturnImei = EnteredImei;
//                    }

                    System.out.println("* while getting returnImei "+ReturnImei);
                    System.out.println("* while getting givenImei "+GivenImei);

                    postedJSON.put("oldSerial", ReturnImei);
                    postedJSON.put("newSerial", GivenImei);
                    postedJSON.put("type", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE)));
                    postedJSON.put("model",
                            new PendingIssuedMaterialDAO(context).getItemDet(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN))));

                    jsonArray.put(postedJSON);
                }
            }

            Log.v("Data",jsonArray.toString());

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        }finally {
            dB.close();
        }

        return jsonArray;
    }



    public JSONArray getReturnMaterial(final String requestedID) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                    +" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +"='"+requestedID+"' AND "+dbHelper.MATERIAL_ISSUED_IS_RETURN+" = '0'";

            Log.v("Query >>",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                    JSONObject postedJSON = new JSONObject();

                    postedJSON.put("hardwareItemCode", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO)));
                    postedJSON.put("itemQty", "1");
                    postedJSON.put("serial", cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN)));
                    postedJSON.put("itemType",cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE)));

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

    public JSONArray getReturnMaterialArray(final ArrayList<MaterialIssued> selectedMaterial) {

        JSONArray jsonArray = new JSONArray();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            for(MaterialIssued material : selectedMaterial){
                JSONObject postedJSON = new JSONObject();
                String GIVEN_IMEI_ESN = "";
                if(material.getMATERIAL_ISSUED_GIVEN_IMEI_ESN() == null){
                    GIVEN_IMEI_ESN = "";
                }else{
                    GIVEN_IMEI_ESN = material.getMATERIAL_ISSUED_GIVEN_IMEI_ESN();
                }

                postedJSON.put("hardwareItemCode", material.getMATERIAL_ISSUED_ITEM_NO());
                postedJSON.put("itemQty", "1");
                postedJSON.put("serial",GIVEN_IMEI_ESN);
                postedJSON.put("itemType",material.getMATERIAL_ISSUED_TYPE());

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

    public String getDescription(String item_no){
        System.out.println("* item_no "+item_no);
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try{
            String select = "SELECT "+dbHelper.PIM_ITEMDESCRIPTION+" FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                    +" WHERE "+dbHelper.PIM_ITEMCODE +"='"+item_no+"'";

            System.out.println("* query "+select);

            Cursor cur = dB.rawQuery(select, null);


            while(cur.moveToFirst()){

                return cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMDESCRIPTION));

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            dB.close();
        }
        return "";
    }
    public ArrayList<MaterialIssued> getReturnMaterialList(final String requestedID) {

        ArrayList<MaterialIssued> Materials = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                    +" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +"='"+requestedID+"' AND "+dbHelper.MATERIAL_ISSUED_IS_RETURN+" = '0'";

            Log.v("Query >>",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                MaterialIssued materialIssued = new MaterialIssued();

                materialIssued.setMATERIAL_ISSUED_ITEM_NO(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO))); //item_no CDHWCU015000
                materialIssued.setMATERIAL_ISSUED_RETURN_IMEI_ESN(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN))); //given_imsi_no
                materialIssued.setMATERIAL_ISSUED_TYPE(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE))); //type CPHN
                materialIssued.setMATERIAL_ISSUED_ISSUE_NO(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ISSUE_NO))); //issue_no 7245095

                Materials.add(materialIssued);
            }
        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        }finally {
            dB.close();
        }

        return Materials;
    }

    public String getCountCreateASR(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String selectQuery = "SELECT count(*) as count " +
                    "FROM " + dbHelper.TABLE_MATERIAL_ISSUED +" WHERE "+dbHelper.MATERIAL_ISSUED_IS_ASR +"= '1'" ;

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

        return "";
    }
    //dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN

    public int updateIMEI_ESN_ItemCode( String ItemCode,String Serial) {
        System.out.println("* ItemCode "+ItemCode +" Serial "+Serial);
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{
//            String sql_c = "SELECT * FROM "+dbHelper.TABLE_MATERIAL_ISSUED+" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID+" = '"+Fault_id+"'";
//
//            cursor = dB.rawQuery(sql_c, null);
//            count = cursor.getCount();
//            String sql = "UPDATE "+dbHelper.TABLE_MATERIAL_ISSUED+
//                    " SET "+dbHelper.MATERIAL_ISSUED_EXISTING_ITEM_CODE+" = '"+ItemCode+"' ," +
//                    dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN+" = '"+Imesi_esn+"' " +
//                    "WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID+" = '"+Fault_id+"' AND "+dbHelper.MATERIAL_ISSUED_TYPE+" = '"+type+"'";
//            System.out.println("* sql "+sql);
//            dB.execSQL(sql);

            ContentValues values = new ContentValues();
            values.put(dbHelper.PIM_EXISTING_ITEM_CODE,ItemCode);

            count = dB.update(dbHelper.TABLE_PENDING_ISSUED_MATERIAL, values,dbHelper.PIM_SERIAL + " = ?",
                    new String[] { Serial});

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

    public String getCountNoASR(){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String selectQuery = "SELECT count(*) as count " +
                    "FROM " + dbHelper.TABLE_MATERIAL_ISSUED +" WHERE "+dbHelper.MATERIAL_ISSUED_IS_ASR +"= '0'" ;

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

        return "";
    }

    public int deleteByItemNo(String item_no,String requestID, String type) {

        int count = 1;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            String select = "DELETE FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                            +" WHERE "+dbHelper.MATERIAL_ISSUED_ITEM_NO + " ='" + item_no
                                +"' AND "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID + " ='" + requestID
                                +"' AND "+dbHelper.MATERIAL_ISSUED_TYPE + " ='" + type +"'";

            System.out.println("* deleteByItemNo  "+select);
            Log.v("DELETE QUERY >> ", select );
            dB.execSQL(select);

//            count = dB.delete(dbHelper.TABLE_MATERIAL_ISSUED,
//                    dbHelper.MATERIAL_ISSUED_ITEM_NO + " ='" + item_no +"'", null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dB != null) {
                dB.close();
            }
        }
        return count;

    }
    public int deleteByItemNoOther(String item_no,String requestID, String type) {

        int count = 1;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            String select = "DELETE FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                            +" WHERE "+dbHelper.MATERIAL_ISSUED_ITEM_NO + " ='" + item_no
                                +"' AND "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID + " ='" + requestID
                                +"' AND "+dbHelper.MATERIAL_ISSUED_TYPE_OTHER + " ='" + type +"'";

            System.out.println("* deleteByItemNoOther  "+select);
            Log.v("DELETE QUERY >> ", select );
            dB.execSQL(select);

//            count = dB.delete(dbHelper.TABLE_MATERIAL_ISSUED,
//                    dbHelper.MATERIAL_ISSUED_ITEM_NO + " ='" + item_no +"'", null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dB != null) {
                dB.close();
            }
        }
        return count;

    }

    public ArrayList<MaterialIssued> getAllIssuedMaterialByRequestID(String RequestId) {

        ArrayList<MaterialIssued> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_MATERIAL_ISSUED
                                +" WHERE "+dbHelper.MATERIAL_ISSUED_REQUESTED_ID +" = "+RequestId;

            Log.v("Query >>>> ",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                MaterialIssued matrl = new MaterialIssued();
                matrl.setMATERIAL_ISSUED_PIM_ID(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_PIM_ID)));
                matrl.setMATERIAL_ISSUED_ID(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ID)));
                matrl.setMATERIAL_ISSUED_ITEM_NO(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ITEM_NO)));
                matrl.setMATERIAL_ISSUED_REQUESTED_ID(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_REQUESTED_ID)));
                matrl.setMATERIAL_ISSUED_ISSUE_NO(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ISSUE_NO)));
                matrl.setMATERIAL_ISSUED_GIVEN_IMEI_ESN(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_GIVEN_IMEI_ESN)));
                matrl.setMATERIAL_ISSUED_RETURN_IMEI_ESN(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_RETURN_IMEI_ESN)));
                matrl.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN)));
                matrl.setMATERIAL_ISSUED_RETURN_STATUS(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_RETURN_STATUS)));
                matrl.setMATERIAL_ISSUED_DATE(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_DATE)));
                matrl.setMATERIAL_ISSUED_IS_SYNC(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_IS_SYNC)));
                matrl.setMATERIAL_ISSUED_TYPE(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE)));
                matrl.setMATERIAL_ISSUED_TYPE_OTHER(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_TYPE_OTHER)));
                matrl.setMATERIAL_ISSUED_IS_ASR(cur.getString(cur.getColumnIndex(dbHelper.MATERIAL_ISSUED_IS_ASR)));

                list.add(matrl);

            }


        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }



}