package com.lk.lankabell.fault.control.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lk.lankabell.fault.control.Data.db.DatabaseHelper;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;

import java.util.ArrayList;

public class PendingIssuedMaterialDAO {

    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;

    private String TAG = "DAO";

    public PendingIssuedMaterialDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int insertORupdate(IssuedMaterialDetails materialDetails) {
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

//        String sql = "SELECT * FROM "+dbHelper.TABLE_PENDING_ISSUED_MATERIAL
//                + " WHERE "+dbHelper.PID_ITEMISSUE_NO+" = '"+issuedDetails.getPID_ITEMISSUE_NO()+"'";
//        Cursor cur = dB.rawQuery(sql,null);

        try {
            ContentValues values = new ContentValues();

            values.put(dbHelper.PIM_ISSUENO, materialDetails.getIssueNo());
            values.put(dbHelper.PIM_ISSUETYPE, materialDetails.getIssueType());
            values.put(dbHelper.PIM_ITEMTYPE, materialDetails.getItemType());
            values.put(dbHelper.PIM_ITEMCODE, materialDetails.getItemCode());
            values.put(dbHelper.PIM_ITEMDESCRIPTION, materialDetails.getItemDescription());
            values.put(dbHelper.PIM_ISSUEDQTY, materialDetails.getIssuedQty());
            values.put(dbHelper.PIM_ITEMCATEGORY, materialDetails.getItemCategory());
            values.put(dbHelper.PIM_SERIAL, materialDetails.getSerial());

            if(materialDetails.getExistingItemCode()== null || materialDetails.getExistingItemCode().equals("")){
                values.put(dbHelper.PIM_EXISTING_ITEM_CODE, "1111");
            }else{
                values.put(dbHelper.PIM_EXISTING_ITEM_CODE, materialDetails.getExistingItemCode());
            }


            count = (int) dB.insert(dbHelper.TABLE_PENDING_ISSUED_MATERIAL, null, values);

        //    Log.v("INSERTED ACCEPTED FAULT", "" + count);

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return count;
    }

    public ArrayList<IssuedMaterialDetails> getAlItemIssuedMaterialsByIssueCode(String sIssueCode) {

        ArrayList<IssuedMaterialDetails> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select = "SELECT * FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                                            +" WHERE "+dbHelper.PIM_ISSUENO +" ='"+sIssueCode+"'";

            System.out.println("* Query "+select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();

                materialDetails.setIssueNo(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUENO)));
                materialDetails.setIssueType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUETYPE)));
                materialDetails.setItemType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMTYPE)));
                materialDetails.setItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCODE)));
                materialDetails.setItemDescription(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMDESCRIPTION)));
                materialDetails.setIssuedQty(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUEDQTY)));
                materialDetails.setItemCategory(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCATEGORY)));
                materialDetails.setSerial(cur.getString(cur.getColumnIndex(dbHelper.PIM_SERIAL)));

