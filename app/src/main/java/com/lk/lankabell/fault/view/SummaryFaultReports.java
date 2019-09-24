package com.lk.lankabell.fault.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.AcceptedFaultAdapter;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.control.ClickListener;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.lk.lankabell.fault.control.TaskType.CHECK_QUOTA;

public class SummaryFaultReports extends AppCompatActivity implements VolleyCallback {


    Button get;
    Button to_date;
    Button from_date;
    Button status;
    public static String sFromDate;
    public static String sToDate;
    public static String tv_status;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener toDate;
    DatePickerDialog.OnDateSetListener fromDate;
    String myFormat = "MM/dd/yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    private RecyclerView recyclerView;
    AcceptedFaultAdapter acceptedFaultAdapter;
    ArrayList <PendingFaults> list;
    TextView tvAccStatus;
    String CustomerPhoneNumber ="";
    TextView tvQuotaBalanceDay;
    TextView tvQuotaBalanceNight;
    SweetAlertDialog pDialog;
    private final static int CALL_PERMISSSION_REQUEST_CODE = 222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_fault_reports);
        getSupportActionBar().setTitle("SUMMARY REPORTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rvView);
        list = new ArrayList<>();
        list.clear();
        get = findViewById(R.id.btn_history);
        to_date = findViewById(R.id.btn_history_to);
        from_date = findViewById(R.id.btn_history_from);
        status = findViewById(R.id.btn_status);
        String currentDate = new DateManager().getTodayDateString1();
        to_date.setText(currentDate);
        tv_status = "COMPLETED";
        AskPhoneCallPermission();
        from_date.setText(new DateManager().getPreviousWeekDateString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        if(new ConnectionDetector(SummaryFaultReports.this).isConnectingToInternet()){
            new SyncData(getApplicationContext())
                    .fetchHistoryReport(SummaryFaultReports.this, TaskType.FATCH_FAULT_HISTORY,from_date.getText().toString(),to_date.getText().toString(),tv_status);
        }
        to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SummaryFaultReports.this, R.style.CalendarTheme, toDate,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                sToDate = sdf.format(myCalendar.getTime());
//                sToDate = sdf_undivided.format(myCalendar.getTime());
//                to_date.setText(sToDate);
                to_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SummaryFaultReports.this, R.style.CalendarTheme, fromDate,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final SpinnerDialog spinnerDialog;
        ArrayList<String> statusAll = new ArrayList<>();
        statusAll.add("PENDING");
        statusAll.add("ACCEPTED");
        statusAll.add("CLOSED");
        spinnerDialog=new SpinnerDialog(SummaryFaultReports.this,statusAll,"Select or Search Status",R.style.DialogAnimations_SmileWindow);// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
               // Toast.makeText(SummaryFaultReports.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                status.setText(item);
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        fromDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                from_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //VALIDATION

                list.clear();
                //acceptedFaultAdapter.notifyDataSetChanged();

                sFromDate = from_date.getText().toString();
                sToDate = to_date.getText().toString();
                // //status = NEW , ACCEPTED , COMPLETED

                if(status.getText().toString().equals("PENDING")){
                    tv_status = "NEW";
                }else if(status.getText().toString().equals("ACCEPTED")){
                    tv_status = "ACCEPTED";
                }else if(status.getText().toString().equals("CLOSED")){
                    tv_status = "COMPLETED";
                }

                if(new ConnectionDetector(SummaryFaultReports.this).isConnectingToInternet()) {
//                    Toast.makeText(SummaryFaultReports.this,
//                            "From: " + sFromDate + " - To: " + sToDate + " tv_status " + tv_status, Toast.LENGTH_LONG).show();
                    pDialog.show();
                    new SyncData(getApplicationContext())
                            .fetchHistoryReport(SummaryFaultReports.this, TaskType.FATCH_FAULT_HISTORY, sFromDate, sToDate, tv_status);
                }else{
                    pDialog.hide();
                    new ToastManager(SummaryFaultReports.this).error("Connection Error");
                }

            }
        });



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

    private void AskPhoneCallPermission(){
        if (ContextCompat.checkSelfPermission(SummaryFaultReports.this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(SummaryFaultReports.this,
                    android.Manifest.permission.CALL_PHONE)) {
                AskPhonePermission();
                new ToastManager(SummaryFaultReports.this).error("Can't make any calls");
            } else {
                ActivityCompat.requestPermissions(SummaryFaultReports.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PERMISSSION_REQUEST_CODE);

            }
        }
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
                if (new ConnectionDetector(SummaryFaultReports.this).isConnectingToInternet()) {
                    new SyncData(SummaryFaultReports.this).CheckQuotaStatus(SummaryFaultReports.this, CHECK_QUOTA, faults.getPF_REQUESTID());
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
                        new SweetAlertDialog(SummaryFaultReports.this, SweetAlertDialog.NORMAL_TYPE)
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
                        new ToastManager(SummaryFaultReports.this).error("Invalid Phone Number");
                    }

                } catch (ActivityNotFoundException e) {
                    new ToastManager(SummaryFaultReports.this).error("Error in your phone call because of"+e.getMessage());
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
            tvQuotaBalance.setVisibility(View.GONE);
            llQuotaBalance.setVisibility(View.GONE);
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
        bSubmit.setVisibility(View.GONE);

        TextView bReject = (TextView)dialog.findViewById(R.id.bReject);
        bReject.setVisibility(View.GONE);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CALL_PERMISSSION_REQUEST_CODE:{
                call(CustomerPhoneNumber);
            }break;
        }


    }
    private void call(String phone_number) {

        Intent my_callIntent = new Intent(Intent.ACTION_CALL);
        if (!phone_number.equals("null")) {
            my_callIntent.setData(Uri.parse("tel:" + phone_number));
            //here the word 'tel' is important for making a call...

            if (ActivityCompat.checkSelfPermission(SummaryFaultReports.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            new ToastManager(SummaryFaultReports.this).error("Invalid Phone Number");
        }
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
    private String CheckInt(String someString) {
        if (someString.equals(null)) {
            return "0";
        } else if (someString.equals("null")) {
            return "0";
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

    @Override
    public void onError(String message, TaskType type) {
        if(acceptedFaultAdapter!=null)
            acceptedFaultAdapter.notifyDataSetChanged();
        if(type == TaskType.FATCH_FAULT_HISTORY){
            pDialog.hide();
            if(message.equals("No Existing Data")){
                new ToastManager(SummaryFaultReports.this).warning(message);
            }else{
                new ToastManager(SummaryFaultReports.this).error("Download Failed "+message);
            }
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

    @Override
    public void onSuccess(String result, TaskType type) {
        //result here.
        Log.v("Results",result);

        switch (type) {

            case FATCH_FAULT_HISTORY:{

            try {
                pDialog.hide();
                JSONObject jsonObject = new JSONObject(result.toString());

                final JSONArray jsonArray = jsonObject.getJSONArray("Data");

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
//                faults.setPF_ISACCEPT(data.getString("IsAccept"));
                    faults.setPF_REQUESTTOLOCATION(data.getString("RequestToLocation"));
                    faults.setPF_SERVICETYPE(data.getString("ServiceType"));
                    faults.setPF_CUSTOMERCATEGORY(data.getString("CustomerCategory"));
                    faults.setPF_CUSTOMERRATINGS(data.getString("CustomerRatings"));
                    faults.setPF_CUSTOMERREMARKS(data.getString("CustomerRemarks"));
                    faults.setPF_DIRECTION(data.getString("Direction"));
                    faults.setACTIVE_SCREEN(data.getString("Status"));

                    list.add(faults);

                }

                acceptedFaultAdapter = new AcceptedFaultAdapter(this, list, SummaryFaultReports.this, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position, View v) {
                        final PendingFaults fault = list.get(position);

                        PendingFaults faults = list.get(position);
                        String fault_id = faults.getPF_REQUESTID();
                        showFaultDetails(faults);
                        new SyncData(SummaryFaultReports.this).checkAccStatus(SummaryFaultReports.this, TaskType.FATCH_ACC_STATUS, fault.getPF_REQUESTID());
                        if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")) {
                            new SyncData(SummaryFaultReports.this).CheckQuotaStatus(SummaryFaultReports.this, CHECK_QUOTA, fault_id);
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
                //set Data and refrese adapter
                acceptedFaultAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            } break;


            case FATCH_ACC_STATUS: {
                Log.v("result",result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    String sStatus = jsonObject.getString("Data").replace("\"","");
                    tvAccStatus.setText(sStatus);

                    if(!sStatus.replace("\"","").equals("REC")){
                        new ToastManager(this).error("Disconnected");
                    }
//

                } catch (JSONException e){
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }
            }
            break;
        }
    }
}
