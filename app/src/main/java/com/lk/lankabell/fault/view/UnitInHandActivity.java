package com.lk.lankabell.fault.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.adapter.unitInHandAdapter;
import com.lk.lankabell.fault.control.ClickListenerUnitInHand;
import com.lk.lankabell.fault.control.Data.AcceptedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.IssuedDetailsDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.Data.UnitInHandDAO;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.model.IssuedDetails;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.UnitInHand;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UnitInHandActivity extends AppCompatActivity implements VolleyCallback {

    RecyclerView rvUnitInHand;
    Button b4GLTE,bCDMA,done;

    ArrayList<UnitInHand> SelectedItems = new ArrayList<>();
    Alerts alerts;
    SweetAlertDialog pDialog;
    int syncCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_in_hand);
        b4GLTE = findViewById(R.id.b4GLTE);
        bCDMA = findViewById(R.id.bCDMA);
        rvUnitInHand = findViewById(R.id.rvUnitInHand);

        done = findViewById(R.id.done);
        getSupportActionBar().setTitle("UNIT IN HAND");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alerts = new Alerts(UnitInHandActivity.this);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        rvUnitInHand.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        setAdapter("LTEU");
//LTE
        b4GLTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                setAdapter("LTEU");
            }
        });
//CDMA
        bCDMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                setAdapter("CPHN");
            }
        });

    }


    public void setAdapter(final String type){
        final ArrayList<UnitInHand> CDMAs = new UnitInHandDAO(UnitInHandActivity.this).getUnitInHand(type);

        unitInHandAdapter unitInHandAdapter = new unitInHandAdapter(UnitInHandActivity.this,CDMAs, new ClickListenerUnitInHand() {
            @Override
            public void onPositionClicked(final int position, View v, CheckBox checkBox) {

                if (v.getId() == R.id.btnCheck) {

                    if (checkBox.isChecked()) {
                        checkBox.setChecked(true);
                        SelectedItems.add(CDMAs.get(position));
                    }else{
                        checkBox.setChecked(false);
                        SelectedItems.remove(CDMAs.get(position));
                    }

                }

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SelectedItems.size()>0){
                    if(new ConnectionDetector(UnitInHandActivity.this).isConnectingToInternet()) {
                        new SweetAlertDialog(UnitInHandActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Do you want to Change to Refurbish?")
                                .setConfirmText("Yes")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        ChangeToRefurbish(SelectedItems,type);


                                    }
                                })
                                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                    }
                                })
                                .show();
                    }else{
                        new ConnectionDetector(UnitInHandActivity.this).showSettingsAlert();
                    }
                }else{
                    alerts.ErrorAlert("Please Select at least one");
                }

            }
        });

        rvUnitInHand.setAdapter(unitInHandAdapter);
        rvUnitInHand.setItemAnimator(new DefaultItemAnimator());
        //rvUnitInHand.addItemDecoration(new DividerItemDecoration(rvUnitInHand.getContext(), DividerItemDecoration.VERTICAL));
        rvUnitInHand.setNestedScrollingEnabled(false);
        rvUnitInHand.getAdapter().notifyDataSetChanged();
    }

    private void ChangeToRefurbish(ArrayList<UnitInHand> selectedItems, String type) {
      //  pDialog.show();
        JSONArray selectedItemsList = new UnitInHandDAO(UnitInHandActivity.this).getUnitInHandArray(selectedItems);

        JSONObject object = new JSONObject();

        try {
            object.put("data", selectedItemsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("Json ", object.toString().replace("\\", ""));

       new SyncData(UnitInHandActivity.this).ChangeRefurbish(UnitInHandActivity.this, TaskType.TEST_TO_REFURBISH, object);
        setAdapter(type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_sync:{
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                   // pDialog.show();
                    new SyncData(UnitInHandActivity.this)
                            .getUnitInHand(UnitInHandActivity.this, TaskType.DOWNLOAD_TEST_UNIT);
                  //  pDialog.hide();
                }else{
                   // pDialog.hide();
                    new ToastManager(UnitInHandActivity.this).error("Network Error");
                }
                return true;
            }
            case android.R.id.home:{
                finish();
                return true;
            }

        }

        return(super.onOptionsItemSelected(item));
    }



    @Override
    public void onSuccess(String result, TaskType type) {

            switch (type){
               case DOWNLOAD_TEST_UNIT:{
                  // pDialog.hide();
                   syncCount++;
                   CheckSyncCount();
                try{
                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //Deleting past Data.
                    int iDelete = new UnitInHandDAO(this).delete();
                    //Log.v("Deleted",""+iDelete);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        UnitInHand unitHand = new UnitInHand();
                        JSONObject row = (JSONObject) jsonArray.get(i);

                        unitHand.setItemDescription(row.getString("itemDescription"));
                        unitHand.setItemCode(row.getString("itemCode"));
                        unitHand.setSerial(row.getString("serial"));
                        unitHand.setItemCategory(row.getString("itemCategory"));

                        int iStatus = new UnitInHandDAO(getApplicationContext()).insert(unitHand);
                        Log.v("Inserted",""+iStatus);

                    }
                    setAdapter("LTEU");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                }break;

                case TEST_TO_REFURBISH :{
                    try {
                        // {"Data":[{"serialNo":"696BCC62","status":"1"}],"ID":"200"}
                        JSONObject jsonObject = new JSONObject(result.toString());

                        final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        final String id = jsonObject.getString("ID");

                        if(id.equals("200")){
                            final StringBuilder serialNo = new StringBuilder();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject row = (JSONObject) jsonArray.get(i);
                                int iStatus = row.getInt("status");
                                if(iStatus==1){
                                    if(jsonArray.length()==1){
                                        serialNo.append(row.getString("serialNo"));
                                    }else{
                                        serialNo.append(row.getString("serialNo")).append(",");
                                    }
                                }
                            }

                            if(new ConnectionDetector(UnitInHandActivity.this).isConnectingToInternet()) {

                                new SyncData(UnitInHandActivity.this).getUnitInHand(UnitInHandActivity.this, TaskType.DOWNLOAD_TEST_UNIT);

                                DownloadUnits();

                            }


//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    alerts.SuccessAlert("Successfully Changed "+serialNo);
//                                }
//                            }, 5000);
                           // setAdapter("LTEU");

                        }else{
                           // pDialog.hide();
                            alerts.ErrorAlert("Failed to Change");
                        }

                       // finish();
                    }catch (Exception e){
                        e.printStackTrace();
                        alerts.ErrorAlert("Failed to Change because "+e.getMessage());
                    }
                }break;

                case FETCH_ACCEPTED_ISSUE_DETAILS:{

                    //Log.v("Results",result);
                    syncCount++;
                    CheckSyncCount();
                    try {

                        JSONObject jsonObject = new JSONObject(result.toString());
                        final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            IssuedDetails issuedDetails = new IssuedDetails();
                            JSONObject data = (JSONObject) jsonArray.get(i);
                            //PENDING JOBS DETAILS
                            issuedDetails.setPID_ITEMISSUE_NO(data.getString("ItemIssueNo"));
                            issuedDetails.setPID_ITEM_ISSUED_DATE(data.getString("ItemIssuedDate"));
                            issuedDetails.setPID_ITEM_ISSUE_REMARK(data.getString("ItemIssueRemark"));
                            issuedDetails.setPID_FROM_LOCATION_TYPE(data.getString("FromLocationType"));
                            issuedDetails.setPID_FROM_LOCATION(data.getString("FromLocation"));
                            issuedDetails.setPID_ITEM_ISSUE_STATUS(data.getString("ItemIssueStatus"));
                            issuedDetails.setPID_STOCK_TYPE(data.getString("StockType"));
                            issuedDetails.setPID_TOTAL_UNITS(data.getString("TotalUnits"));

                            JSONArray jsonArrayDet = new JSONArray(data.getString("Details"));

                            //    Log.v("DET-Array",jsonArrayDet.toString());

                            //Deleting past Data.
                            int iDelete = new PendingIssuedMaterialDAO(this).deleteAll(issuedDetails.getPID_ITEMISSUE_NO());
                            int iDelete2 = new AcceptedIssuedMatrlDAO(this).deleteAll(issuedDetails.getPID_ITEMISSUE_NO());

                            // Log.v("Pending Issued Material",""+ iDelete);

                            for (int j = 0; j < jsonArrayDet.length(); j++) {

                                IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                                JSONObject dataDet = (JSONObject) jsonArrayDet.get(j);
                                //PENDING JOBS DETAILS
                                //    Log.v("Issue No",dataDet.toString());itemDescription
                                materialDetails.setIssueNo(dataDet.getString("IssueNo"));
                                materialDetails.setIssueType(dataDet.getString("IssueType"));
                                materialDetails.setItemType(dataDet.getString("ItemType"));
                                materialDetails.setItemCode(dataDet.getString("ItemCode"));
                                materialDetails.setItemDescription(dataDet.getString("ItemDescription"));
                                materialDetails.setIssuedQty(dataDet.getString("IssuedQty"));
                                materialDetails.setItemCategory(dataDet.getString("ItemCategory"));
                                materialDetails.setSerial(dataDet.getString("Serial"));

                                int iStatusDet = new PendingIssuedMaterialDAO(this).insertORupdate(materialDetails);
                            }

                            int iStatus = new IssuedDetailsDAO(getApplicationContext()).insertORupdate(issuedDetails);
                            int iStatusDet2 = new AcceptedIssuedMatrlDAO(this).insertORupdate(issuedDetails,"1");

                        }


                    } catch (JSONException e){
                        Log.e("Error",e.toString());
                        e.printStackTrace();
                    }


                } break;


                case FETCH_CDMA_ISSUE_DETAILS:{

                    // Log.v("CDMA Results",result);
                    pDialog.hide();
                    syncCount++;
                    CheckSyncCount();
                    try {

                        JSONObject jsonObject = new JSONObject(result.toString());
                        final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            IssuedDetails issuedDetails = new IssuedDetails();
                            JSONObject data = (JSONObject) jsonArray.get(i);
                            //PENDING JOBS DETAILS
                            issuedDetails.setPID_ITEMISSUE_NO(data.getString("ItemIssueNo"));
                            issuedDetails.setPID_ITEM_ISSUED_DATE(data.getString("ItemIssuedDate"));
                            issuedDetails.setPID_ITEM_ISSUE_REMARK(data.getString("ItemIssueRemark"));
                            issuedDetails.setPID_FROM_LOCATION_TYPE(data.getString("FromLocationType"));
                            issuedDetails.setPID_FROM_LOCATION(data.getString("FromLocation"));
                            issuedDetails.setPID_ITEM_ISSUE_STATUS(data.getString("ItemIssueStatus"));
                            issuedDetails.setPID_STOCK_TYPE(data.getString("StockType"));
                            issuedDetails.setPID_TOTAL_UNITS(data.getString("TotalUnits"));

                            JSONArray jsonArrayDet = new JSONArray(data.getString("Details"));

                            //       Log.v("DET-Array",jsonArrayDet.toString());

                            //Deleting past Data.
                            int iDelete = new PendingIssuedMaterialDAO(this).deleteAll(issuedDetails.getPID_ITEMISSUE_NO());

                            //Log.v("Pending Issued Material",""+ iDelete);

                            for (int j = 0; j < jsonArrayDet.length(); j++) {

                                IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                                JSONObject dataDet = (JSONObject) jsonArrayDet.get(j);
                                //PENDING JOBS DETAILS
                                //         Log.v("Issue No",dataDet.toString());
                                materialDetails.setIssueNo(dataDet.getString("IssueNo"));
                                materialDetails.setIssueType(dataDet.getString("IssueType"));
                                materialDetails.setItemType(dataDet.getString("ItemType"));
                                materialDetails.setItemCode(dataDet.getString("ItemCode"));
                                materialDetails.setItemDescription(dataDet.getString("ItemDescription"));
                                materialDetails.setIssuedQty(dataDet.getString("IssuedQty"));
                                materialDetails.setItemCategory(dataDet.getString("ItemCategory"));
                                materialDetails.setSerial(dataDet.getString("Serial"));

                                int iStatusDet = new PendingIssuedMaterialDAO(this).insertORupdate(materialDetails);
                            }

                            int iStatus = new AcceptedIssuedMatrlDAO(this).insertORupdateCDMA(issuedDetails);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } break;

            }


    }

    private void DownloadUnits() {
            new SyncData(UnitInHandActivity.this)
                    .fetchAcceptedIssueDetails(UnitInHandActivity.this, TaskType.FETCH_ACCEPTED_ISSUE_DETAILS);

           new SyncData(UnitInHandActivity.this).fetchCDMAIssueDetails(UnitInHandActivity.this, TaskType.FETCH_CDMA_ISSUE_DETAILS);

           CheckSyncCount();
    }

    private void CheckSyncCount() {
        if(syncCount==3){
            alerts.SuccessAlert("Successfully Changed ");
        }
        System.out.println(syncCount);
    }


    @Override
    public void onError(String message, TaskType type) {

        switch (type) {
            case TEST_TO_REFURBISH: {
              //  pDialog.hide();
                alerts.ErrorAlert("Failed To change to Refurbish " + message);
                CheckSyncCount();
            }
            break;

            case DOWNLOAD_TEST_UNIT: {

                try {
                    if(message.equals("No Data Found")){
                        alerts.SuccessAlert(message);
                    }else{
                        alerts.ErrorAlert("Download Failed " + message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            break;
            case FETCH_CDMA_ISSUE_DETAILS: {
                pDialog.hide();
                syncCount++;
                CheckSyncCount();
               // pDialog.hide();
               //alerts.ErrorAlert("Failed To change to Refurbish " + message);
            }
            break;
            case FETCH_ACCEPTED_ISSUE_DETAILS: {
                syncCount++;
                CheckSyncCount();
                //alerts.ErrorAlert("Failed To change to Refurbish " + message);
            }
            break;

        }
    }

    @Override
    public void onSuccess(String id, TaskType type, String faultType) {

    }
}
