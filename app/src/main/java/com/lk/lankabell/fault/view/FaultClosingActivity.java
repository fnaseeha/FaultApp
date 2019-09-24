package com.lk.lankabell.fault.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.AcceptedFaultAdapter;
import com.lk.lankabell.fault.adapter.AsrIssueAdapter;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.ClickListener;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.control.ClickListenerUnitInHand;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.ActionTakenDAO;
import com.lk.lankabell.fault.control.Data.BiItemsDAO;
import com.lk.lankabell.fault.control.Data.CompetedJobDAO;
import com.lk.lankabell.fault.control.Data.CustomerPriorityDAO;
import com.lk.lankabell.fault.control.Data.LocationDAO;
import com.lk.lankabell.fault.control.Data.MaterialIssuedDAO;
import com.lk.lankabell.fault.control.Data.PendingFaultDAO;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.helper.Other;
import com.lk.lankabell.fault.model.CompetedJob;
import com.lk.lankabell.fault.model.LocationDetails;
import com.lk.lankabell.fault.model.MaterialIssued;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static android.view.View.GONE;
import static com.lk.lankabell.fault.control.TaskType.CHECK_QUOTA;

public class FaultClosingActivity extends AppCompatActivity implements VolleyCallback {
    private RecyclerView recyclerView;

    ArrayList<PendingFaults> list;
    AcceptedFaultAdapter acceptedFaultAdapter;
    private CoordinatorLayout coordinatorLayout;
    Alerts alerts;
    double lattitude, longitude;

