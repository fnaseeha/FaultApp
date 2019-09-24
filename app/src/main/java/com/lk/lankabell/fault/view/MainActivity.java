package com.lk.lankabell.fault.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ChartAdapter;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.config.AppConfig;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.AcceptedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.ActionTakenDAO;
import com.lk.lankabell.fault.control.Data.BiItemsDAO;
import com.lk.lankabell.fault.control.Data.CompetedJobDAO;
import com.lk.lankabell.fault.control.Data.CustomerPriorityDAO;
import com.lk.lankabell.fault.control.Data.IssuedDetailsDAO;
import com.lk.lankabell.fault.control.Data.LocationDAO;
import com.lk.lankabell.fault.control.Data.PendingFaultDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.Data.TimeCapDAO;
import com.lk.lankabell.fault.control.Data.UserProfileDAO;
import com.lk.lankabell.fault.control.MyJobService;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.helper.Other;
import com.lk.lankabell.fault.helper.TimeFormatter;
import com.lk.lankabell.fault.model.ActionTaken;
import com.lk.lankabell.fault.model.BiItems;
import com.lk.lankabell.fault.model.CompetedJob;
import com.lk.lankabell.fault.model.CustomerPriority;
import com.lk.lankabell.fault.model.IssuedDetails;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.LocationDetails;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.model.TimeCap;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VolleyCallback {

    private LineChart chart;
    //private PieChart pieChart;
    private Typeface mTf;

    private TextView tvFaultCnt,tv_accept_pending,tvMRPending,tv_pending_cnt1,tv4GLTE,tvCDMA,tvSIM,tvOTHER;
    ArrayList<PendingFaults> list;

    private FirebaseJobDispatcher jobDispatcher;
    Alerts alerts;
    double lattitude,longitude;
    private SimpleLocation location;
    private static final int REQUEST_LOCATION = 1;
    LinearLayout lin_Accept,lin_to_close,ll_issued_material,ll_4G_LTE,ll_CDMA,ll_SIM,ll_OTHER;
    TextView tv_nav_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("FAULT MANAGEMENT");
        getSupportActionBar().setTitle(Html.fromHtml("<small>FAULT MANAGEMENT</small>"));

        setData();
        alerts = new Alerts(MainActivity.this);
        location = new SimpleLocation(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        tv_nav_version = (TextView) header.findViewById(R.id.tv_nav_version);

        //-------- BACKGROUND SERVICES ---------
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        if(AppContoller.getEMPNO(MainActivity.this).equals("0")){
            alerts.ErrorAlert_only("Please Log this APP through Core App");
          //  new ToastManager(MainActivity.this).error("");
        }

        String version = "1.0"; //1.6.2
        String newversion = version;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
            if (version.length() == 5) {
                newversion = version.substring(0, 3) + version.substring(4);
            } else if (version.length() == 7) {
                newversion = version.substring(0, 3) + version.substring(4, 5) + version.substring(6, 7);
            } else {
                newversion = version;
            }
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("* new version " + newversion);
        String isLive = CheckLive();
        System.out.println("Is LIVE "+isLive+" IP "+AppConfig.IP);
        tv_nav_version.setText(isLive+" : "+newversion+" dfndfnd");

        if(new TimeCapDAO(MainActivity.this).getTimeCap(AppContoller.TodayLogedIn).size()==0){
            //initial login, then call accepted issued unit details ,
            if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
               // new ToastManager(MainActivity.this).success("call accepted issue note");
                System.out.println("* call accepted issue note");
               new SyncData(getApplicationContext())
                       .fetchAcceptedIssueDetails(MainActivity.this, TaskType.FETCH_ACCEPTED_ISSUE_DETAILS);

               new SyncData(getApplicationContext()).updateVersion(MainActivity.this,TaskType.UPDATE_VAERSION,AppContoller.getEpfNo(MainActivity.this),newversion);

            }
        }
        new SyncData(this).fetchActionTaken(this, TaskType.DOWNLOAD_ACTION_TAKEN);
        StartBackgroundService();

        checkLocationPermission();
        getLocations();
        //if timecap is empty

        // update timcap table


    }

    private String CheckLive() {
        if(AppConfig.IP.equals("http://119.235.1.88:8096/")){
            return "L";
        }else{
            return "T";
        }
    }

    public void getLocations(){
        checkLocationPermission();
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
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocations();
            }else{
                checkLocationPermission();
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
        getLocations();

        if(AppContoller.getEMPNO(MainActivity.this).equals("0")){
            alerts.ErrorAlert_only("Please Log this APP through Core App");
       //    new ToastManager(MainActivity.this).error("Please Log this APP through Core App");
        }
       // CheckGPS();
        if(new TimeCapDAO(MainActivity.this).getTimeCap(AppContoller.TodayLogedIn).size()==0){
            //initial login, then call accepted issued unit details ,
            if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
               // new ToastManager(MainActivity.this).success("call accepted issue note");
                System.out.println("* call accepted issue note");

               new SyncData(getApplicationContext())
                       .fetchAcceptedIssueDetails(MainActivity.this, TaskType.FETCH_ACCEPTED_ISSUE_DETAILS);
            }
        }
        new SyncData(this).fetchActionTaken(this, TaskType.DOWNLOAD_ACTION_TAKEN);

        String version = "1.0"; //1.6.2
        String newversion = version;
        String isLive = CheckLive();
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
            tv_nav_version.setText(isLive+" "+version);
            if (version.length() == 5) {
                newversion = version.substring(0, 3) + version.substring(4);
            } else if (version.length() == 7) {
                newversion = version.substring(0, 3) + version.substring(4, 5) + version.substring(6, 7);
            } else {
                newversion = version;
            }
        } catch(PackageManager.NameNotFoundException e) {
            tv_nav_version.setText(isLive+" "+version);
            e.printStackTrace();
        }

        System.out.println("* new version " + newversion);

