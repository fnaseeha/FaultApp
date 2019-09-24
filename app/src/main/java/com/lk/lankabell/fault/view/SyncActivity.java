package com.lk.lankabell.fault.view;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.adapter.SharedPreferencesHelper;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.AcceptedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.CompetedJobDAO;
import com.lk.lankabell.fault.control.Data.IssuedDetailsDAO;
import com.lk.lankabell.fault.control.Data.LocationDAO;
import com.lk.lankabell.fault.control.Data.PendingFaultDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.Data.RejectDAO;
import com.lk.lankabell.fault.control.Data.RejectedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.MyJobService;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.model.IssuedDetails;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.LocationDetails;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import im.delight.android.location.SimpleLocation;

public class SyncActivity extends AppCompatActivity implements VolleyCallback {

    Button btn_pending_jobs,btn_upload_accepted, btnIssueDetailSyncDate,btn_upload_rejected,btn_sync_accepted_units,btn_sync_rejected_units,btn_sync_completeJobs,
            btnFetchCDMAIssueDetails,btn_sync_visit_log;

    TextView tvSyncPendingFaultOn,tvAcceptedUnsyncCount,tvAcceptedUnsyncDate,tvIssueDetailSyncDate,tvRejectedUnsyncCount,tvRejectedUnsyncDate,tvAccptedUnitsUnsynced,
            tvAccptedUnitsUnsyncedDate,tvRejectedUnitsUnsynced,tvRejectedUnitsUnsyncedDate,tvCompleteJobunsyncd,tvCompleteJobunsyncdDate,
            tvFetchCDMAIssueDetailsSyncDate,tvVisitLogUnsyncd,tvVisitLogDate;

    ArrayList<PendingFaults> acceptedList;
    ArrayList<PendingFaults> rejectedList;

   // ArrayList<IssuedDetails> issuedDetailsArrayList;