    private SimpleLocation location;
    ArrayList<String> actionTakenArrayList;
    public String fault_id = "";
    private final static int SEND_PERMISSSION_REQUEST_CODE = 111;
    private final static int CALL_PERMISSSION_REQUEST_CODE = 222;
    String phone_number;
    String CustomerPhoneNumber ="";
    SweetAlertDialog pDialog;
    TextView tvQuotaBalanceDay;
    TextView tvQuotaBalanceNight;
    SpinnerDialog spinnerBiDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_request);
        getSupportActionBar().setTitle("FAULT CLOSING");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //coordinator_layout
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        //Pending Faults - Lists code
        recyclerView = (RecyclerView) findViewById(R.id.rvView);
        location = new SimpleLocation(this);
        alerts = new Alerts(FaultClosingActivity.this);
        phone_number = AppContoller.getPhoneNumber(FaultClosingActivity.this);
        AskSmsPermission();
        AskPhoneCallPermission();
        setRecyclerViewDate();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

    }

    private void checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_PERMISSSION_REQUEST_CODE);

        } else {
            // permission has been granted, continue as usual
        }
    }
    private void AskPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Nowx
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},CALL_PERMISSSION_REQUEST_CODE);

        } else {
            // permission has been granted, continue as usual
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        setRecyclerViewDate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setRecyclerViewDate();
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

    private void setRecyclerViewDate() {

        list = new AcceptFaultDAO(this).getAllAccepted();

        for(PendingFaults faults: list) {

            double pendingHrs = Double.parseDouble(new CustomerPriorityDAO(this)
                    .getMinmumRespTime(faults.getPF_CUSTOMERRATINGS()))*60 - DateManager.getHrsGivenDate(faults.getAF_ACCEPTEDO_DATE());


            faults.setPF_RESP_TIME(pendingHrs);
        }
        acceptedFaultAdapter = new AcceptedFaultAdapter(FaultClosingActivity.this, list,FaultClosingActivity.this, new ClickListener() {
            @Override
            public void onPositionClicked(int position, View v) {

                if (v.getId() == R.id.btnCheck) {

                    MaterialRequestActivity.fault = list.get(position);
                    Intent intent = new Intent(FaultClosingActivity.this, MaterialRequestActivity.class);
                    startActivity(intent);

                 //   Toast.makeText(FaultClosingActivity.this, "Material Button", Toast.LENGTH_SHORT).show();


                } else if (v.getId() == R.id.view_foreground) {

                    final PendingFaults fault = list.get(position);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Ref Id "
                            + fault.getPF_REQUESTTOREFID().toString(), Snackbar.LENGTH_LONG);

                    snackbar.show();

//                    new SyncData(FaultClosingActivity.this)
//                            .fetchActionTaken(FaultClosingActivity.this, TaskType.DOWNLOAD_ACTION_TAKEN);

                    showFaultClosingMsg(list.get(position));


                } else if (v.getId() == R.id.tvMore) {

                    final PendingFaults fault = list.get(position);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Ref Id "
                            + fault.getPF_REQUESTTOREFID().toString(), Snackbar.LENGTH_LONG);

                    snackbar.show();
                    PendingFaults faults = list.get(position);
                    fault_id = faults.getPF_REQUESTID();
                    showFaultDetails(faults);
                    new SyncData(FaultClosingActivity.this).checkAccStatus(FaultClosingActivity.this, TaskType.FATCH_ACC_STATUS, fault.getPF_REQUESTID());
                    if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
                        if (new ConnectionDetector(FaultClosingActivity.this).isConnectingToInternet()) {
                            new SyncData(FaultClosingActivity.this).CheckQuotaStatus(FaultClosingActivity.this, CHECK_QUOTA, fault_id);
                        }else{
                            System.out.println("* network error");
                        }
                    }
                }

            }

            @Override
            public void onPositionClickedLong(int position, View view) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(acceptedFaultAdapter);
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                        // do whatever
//                        final PendingFaults fault = list.get(position);
//                        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Ref Id "
//                                +fault.getPF_REQUESTTOREFID().toString(), Snackbar.LENGTH_LONG);
//
//                        snackbar.show();
//
//                        if(fault!=null) {
//                            showFaultDetails(fault);
//                        }
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//
//                    }
//                })
//        );
        //set Data and refrese adapter
        acceptedFaultAdapter.notifyDataSetChanged();

    }

    private void AskSmsPermission() {
        if (ContextCompat.checkSelfPermission(FaultClosingActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(FaultClosingActivity.this,
                    android.Manifest.permission.SEND_SMS)) {
                checkSMSPermission();
                new ToastManager(FaultClosingActivity.this).error("SMS Won't send to Coordinator");


            } else {
                ActivityCompat.requestPermissions(FaultClosingActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_PERMISSSION_REQUEST_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SEND_PERMISSSION_REQUEST_CODE:{
                sendSMSBody(phone_number, fault_id);
            }break;
            case CALL_PERMISSSION_REQUEST_CODE:{
                call(CustomerPhoneNumber);
            }break;
        }


    }

    public void getLocations() {
        getLocation();
        location.beginUpdates();

        location.setListener(new SimpleLocation.Listener() {
            @Override
            public void onPositionChanged() {
                getLocation();
                System.out.println("* location changed " + lattitude + " " + longitude);

                saveLocation();
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2000);

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
        if (loc_count > 50) {
            int delete = new LocationDAO(this).delete();
            System.out.println("* deleted " + delete);
        }
        int insert = new LocationDAO(this).insert(locationDetails);

        System.out.println("* inserted " + insert);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2000) {
            getLocations();
        }
    }

    private void getLocation() {
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
        // System.out.println("* location changed "+lattitude+" "+longitude);
        saveLocation();
    }


    private boolean CheckGPS() {

        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            System.out.println("* provider " + provider);

//        int j = getLocationMode(getApplicationContext());

            if (provider.equalsIgnoreCase("gps")) {
                alerts.showLocationAlert("Your Location Mode is not High priority.\nPlease Set Location to " +
                        "High Priority");
                return false;
            } else if (provider.equalsIgnoreCase("network")) {
                alerts.showLocationAlert("Your Location Mode is not High priority.\nPlease Set Location to " +
                        "High Priority");
                return false;
            } else if (provider.contains("network,gps") || provider.contains("gps,network")) {
                System.out.println("* gps is enable");
                return true;
            } else {
                alerts.showLocationAlert("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private String CheckString(String someString) {
        if (someString.equals(null)) {
            return "-";
        } else if (someString.equals("null")) {
            return "-";
        } else {
            return someString;
        }
    }

    private String returnEmptyString(String someString) {
        if (someString.equals(null)) {
            return "";
        } else if (someString.equals("null")) {
            return "";
        } else {
            return someString;
        }
    }

    private String CheckStringAddress(String pf_requesttoadd1, String pf_requesttoadd2, String pf_requesttoadd3) {
        if (pf_requesttoadd1.equals("null") && pf_requesttoadd2.equals("null") && pf_requesttoadd3.equals("null")) {
            return "-";
        } else {
            return returnEmptyString(pf_requesttoadd1) + " " + returnEmptyString(pf_requesttoadd2) + " " + returnEmptyString(pf_requesttoadd3);
        }
    }
    private void AskPhoneCallPermission(){
        if (ContextCompat.checkSelfPermission(FaultClosingActivity.this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(FaultClosingActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {
                AskPhonePermission();
                new ToastManager(FaultClosingActivity.this).error("Can't make any calls");
            } else {
                ActivityCompat.requestPermissions(FaultClosingActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                       CALL_PERMISSSION_REQUEST_CODE);

            }
        }
    }
    private String CheckInt(String someString) {
        if (someString.equals(null)) {
            return "0";
        } else if (someString.equals("null")) {
            return "0";
        } else {
            return someString;
        }
    }

    TextView tvAccStatus;

    private void showFaultDetails(final PendingFaults faults) {

        final View dialogView = View.inflate(this, R.layout.show_details_pending_fault, null);

        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        TextView tvRequestId = (TextView) dialog.findViewById(R.id.tvRequestId);
        tvRequestId.setText(faults.getPF_REQUESTID());

        tvAccStatus = (TextView) dialog.findViewById(R.id.tvAccStatus);
        tvAccStatus.setText("");

        TextView tvBatchId = (TextView) dialog.findViewById(R.id.tvBatchId);
        tvBatchId.setText(CheckString(faults.getPF_REQUESTBATCHID()));

        TextView tvRefId = (TextView) dialog.findViewById(R.id.tvRefId);
        tvRefId.setText(CheckString(faults.getPF_REQUESTTOREFID()));

        TextView tvName = (TextView) dialog.findViewById(R.id.tvName);
        tvName.setText(CheckString(faults.getPF_REQUESTTONAME()));

        final TextView tvContact = (TextView) dialog.findViewById(R.id.tvContact);
        tvContact.setText(CheckString(faults.getPF_REQUESTTOCONTACT()));
        CustomerPhoneNumber = faults.getPF_REQUESTTOCONTACT();

        TextView tvAddress = (TextView) dialog.findViewById(R.id.tvAddress);
        tvAddress.setText(CheckStringAddress(faults.getPF_REQUESTTOADD1(), faults.getPF_REQUESTTOADD2(), faults.getPF_REQUESTTOADD3()));

        TextView tvAssignedDate = (TextView) dialog.findViewById(R.id.tvAssignedDate);
        tvAssignedDate.setText(CheckString(faults.getPF_REQUESTASSIGNEDDATE()));

        TextView tvStatus = (TextView) dialog.findViewById(R.id.tvStatus);
        if (faults.getPF_STATUS().toUpperCase().equals("NEW"))
            tvStatus.setText("Pending");

        TextView tvRequestType = (TextView) dialog.findViewById(R.id.tvRequestType);
        if (faults.getPF_REQUESTTYPE().toUpperCase().equals("L")) {
            tvRequestType.setText("LTE");
        } else if (faults.getPF_REQUESTTYPE().toUpperCase().equals("C")) {
            tvRequestType.setText("CDMA");
        }

        ImageView refresh =  dialog.findViewById(R.id.ivRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionDetector(FaultClosingActivity.this).isConnectingToInternet()) {
                    new SyncData(FaultClosingActivity.this).CheckQuotaStatus(FaultClosingActivity.this, CHECK_QUOTA, faults.getPF_REQUESTID());
                }else{
                    System.out.println("* network error");
                }
            }
        });
        TextView tvQuotaBalance = dialog.findViewById(R.id.tvQuotaBalance);
        LinearLayout llQuotaBalance = dialog.findViewById(R.id.llQuotaBalance);
         tvQuotaBalanceDay = dialog.findViewById(R.id.tvQuotaBalanceDay);
         tvQuotaBalanceNight = dialog.findViewById(R.id.tvQuotaBalanceNight);
        ImageView call_contact = dialog.findViewById(R.id.call_contact);

        call_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call
                try {
                    if(!tvContact.getText().toString().equals("0")) {
                        new SweetAlertDialog(FaultClosingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Do you want to call?")
                                .setConfirmText("Yes")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        call(tvContact.getText().toString());

                                    }
                                })
                                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                    }
                                })
                                .show();
                    }else {
                        new ToastManager(FaultClosingActivity.this).error("Invalid Phone Number");
                    }

                } catch (ActivityNotFoundException e) {
                    new ToastManager(FaultClosingActivity.this).error("Error in your phone call because of"+e.getMessage());
                }
            }
        });

        System.out.println("* day "+faults.getPF_LTE_DAY()+" night "+faults.getPF_LTE_NIGHT());

        if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
            tvQuotaBalance.setVisibility(View.VISIBLE);
            llQuotaBalance.setVisibility(View.VISIBLE);
            String day="0";
            String night="0";
            if(faults.getPF_LTE_DAY() != null){
                day= CheckInt(faults.getPF_LTE_DAY());
            }
            if(faults.getPF_LTE_NIGHT() != null){
                night = CheckInt(faults.getPF_LTE_NIGHT());
            }

            System.out.println("* day "+day+" night "+night);
            switch(day){
                case "100":{
                    tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
                }break;
                case "0":{
                    tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
                }break;
                default:tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
            }
            switch(night){
                case "100":{
                    tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
                }break;
                case "0":{
                    tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
                }break;
                default:tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
            }
        }else{
            tvQuotaBalance.setVisibility(GONE);
            llQuotaBalance.setVisibility(GONE);
        }

        TextView tvSubType = (TextView) dialog.findViewById(R.id.tvSubType);
        tvSubType.setText(CheckString(faults.getPF_REQUESTSUBTYPE()));

        TextView tvCategory = (TextView) dialog.findViewById(R.id.tvCategory);
        tvCategory.setText(CheckString(faults.getPF_REQUESTCATEGORY()));

        TextView tvLocation = (TextView) dialog.findViewById(R.id.tvLocation);
        tvLocation.setText(CheckString(faults.getPF_REQUESTTOLOCATION()));

        TextView tvCustomerCategory = (TextView) dialog.findViewById(R.id.tvCustomerCategory);
        tvCustomerCategory.setText(CheckString(faults.getPF_CUSTOMERCATEGORY()));

        TextView tvCustomerRatings1 = (TextView) dialog.findViewById(R.id.tvCustomerRatings1);
        tvCustomerRatings1.setText(CheckString(faults.getPF_CUSTOMERRATINGS()));

        TextView tvCustomerRemarks = (TextView) dialog.findViewById(R.id.tvCustomerRemarks);
        tvCustomerRemarks.setText(CheckString(faults.getPF_CUSTOMERREMARKS()));

        //tvProName.setText(user.getCOL_USER_USERNAME());
        //TextView tvLastSyncDateTime = (TextView) dialog.findViewById(R.id.tvLastSyncDateTime);
        //tvLastSyncDateTime.setText(new AppContoller().getSPLastSyncDateTime(MainActivity.this));

        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        TextView bSubmit = (TextView)dialog.findViewById(R.id.bSubmit);
        bSubmit.setVisibility(GONE);

        TextView bReject = (TextView)dialog.findViewById(R.id.bReject);
        bReject.setVisibility(GONE);

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





    private void showFaultClosingMsg(final PendingFaults faults) {


        final View dialogView = View.inflate(this,R.layout.show_close_dialogbox,null);
        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        TextView tvRequestID = (TextView) dialog.findViewById(R.id.tvRequestID);
        tvRequestID.setText(faults.getPF_REQUESTID());

        TextView tvDN_No = (TextView) dialog.findViewById(R.id.tvDN_No);
        tvDN_No.setText(faults.getPF_REQUESTTOREFID());

        ArrayList <String> actionTakenArrayList = new ActionTakenDAO(FaultClosingActivity.this).getAllActions();


        final TextView tvAction = (TextView) dialog.findViewById(R.id.tvAction_taken);
        final TextView RemarkClose = (TextView) dialog.findViewById(R.id.RemarkClose);


        final SpinnerDialog spinnerDialog;
        spinnerDialog=new SpinnerDialog(FaultClosingActivity.this,actionTakenArrayList,"Select or Search Action Taken",R.style.DialogAnimations_SmileWindow);// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
              //  Toast.makeText(FaultClosingActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
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
                MaterialRequestActivity.fault = faults;
                Intent intent = new Intent(FaultClosingActivity.this, MaterialRequestActivity.class);
                startActivity(intent);
               revealShow(dialogView, false, dialog);
            }
        });



        TextView bClose = (TextView)dialog.findViewById(R.id.bClose);

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( tvAction.getText().toString().equals("OTHERS") && RemarkClose.getText().toString().equals("")){
                    alerts.ErrorAlert("Please Type an Action Taken");

                }else if(tvAction.getText().toString().equals("")  ){
                    alerts.ErrorAlert("Please Select an Action Taken");

                }else {
                    if(RemarkClose.getText().toString().length()>50){
                        alerts.ErrorAlert("Remark length is too long");
                    }else {
                        CompetedJob job = new CompetedJob();

                        job.setCOMPLETED_JOB_REQUESTED_ID(faults.getPF_REQUESTID());
                        job.setCOMPLETED_JOB_REQUEST_TO_REFID(faults.getPF_REQUESTTOREFID());
                        job.setCOMPLETED_JOB_IS_MATERIAL_REQUEST("0");
                        job.setCOMPLETED_JOB_REMARK(RemarkClose.getText().toString());
                        job.setCOMPLETED_JOB_ACTION_TAKEN(new ActionTakenDAO(FaultClosingActivity.this).getActionCodeByName(tvAction.getText().toString()));

                        Other otherFunction = new Other(FaultClosingActivity.this);
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

        new SweetAlertDialog(FaultClosingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do you want to Complete?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        int iStatus = new CompetedJobDAO(FaultClosingActivity.this).insertORupdate(job);
                        fault_id = job.getCOMPLETED_JOB_REQUESTED_ID();

                        if(iStatus>0){
                            if(new ConnectionDetector(getApplication()).isConnectingToInternet()){
                                sendFaultToServer(job.getCOMPLETED_JOB_REQUESTED_ID());
                            }
                            setRecyclerViewDate();
                            revealShow(dialogView, false, dialog);
                            sDialog.dismissWithAnimation();
                        }
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                      //  InsertASR(job.getCOMPLETED_JOB_REQUESTED_ID());
                    }
                })
                .show();
    }

    private void sendFaultToServer(String fault_id) {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
           pDialog.show();
            JSONArray completedList = new CompetedJobDAO(FaultClosingActivity.this).getCompletedFault(fault_id);

            JSONObject object = new JSONObject();

            try {
                object.put("data", completedList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Json ",object.toString().replace("\\",""));

            new SyncData(FaultClosingActivity.this)
                    .syncCompletedJobs(FaultClosingActivity.this, TaskType.UPLOAD_COMPLETED_JOBS,object);

        }
    }


    @SuppressLint("NewApi")
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final View view = dialogView.findViewById(R.id.dialog);

            int w = view.getWidth();
            int h = view.getHeight();

            int endRadius = (int) Math.hypot(w, h);

            int cx = 0;
            int cy = 0;

            if (b) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
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
        }else{
            if(dialog != null) {
                if (b) {
                    dialog.show();
                } else {
                    dialog.dismiss();
                }
            }
        }
    }
    private void updateVisitLog(String faultId) {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

            JSONArray VisitList = new CompetedJobDAO(FaultClosingActivity.this).getVisitLogArray(faultId);

            JSONObject object = new JSONObject();

            try {
                object.put("data", VisitList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Json ",object.toString().replace("\\",""));

            new SyncData(FaultClosingActivity.this)
                    .updateVisitLocation(FaultClosingActivity.this, TaskType.VIST_LOG,object);

        } else {
            new ConnectionDetector(FaultClosingActivity.this).showSettingsAlert();
        }
    }

    private void sendSms(String fault_id) {


        if (ContextCompat.checkSelfPermission(FaultClosingActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(FaultClosingActivity.this,
                    android.Manifest.permission.SEND_SMS)) {
                alerts.ErrorAlert("SMS Won't send to Coordinator");

            } else {
                ActivityCompat.requestPermissions(FaultClosingActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_PERMISSSION_REQUEST_CODE);
            }
        }else {
            // Permission already granted
            sendSMSBody(phone_number,fault_id);
        }
    }

    private void sendSMSBody(String phone_number, String fault_id) {

        SmsManager smsManager = SmsManager.getDefault();
        System.out.println("* phone_number "+phone_number);
        smsManager.sendTextMessage(phone_number,null,"ASR has been created for this Fault id : "+fault_id,null,null);
    }
    private void call(String phone_number) {

        Intent my_callIntent = new Intent(Intent.ACTION_CALL);
        if (!phone_number.equals("null")) {
            my_callIntent.setData(Uri.parse("tel:" + phone_number));
            //here the word 'tel' is important for making a call...

            if (ActivityCompat.checkSelfPermission(FaultClosingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(my_callIntent);
        }else{
            new ToastManager(FaultClosingActivity.this).error("Invalid Phone Number");
        }
    }

        private void InsertASR(final String faultId) {

            final View dialogView = View.inflate(this,R.layout.layout_show_asr_items,null);
            final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);

            final TextView tvRequestId = (TextView) dialog.findViewById(R.id.tvRequestId);
            final RecyclerView rvView = (RecyclerView) dialog.findViewById(R.id.rvView);
            final Button bSubmit = (Button) dialog.findViewById(R.id.bSubmit);
            final Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
            final TextView tvBICode = (TextView) dialog.findViewById(R.id.tvBICode);
            tvRequestId.setText("Fault ID : "+faultId);

            ArrayList<String>AllBiItems = new BiItemsDAO(FaultClosingActivity.this).getAllActions();
            spinnerBiDialog=new SpinnerDialog(FaultClosingActivity.this,AllBiItems,"Select or Search BI Item",R.style.DialogAnimations_SmileWindow);// With 	Animation

            spinnerBiDialog.setCancellable(true); // for cancellable
            spinnerBiDialog.setShowKeyboard(false);// for open keyboard by default

            spinnerBiDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    //  Toast.makeText(MaterialRequestActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                    tvBICode.setText(item);
                }
            });

            tvBICode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    spinnerBiDialog.showSpinerDialog();

                }
            });

            final ArrayList<MaterialIssued> lists = new MaterialIssuedDAO(FaultClosingActivity.this)
                    .getReturnMaterialList(faultId);

            final ArrayList<MaterialIssued> selectedM = new ArrayList<>();

            AsrIssueAdapter asrAdapter = new AsrIssueAdapter(lists,FaultClosingActivity.this, new ClickListenerMaterial(){

                @Override
                public void onPositionClicked(int position, View view, CheckBox btn) {
                    if (view.getId() == R.id.btnCheck) {
                        if(btn.isChecked()) {
                            selectedM.add(lists.get(position));
                        }else{
                            selectedM.remove(lists.get(position));
                        }

                    }
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            rvView.setLayoutManager(mLayoutManager);
            rvView.setItemAnimator(new DefaultItemAnimator());
            rvView.addItemDecoration(new DividerItemDecoration(rvView.getContext(), DividerItemDecoration.VERTICAL));
            rvView.setAdapter(asrAdapter);
            rvView.setNestedScrollingEnabled(false);

            asrAdapter.notifyDataSetChanged();


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();


            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    if(tvBICode.getText().toString().equals("")){
//                        alerts.ErrorAlert("Please Select a BI Code");
//                    }else {
                        if (selectedM.size() > 0) {
                            new SweetAlertDialog(FaultClosingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Do you want to create ASR?")
                                    .setConfirmText("Yes")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            SmsManager smsManager = SmsManager.getDefault();
                                            StringBuilder selected_itemCode = new StringBuilder();

                                            for (MaterialIssued m : selectedM) {

                                                if (m == selectedM.get(selectedM.size() - 1)) {
                                                    selected_itemCode.append(m.getMATERIAL_ISSUED_ITEM_NO());
                                                } else {
                                                    selected_itemCode.append(m.getMATERIAL_ISSUED_ITEM_NO()).append(",");
                                                }

                                            }
                                            if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                                                JSONArray ReturnUnitList = new CompetedJobDAO(FaultClosingActivity.this).getReturnUnitDetails(faultId, selectedM);

                                                JSONObject object = new JSONObject();

                                                try {
                                                    object.put("data", ReturnUnitList);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                Log.v("Json ", object.toString().replace("\\", ""));

                                                System.out.println("* phone_number " + phone_number);

                                                smsManager.sendTextMessage(phone_number, null,
                                                        "ASR has been created for this Fault id : " + faultId + " and itemCodes are " + selected_itemCode + " by " + AppContoller.getEMPNO(FaultClosingActivity.this), null, null);
                                                sDialog.dismissWithAnimation();
                                                new SyncData(FaultClosingActivity.this).InsertASR(FaultClosingActivity.this, TaskType.ASR_CREATE, object);

                                            } else {
                                                smsManager.sendTextMessage(phone_number, null,
                                                        "ASR has been created for this Fault id : " + faultId + " and itemCodes are " + selected_itemCode + " by " + AppContoller.getEMPNO(FaultClosingActivity.this) + " While Internet connection is lost", null, null);
                                                sDialog.dismissWithAnimation();
                                                alerts.WarningAlert("Internet Connection lost. SMS has been sent");

                                            }

                                        }
                                    })
                                    .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            updateVisitLog(faultId);
                                            finish();
                                        }
                                    })
                                    .show();
                        } else {
                            alerts.ErrorAlert("Please select at least one item");
                        }

                }
            });

            }

    private void updateLteQuota(String Day, String Night,String fault_id) {

        System.out.println("* DAy "+Day+" Night "+Night+"fault_id "+fault_id);
        tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
        tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.not_balnce_quota));

        if(Day.equals("100")){
            tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
        }
        if(Night.equals("100")){
            tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
        }


        if(new AcceptFaultDAO(FaultClosingActivity.this).updateQuota_a(Day,Night,fault_id)>0){
            System.out.println("* success fully updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }

        if(new PendingFaultDAO(FaultClosingActivity.this).updateQuota(Day,Night,fault_id)>0){
            System.out.println("* success fully LteQuota updated");
        }else{
            System.out.println("* Update Lte Quota Failed");
        }


    }

    @Override
    public void onSuccess(String result, TaskType type) {

        switch (type) {
            case FATCH_ACC_STATUS: {
                Log.v("result",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    String sStatus = jsonObject.getString("Data").replace("\"","");
                    tvAccStatus.setText(sStatus);

                    if(!sStatus.replace("\"","").equals("REC")){
                        new ToastManager(this).error("Disconnected");
                    }

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }
            }
            break;
            case UPLOAD_COMPLETED_JOBS: {
                pDialog.hide();
                Log.v("UPLOAD_COMPLETED_JOBS", result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int ID = Integer.parseInt(jsonObject.getString("ID"));

                    Log.v("Json F",jsonArray.toString().replace("\\",""));

                    if(ID == 200){

                        JSONArray RMeterial = new MaterialIssuedDAO(FaultClosingActivity.this).getReturnMaterial(fault_id);

                        System.out.println("* RMeterial "+RMeterial.length());

                      //  for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(0);

                            int iStatus = data.getInt("status");
                             final String req_id = data.getString("requestId");

                            if (iStatus >= 1) {
                             //   new ToastManager(FaultClosingActivity.this).success("");
                                //update sync_id
                                int iUpdated = new CompetedJobDAO(FaultClosingActivity.this).updateIsSynced(req_id);
                                System.out.println("* updated "+iUpdated);

                                if(RMeterial.length()>0) {
                                 //   InsertASR(req_id);
                                    new ToastManager(FaultClosingActivity.this).success("Successfully Fault Closed");
                                    new SweetAlertDialog(FaultClosingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                            .setTitleText("Do you want to Create ASR?")
                                            .setConfirmText("Yes")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    InsertASR(req_id);
                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    alerts.SuccessAlert("Successfully Fault Closed");
                                                    updateVisitLog(req_id);
                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();


                                }else{
                                    alerts.SuccessAlert("Successfully Fault Closed");
                                    updateVisitLog(req_id);
                                }
                               // updateVisitLog(data.getString("requestId"));

                              //  int iUpdated = new CompetedJobDAO(FaultClosingActivity.this).updateIsSynced(data.getString("requestId"));

                            }else{
                                new ToastManager(FaultClosingActivity.this).error("Fault Closing is Failed");
                            }
                      //  }
                    }else{
                        new ToastManager(FaultClosingActivity.this).error("Fault Closing is Failed");
                    }


                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                }
            }
            break;

            case VIST_LOG:{

                Log.v("VIST_LOG success",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));

                 //   new ToastManager(FaultClosingActivity.this).success("Successfully Closed");
                    if(id == 200) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(i);

                            int iStatus = data.getInt("status");
                            String requestId = data.getString("requestId");

                            if (iStatus >= 1) {

                                int iUpdated = new CompetedJobDAO(FaultClosingActivity.this).updateIsVisitLogSynced(data.getString("requestId"));

                                System.out.println("* iUpdated "+iUpdated);
                            }

                        }
                    }else{

                        System.out.println("* visit log error with id"+id);
                    }
                    Intent intent = new Intent(FaultClosingActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                    Intent intent = new Intent(FaultClosingActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            } break;
            case ASR_CREATE:{

                Log.v("ASR_CREATE success",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));
                    //String requestId = jsonObject.getString("requestId");


                    sendSms(fault_id);
                    if(id == 200) {
                       // sendSms(fault_id);
                        alerts.SuccessAlert("Successfully ASR is Created");
                    }else{
                        new ToastManager(FaultClosingActivity.this).error("ASR Created is Failed");
                        System.out.println("* ASR_CREATE error with id"+id);
                    }
                    updateVisitLog(fault_id);

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }

            } break;

        }
    }
    /* case VIST_LOG:{
                new ToastManager(MaterialRequestActivity.this).success("Successfully Closed");
            }break;
            case UPLOAD_COMPLETED_JOBS:{
                new ToastManager(MaterialRequestActivity.this).error("Fault Closing Failed");
            }break;*/
    @Override
    public void onError(String result, TaskType type) {
        switch (type){
            case VIST_LOG:{
                if (!new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(FaultClosingActivity.this).error("Connection Failed");
                }else{
                    new ToastManager(FaultClosingActivity.this).success("Successfully Closed");
                }
                System.out.println("* error "+result+" type "+type);
                Intent intent = new Intent(FaultClosingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }break;
            case UPLOAD_COMPLETED_JOBS:{
                pDialog.hide();
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(FaultClosingActivity.this).error("Fault Closing is failed");
                }else{
                    new ToastManager(FaultClosingActivity.this).error("Connection Failed");
                }
                Intent intent = new Intent(FaultClosingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case ASR_CREATE:{
                updateVisitLog(fault_id);
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(FaultClosingActivity.this).error("ASR created is failed");
                }else{
                    new ToastManager(FaultClosingActivity.this).error("Connection Failed");
                }

            }
            break;
            case CHECK_QUOTA:{
                System.out.println("* ----- tvQuotaBalanceDay "+tvQuotaBalanceDay);
                System.out.println("* ----- tvQuotaBalanceNight "+tvQuotaBalanceNight);

            }
            break;
        }


    }

    @Override
    public void onSuccess(String Data, TaskType type, String fault_id) {

        switch(type) {
            case CHECK_QUOTA: {

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


            }break;
        }

    }
}