//        Intent svrintent = new Intent(this, SampleTrackingService.class);
//            getBaseContext().startService(svrintent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.endUpdates();
    }

    private void StartBackgroundService() {
        //if(new ConnectionDetector(this).isConnectingToInternet()){
            //   Toast.makeText(this, "Background service started", Toast.LENGTH_LONG).show();
            Job job = jobDispatcher.newJobBuilder()
                    .setService(MyJobService.class)
                    .setLifetime(Lifetime.FOREVER)//life time of application
                    .setTag("JOB_01")
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(0,60))
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setReplaceCurrent(false)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build();
            jobDispatcher.mustSchedule(job);
           //   Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();
        //}
    }

    private void setData() {

        list = new AcceptFaultDAO(this).getAllAccepted();
        TextView tvRefId = (TextView) findViewById(R.id.tv_itme_code);
        TextView tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        TextView tv_customer_phone_number = (TextView) findViewById(R.id.tv_customer_phone_number);

        LinearLayout lin_lay_no_data = (LinearLayout) findViewById(R.id.lin_lay_no_data);
        LinearLayout lin_lay_visibility = (LinearLayout) findViewById(R.id.lin_lay02);

        ll_4G_LTE = findViewById(R.id.ll_4G_LTE);
        ll_CDMA = findViewById(R.id.ll_CDMA);
        ll_SIM = findViewById(R.id.ll_SIM);
        ll_OTHER = findViewById(R.id.ll_OTHER);

        ll_4G_LTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent sendStock = new Intent(MainActivity.this,MyStockActivity.class);
                sendStock.putExtra("type","LTE");
               startActivity(sendStock);
            }
        });

        ll_CDMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent sendStock = new Intent(MainActivity.this,MyStockActivity.class);
                sendStock.putExtra("type","CDMA");
               startActivity(sendStock);
            }
        });
        ll_SIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent sendStock = new Intent(MainActivity.this,MyStockActivity.class);
                sendStock.putExtra("type","SIM");
               startActivity(sendStock);
            }
        });

        ll_OTHER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent sendStock = new Intent(MainActivity.this,MyStockActivity.class);
                sendStock.putExtra("type","OTHER");
               startActivity(sendStock);
            }
        });

        if(list.size()>0) {
            lin_lay_no_data.setVisibility(View.GONE);
            lin_lay_visibility.setVisibility(View.VISIBLE);
            PendingFaults faults = list.get(0);

            if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
               tvRefId.setText(faults.getPF_REQUESTTOREFID() + " \n LTE" );
            }else if(faults.getPF_REQUESTTYPE().toUpperCase().equals("C")){
                tvRefId.setText(faults.getPF_REQUESTTOREFID() + " \nCDMA" );
            }else
                tvRefId.setText(faults.getPF_REQUESTTOREFID());



            tv_customer_name.setText(faults.getPF_REQUESTTONAME());
            tv_customer_phone_number.setText(faults.getPF_REQUESTTOLOCATION() + " \n" + faults.getPF_REQUESTTOCONTACT());

        }else {
            lin_lay_visibility.setVisibility(View.GONE);
            lin_lay_no_data.setVisibility(View.VISIBLE);
        }

        lin_Accept = findViewById(R.id.lin_Accept);
        lin_to_close = findViewById(R.id.lin_to_close);

        tvFaultCnt = (TextView) findViewById(R.id.tvFaultCnt);
        tvFaultCnt.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingFaultDAO(this).getCount())));
        tvFaultCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ToAcceptFaultActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

            }
        });
        //--------- FAULT PENDING ---------
        tv_pending_cnt1 = (TextView) findViewById(R.id.tv_pending_cnt1);
