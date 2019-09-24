package com.lk.lankabell.fault.control;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.adapter.SharedPreferencesHelper;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.AcceptedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.CompetedJobDAO;
import com.lk.lankabell.fault.control.Data.IssuedDetailsDAO;
import com.lk.lankabell.fault.control.Data.PendingFaultDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.Data.RejectDAO;
import com.lk.lankabell.fault.control.Data.RejectedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.UnitInHandDAO;
import com.lk.lankabell.fault.model.IssuedDetails;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.model.UnitInHand;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyJobService extends JobService implements VolleyCallback{

    private static final String TAG = MyJobService.class.getSimpleName();

    BackgroundService service;

    @Override
    public boolean onStartJob(final JobParameters job) {
        // Do some work here

        service = new BackgroundService(){
            @Override
            protected void onPostExecute(String s) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new SyncData(getApplicationContext()).getPendingFaults(MyJobService.this, TaskType.GET_PENDING_JOBS,"1" );
                }

                //Toast.makeText(MyJobService.this, "Result :"+s, Toast.LENGTH_SHORT).show();
                jobFinished(job,false);
            }
        };
        service.execute();

        return true; // Answers the question: "Is there still work going on?"
    }
    private void UpdateQuotaStatus(String fault_id) {
      new SyncData(MyJobService.this).CheckQuotaStatus(MyJobService.this,TaskType.CHECK_QUOTA,fault_id);
    }
    private void UpdateAcceptQuotaStatus(String fault_id) {
        System.out.println("* fault_id "+fault_id);
      new SyncData(MyJobService.this).CheckQuotaStatus(MyJobService.this,TaskType.CHECK_QUOTA_A,fault_id);
    }
    @Override
    public boolean onStopJob(final JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    private void updateLteQuota(String Day, String Night,String fault_id) {
        if(new PendingFaultDAO(MyJobService.this).updateQuota(Day,Night,fault_id)>0){
            System.out.println("* success fully LteQuota updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }
    }
    private void updateLteQuota_A(String Day, String Night,String fault_id) {
        if(new AcceptFaultDAO(MyJobService.this).updateQuota_a(Day,Night,fault_id)>0){
            System.out.println("* success fully updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }
    }

    @Override
    public void onSuccess(String Data, TaskType type, String fault_id) {

        switch(type) {
            case CHECK_QUOTA: {

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_PENDING_JOBS,  DateManager.getCurrentDate());
               // Log.v("Results", Data);
                switch (Data) {
                    case "Both Day & Night Data Balances are ok.": {
                        updateLteQuota("100", "100",fault_id);
                    }
                    break;
                    case "Night Data Balance is 0.": {
                        updateLteQuota("100", "0",fault_id);
                    }
                    break;
                    case "Day Data Balances is 0.": {
                        updateLteQuota("0", "100",fault_id);
                    }
                    break;
                    case "Both Day & Night Data Balances are 0.": {
                        updateLteQuota("0", "0",fault_id);
                    }
                    break;
                }
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new SyncData(getApplication())
                            .fetchPendingIssueDetails(MyJobService.this, TaskType.FETCH_PENDING_ISSUE_DETAILS);
                }

            }break;
            case CHECK_QUOTA_A: {

                Log.v("Results", Data);
                switch (Data) {
                    case "Both Day & Night Data Balances are ok.": {
                        updateLteQuota_A("100", "100",fault_id);
                    }
                    break;
                    case "Night Data Balance is 0.": {
                        updateLteQuota_A("100", "0",fault_id);
                    }
                    break;
                    case "Day Data Balances is 0.": {
                        updateLteQuota_A("0", "100",fault_id);
                    }
                    break;
                    case "Both Day & Night Data Balances are 0.": {
                        updateLteQuota_A("0", "0",fault_id);
                    }
                    break;
                }

            }
            break;
            //Next Call


        }


    }
    @Override
    public void onSuccess(String result, TaskType type) {

        switch (type){

            case GET_PENDING_JOBS: {

             //   Log.v("Results",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //Deleting past Data.
                    //int iDelete = new PendingFaultDAO(this).deleteAll();
                    //Log.v("Deleted",""+iDelete);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        PendingFaults faults = new PendingFaults();
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        //PENDING JOBS DETAILS
                        faults.setPF_REQUESTID(data.getString("RequestId"));
                        faults.setPF_REQUESTBATCHID(data.getString("RequestBatchId"));
                        faults.setPF_REQUESTTOREFID(data.getString("RequestToRefId"));
                        faults.setPF_REQUESTTONAME(data.getString("RequestToName"));
                        faults.setPF_REQUESTTOCONTACT(data.getString("RequestToContact"));
                        faults.setPF_REQUESTTOADD1(data.getString("RequestToAdd1"));
                        faults.setPF_REQUESTTOADD2(data.getString("RequestToAdd2"));
                        faults.setPF_REQUESTTOADD3(data.getString("RequestToAdd3"));
                        faults.setPF_REQUESTASSIGNEDTO(data.getString("RequestAssignedTo"));
                        faults.setPF_REQUESTASSIGNEDDATE(data.getString("RequestAssignedDate"));
                        faults.setPF_STATUS(data.getString("Status"));
                        faults.setPF_PRIORITY(data.getString("Priority"));
                        faults.setPF_REQUESTTYPE(data.getString("RequestType"));
                        faults.setPF_REQUESTSUBTYPE(data.getString("RequestSubType"));
                        faults.setPF_REQUESTCATEGORY(data.getString("RequestCategory"));
                        faults.setPF_ISACCEPT(data.getString("IsAccept"));
                        faults.setPF_REQUESTTOLOCATION(data.getString("RequestToLocation"));
                        faults.setPF_SERVICETYPE(data.getString("ServiceType"));
                        faults.setPF_CUSTOMERCATEGORY(data.getString("CustomerCategory"));
                        faults.setPF_CUSTOMERRATINGS(data.getString("CustomerRatings"));
                        faults.setPF_CUSTOMERREMARKS(data.getString("CustomerRemarks"));
                        faults.setPF_DIRECTION(data.getString("Direction"));

                        int iStatus = new PendingFaultDAO(getApplicationContext()).insertORupdate(faults);
                   //     Log.v("Inserted",""+iStatus);


                        //faultssA.add(faults);
                    }
                    //acceptedOrderAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_PENDING_JOBS,  DateManager.getCurrentDate());

                ArrayList<PendingFaults> list = new ArrayList<>();
                list = new PendingFaultDAO(MyJobService.this).getLtePendingFaults();
                if(list.size()>0){

                    for(int i =0;i<list.size();i++){
                        UpdateQuotaStatus(list.get(i).getPF_REQUESTID());
                    }

                }else{
                    if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                        new SyncData(getApplication())
                                .fetchPendingIssueDetails(MyJobService.this, TaskType.FETCH_PENDING_ISSUE_DETAILS);
                    }
                    System.out.println("* Pending Fault Error");
                }



            } break;

            case FETCH_PENDING_ISSUE_DETAILS:{

            //    Log.v("Results",result);

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

                       // Log.v("Pending Issued Material",""+ iDelete);

                        for (int j = 0; j < jsonArrayDet.length(); j++) {

                            IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                            JSONObject dataDet = (JSONObject) jsonArrayDet.get(j);
                            //PENDING JOBS DETAILS
                        //    Log.v("Issue No",dataDet.toString());
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
                   //     Log.v("Inserted",""+ iStatus);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_FETCH_PENDING_ISSUE_DETAILS,  DateManager.getCurrentDate());


                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    new SyncData(getApplicationContext()).getPendingFaults(MyJobService.this, TaskType.GET_ACCEPTED_JOBS,"2" );
                }


            } break;
            case GET_ACCEPTED_JOBS:{
                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //Deleting past Data.
                    //int iDelete = new PendingFaultDAO(this).deleteAll();
                    //Log.v("Deleted",""+iDelete);
                    ArrayList<PendingFaults> AlreadyAcceptedFault = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        PendingFaults faults = new PendingFaults();
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        //PENDING JOBS DETAILS
                        faults.setPF_REQUESTID(data.getString("RequestId"));
                        faults.setPF_REQUESTBATCHID(data.getString("RequestBatchId"));
                        faults.setPF_REQUESTTOREFID(data.getString("RequestToRefId"));
                        faults.setPF_REQUESTTONAME(data.getString("RequestToName"));
                        faults.setPF_REQUESTTOCONTACT(data.getString("RequestToContact"));
                        faults.setPF_REQUESTTOADD1(data.getString("RequestToAdd1"));
                        faults.setPF_REQUESTTOADD2(data.getString("RequestToAdd2"));
                        faults.setPF_REQUESTTOADD3(data.getString("RequestToAdd3"));
                        faults.setPF_REQUESTASSIGNEDTO(data.getString("RequestAssignedTo"));
                        faults.setPF_REQUESTASSIGNEDDATE(data.getString("RequestAssignedDate"));
                        faults.setPF_STATUS(data.getString("Status"));
                        faults.setPF_PRIORITY(data.getString("Priority"));
                        faults.setPF_REQUESTTYPE(data.getString("RequestType"));
                        faults.setPF_REQUESTSUBTYPE(data.getString("RequestSubType"));
                        faults.setPF_REQUESTCATEGORY(data.getString("RequestCategory"));
                        faults.setPF_ISACCEPT(data.getString("IsAccept"));
                        faults.setPF_REQUESTTOLOCATION(data.getString("RequestToLocation"));
                        faults.setPF_SERVICETYPE(data.getString("ServiceType"));
                        faults.setPF_CUSTOMERCATEGORY(data.getString("CustomerCategory"));
                        faults.setPF_CUSTOMERRATINGS(data.getString("CustomerRatings"));
                        faults.setPF_CUSTOMERREMARKS(data.getString("CustomerRemarks"));
                        faults.setPF_DIRECTION(data.getString("Direction"));

                        int iStatus1 = new PendingFaultDAO(getApplicationContext()).insertORupdate(faults);
                        int iStatus2 = new AcceptFaultDAO(getApplicationContext()).insertORupdate(faults,"1");
                        if(iStatus2 >0){
                            AlreadyAcceptedFault.add(faults);
                        }
                        //     Log.v("Inserted",""+iStatus);
                    }

                    if(AlreadyAcceptedFault.size()>0){
                        for(PendingFaults pf:  AlreadyAcceptedFault){
                            System.out.println("* getPF_REQUESTTYPE  "+pf.getPF_REQUESTTYPE()+" getPF_REQUESTID "+pf.getPF_REQUESTID());
                            if(pf.getPF_REQUESTTYPE().equals("L")){
                                UpdateAcceptQuotaStatus(pf.getPF_REQUESTID());
                            }
                        }
                    }
                    ArrayList<PendingFaults> list = new ArrayList<>();
                    list = new PendingFaultDAO(MyJobService.this).getLtePendingFaults();
                    if(list.size()>0) {
                        for (int i = 0; i < list.size(); i++) {
                            UpdateQuotaStatus(list.get(i).getPF_REQUESTID());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {


                    JSONArray acceptMappers = new AcceptFaultDAO(MyJobService.this).getAcceptedFaults();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", acceptMappers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SyncData(getApplicationContext())
                            .syncAcceptedData(MyJobService.this, TaskType.UPLOAD_ACCEPTED_JOBS, object);
                }

            }break;

            case UPLOAD_ACCEPTED_JOBS:{

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i ++) {

                        JSONObject data = (JSONObject) jsonArray.get(i);
                        int iStatus = data.getInt("status");
                        if(iStatus >=1){
                            int iUpdated = new AcceptFaultDAO(MyJobService.this)
                                    .updateIsSynced(data.getString("requestId"));
                         //   Log.v("Updated ",""+iUpdated);
                        }

                    }
                    ArrayList<PendingFaults> list = new ArrayList<>();
                    list = new PendingFaultDAO(MyJobService.this).getLtePendingFaults();
                    if(list.size()>0) {
                        for (int i = 0; i < list.size(); i++) {
                            UpdateQuotaStatus(list.get(i).getPF_REQUESTID());
                        }
                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_ACCEPTED_JOBS,  DateManager.getCurrentDate());

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    JSONArray rejectedList = new RejectDAO(MyJobService.this).getRejectedFaults();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                 //   Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(getApplicationContext())
                            .syncRejectedData(MyJobService.this, TaskType.UPLOAD_REJECTED_JOBS,object);

                }


            } break;

            case UPLOAD_REJECTED_JOBS:{
                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_REJECTED_JOBS,  DateManager.getCurrentDate());

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    //JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectedUnits();
                    JSONArray rejectedList = new AcceptedIssuedMatrlDAO(MyJobService.this).getAcceptedUnits();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                //    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(getApplicationContext())
                            .syncAcceptedUnitsData(MyJobService.this, TaskType.UPLOAD_ACCEPTED_UNITS,object);

                }

            } break;

            case UPLOAD_ACCEPTED_UNITS:{

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_ACCPTED_UNITS,  DateManager.getCurrentDate());

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    //JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectedUnits();
                    JSONArray rejectedList = new RejectedIssuedMatrlDAO(MyJobService.this).getRejectUnits();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

               //     Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(getApplicationContext())
                            .syncRejectedUnitsData(MyJobService.this, TaskType.UPLOAD_REJECTED_UNITS,object);


                }

            } break;

            case UPLOAD_REJECTED_UNITS:{

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_REJECTED_UNITS,  DateManager.getCurrentDate());

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    JSONArray rejectedList = new CompetedJobDAO(MyJobService.this).getAllCompletedUnsunced();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                //    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(MyJobService.this)
                            .syncCompletedJobs(MyJobService.this, TaskType.UPLOAD_COMPLETED_JOBS,object);

                }

            } break;

            case UPLOAD_COMPLETED_JOBS:{

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_COMPLETED_JOBS,  DateManager.getCurrentDate());

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    try {

                        JSONArray VisitLogList = new CompetedJobDAO(MyJobService.this).getAllVisitLogArrayUnSync();

                        JSONObject object = new JSONObject();

                        try {
                            object.put("data", VisitLogList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                  //      Log.v("Json ",object.toString().replace("\\",""));

                         new SyncData(MyJobService.this)
                                .updateVisitLocation(MyJobService.this, TaskType.VIST_LOG,object);

                    } catch (Exception e) {
                   //     Log.e("Error", e.toString());
                        e.printStackTrace();
                    }

                }

            } break;

            case VIST_LOG:{

                new SharedPreferencesHelper().setLocalSharedPreference(MyJobService.this,
                        AppContoller.SP_LAST_SYNC_VISIT_LOG,  DateManager.getCurrentDate());

                new SyncData(MyJobService.this)
                        .getUnitInHand(MyJobService.this, TaskType.DOWNLOAD_TEST_UNIT);

            } break;

            case DOWNLOAD_TEST_UNIT:{

                try {

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
                        //     Log.v("Inserted",""+iStatus);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } break;


        }

    }

    @Override
    public void onError(String message, TaskType type) {

        switch (type){
            case CHECK_QUOTA_A:{
                System.out.println("* mess CHECK_QUOTA_A "+message);
            }break;
            case CHECK_QUOTA:{
                System.out.println("* mess CHECK_QUOTA "+message);
            }break;
//            case CHECK_QUOTA:{
//                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
//                    new SyncData(getApplication())
//                            .fetchPendingIssueDetails(MyJobService.this, TaskType.FETCH_PENDING_ISSUE_DETAILS);
//                }
//            }break;


        }
    }

    public static class BackgroundService extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            return "Background Task Job";
        }
    }

}