    int iArraySize = 0;
    int iCount = 1;
    Alerts alerts;
    double lattitude,longitude;
    private SimpleLocation location;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(SyncActivity.this);
        pDialog.setCancelable(false);
        alerts = new Alerts(SyncActivity.this);
        location = new SimpleLocation(this);
        setContentView(R.layout.activity_sync_layout);
        getSupportActionBar().setTitle("MANUAL SYNC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CheckPendingCount();
        //LAST SYNC COUNT, DATE & TIME
        tvSyncPendingFaultOn = (TextView) findViewById(R.id.tvSyncPendingFaultOn);
        tvAcceptedUnsyncDate = (TextView) findViewById(R.id.tvAcceptedUnsyncDate);
        tvAcceptedUnsyncCount = (TextView) findViewById(R.id.tvAcceptedUnsyncCount);
        tvIssueDetailSyncDate = (TextView) findViewById(R.id.tvIssueDetailSyncDate);
        tvRejectedUnsyncCount = (TextView) findViewById(R.id.tvRejectedUnsyncCount);
        tvRejectedUnsyncDate = (TextView) findViewById(R.id.tvRejectedUnsyncDate);
        tvAccptedUnitsUnsynced = (TextView) findViewById(R.id.tvAccptedUnitsUnsynced);
        tvAccptedUnitsUnsyncedDate = (TextView) findViewById(R.id.tvAccptedUnitsUnsyncedDate);
        tvFetchCDMAIssueDetailsSyncDate = (TextView) findViewById(R.id.tvFetchCDMAIssueDetailsSyncDate);

        tvRejectedUnitsUnsynced= (TextView) findViewById(R.id.tvRejectedUnitsUnsynced);
        tvRejectedUnitsUnsyncedDate = (TextView) findViewById(R.id.tvRejectedUnitsUnsyncedDate);
        tvCompleteJobunsyncd = (TextView) findViewById(R.id.tvCompleteJobunsyncd);
        tvCompleteJobunsyncdDate = (TextView) findViewById(R.id.tvCompleteJobunsyncdDate);
        tvVisitLogDate = (TextView) findViewById(R.id.tvVisitLogDate);
        tvVisitLogUnsyncd = (TextView) findViewById(R.id.tvVisitLogUnsyncd);



        //CAllING FETCH_CDMA_ISSUE_DETAILS
        btnFetchCDMAIssueDetails = (Button) findViewById(R.id.btnFetchCDMAIssueDetails);
        btnFetchCDMAIssueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    pDialog.show();
                    pDialog.setMessage("Please Wait ...");
                    new SyncData(SyncActivity.this).fetchCDMAIssueDetails(SyncActivity.this, TaskType.FETCH_CDMA_ISSUE_DETAILS);

                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });

        //CAllING DOWNLOAD PENDING JOBS
        btn_pending_jobs = (Button) findViewById(R.id.btn_pending_jobs);
        btn_pending_jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    JSONArray rejectedList = new RejectDAO(SyncActivity.this).getRejectedFaults();
                    if(rejectedList.length()>0){
                        try {
                            JSONObject object = new JSONObject();

                            object.put("data", rejectedList);

                            Log.v("Json ", object.toString().replace("\\", ""));

                            new SyncData(SyncActivity.this)
                                    .syncRejectedData(SyncActivity.this, TaskType.UPLOAD_REJECTED_JOBS_WHILE_PENDING, object);

                            new ToastManager(SyncActivity.this).warning("Please Sync Rejected Faults");

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_JOBS, "1");
                    }

                } else {

                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });


        //UPLOADING ACCEPTED JOBS
        btn_upload_accepted = (Button) findViewById(R.id.btn_upload_accepted);
        btn_upload_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    JSONArray acceptMappers = new AcceptFaultDAO(SyncActivity.this).getAcceptedFaults();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", acceptMappers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .syncAcceptedData(SyncActivity.this, TaskType.UPLOAD_ACCEPTED_JOBS,object);

                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });

        //UPLOADING REJECTED JOBS
        btn_upload_rejected = (Button) findViewById(R.id.btn_upload_rejected);
        btn_upload_rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    try {
                       // pDialog.setTitle("Please Wait");
               //   showDialog();
                    JSONArray rejectedList = new RejectDAO(SyncActivity.this).getRejectedFaults();

                    JSONObject object = new JSONObject();


                        object.put("data", rejectedList);


                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .syncRejectedData(SyncActivity.this, TaskType.UPLOAD_REJECTED_JOBS,object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    //   hideDialog();
                    }
                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }



            }
        });


        //UPLOADING ACCEPTED UNITS
        btn_sync_accepted_units = (Button) findViewById(R.id.btn_sync_accepted_units);
        btn_sync_accepted_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    //JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectedUnits();
                    JSONArray accetedUnitList = new AcceptedIssuedMatrlDAO(SyncActivity.this).getAcceptedUnits();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", accetedUnitList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .syncAcceptedUnitsData(SyncActivity.this, TaskType.UPLOAD_ACCEPTED_UNITS,object);

                } else {
                    //new ConnectionDetector(getApplication()).showSettingsAlert();
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }



            }
        });
        //UPLOADING REJECTED UNITS
        btn_sync_rejected_units = (Button) findViewById(R.id.btn_sync_rejected_units);
        btn_sync_rejected_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    //JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectedUnits();
                    JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectUnits();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .syncRejectedUnitsData(SyncActivity.this, TaskType.UPLOAD_REJECTED_UNITS,object);


                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }



            }
        });


        //UPLOADING COMPLETE JOBS
        btn_sync_completeJobs = (Button) findViewById(R.id.btn_sync_completeJobs);
        btn_sync_completeJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    JSONArray rejectedList = new CompetedJobDAO(SyncActivity.this).getAllCompletedUnsunced();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", rejectedList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .syncCompletedJobs(SyncActivity.this, TaskType.UPLOAD_COMPLETED_JOBS,object);

                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });
        //UPLOADING VISIT LOG
        btn_sync_visit_log = (Button) findViewById(R.id.btn_sync_visit_log);
        btn_sync_visit_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    JSONArray visitLogList = new CompetedJobDAO(SyncActivity.this).getAllVisitLogArrayUnSync();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", visitLogList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ",object.toString().replace("\\",""));

                    new SyncData(SyncActivity.this)
                            .updateVisitLocation(SyncActivity.this, TaskType.VIST_LOG,object);

                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });

        //DOWNLOAD FETCH PENDING ISSUE DETAILS
        btnIssueDetailSyncDate = (Button) findViewById(R.id.btnIssueDetailSyncDate);
        btnIssueDetailSyncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    new SyncData(SyncActivity.this)
                            .fetchPendingIssueDetails(SyncActivity.this, TaskType.FETCH_PENDING_ISSUE_DETAILS);

                } else {
                    new ConnectionDetector(SyncActivity.this).showSettingsAlert();
                }
            }
        });

        //Update date and icons
        updateDateAndIcon();

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    //
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void CheckPendingCount() {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
            new SyncData(getApplicationContext()).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_COUNT,"1" );
        }
    }

    public void getLocations(){
        getLocation();
        location.beginUpdates();

        location.setListener(new SimpleLocation.Listener() {
            @Override
            public void onPositionChanged() {
                getLocation();
                System.out.println("* location changed "+lattitude+" "+longitude);

                saveLocation();
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2000);

        } else {
            getLocations();
            // permission has been granted, continue as usual

        }
    }

    private void saveLocation() {
        System.out.println("* save Location");
        //location latitude , longitute,
        LocationDetails locationDetails = new LocationDetails();
        locationDetails.setLATITUDE(location.getLatitude());
        locationDetails.setLONGITUDE(location.getLongitude());
        locationDetails.setEMPNO(AppContoller.getEMPNO(this));
        int loc_count = new LocationDAO(this).getLocationCount();
        if(loc_count>50){
            int delete = new LocationDAO(this).delete();
            System.out.println("* deleted "+delete);
        }
        int insert = new LocationDAO(this).insert(locationDetails);

        System.out.println("* inserted "+insert);
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 2000) {
            getLocations();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.endUpdates();
    }
    private void getLocation() {
        lattitude  = location.getLatitude();
        longitude = location.getLongitude();
        // System.out.println("* location changed "+lattitude+" "+longitude);
        saveLocation();
    }


    private boolean CheckGPS() {

        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            System.out.println("* provider " + provider);

//        int j = getLocationMode(getApplicationContext());

            if(provider.equalsIgnoreCase("gps") ){
                alerts.showLocationAlert("Your Location Mode is not High priority.\nPlease Set Location to " +
                        "High Priority");
                return false;
            }else if(provider.equalsIgnoreCase("network")){
                alerts.showLocationAlert("Your Location Mode is not High priority.\nPlease Set Location to " +
                        "High Priority");
                return false;
            }else if(provider.contains("network,gps")||provider.contains("gps,network")){
                System.out.println("* gps is enable");
                return true;
            }else{
                alerts.showLocationAlert("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void updateDateAndIcon(){

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateCurrentDate = sdf.parse(DateManager.getCurrentDate());
            /**
             * -------------------DOWNLOADING ----------------
             */

               // --------- PENDING JOBS --------
            {
               String lastSaveDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_PENDING_JOBS);

                Date date2 = null;

                if (lastSaveDate.equals("")) {
                    date2 = sdf.parse("2019-01-01");
                } else {
                    date2 = sdf.parse(lastSaveDate);
                }

                tvSyncPendingFaultOn.setText("Last update on " + lastSaveDate);

                if (dateCurrentDate.compareTo(date2) == 0) {
                    btn_pending_jobs.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_pending_jobs.setBackgroundResource(R.drawable.sync_error);
                }
            }

            //---------FETCH PENDING ISSUE DETAILS --------
            {
                String lastSaveDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_FETCH_PENDING_ISSUE_DETAILS);
                Date date2 = null;
                if (lastSaveDate.equals("")) {
                    date2 = sdf.parse("2019-01-01");
                } else {
                    date2 = sdf.parse(lastSaveDate);
                }
                tvIssueDetailSyncDate.setText("Last update on " + lastSaveDate);
                if (dateCurrentDate.compareTo(date2) == 0) {
                    btnIssueDetailSyncDate.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btnIssueDetailSyncDate.setBackgroundResource(R.drawable.sync_error);
                }
            }

            {
                String lastSaveDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_FETCH_CDMA_ISSUE_DETAILS);
                Date date2 = null;
                if (lastSaveDate.equals("")) {
                    date2 = sdf.parse("2019-01-01");
                } else {
                    date2 = sdf.parse(lastSaveDate);
                }
                tvFetchCDMAIssueDetailsSyncDate.setText("Last update on " + lastSaveDate);
                if (dateCurrentDate.compareTo(date2) == 0) {
                    btnFetchCDMAIssueDetails.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btnFetchCDMAIssueDetails.setBackgroundResource(R.drawable.sync_error);
                }
            }

            /**
              * -------------------------UPLOADING ------------------------
             */
                // --------- ACCEPTED JOBS  ---------
            {
                // GET ALL ACCEPTED LIST
                acceptedList = new AcceptFaultDAO(SyncActivity.this).getAllAcceptedToSync();

                tvAcceptedUnsyncCount.setText("Un-synced data - "
                                        + new DecimalFormat("00").format((acceptedList.size())));

                String LastAccepetedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_ACCEPTED_JOBS);

                tvAcceptedUnsyncDate.setText(LastAccepetedSyncDate);

                if (acceptedList.size() == 0) {

                    btn_upload_accepted.setBackgroundResource(R.drawable.sync_success);

                } else {

                    btn_upload_accepted.setBackgroundResource(R.drawable.sync_error);

                }
            }

            // --------- REJECTED JOBS  ---------
            {
                // GET ALL REJECTED LIST
                rejectedList = new RejectDAO(SyncActivity.this).getAllRejected();

                tvRejectedUnsyncCount.setText("Un-synced data - "
                        + new DecimalFormat("00").format( rejectedList.size()));

                String LastRejectedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_REJECTED_JOBS);
                Date dateLastAccepeted = null;
                if (LastRejectedSyncDate.equals("")) {
                    dateLastAccepeted = sdf.parse("2019-01-01");
                } else {
                    dateLastAccepeted = sdf.parse(LastRejectedSyncDate);
                }
                tvRejectedUnsyncDate.setText(LastRejectedSyncDate);

                if (rejectedList.size() == 0) {
                    btn_upload_rejected.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_upload_rejected.setBackgroundResource(R.drawable.sync_error);
                }
            }


            // --------- ACCEPTED UNITS  ---------
            {
                //GET ALL ACCEPTED UNITS
                JSONArray acceptedUnitList = new AcceptedIssuedMatrlDAO(SyncActivity.this).getAcceptedUnits();

                tvAccptedUnitsUnsynced.setText("Un-synced data - "+ new DecimalFormat("00").format(acceptedUnitList.length()));

                String LastRejectedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_ACCPTED_UNITS);

                tvAccptedUnitsUnsyncedDate.setText(LastRejectedSyncDate);

                if (acceptedUnitList.length() == 0) {
                    btn_sync_accepted_units.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_sync_accepted_units.setBackgroundResource(R.drawable.sync_error);
                }
            }


            // --------- REJECTED UNITS  ---------
            {
                //GET ALL REJECTED UNITS

                JSONArray rejectedUnitList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectUnits();

                tvRejectedUnitsUnsynced.setText("Un-synced data - "+ new DecimalFormat("00").format(rejectedUnitList.length()));

                String LastRejectedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_REJECTED_UNITS);

                tvRejectedUnitsUnsyncedDate.setText(LastRejectedSyncDate);

                if (rejectedUnitList.length() == 0) {
                    btn_sync_rejected_units.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_sync_rejected_units.setBackgroundResource(R.drawable.sync_error);
                }

            }

            // --------- COMPLETED UNITS  ---------
            {
                //GET ALL COMPLETED UNITS

                JSONArray allCompletedUnsunced = new CompetedJobDAO(SyncActivity.this).getAllCompletedUnsunced();

                tvCompleteJobunsyncd.setText("Un-synced data - "+ new DecimalFormat("00").format(allCompletedUnsunced.length()));

                String LastCompletedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_COMPLETED_JOBS);

                tvCompleteJobunsyncdDate.setText(LastCompletedSyncDate);

                if (allCompletedUnsunced.length() == 0) {
                    btn_sync_completeJobs.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_sync_completeJobs.setBackgroundResource(R.drawable.sync_error);
                }

            }

            // --------- VISIT LOG  ---------
            {
                //GET ALL VISIT LOG

                JSONArray allVisitLogUnsunced = new CompetedJobDAO(SyncActivity.this).getAllVisitLogArrayUnSync();

                tvVisitLogUnsyncd.setText("Un-synced data - "+ new DecimalFormat("00").format(allVisitLogUnsunced.length()));

                String LastCompletedSyncDate = new SharedPreferencesHelper()
                        .getLocalSharedPreference(SyncActivity.this, AppContoller.SP_LAST_SYNC_VISIT_LOG);

                tvVisitLogDate.setText(LastCompletedSyncDate);

                if (allVisitLogUnsunced.length() == 0) {
                    btn_sync_visit_log.setBackgroundResource(R.drawable.sync_success);
                } else {
                    btn_sync_visit_log.setBackgroundResource(R.drawable.sync_error);
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
/*https://www.youtube.com/watch?v=7Jk2F8if1k8*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }
    @Override
    public void onSuccess(String Data, TaskType type, String fault_id) {

        switch(type) {
            case CHECK_QUOTA: {

                Log.v("Results", Data);
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

            }
            break;

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
        }

    }
    @Override
    public void onSuccess(String result, TaskType type) {
        switch (type){

            case GET_PENDING_COUNT:{
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                    int total_pending = jsonArray.length();
                    int local_pending = Integer.parseInt(new PendingFaultDAO(SyncActivity.this).getPendingCountOnly());

                    System.out.println("* total_pending "+total_pending);
                    System.out.println("* local_pending "+local_pending);
                    if (total_pending == local_pending) {
                        btn_pending_jobs.setBackgroundResource(R.drawable.sync_success);
                    } else {
                        btn_pending_jobs.setBackgroundResource(R.drawable.sync_error);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }break;

            case GET_PENDING_JOBS:{

              //  Log.v("Results",result);

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

                        int iStatus = new PendingFaultDAO(this).insertORupdate(faults);

                       Log.v("Inserted",""+iStatus);
                        //faultssA.add(faults);
                        if(jsonArray.length() == i+1){
                            new ToastManager(SyncActivity.this).success("Done");
                        }
                    }
                    //acceptedOrderAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateDateAndIcon();
                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_PENDING_JOBS,  DateManager.getCurrentDate());

                ArrayList<PendingFaults> list = new ArrayList<>();
                list = new PendingFaultDAO(SyncActivity.this).getLtePendingFaults();
                if(list.size()>0){
                    for(int i =0;i<list.size();i++){
                        UpdateQuotaStatus(list.get(i).getPF_REQUESTID());
                    }
                }else{

                    System.out.println("* Pending Fault Error");
                }
                new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_ACCEPTED_JOBS, "2");


                //update date and icon


            } break;

            case GET_ACCEPTED_JOBS:{

              //  Log.v("Results",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");

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

                        int iStatus = new AcceptFaultDAO(this).insertORupdate(faults,"1");
                        int iStatus1 = new PendingFaultDAO(this).insertORupdate(faults);
                        if(iStatus >0){
                            AlreadyAcceptedFault.add(faults);
                        }
                       // Log.v("Inserted",""+iStatus);
                    }

                    ArrayList<PendingFaults> list = new ArrayList<>();
                    list = new PendingFaultDAO(SyncActivity.this).getLtePendingFaults();
                    if(list.size()>0) {
                        for (int i = 0; i < list.size(); i++) {
                            UpdateQuotaStatus(list.get(i).getPF_REQUESTID());
                        }
                    }
                    if(AlreadyAcceptedFault.size()>0){
                       for(PendingFaults pf:  AlreadyAcceptedFault){
                           System.out.println("* getPF_REQUESTTYPE  "+pf.getPF_REQUESTTYPE()+" getPF_REQUESTID "+pf.getPF_REQUESTID());
                           if(pf.getPF_REQUESTTYPE().equals("L")){
                               UpdateAcceptQuotaStatus(pf.getPF_REQUESTID());
                           }
                       }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //update date and icon

            } break;

            case UPLOAD_ACCEPTED_JOBS:{

                //Log.v("UPLOAD ACCEPTED JOBS ",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i ++) {

                        JSONObject data = (JSONObject) jsonArray.get(i);
                        int iStatus = data.getInt("status");
                        if(iStatus >=1){
                            int iUpdated = new AcceptFaultDAO(SyncActivity.this)
                                                    .updateIsSynced(data.getString("requestId"));
                            Log.v("Updated ",""+iUpdated);
                        }

                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_ACCEPTED_JOBS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case UPLOAD_REJECTED_JOBS:{

             //   hideDialog();
              Log.v("UPLOAD REJECTED JOBS",result);

              try {

                  JSONObject jsonObject = new JSONObject(result.toString());
                  //  new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_JOBS, "1");
                  int id = Integer.parseInt(jsonObject.get("ID").toString());
                  final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                  if(id == 200) {

                      for (int i = 0; i < jsonArray.length(); i++) {

                          JSONObject data = (JSONObject) jsonArray.get(i);

                          String iStatus = data.getString("status");

                          if(isNumeric(iStatus)) {
                              if (Integer.parseInt(iStatus) >= 1) {

                                  int iUpdated = new RejectDAO(SyncActivity.this)
                                          .updateIsSynced(data.getString("requestId"));
                              }
                          }else{
                              if (iStatus.equalsIgnoreCase("Such Fault ID Not Exist")) {

                                  int iUpdated = new RejectDAO(SyncActivity.this)
                                          .updateIsSynced(data.getString("requestId"));

                              }
                          }

                      }
                  }else if(id == 400){

                      for (int i = 0; i < jsonArray.length(); i++) {

                          JSONObject data = (JSONObject) jsonArray.get(i);

                          String iStatus = data.getString("status");

                          if (iStatus.equalsIgnoreCase("Such Fault ID Not Exist")) {

                              int iUpdated = new RejectDAO(SyncActivity.this)
                                      .updateIsSynced(data.getString("requestId"));

                          }

                      }

                  }else{
                      System.out.println("* Error Rejected Fault");
                  }


                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_REJECTED_JOBS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case UPLOAD_REJECTED_JOBS_WHILE_PENDING:{

             //   hideDialog();
              Log.v("UPLOAD REJECTED JOBS",result);

              try {

                  JSONObject jsonObject = new JSONObject(result.toString());
                  //  new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_JOBS, "1");
                  int id = Integer.parseInt(jsonObject.get("ID").toString());
                  final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                  if(id == 200) {

                      for (int i = 0; i < jsonArray.length(); i++) {

                          JSONObject data = (JSONObject) jsonArray.get(i);

                          String iStatus = data.getString("status");

                          if(isNumeric(iStatus)) {
                              if (Integer.parseInt(iStatus) >= 1) {

                                  int iUpdated = new RejectDAO(SyncActivity.this)
                                          .updateIsSynced(data.getString("requestId"));
                              }
                          }else{
                              if (iStatus.equalsIgnoreCase("Such Fault ID Not Exist")) {

                                  int iUpdated = new RejectDAO(SyncActivity.this)
                                          .updateIsSynced(data.getString("requestId"));

                              }
                          }


                      }
                  }else if(id == 400){

                      for (int i = 0; i < jsonArray.length(); i++) {

                          JSONObject data = (JSONObject) jsonArray.get(i);

                          String iStatus = data.getString("status");

                          if (iStatus.equalsIgnoreCase("Such Fault ID Not Exist")) {

                              int iUpdated = new RejectDAO(SyncActivity.this)
                                      .updateIsSynced(data.getString("requestId"));

                          }

                      }

                  }else{
                      System.out.println("* Error Rejected Fault");
                  }
                  new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_JOBS, "1");

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_REJECTED_JOBS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case UPLOAD_ACCEPTED_UNITS:{

              Log.v("UPLOAD ACCEPTED UNITS",result);

              try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                    for (int i = 0; i < jsonArray.length(); i ++) {

                        JSONObject data = (JSONObject) jsonArray.get(i);

                        int iStatus = data.getInt("status");

                        if(iStatus >=1){

                            int iUpdated = new AcceptedIssuedMatrlDAO(SyncActivity.this)
                                                            .updateIsSynced(data.getString("requestId"));

                        }

                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_ACCPTED_UNITS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case VIST_LOG:{

            //  Log.v("VIST_LOG success",result);

              try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));

                    if(id == 200) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(i);

                            int iStatus = data.getInt("status");
                            String requestId = data.getString("requestId");

                            if (iStatus >= 1) {
                                //requestId
                                int iUpdated = new CompetedJobDAO(SyncActivity.this).updateIsVisitLogSynced(data.getString("requestId"));
              //                  System.out.println("* iUpdated "+iUpdated+" req id "+data.getString("requestId"));

                //                Log.v("requestId ", "" + requestId);
                            }

                        }
                    }else{
                        System.out.println("* visit log error with id"+id);
                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_VISIT_LOG,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case UPLOAD_REJECTED_UNITS:{

                //Log.v("UPLOAD REJECTED UNITS",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");


                    for (int i = 0; i < jsonArray.length(); i ++) {

                        JSONObject data = (JSONObject) jsonArray.get(i);

                        int iStatus = data.getInt("status");

                        if(iStatus >=1){

                            int iUpdated = new RejectedIssuedMatrlDAO(SyncActivity.this)
                                    .updateIsSynced(data.getString("requestId"));

                  //          Log.v("Updated ",""+iUpdated);
                        }

                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_REJECTED_UNITS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case UPLOAD_COMPLETED_JOBS:{

               // Log.v("UPLOAD_COMPLETED_JOBS",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));

                    if(id == 200){

                        for (int i = 0; i < jsonArray.length(); i ++) {

                            JSONObject data = (JSONObject) jsonArray.get(i);

                            String iStatus = data.getString("status");

                            if(isNumeric(iStatus)){
                                if(Integer.parseInt(iStatus) >=1){
//                            updateVisitLog(data.getString("requestId"));
//
                                    int iUpdated = new CompetedJobDAO(SyncActivity.this).updateIsSynced(data.getString("requestId"));
//
                                    //           Log.v("Updated ",""+iUpdated);
                                }
                            }else if(iStatus.equalsIgnoreCase("Such Fault ID Doesnt Exist")){
                                int iUpdated = new CompetedJobDAO(SyncActivity.this).updateIsSynced(data.getString("requestId"));
                            }


                        }
                    }else if(id == 400){

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(i);

                            String iStatus = data.getString("status");
                            System.out.println("* iStatus "+iStatus);
                            System.out.println("* requestId "+ data.getString("requestId"));
                            new ToastManager(SyncActivity.this).error(iStatus);

                            if (iStatus.equalsIgnoreCase("Such Fault ID Doesnt Exist")) {

                                int iUpdated = new CompetedJobDAO(SyncActivity.this).updateIsSynced(data.getString("requestId"));

                            }

                        }

                    }else{
                        new ToastManager(SyncActivity.this).error("Fail to Upload Completed Jobs");
                        System.out.println("* Fail UPLOAD_COMPLETED_JOBS id is "+id);
                    }


                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                    new ToastManager(SyncActivity.this).error("Fail to Upload Completed Jobs because "+e.getMessage());
                }

                new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                        AppContoller.SP_LAST_SYNC_COMPLETED_JOBS,  DateManager.getCurrentDate());

                updateDateAndIcon();

            } break;

            case FETCH_PENDING_ISSUE_DETAILS:{

                //Log.v("Results",result);

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

                        Log.v("DET-Array",jsonArrayDet.toString());

                        //Deleting past Data.
                        int iDelete = new PendingIssuedMaterialDAO(this).deleteAll(issuedDetails.getPID_ITEMISSUE_NO());

                       Log.v("Pending Issued Material",""+ iDelete);

                        for (int j = 0; j < jsonArrayDet.length(); j++) {

                            IssuedMaterialDetails materialDetails = new IssuedMaterialDetails();
                            JSONObject dataDet = (JSONObject) jsonArrayDet.get(j);
                            //PENDING JOBS DETAILS
                    //        Log.v("Issue No",dataDet.toString());
                            materialDetails.setIssueNo(dataDet.getString("IssueNo"));
                            materialDetails.setIssueType(dataDet.getString("IssueType"));
                            materialDetails.setItemType(dataDet.getString("ItemType"));
                            materialDetails.setItemCode(dataDet.getString("ItemCode"));
                            materialDetails.setItemDescription(dataDet.getString("ItemDescription"));
                            materialDetails.setIssuedQty(dataDet.getString("IssuedQty"));
                            materialDetails.setItemCategory(dataDet.getString("ItemCategory"));
                            materialDetails.setSerial(dataDet.getString("Serial"));

                            int iStatusDet = new PendingIssuedMaterialDAO(this).insertORupdate(materialDetails);
                            Log.v("Inserted",""+ iStatusDet+" : "+dataDet.getString("IssueNo"));

                        }

                        int iStatus = new IssuedDetailsDAO(this).insertORupdate(issuedDetails);
                      Log.v("Inserted",""+ iStatus);

                    }

                    new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                            AppContoller.SP_FETCH_PENDING_ISSUE_DETAILS,  DateManager.getCurrentDate());

                    //update date and icon
                    updateDateAndIcon();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } break;


            case FETCH_CDMA_ISSUE_DETAILS:{

               // Log.v("CDMA Results",result);
                pDialog.hide();
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
                        //new AcceptedIssuedMatrlDAO(this).insertORupdate(list.get(pos))
                     //   Log.v("Inserted",""+ iStatus);
                        if(jsonArray.length() == i+1){
                            new ToastManager(SyncActivity.this).success("Done");
                        }
                    }

                    new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                            AppContoller.SP_FETCH_CDMA_ISSUE_DETAILS,  DateManager.getCurrentDate());

                    //update date and icon
                    updateDateAndIcon();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } break;



        }
    }

    private void updateLteQuota(String Day, String Night,String fault_id) {
        if(new PendingFaultDAO(SyncActivity.this).updateQuota(Day,Night,fault_id)>0){
            System.out.println("* success fully updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }
        updateDateAndIcon();
        new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                AppContoller.SP_LAST_SYNC_PENDING_JOBS,  DateManager.getCurrentDate());
    }
    private void updateLteQuota_A(String Day, String Night,String fault_id) {
        if(new AcceptFaultDAO(SyncActivity.this).updateQuota_a(Day,Night,fault_id)>0){
            System.out.println("* success fully updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }
        updateDateAndIcon();
        new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                AppContoller.SP_LAST_SYNC_PENDING_JOBS,  DateManager.getCurrentDate());
    }

    private void UpdateQuotaStatus(String fault_id) {
        System.out.println("* fault_id "+fault_id);
        new SyncData(SyncActivity.this).CheckQuotaStatus(SyncActivity.this,TaskType.CHECK_QUOTA,fault_id);
    }

    private void UpdateAcceptQuotaStatus(String fault_id) {
        System.out.println("* fault_id "+fault_id);
       new SyncData(SyncActivity.this).CheckQuotaStatus(SyncActivity.this,TaskType.CHECK_QUOTA_A,fault_id);
    }

    private void updateVisitLog(String faultId) {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

            JSONArray VisitList = new CompetedJobDAO(SyncActivity.this).getVisitLogArray(faultId);

            JSONObject object = new JSONObject();

            try {
                object.put("data", VisitList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Json ",object.toString().replace("\\",""));

            new SyncData(SyncActivity.this)
                    .updateVisitLocation(SyncActivity.this, TaskType.VIST_LOG,object);

        } else {
            new ConnectionDetector(SyncActivity.this).showSettingsAlert();
        }
    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void onError(String message, TaskType type) {
        System.out.println("* error msg "+message+" type "+type);
        switch (type){
            case GET_PENDING_JOBS:{
                if(message.equals("No Existing Data")){
                    updateIconAncDate(AppContoller.SP_LAST_SYNC_PENDING_JOBS,tvSyncPendingFaultOn,btn_pending_jobs);
                    new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_ACCEPTED_JOBS, "2");
                }else if(message.contains("ORA-")){
                    new ToastManager(SyncActivity.this).warning("Server Error");
                }else{
                    new ToastManager(SyncActivity.this).warning(message);
                    new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_ACCEPTED_JOBS, "2");
                }
            } break;

            case FETCH_CDMA_ISSUE_DETAILS:{
                pDialog.hide();
                if(message.equals("No Existing Data")){
                    updateIconAncDate(AppContoller.SP_FETCH_CDMA_ISSUE_DETAILS,tvFetchCDMAIssueDetailsSyncDate,btnFetchCDMAIssueDetails);
                }else if(message.contains("ORA-")){
                    new ToastManager(SyncActivity.this).warning("Server Error");
                }else{
                    new ToastManager(SyncActivity.this).warning(message);
                }
            } break;

            case UPLOAD_ACCEPTED_JOBS:{
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;

            case UPLOAD_REJECTED_JOBS:{
            //    hideDialog();
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;
            case UPLOAD_REJECTED_JOBS_WHILE_PENDING:{
            //    hideDialog();
                new SyncData(SyncActivity.this).getPendingFaults(SyncActivity.this, TaskType.GET_PENDING_JOBS, "1");

            } break;

            case UPLOAD_ACCEPTED_UNITS:{
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;

            case UPLOAD_REJECTED_UNITS:{
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;
            case VIST_LOG:{
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;

            case UPLOAD_COMPLETED_JOBS:{
                new ToastManager(SyncActivity.this).error("Uploading Error.. Please try again.");
            } break;

            case FETCH_PENDING_ISSUE_DETAILS:{
                if(message.equals("No Existing Data")){
                    updateIconAncDate(AppContoller.SP_FETCH_PENDING_ISSUE_DETAILS,tvIssueDetailSyncDate,btnIssueDetailSyncDate);
                }else if(message.contains("ORA-")){
                    new ToastManager(SyncActivity.this).warning("Server Error");
                }else{
                    new ToastManager(SyncActivity.this).warning(message);
                }

            } break;

            case CHECK_QUOTA:{
                System.out.println("* mess "+message);
            }break;
            case CHECK_QUOTA_A:{
                System.out.println("* mess "+message);
            }break;
        }
    }

    private void updateIconAncDate(String spLastSyncPendingJobs, TextView tvSyncPendingFaultOn, Button btn_pending_jobs) {

        try {
            tvSyncPendingFaultOn.setText("Last update on " + DateManager.getCurrentDate());

            btn_pending_jobs.setBackgroundResource(R.drawable.sync_success);

            new SharedPreferencesHelper().setLocalSharedPreference(SyncActivity.this,
                    spLastSyncPendingJobs,  DateManager.getCurrentDate());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