//        tv_pending_cnt1.setText(new DecimalFormat("00")
//                .format(Integer.parseInt(new AcceptFaultDAO(this).getCountPending())));
        tv_pending_cnt1.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingFaultDAO(this).getCountAcceptPending())));

        lin_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ToAcceptFaultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        //----- ACCEPTED PENDING -----------
        tv_accept_pending = (TextView) findViewById(R.id.tv_accept_pending);
        tv_accept_pending.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new AcceptFaultDAO(this).getCountPending())));
        lin_to_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FaultClosingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        //---------- MR ACCEPT PENDING --------
        tvMRPending = (TextView) findViewById(R.id.tvMRPending);
        ll_issued_material = findViewById(R.id.lin_Completed_Numbers1);
        tvMRPending.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new IssuedDetailsDAO(this).getCountMRAcceptPending())));

        ll_issued_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ToAcceptUnitsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });




        tv4GLTE = (TextView) findViewById(R.id.tv4GLTE);
        tv4GLTE.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingIssuedMaterialDAO(this).getCountAvailableUnits("LTEU"))));

        tvCDMA = (TextView) findViewById(R.id.tvCDMA);
        tvCDMA.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingIssuedMaterialDAO(this).getCountAvailableUnits("CPHN"))));

        tvSIM = (TextView) findViewById(R.id.tvSIM);
        tvSIM.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingIssuedMaterialDAO(this).getCountAvailableUnits("LTES"))));


        tvOTHER = (TextView) findViewById(R.id.tvOTHER);
        tvOTHER.setText(new DecimalFormat("00")
                .format(Integer.parseInt(new PendingIssuedMaterialDAO(this).getCountAvailableUnits("OTHER"))));


        chart = findViewById(R.id.chart);
        lineChartData();
        chart.setData((LineData) new ChartAdapter(MainActivity.this).generateDataLine(1));

        ImageView btnMaterial = (ImageView) findViewById(R.id.btnMaterial);
        btnMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialRequestActivity.fault = list.get(0);
                Intent intent = new Intent(MainActivity.this, MaterialRequestActivity.class);
                startActivity(intent);
            }
        });

        ImageView btnDone = (ImageView) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PendingFaults fault = list.get(0);
                showFaultClosingMsg(fault);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }



    private void showFaultClosingMsg(final PendingFaults faults) {

        final View dialogView = View.inflate(this,R.layout.show_close_dialogbox,null);
        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        TextView tvRequestID = (TextView) dialog.findViewById(R.id.tvRequestID);
        tvRequestID.setText(faults.getPF_REQUESTID());

        TextView tvDN_No = (TextView) dialog.findViewById(R.id.tvDN_No);
        tvDN_No.setText(faults.getPF_REQUESTTOREFID());

        ArrayList<String> actionTakenArrayList = new ActionTakenDAO(MainActivity.this).getAllActions();

        final TextView tvAction = (TextView) dialog.findViewById(R.id.tvAction_taken);
        final SpinnerDialog spinnerDialog;
        spinnerDialog=new SpinnerDialog(MainActivity.this,actionTakenArrayList,"Select or Search Action Taken",R.style.DialogAnimations_SmileWindow);// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default


        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
              //  Toast.makeText(MainActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                tvAction.setText(item);
            }
        });

        tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });


        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });


        TextView btnMaterialRequestew = (TextView)dialog.findViewById(R.id.btnMaterialRequestew);
        btnMaterialRequestew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MaterialRequestActivity.class);
                startActivity(intent);
                revealShow(dialogView, false, dialog);

            }
        });

        TextView bClose = (TextView)dialog.findViewById(R.id.bClose);
        final TextView RemarkClose = (TextView)dialog.findViewById(R.id.RemarkClose);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( tvAction.getText().toString().equals("OTHERS") && RemarkClose.getText().toString().equals("")){
                    alerts.ErrorAlert("Please Type an Action Taken");

                }else if(tvAction.getText().toString().equals("")  ){
                    alerts.ErrorAlert("Please Select an Action Taken");

                } else {
                    if(RemarkClose.getText().toString().length()>50){
                        alerts.ErrorAlert("Remark length is too long");
                    }else{
                        CompetedJob job = new CompetedJob();

                        job.setCOMPLETED_JOB_REQUESTED_ID(faults.getPF_REQUESTID());
                        job.setCOMPLETED_JOB_REQUEST_TO_REFID(faults.getPF_REQUESTTOREFID());
                        job.setCOMPLETED_JOB_IS_MATERIAL_REQUEST("0");
                        job.setCOMPLETED_JOB_REMARK(RemarkClose.getText().toString());
                        job.setCOMPLETED_JOB_ACTION_TAKEN(new ActionTakenDAO(MainActivity.this)
                                .getActionCodeByName(tvAction.getText().toString()));

                        Other otherFunction = new Other(MainActivity.this);
                        otherFunction.setLatLong(job);

                        dialogCompleteTask(dialogView, dialog, job);
                    }
                }
            }
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("NewApi")
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }



    public void dialogCompleteTask(final View dialogView, final Dialog dialog ,final CompetedJob job){
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do you want to Complete?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        int iStatus = new CompetedJobDAO(MainActivity.this).insertORupdate(job);
                        if(iStatus>0){
                            setData();
                            revealShow(dialogView, false, dialog);
                            sDialog.dismissWithAnimation();
                        }

                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @SuppressLint("NewApi")
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = 0;
        int cy = 0;

        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }


    private void lineChartData() {

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        YAxis leftAxis = chart.getAxisLeft();

        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // set data
        chart.setData((LineData) new ChartAdapter(MainActivity.this).generateDataLine(1));

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateX(900);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           logout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;

        }else if(id==R.id.action_sync){
            setData();
            lineChartData();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashbaord) {

        } else if (id == R.id.nav_pending) {
            // Handle the camera action
            Intent intent = new Intent(getApplicationContext(), ToAcceptFaultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //onNavigationItemSelected(0);

        }else if (id == R.id.nav_manual) {
            Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        else if (id == R.id.nav_close) {
            Intent intent = new Intent(getApplicationContext(), FaultClosingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if (id == R.id.nav_accept_units) {
            Intent intent = new Intent(getApplicationContext(), ToAcceptUnitsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_summary) {
            Intent intent = new Intent(getApplicationContext(), SummaryReportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_sign_out) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            logout();
        }else if (id == R.id.nav_refurbish) {
            Intent intent = new Intent(getApplicationContext(), UnitInHandActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (id == R.id.nav_my_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
//        else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_nav_cameramanage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do you want to Logout?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finish();
                        overridePendingTransition(R.anim.left_in, R.anim.slide_to_right);

                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }
    @Override
    public void onSuccess(String id, TaskType type, String faultType) {

    }
    @Override
    public void onSuccess(String result, TaskType type) {
        switch (type){

            case DOWNLOAD_ACTION_TAKEN:{

            //    Log.v("Results",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //Deleting past Data.
                    int iDelete = new ActionTakenDAO(this).delete();
                //    Log.v("Deleted",""+iDelete);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        ActionTaken taken = new ActionTaken();
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        //PENDING JOBS DETAILS
                        taken.setACTION_TAKEN_CODE(data.getString("ActionCode"));
                        taken.setACTION_DESCRIPTION(data.getString("ActionDescription"));

                        int iStatus = new ActionTakenDAO(this).insertORupdate(taken);
              //          Log.v("Inserted",""+iStatus);
                        //faultssA.add(faults);
                    }
                    //sort array


                    //acceptedOrderAdapter.notifyDataSetChanged();
                    new SyncData(this).fetchCustomerPriorityDetails(this, TaskType.DOWNLOAD_CUSTOMER_PRIORITY_LIST);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } break;

            case UPDATE_VAERSION:{

            //    Log.v("Results",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    final String data = jsonObject.getString("Data");
                    final String id = jsonObject.getString("ID");

                    System.out.println("* update version "+data+" id "+id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } break;

            case DOWNLOAD_CUSTOMER_PRIORITY_LIST:{

               // Log.v("Results",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //Deleting past Data.
                    int iDelete = new CustomerPriorityDAO(this).delete();

                 //   Log.v("Deleted",""+iDelete);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        CustomerPriority priority = new CustomerPriority();
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        //PENDING JOBS DETAILS
                        priority.setCP_CUSTOMERSTAT(data.getString("CustomerStat"));
                        priority.setCP_STAT_LEVEL(data.getString("StatLevel"));
                        priority.setCP_STAT_LEVEL_TIME(data.getString("StatLevelTime"));

                        int iStatus = new CustomerPriorityDAO(this).insertORupdate(priority);
                   //     Log.v("Inserted",""+iStatus);

                    }
                    new SyncData(this).fetchCoordinator(this, TaskType.FETCH_COORDINATOR);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } break;
            case FETCH_COORDINATOR:{
             //   Log.v("Results",result);

                String empNo = new AppContoller().getEMPNO(MainActivity.this);
                if(new UserProfileDAO(MainActivity.this).updateCordinatorNumber(result,empNo)>0){
                 System.out.println("* cordinator number updated "+result);
                }else{
                  System.out.println("* cordinator number not updated "+result);
                  //alerts.OkAlert("Please LogIn with CoreApp");
                }

               // new SyncData(MainActivity.this).fetchBiCode(MainActivity.this,TaskType.FETCH_BI_ITEMS);
            }break;

            case FETCH_BI_ITEMS:{
             //   Log.v("Results",result);

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                    if(new BiItemsDAO(MainActivity.this).delete()>0){
                        System.out.println("* Deleted all BI Items");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        BiItems biItems = new BiItems();
                        JSONObject data = (JSONObject)jsonArray.get(i);
                        biItems.setBI_SERVICE_CODE(data.getString("serviceCode"));
                        biItems.setBI_SERVICE_NAME(data.getString("serviceName"));
                        if(new BiItemsDAO(MainActivity.this).insertORupdate(biItems)>0){
                            System.out.println("* inserted BI Items");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }break;

            case FETCH_ACCEPTED_ISSUE_DETAILS:{
                try {
                    System.out.println("*  FETCH_ACCEPTED_ISSUE_DETAILS success");

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
                        //     Log.v("Inserted",""+ iStatus);

                        TimeCap timeCap2 = new TimeCap();
                        timeCap2.setRawName(AppContoller.TodayLogedIn);
                        timeCap2.setDate(TimeFormatter.convertStringTimetoMilliseconds(new Date().getTime(), "yyyy-MM-dd HH:mm:ss"));

                        // saving new loging details
                        if(new TimeCapDAO(MainActivity.this).insertOrUpdate(timeCap2)>0){
                            System.out.println("* timcap inserted");
                        }else{
                            System.out.println("* time cap not inserted");
                        }
                    }


                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }
            }break;


        }
    }

    @Override
    public void onError(String message, TaskType type) {
        switch (type){
            case FETCH_COORDINATOR:{
                System.out.println("Error Fetch coordinator");
            }break;
            case FETCH_ACCEPTED_ISSUE_DETAILS:{
                System.out.println("*  FETCH_ACCEPTED_ISSUE_DETAILS failed  "+message);
            }break;

        }
    }
}
