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
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.PendingFaultAdapter;
import com.lk.lankabell.fault.adapter.RecyclerItemClickListener;
import com.lk.lankabell.fault.adapter.RecyclerItemTouchHelper;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.LocationDAO;
import com.lk.lankabell.fault.control.Data.PendingFaultDAO;
import com.lk.lankabell.fault.control.Data.RejectDAO;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.model.LocationDetails;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;

import static com.lk.lankabell.fault.control.TaskType.CHECK_QUOTA;

public class ToAcceptFaultActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, VolleyCallback {
    private RecyclerView recyclerView;

    ArrayList<PendingFaults> list;
    PendingFaultAdapter faultAdapter;
    private CoordinatorLayout coordinatorLayout;
    Alerts alerts;
    double lattitude,longitude;

    private SimpleLocation location;
    private final static int CALL_PERMISSSION_REQUEST_CODE = 222;
    String CustomerPhoneNumber ="";
    TextView tvQuotaBalanceDay;
    TextView tvQuotaBalanceNight;

    String fault_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_accept);
        getSupportActionBar().setTitle("TO ACCEPT JOBS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //coordinator_layout
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        alerts = new Alerts(ToAcceptFaultActivity.this);
        location = new SimpleLocation(this);
        //Pending Faults - Lists code
        recyclerView = (RecyclerView) findViewById(R.id.rvView);
        checkLocationPermission();
        AskPhoneCallPermission();
        CheckGPS();
        list = new PendingFaultDAO(this).getAllPendingFaults();
        faultAdapter = new PendingFaultAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(faultAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        final PendingFaults fault = list.get(position);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Ref Id "
                                +fault.getPF_REQUESTTOREFID().toString(), Snackbar.LENGTH_LONG);

                        snackbar.show();

                        if(fault!=null) {

                            showFaultDetails(fault);

                            new SyncData(ToAcceptFaultActivity.this)
                                    .checkAccStatus(ToAcceptFaultActivity.this,
                                            TaskType.FATCH_ACC_STATUS,fault.getPF_REQUESTID());
                            fault_id = fault.getPF_REQUESTID();

                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever

                    }
                })
        );

        //set Data and refrese adapter
        faultAdapter.notifyDataSetChanged();


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, ToAcceptFaultActivity.this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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


    @Override
    protected void onPause() {
        super.onPause();
        location.endUpdates();
    }
    private void AskPhonePermissionAgain() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Nowx
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},CALL_PERMISSSION_REQUEST_CODE);

        } else {
            // permission has been granted, continue as usual
        }
    }

    private void AskPhoneCallPermission(){
        if (ContextCompat.checkSelfPermission(ToAcceptFaultActivity.this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(ToAcceptFaultActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {
                AskPhonePermissionAgain();
                new ToastManager(ToAcceptFaultActivity.this).error("Can't make any calls");
            } else {
                ActivityCompat.requestPermissions(ToAcceptFaultActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PERMISSSION_REQUEST_CODE);

            }
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

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 2000) {
            getLocations();
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
        //list.clear();
       // recyclerView.clearFocus();
        list = new PendingFaultDAO(this).getAllPendingFaults();
        //set Data and refrese adapter
        checkLocationPermission();
        faultAdapter.notifyDataSetChanged();
    }

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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        final PendingFaults fault = list.get(viewHolder.getAdapterPosition());

        final int acceptedIndex = viewHolder.getAdapterPosition();
        if(new ConnectionDetector(this).isConnectingToInternet()) {
            // remove the item from recycler view
            faultAdapter.removeItem(viewHolder.getAdapterPosition());

            //ACCEPTING

            int success = new AcceptFaultDAO(this).insertORupdate(fault,"0");

            if(success>=1) {
                // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Ref ID " + fault.getPF_REQUESTTOREFID() + " accepted", Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        int iStatus = new AcceptFaultDAO(ToAcceptFaultActivity.this).deleteByrequestId(fault.getPF_REQUESTID());
                        if(iStatus>=1)
                            Log.v("deleteByrequestId",iStatus+"");
                            faultAdapter.restoreItem(fault, acceptedIndex);

                    }
                });
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();
            }

        }else {
            new ConnectionDetector(this).showSettingsAlert();
        }

    }
    private String CheckString(String someString) {
        if(someString.equals(null)){
            return "-";
        }else if(someString.equals("null")){
            return "-";
        }else{
            return someString;
        }
    }
    private String CheckStringAddress(String pf_requesttoadd1, String pf_requesttoadd2, String pf_requesttoadd3) {
        if(pf_requesttoadd1.equals("null")&&pf_requesttoadd2.equals("null")&&pf_requesttoadd3.equals("null")){
            return "-";
        }else{
            return returnEmptyString(pf_requesttoadd1)+" "+returnEmptyString(pf_requesttoadd2)+" "+returnEmptyString(pf_requesttoadd3);
        }
    }
    private String returnEmptyString(String someString) {
        if(someString.equals(null)){
            return "";
        }else if(someString.equals("null")){
            return "";
        }else{
            return someString;
        }
    }
    private String CheckInt(String someString) {
        if(someString.equals(null)){
            return "0";
        }else if(someString.equals("null")){
            return "0";
        }else{
            return someString;
        }
    }
    TextView tvAccStatus;
    private void showFaultDetails(final PendingFaults faults) {
        final View dialogView = View.inflate(this,R.layout.show_details_pending_fault,null);
        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        TextView tvRequestId = (TextView) dialog.findViewById(R.id.tvRequestId);
        tvRequestId.setText(CheckString(faults.getPF_REQUESTID()));

        tvAccStatus= (TextView) dialog.findViewById(R.id.tvAccStatus);
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
        tvAddress.setText(CheckStringAddress(faults.getPF_REQUESTTOADD1(),faults.getPF_REQUESTTOADD2(),faults.getPF_REQUESTTOADD3()));

        TextView tvAssignedDate = (TextView) dialog.findViewById(R.id.tvAssignedDate);
        tvAssignedDate.setText(CheckString(faults.getPF_REQUESTASSIGNEDDATE()));

        TextView tvStatus = (TextView) dialog.findViewById(R.id.tvStatus);
        if(faults.getPF_STATUS().toUpperCase().equals("NEW"))
            tvStatus.setText("Pending");

        TextView tvRequestType = (TextView) dialog.findViewById(R.id.tvRequestType);
        if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
            tvRequestType.setText("LTE");
        }else if(faults.getPF_REQUESTTYPE().toUpperCase().equals("C")){
            tvRequestType.setText("CDMA");
        }

        ImageView refresh =  dialog.findViewById(R.id.ivRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionDetector(ToAcceptFaultActivity.this).isConnectingToInternet()) {
                    new SyncData(ToAcceptFaultActivity.this).CheckQuotaStatus(ToAcceptFaultActivity.this, CHECK_QUOTA, faults.getPF_REQUESTID());
                }else{
                    System.out.println("* network error");
                }
            }
        });

        fault_id = faults.getPF_REQUESTID();

        ImageView call_contact = dialog.findViewById(R.id.call_contact);

        call_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call
                try {

                    if(!tvContact.getText().toString().equals("0")) {
                        new SweetAlertDialog(ToAcceptFaultActivity.this, SweetAlertDialog.NORMAL_TYPE)
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
                    }else{
                        new ToastManager(ToAcceptFaultActivity.this).error("Invalid Phone Number");
                    }

                } catch (ActivityNotFoundException e) {
                    new ToastManager(ToAcceptFaultActivity.this).error("Error in your phone call because of"+e.getMessage());
                }
            }
        });


        TextView tvQuotaBalance = dialog.findViewById(R.id.tvQuotaBalance);
        LinearLayout llQuotaBalance = dialog.findViewById(R.id.llQuotaBalance);

       tvQuotaBalanceDay = dialog.findViewById(R.id.tvQuotaBalanceDay);
       tvQuotaBalanceNight = dialog.findViewById(R.id.tvQuotaBalanceNight);

        if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
            tvQuotaBalance.setVisibility(dialogView.VISIBLE);
            llQuotaBalance.setVisibility(dialogView.VISIBLE);

            String day = "0";
            String night = "0";

            if(faults.getPF_LTE_DAY()!= null){
                day = CheckInt(faults.getPF_LTE_DAY());
            }
            if(faults.getPF_LTE_NIGHT()!= null){
                night = CheckInt(faults.getPF_LTE_NIGHT());
            }


            switch(day){
                case "100":{
                    tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
                }break;
                case "0":{
                    tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
                }break;
            }
            switch(night){
                case "100":{
                    tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
                }break;
                case "0":{
                    tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
                }break;
            }
        }else{
            tvQuotaBalance.setVisibility(dialogView.GONE);
            llQuotaBalance.setVisibility(dialogView.GONE);
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

        TextView bSubmit = dialog.findViewById(R.id.bSubmit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int success = new AcceptFaultDAO(ToAcceptFaultActivity.this).insertORupdate(faults,"0");
                if(success >=1){
                    successAlert("Fault Accepted!",dialogView, dialog);
                }

//                revealShow(dialogView, false, dialog);
            }
        });

        TextView bReject = (TextView)dialog.findViewById(R.id.bReject);
        bReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRejectWarning(faults,dialogView,dialog);
                //revealShow(dialogView, false, dialog);
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



    private void call(String phone_number) {

        Intent my_callIntent = new Intent(Intent.ACTION_CALL);
        if (!phone_number.equals("null")) {
            my_callIntent.setData(Uri.parse("tel:" + phone_number));
            //here the word 'tel' is important for making a call...

            if (ActivityCompat.checkSelfPermission(ToAcceptFaultActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            new ToastManager(ToAcceptFaultActivity.this).error("Invalid Phone Number");
        }
    }
    public void showRejectWarning(final PendingFaults faults,final View dialogView,final Dialog dialog){
        new SweetAlertDialog(ToAcceptFaultActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Do you want to reject?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        int Rejects = new RejectDAO(ToAcceptFaultActivity.this).insertORupdate(faults);
                        if(Rejects >=1){
                            sDialog.dismissWithAnimation();
                            successAlert("Fault Rejected!",dialogView, dialog);
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


    public void successAlert(final String sResult, final View dialogView, final Dialog dialog1){
        final SweetAlertDialog dialog = new SweetAlertDialog(ToAcceptFaultActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitleText(sResult);
        dialog.setConfirmText("OK");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                revealShow(dialogView, false, dialog1);
                dialog.cancel();

                setRecyclerViewRefresh();
//                Activity activity = (Activity) context;
//                activity.finish();
            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_PERMISSSION_REQUEST_CODE) {
            call(CustomerPhoneNumber);
        }
    }

    public void setRecyclerViewRefresh(){

        list = new PendingFaultDAO(this).getAllPendingFaults();
        faultAdapter = new PendingFaultAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(faultAdapter);
        recyclerView.setNestedScrollingEnabled(false);
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
    private void updateLteQuota(String Day, String Night,String fault_id) {

        System.out.println("* DAy "+Day+" Night "+Night);
        switch(Day){
            case "100":{
                tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.balance_quota));
            }break;
            case "0":{
                tvQuotaBalanceDay.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.not_balnce_quota));
            }break;
        }
        switch(Night) {
            case "100": {
                tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.balance_quota));
            }
            break;
            case "0": {
                tvQuotaBalanceNight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.not_balnce_quota));
            }
            break;
        }
        if (new PendingFaultDAO(ToAcceptFaultActivity.this).updateQuota(Day, Night, fault_id) > 0) {
            System.out.println("* success fully updated");
        } else {
            System.out.println("* Update Lte Quota Failed");
        }


    }

    @Override
    public void onSuccess(String id, TaskType type, String faultType) {

    }
    @Override
    public void onSuccess(String result, TaskType type) {

        switch(type){
            case FATCH_ACC_STATUS : {
                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    String sStatus = jsonObject.getString("Data");
                    tvAccStatus.setText(sStatus.replace("\"", ""));

                    if (!sStatus.replace("\"", "").equals("REC")) {
                        new ToastManager(this).error("Disconnected");
                    }


                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                }
            }
            break;
            case CHECK_QUOTA :{
               try{
                   System.out.println("* CHECK_QUOTA "+result);

                   switch (result) {
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
               }catch (Exception e){
                   e.printStackTrace();
               }

            }break;

        }
    }

    @Override
    public void onError(String message, TaskType type) {
        switch (type){
            case CHECK_QUOTA:{
                System.out.println("* message "+message);
            }
        }
    }
}