//                if(cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE)).equals("null")||cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE)).equals(null)){
//                    materialDetails.setExistingItemCode("1111");
//                }else{
//                    materialDetails.setExistingItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE)));
//                }
//
//                System.out.println("* eee "+materialDetails.getExistingItemCode());


                list.add(materialDetails);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }

    public String getCountAvailableUnits(String type){

        Cursor cursor=null;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String select="";

            if(type.equals("CPHN")) {
                select = "SELECT ifnull(sum(IssuedQty),0) as count  FROM pending_issued_material_tbl " +
                         "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                         "AND ItemCategory ='CPHN' " +
                         "AND Serial NOT IN (Select given_imei_esn from material_issued_tbl where type = 'CPHN') AND Serial != 'null'";

            }else if(type.equals("LTES")) {
                select = "SELECT ifnull(sum(IssuedQty),0) as count  FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                        "AND ItemCategory ='LTES' " +
                        "AND Serial NOT IN (Select given_imei_esn from material_issued_tbl where type = 'LTES')";

            }else if(type.equals("LTEU")) {
                select = "SELECT ifnull(sum(IssuedQty),0) as count  FROM pending_issued_material_tbl " +
                            "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                                            "AND ItemCategory ='LTEU' " +
                                                "AND Serial NOT IN (Select given_imei_esn from material_issued_tbl where type = 'LTEU')";
            }else if(type.equals("OTHER")) {

                select = "SELECT ifnull(sum(IssuedQty) - (Select count(*) from material_issued_tbl where type = 'OTHER'),0) " +
                        "as count    FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                        "AND ItemCategory NOT IN ('CPHN','LTES','LTEU')";
            }

            Log.v("Query ",select);

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

    public ArrayList<IssuedMaterialDetails> getAllAcceptedMaterial(String type) {

        ArrayList<IssuedMaterialDetails> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select="";

            if(type.equals("CPHN")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) AND ItemCategory ='CPHN' AND Serial != 'null' ORDER BY ItemCode ASC";

            }else if(type.equals("LTES")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) AND ItemCategory ='LTES' ORDER BY ItemCode ASC";

            }else if(type.equals("LTEU")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) AND ItemCategory ='LTEU' ORDER BY ItemCode ASC";

            }else if(type.equals("OTHER")) {

                select = "SELECT id,IssueNo,IssueType,ItemType,ItemCode,ItemDescription,IssuedQty - (Select count(*) from material_issued_tbl where type = 'OTHER' AND item_no =ItemCode) as IssuedQty,ItemCategory,Serial \n" +
                        "FROM pending_issued_material_tbl \" +\n" +
                        "                        \"WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) AND ItemCategory NOT IN ('CPHN','LTES','LTEU') ORDER BY ItemCode ASC";

            }

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                materialDetails.setsID(cur.getString(cur.getColumnIndex(dbHelper.PIM_ID)));
                materialDetails.setIssueNo(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUENO)));
                materialDetails.setIssueType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUETYPE)));
                materialDetails.setItemType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMTYPE)));
                materialDetails.setItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCODE)));
                materialDetails.setItemDescription(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMDESCRIPTION)));
                materialDetails.setIssuedQty(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUEDQTY)));
                materialDetails.setItemCategory(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCATEGORY)));
                materialDetails.setSerial(cur.getString(cur.getColumnIndex(dbHelper.PIM_SERIAL)));
               // materialDetails.setExistingItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE)));

                list.add(materialDetails);
            }

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }
    public ArrayList<IssuedMaterialDetails> searchMaterial(String type,String searchText) {

        System.out.println("* searchText Model "+searchText);
        ArrayList<IssuedMaterialDetails> list = new ArrayList<>();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            String select="";

            if(type.equals("CPHN")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                        "AND ItemCategory ='CPHN' " +
                        "AND Serial LIKE '%"+searchText+"%'" +
                        "ORDER BY ItemCode ASC";

            }else if(type.equals("LTES")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                        "AND ItemCategory ='LTES' " +
                        "AND Serial LIKE '%"+searchText+"%'" +
                        "ORDER BY ItemCode ASC";

            }else if(type.equals("LTEU")) {

                select = "SELECT * FROM pending_issued_material_tbl " +
                        "WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl) " +
                        "AND ItemCategory ='LTEU' " +
                        "AND Serial LIKE '%"+searchText+"%'" +
                        "ORDER BY ItemCode ASC";

            }else if(type.equals("OTHER")) {

                select = "SELECT id,IssueNo,IssueType,ItemType,ItemCode,ItemDescription,IssuedQty - (Select count(*) from material_issued_tbl where type = 'OTHER' AND item_no =ItemCode) as IssuedQty,ItemCategory,Serial \n" +
                        "FROM pending_issued_material_tbl \" +\n" +
                        "                        \"WHERE IssueNo IN (select ItemIssueNo from accepted_issue_details_tbl)" +
                        " AND ItemCategory NOT IN ('CPHN','LTES','LTEU') " +
                        "AND ItemCode LIKE '%"+searchText+"%'" +
                        "ORDER BY ItemCode ASC";

            }

            Log.v("Query",select);

            Cursor cur = dB.rawQuery(select, null);

            while (cur.moveToNext()) {

                IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                materialDetails.setsID(cur.getString(cur.getColumnIndex(dbHelper.PIM_ID)));
                materialDetails.setIssueNo(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUENO)));
                materialDetails.setIssueType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUETYPE)));
                materialDetails.setItemType(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMTYPE)));
                materialDetails.setItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCODE)));
                materialDetails.setItemDescription(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMDESCRIPTION)));
                materialDetails.setIssuedQty(cur.getString(cur.getColumnIndex(dbHelper.PIM_ISSUEDQTY)));
                materialDetails.setItemCategory(cur.getString(cur.getColumnIndex(dbHelper.PIM_ITEMCATEGORY)));
                materialDetails.setSerial(cur.getString(cur.getColumnIndex(dbHelper.PIM_SERIAL)));
               // materialDetails.setExistingItemCode(cur.getString(cur.getColumnIndex(dbHelper.PIM_EXISTING_ITEM_CODE)));

                list.add(materialDetails);
            }

        } catch (Exception e) {
            Log.e(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return list;
    }

    public int deleteAll(String issueNo){
        int count = 0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        try{
            count = dB.delete(dbHelper.TABLE_PENDING_ISSUED_MATERIAL,dbHelper.PIM_ISSUENO + " =?", new String[] { issueNo});

        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(dB != null){
                dB.close();
            }
        }
        return count;
    }

    public int updateQty(String itemCode,int qty) {
        int count =0;
        if(dB == null){
            open();
        }else if(!dB.isOpen()){
            open();
        }
        Cursor cursor = null;

        try{

            ContentValues values = new ContentValues();
            values.put(dbHelper.PIM_ISSUEDQTY,qty);

            count = dB.update(dbHelper.TABLE_PENDING_ISSUED_MATERIAL, values,dbHelper.PIM_ITEMCODE + " =?",
                    new String[] { String.valueOf(itemCode)});

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

    public String getItemDet(String serial){
        Cursor cursor=null;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            String select = "SELECT " + dbHelper.PIM_ITEMDESCRIPTION
                            +" FROM " + dbHelper.TABLE_PENDING_ISSUED_MATERIAL
                            +" WHERE "+dbHelper.PIM_SERIAL +" ='"+serial+"'";


            Log.v("Query > ",select);

            cursor = dB.rawQuery(select, null);

            while(cursor.moveToNext()){

                return cursor.getString(cursor.getColumnIndex(dbHelper.PIM_ITEMDESCRIPTION));

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
}