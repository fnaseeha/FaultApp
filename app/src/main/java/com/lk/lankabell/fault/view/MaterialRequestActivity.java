package com.lk.lankabell.fault.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.AsrIssueAdapter;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.MRDetailsAdapter;
import com.lk.lankabell.fault.adapter.SummaryAdapter;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.control.Data.ActionTakenDAO;
import com.lk.lankabell.fault.control.Data.BiItemsDAO;
import com.lk.lankabell.fault.control.Data.CompetedJobDAO;
import com.lk.lankabell.fault.control.Data.MaterialIssuedDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.helper.Alerts;
import com.lk.lankabell.fault.helper.Other;
import com.lk.lankabell.fault.model.CompetedJob;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.MaterialIssued;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class MaterialRequestActivity extends AppCompatActivity implements VolleyCallback {
    private RecyclerView recyclerView;

    ArrayList<IssuedMaterialDetails> list;
    MRDetailsAdapter mrDetailsAdapter;
    private CoordinatorLayout coordinatorLayout;

    public static PendingFaults fault;
    Button b4GLTE, bCDMA, bSIM, bOther;
    Alerts alerts;
    int count = 0;
    private final static int SEND_PERMISSSION_REQUEST_CODE = 111;
    String phone_number;
    SweetAlertDialog pDialog;
    SpinnerDialog spinnerDialog, spinnerBiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_request_details);
        getSupportActionBar().setTitle("MR - AVAILABLE UNITS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //coordinator_layout
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        alerts = new Alerts(MaterialRequestActivity.this);
        //Pending Faults - Lists code
        recyclerView = (RecyclerView) findViewById(R.id.rvView);
        phone_number = AppContoller.getPhoneNumber(MaterialRequestActivity.this);
        setRecyclerViewDate("LTEU");

        b4GLTE = (Button) findViewById(R.id.b4GLTE);
        bCDMA = (Button) findViewById(R.id.bCDMA);
        bSIM = (Button) findViewById(R.id.bSIM);
        bOther = (Button) findViewById(R.id.bOther);

        AskSmsPermission();
        new SyncData(MaterialRequestActivity.this).itemIssueValidation(MaterialRequestActivity.this, TaskType.IS_4GLTE_VALIDATION_NEW, fault.getPF_REQUESTID(), "LTE");

        b4GLTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("* fult_id  b4GLTE" + fault.getPF_REQUESTID());
                new SyncData(MaterialRequestActivity.this).itemIssueValidation(MaterialRequestActivity.this, TaskType.IS_4GLTE_VALIDATION, fault.getPF_REQUESTID(), "LTE");
                // Clear collection..
            }
        });


        bCDMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("* fault_id  bCDMA" + fault.getPF_REQUESTID());
                new SyncData(MaterialRequestActivity.this).itemIssueValidation(MaterialRequestActivity.this, TaskType.IS_CDMA_VALIDATION, fault.getPF_REQUESTID(), "CDMA");
            }
        });


        bSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("* fult_id bSIM" + fault.getPF_REQUESTID());
                new SyncData(MaterialRequestActivity.this).itemIssueValidation(MaterialRequestActivity.this, TaskType.IS_SIM_VALIDATION, fault.getPF_REQUESTID(), "LTE");
            }
        });


        bOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list.clear();
                recyclerView.getAdapter().notifyDataSetChanged();

                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                setRecyclerViewDate("OTHER");
            }
        });

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
    }


    String sReturnIMEI = "";
    String sItemCode = "";

    private void setRecyclerViewDate(final String type) {


        list = new PendingIssuedMaterialDAO(this).getAllAcceptedMaterial(type);
        System.out.println("* list other " + list.size());

        mrDetailsAdapter = new MRDetailsAdapter(MaterialRequestActivity.this, list, fault, new ClickListenerMaterial() {

            @Override
            public void onPositionClicked(int position, View v, CheckBox checkBox) {


                if (v.getId() == R.id.btnCheck) {
                    System.out.println("*  btnCheck is checked " + checkBox.isChecked() + " count " + count);

                    if (checkBox.isChecked()) {

                        count++;
                        IssuedMaterialDetails materialDetails = list.get(position);
                        checkBox.setChecked(false);
                        //  new ToastManager(MaterialRequestActivity.this).success("Checked " + materialDetails.getItemCode());
                        if (!type.equals("OTHER")) {

                            if (type.equals("LTES")) { //SIM
                                //     new ToastManager(MaterialRequestActivity.this).success(""+materialDetails.getSerial());
                                new SyncData(MaterialRequestActivity.this)
                                        .FetchExistingLTEImsi(MaterialRequestActivity.this,
                                                TaskType.GET_PAST_ENS_IMEI, materialDetails.getSerial(), fault);
                                isHaveReturnDialog(materialDetails, checkBox, type);
                            }

                            if (type.equals("CPHN")) { //CDMA
                                //    new ToastManager(MaterialRequestActivity.this).success(""+materialDetails.getSerial());
                                new SyncData(MaterialRequestActivity.this)
                                        .FetchExisitingCDMA(MaterialRequestActivity.this,
                                                TaskType.GET_PAST_ENS_IMEI, materialDetails.getSerial(), fault);
                                isHaveReturnDialog(materialDetails, checkBox, type);
                            }

                            if (type.equals("LTEU")) { //UNIT
                                //   new ToastManager(MaterialRequestActivity.this).success(""+materialDetails.getSerial());
                                new SyncData(MaterialRequestActivity.this)
                                        .FetchExisitingLTEEmei(MaterialRequestActivity.this,
                                                TaskType.GET_PAST_ENS_IMEI, materialDetails.getSerial(), fault);
                                isHaveReturnDialog(materialDetails, checkBox, type);
                            }

                        } else {
                            System.out.println("* materialDetails.getItemCode() " + materialDetails.getItemCode());
                            isHaveReturnDialog(materialDetails, checkBox, type);
                            // imm Supervisor requested to hard code those items are ASR items. materialDetails.getItemCode().equals("CDHWCU010108")
                            //                                    || materialDetails.getItemCode().equals("CDHWCU010110")
                            //                                        || materialDetails.getItemCode().equals("LTCUH010104") but cancelled on 28th June 2019
                        }

                    } else {
                        count--;
                        int delteOth = 0;
                        if (type.equals("OTHER")) {
                            delteOth = new MaterialIssuedDAO(MaterialRequestActivity.this)
                                    .deleteByItemNoOther(list.get(position).getItemCode(), fault.getPF_REQUESTID(), type);
                        } else {
                            delteOth = new MaterialIssuedDAO(MaterialRequestActivity.this)
                                    .deleteByItemNo(list.get(position).getItemCode(), fault.getPF_REQUESTID(), type);
                            // int up = new MaterialIssuedDAO(MaterialRequestActivity.this).updateIMEI_ESN_ItemCode("",list.get(position).getSerial());
                            // System.out.println("* up erased "+up);
                        }
                        System.out.println("* deleted " + delteOth + " type " + type);

//                        Toast.makeText(MaterialRequestActivity.this,
//                                "Unchecked (" + list.get(position).getItemCode() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mrDetailsAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        //set Data and refrese adapter
        mrDetailsAdapter.notifyDataSetChanged();

    }

    private void InsertASR(final String faultId) {

        final View dialogView = View.inflate(this, R.layout.layout_show_asr_items, null);
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final TextView tvRequestId = (TextView) dialog.findViewById(R.id.tvRequestId);
        final RecyclerView rvView = (RecyclerView) dialog.findViewById(R.id.rvView);
        final Button bSubmit = (Button) dialog.findViewById(R.id.bSubmit);
        final Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
        final TextView tvBICode = (TextView) dialog.findViewById(R.id.tvBICode);

        ArrayList<String> AllBiItems = new BiItemsDAO(MaterialRequestActivity.this).getAllActions();
        spinnerBiDialog = new SpinnerDialog(MaterialRequestActivity.this, AllBiItems, "Select or Search BI Item", R.style.DialogAnimations_SmileWindow);// With 	Animation

        spinnerBiDialog.setCancellable(true); // for cancellable
        spinnerBiDialog.setShowKeyboard(false);// for open keyboard by default

        spinnerBiDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                tvBICode.setText(item);
            }
        });

        tvBICode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerBiDialog.showSpinerDialog();

            }
        });

        tvRequestId.setText("Fault ID : " + faultId);

        final ArrayList<MaterialIssued> lists = new MaterialIssuedDAO(MaterialRequestActivity.this)
                .getReturnMaterialList(faultId);

        final ArrayList<MaterialIssued> selectedM = new ArrayList<>();

        AsrIssueAdapter asrAdapter = new AsrIssueAdapter(lists, MaterialRequestActivity.this, new ClickListenerMaterial() {

            @Override
            public void onPositionClicked(int position, View view, CheckBox btn) {
                if (view.getId() == R.id.btnCheck) {
                    if (btn.isChecked()) {
                        selectedM.add(lists.get(position));
                    } else {
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
                // showViewSummaryReport(fault);
            }
        });
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (tvBICode.getText().toString().equals("")) {
//                    alerts.ErrorAlert("Please select a BI Item");
//                } else {
                if (selectedM.size() > 0) {
                    new SweetAlertDialog(MaterialRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
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

                                        JSONArray ReturnUnitList = new CompetedJobDAO(MaterialRequestActivity.this).getReturnUnitDetails(faultId, selectedM);

                                        JSONObject object = new JSONObject();

                                        try {
                                            object.put("data", ReturnUnitList);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Log.v("Json ", object.toString().replace("\\", ""));

                                        System.out.println("* phone_number " + phone_number);

                                        smsManager.sendTextMessage(phone_number, null,
                                                "ASR has been created for this Fault id : " + faultId + " and itemCodes are " + selected_itemCode + " by " + AppContoller.getEMPNO(MaterialRequestActivity.this), null, null);
                                        sDialog.dismissWithAnimation();
                                        new SyncData(MaterialRequestActivity.this).InsertASR(MaterialRequestActivity.this, TaskType.ASR_CREATE, object);

                                    } else {
                                        smsManager.sendTextMessage(phone_number, null,
                                                "ASR has been created for this Fault id : " + faultId + " and itemCodes are " + selected_itemCode + " by " + AppContoller.getEMPNO(MaterialRequestActivity.this) + " While Internet connection is lost", null, null);
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


    private void AskSmsPermission() {
        if (ContextCompat.checkSelfPermission(MaterialRequestActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MaterialRequestActivity.this,
                    android.Manifest.permission.SEND_SMS)) {
                new ToastManager(MaterialRequestActivity.this).error("SMS Won't send to Coordinator!");
            } else {
                ActivityCompat.requestPermissions(MaterialRequestActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_PERMISSSION_REQUEST_CODE);

            }
        }
    }

    private void sendSms(String fault_id) {


        if (ContextCompat.checkSelfPermission(MaterialRequestActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MaterialRequestActivity.this,
                    android.Manifest.permission.SEND_SMS)) {
                new ToastManager(MaterialRequestActivity.this).error("SMS Won't send to Coordinator");

            } else {
                ActivityCompat.requestPermissions(MaterialRequestActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_PERMISSSION_REQUEST_CODE);

            }
        } else {
            // Permission already granted
            sendSMSBody(phone_number, fault_id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_PERMISSSION_REQUEST_CODE) {
            sendSMSBody(phone_number, fault.getPF_REQUESTID());
        }
    }

    private void sendSMSBody(String phone_number, String fault_id) {

        SmsManager smsManager = SmsManager.getDefault();
        System.out.println("* phone_number " + phone_number);
        smsManager.sendTextMessage(phone_number, null, "ASR has been created for this Fault id : " + fault_id, null, null);
    }

    public void isHaveReturnDialog(final IssuedMaterialDetails materialDetails, final CheckBox checkBox, final String type) {
        new SweetAlertDialog(MaterialRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Is return?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if (type.equals("OTHER")) {
                            checkBox.setChecked(true);
                            int status = insertORupdatIssueDetails("1", materialDetails, type);

                            if (status > 0) {
                                setRecyclerViewDate(type);
                                // askAsrCreated(fault, materialDetails,sDialog);
                            }
                        } else {
                            // checkBox.setChecked(true);
                            showAddIssuedMaterial(materialDetails, checkBox, type);
                        }
                        sDialog.dismissWithAnimation();

                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                        MaterialIssued issued = new MaterialIssued();
                        issued.setMATERIAL_ISSUED_PIM_ID(materialDetails.getsID());
                        issued.setMATERIAL_ISSUED_ITEM_NO(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_REQUESTED_ID(fault.getPF_REQUESTID());
                        issued.setMATERIAL_ISSUED_ISSUE_NO(materialDetails.getIssueNo());
                        issued.setMATERIAL_ISSUED_GIVEN_IMEI_ESN(materialDetails.getSerial());//newSerial

                        issued.setMATERIAL_ISSUED_RETURN_IMEI_ESN("");//oldSerial
                        issued.setMATERIAL_ISSUED_EXISTING_ITEM_CODE(sItemCode);
                        issued.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN("");
                        issued.setMATERIAL_ISSUED_RETURN_STATUS("");
                        issued.setMATERIAL_ISSUED_IS_ASR("1");
                        issued.setMATERIAL_ISSUED_IS_RETURN("0");

                        if (type.equals("OTHER")) {
                            issued.setMATERIAL_ISSUED_TYPE(materialDetails.getItemType());//type
                            issued.setMATERIAL_ISSUED_TYPE_OTHER(type);
                        } else {
                            issued.setMATERIAL_ISSUED_TYPE(type);
                        }
                        checkBox.setChecked(true);
                        int status = new MaterialIssuedDAO(MaterialRequestActivity.this).insert(issued);

                        if (status > 0) {
                            setRecyclerViewDate(type);
                        }

                        // new ToastManager(MaterialRequestActivity.this).success("ASR CREATED!");
                    }
                })
                .show();
    }

    private int insertORupdatIssueDetails(String returnValue, IssuedMaterialDetails materialDetails, String type) {
        System.out.println("* newSerial " + materialDetails.getSerial());//
        System.out.println("* oldSerial " + sReturnIMEI);//sReturnIMEI
        System.out.println("* sItemCode " + sItemCode);//sReturnIMEI
        System.out.println("* type " + type);//sReturnIMEI


        MaterialIssued issued = new MaterialIssued();
        issued.setMATERIAL_ISSUED_PIM_ID(materialDetails.getsID());
        issued.setMATERIAL_ISSUED_ITEM_NO(materialDetails.getItemCode());
        issued.setMATERIAL_ISSUED_REQUESTED_ID(fault.getPF_REQUESTID());
        issued.setMATERIAL_ISSUED_ISSUE_NO(materialDetails.getIssueNo());
        issued.setMATERIAL_ISSUED_GIVEN_IMEI_ESN(materialDetails.getSerial());//newSerial

        issued.setMATERIAL_ISSUED_RETURN_IMEI_ESN(sReturnIMEI);//oldSerial
        issued.setMATERIAL_ISSUED_EXISTING_ITEM_CODE(sItemCode);
        issued.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN("");
        issued.setMATERIAL_ISSUED_RETURN_STATUS("");
        issued.setMATERIAL_ISSUED_IS_ASR("1");

        if (returnValue.equals("0")) {
            issued.setMATERIAL_ISSUED_IS_RETURN("0");
        } else {
            issued.setMATERIAL_ISSUED_IS_RETURN("1");
        }

        if (type.equals("OTHER")) {
            issued.setMATERIAL_ISSUED_TYPE(materialDetails.getItemType());//type
            issued.setMATERIAL_ISSUED_TYPE_OTHER(type);
        } else {
            issued.setMATERIAL_ISSUED_TYPE(type);
        }
        System.out.println("* material " + materialDetails);
// new MaterialIssuedDAO(MaterialRequestActivity.this).deleteByItemNo(materialDetails.getItemCode(),tvRequestID.getText().toString(),type);
        if (type.equals("OTHER")) {

            int g = new MaterialIssuedDAO(MaterialRequestActivity.this)
                    .deleteByItemNoOther(materialDetails.getItemCode(), fault.getPF_REQUESTID(), type);
            System.out.println("* delete " + g);
        } else {
            int g = new MaterialIssuedDAO(MaterialRequestActivity.this)
                    .deleteByItemNo(materialDetails.getItemCode(), fault.getPF_REQUESTID(), type);
            System.out.println("* delete " + g);
        }

        System.out.println("* issued " + issued);
        return new MaterialIssuedDAO(MaterialRequestActivity.this).insertORupdate(issued);
    }

//    private void askAsrCreated(final PendingFaults fault, final IssuedMaterialDetails materialDetails, final SweetAlertDialog sDialog) {
//
//        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(MaterialRequestActivity.this);
//        dialog
//                .setTitle("Confirm")
//                .setMessage("Do you want to create ASR?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        sDialog.dismiss();
//                        sendSms(fault, materialDetails);
//                        new ToastManager(MaterialRequestActivity.this).success("ASR CREATED!");
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        sDialog.dismiss();
//                    }
//                });
//        dialog.show();
//    }

    public void isASRItem(final IssuedMaterialDetails materialDetails, final CheckBox checkBox, final String type) {
        new SweetAlertDialog(MaterialRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Is ASR Item?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        //showAddIssuedMaterial(materialDetails,checkBox,type);
                        MaterialIssued issued = new MaterialIssued();
                        issued.setMATERIAL_ISSUED_PIM_ID(materialDetails.getsID());
                        issued.setMATERIAL_ISSUED_ITEM_NO(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_REQUESTED_ID(fault.getPF_REQUESTID());
                        issued.setMATERIAL_ISSUED_ISSUE_NO(materialDetails.getIssueNo());

                        issued.setMATERIAL_ISSUED_GIVEN_IMEI_ESN("");
                        issued.setMATERIAL_ISSUED_RETURN_IMEI_ESN("");
                        issued.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN("");
                        issued.setMATERIAL_ISSUED_RETURN_STATUS("");
                        issued.setMATERIAL_ISSUED_TYPE(type);
                        issued.setMATERIAL_ISSUED_IS_ASR("1");

                        new MaterialIssuedDAO(MaterialRequestActivity.this)
                                .deleteByItemNo(materialDetails.getItemCode(), fault.getPF_REQUESTID(), type);

                        int iStatus = new MaterialIssuedDAO(MaterialRequestActivity.this).insertORupdate(issued);

                        if (iStatus > 0) {
                            setRecyclerViewDate(type);
                            checkBox.setChecked(true);
                            // askAsrCreated(fault, materialDetails,sDialog);

                        }

                        sDialog.dismissWithAnimation();

                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        MaterialIssued issued = new MaterialIssued();
                        issued.setMATERIAL_ISSUED_PIM_ID(materialDetails.getsID());
                        issued.setMATERIAL_ISSUED_ITEM_NO(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_REQUESTED_ID(fault.getPF_REQUESTID());
                        issued.setMATERIAL_ISSUED_ISSUE_NO(materialDetails.getIssueNo());

                        issued.setMATERIAL_ISSUED_GIVEN_IMEI_ESN(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_RETURN_IMEI_ESN(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN("");
                        issued.setMATERIAL_ISSUED_RETURN_STATUS("");
                        issued.setMATERIAL_ISSUED_TYPE(type);
                        issued.setMATERIAL_ISSUED_IS_ASR("0");

                        new MaterialIssuedDAO(MaterialRequestActivity.this)
                                .deleteByItemNo(materialDetails.getItemCode(), fault.getPF_REQUESTID(), type);

                        int iStatus = new MaterialIssuedDAO(MaterialRequestActivity.this).insertORupdate(issued);

                        if (iStatus > 0) {
                            setRecyclerViewDate(type);
                            checkBox.setChecked(true);
                        }

                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void showChangeToRepun(final String fault_id, final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view2 = layoutInflaterAndroid.inflate(R.layout.message_pop_up, null);
        builder.setView(view2);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final EditText remark = view2.findViewById(R.id.etRemark);
        final TextView btnClose = view2.findViewById(R.id.btnClose);

        view2.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remark.getText().toString().equals("")) {
                    alerts.ErrorAlert("Please Enter Remark");
                } else {
                    alertDialog.dismiss();
                    new SyncData(MaterialRequestActivity.this).ChangeToRepun(MaterialRequestActivity.this, TaskType.CHANGE_TO_REPUN, fault_id, remark.getText().toString(), type);
                }
            }
        });

        view2.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showViewSummaryReport(final PendingFaults fault) {


        final View dialogView = View.inflate(this, R.layout.view_summery_report, null);
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rvView);

        TextView issuedNumber = (TextView) dialog.findViewById(R.id.tvRequestId);

        issuedNumber.setText("Fault ID : " + fault.getPF_REQUESTID());

        ArrayList<String> actionTakenArrayList = new ActionTakenDAO(MaterialRequestActivity.this).getAllActions();

        final TextView tvAction = (TextView) dialog.findViewById(R.id.tvAction_taken);

        spinnerDialog = new SpinnerDialog(MaterialRequestActivity.this, actionTakenArrayList, "Select or Search Action Taken", R.style.DialogAnimations_SmileWindow);// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //  Toast.makeText(MaterialRequestActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                tvAction.setText(item);
            }
        });

        tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerDialog.showSpinerDialog();

            }
        });

        final ArrayList<MaterialIssued> lists = new MaterialIssuedDAO(MaterialRequestActivity.this)
                .getAllIssuedMaterialByRequestID(fault.getPF_REQUESTID());

        SummaryAdapter faultAdapter = new SummaryAdapter(lists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(faultAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        //set Data and refrese adapter
        faultAdapter.notifyDataSetChanged();

        ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        TextView bConfirmed = (TextView) dialog.findViewById(R.id.bConfirmed);
        final TextView RemarkClose = (TextView) dialog.findViewById(R.id.RemarkClose);
        bConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validation
                if (tvAction.getText().toString().equals("OTHERS") && RemarkClose.getText().toString().equals("")) {
                    alerts.ErrorAlert("Please Type an Action Taken");

                } else if (tvAction.getText().toString().equals("")) {
                    alerts.ErrorAlert("Please Select an Action Taken");

                } else {

                    CompetedJob job = new CompetedJob();

                    job.setCOMPLETED_JOB_REQUESTED_ID(fault.getPF_REQUESTID());
                    job.setCOMPLETED_JOB_REQUEST_TO_REFID(fault.getPF_REQUESTTOREFID());
                    job.setCOMPLETED_JOB_IS_MATERIAL_REQUEST("1");
                    job.setCOMPLETED_JOB_ACTION_TAKEN(new ActionTakenDAO(MaterialRequestActivity.this).getActionCodeByName(tvAction.getText().toString()));
                    job.setCOMPLETED_JOB_REMARK(RemarkClose.getText().toString());

                    Other otherFunction = new Other(MaterialRequestActivity.this);
                    otherFunction.setLatLong(job);

                    dialogCompleteTask(dialogView, dialog, job);
                    //revealShow(dialogView, false, dialog);
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
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void dialogCompleteTask(final View dialogView, final Dialog dialog, final CompetedJob job) {

        //check isReturn or this fault id?
        new SweetAlertDialog(MaterialRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do you want to Complete?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        int iStatus = new CompetedJobDAO(MaterialRequestActivity.this).insertORupdate(job);

                        if (iStatus > 0) {
                            if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                                sendFaultToServer(job.getCOMPLETED_JOB_REQUESTED_ID());
                            }
                            revealShow(dialogView, false, dialog);
                            sDialog.dismissWithAnimation();
                            // finish();
                        }
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // InsertASR(job.getCOMPLETED_JOB_REQUESTED_ID());
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void sendFaultToServer(String fault_id) {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
            if (pDialog != null) {
                pDialog.show();
            }


            JSONArray completedList = new CompetedJobDAO(MaterialRequestActivity.this).getCompletedFault(fault_id);

            JSONObject object = new JSONObject();

            try {
                object.put("data", completedList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Json ", object.toString().replace("\\", ""));

            new SyncData(MaterialRequestActivity.this)
                    .syncCompletedJobs(MaterialRequestActivity.this, TaskType.UPLOAD_COMPLETED_JOBS, object);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:

                String type = "None";

                int count = 0;
                ArrayList<MaterialIssued> lists = new MaterialIssuedDAO(MaterialRequestActivity.this)
                        .getAllIssuedMaterialByRequestID(fault.getPF_REQUESTID());
                for (MaterialIssued k : lists) {
                    type = k.getMATERIAL_ISSUED_TYPE();
                    if (type.equals("CPHN")) {
                        count++;
                    }
                }

                if (lists.size() > 1 && count > 1) {
                    alerts.ErrorAlert("Select only one CDMA");
                } else {
                    JSONArray RMeterial = new MaterialIssuedDAO(MaterialRequestActivity.this).getReturnMaterial(fault.getPF_REQUESTID());
                    showViewSummaryReport(fault);
//                    if (RMeterial.length() > 0) {
//                        InsertASR(fault.getPF_REQUESTID());
//                    } else {
//
//                    }

                }


                Toast.makeText(MaterialRequestActivity.this,
                        "Confirmed. ", Toast.LENGTH_SHORT).show();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    TextView tvReturn_IMEI_ESN;

    private void updateVisitLog(String faultId) {
        if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

            JSONArray VisitList = new CompetedJobDAO(MaterialRequestActivity.this).getVisitLogArray(faultId);

            JSONObject object = new JSONObject();

            try {
                object.put("data", VisitList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Json ", object.toString().replace("\\", ""));

            new SyncData(MaterialRequestActivity.this)
                    .updateVisitLocation(MaterialRequestActivity.this, TaskType.VIST_LOG, object);

        } else {
            new ConnectionDetector(MaterialRequestActivity.this).showSettingsAlert();
        }
    }

    private void showAddIssuedMaterial(final IssuedMaterialDetails materialDetails, final CheckBox checkBox, final String type) {

        final View dialogView = View.inflate(this, R.layout.show_material_issue_confirmed_view, null);
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final TextView tvRequestID = (TextView) dialog.findViewById(R.id.tvRequestID);
        tvRequestID.setText(fault.getPF_REQUESTID());

        final TextView tvIssueNo = (TextView) dialog.findViewById(R.id.tvIssueNo);
        tvIssueNo.setText(materialDetails.getIssueNo());

        final TextView tvIN_IMEI_ESN = (TextView) dialog.findViewById(R.id.tvIN_IMEI_ESN);
        tvIN_IMEI_ESN.setText(materialDetails.getSerial());

        tvReturn_IMEI_ESN = (TextView) dialog.findViewById(R.id.tvReturn_IMEI_ESN);
        tvReturn_IMEI_ESN.setText(sReturnIMEI);


        final LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.li_entered_code);

        final Switch aSwitch = (Switch) dialog.findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    linearLayout.setVisibility(View.GONE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        final EditText tvAvailableIMEIno = dialog.findViewById(R.id.tvAvailableIMEIno);

        ImageView imageView = dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        TextView bAdd = (TextView) dialog.findViewById(R.id.bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!aSwitch.isChecked() && tvAvailableIMEIno.getText().toString().equals("")) {
                    new ToastManager(MaterialRequestActivity.this).error("Please Enter valid Return IMEI/ESN number");

                } else if (tvReturn_IMEI_ESN.getText().toString().equals("") && tvAvailableIMEIno.getText().toString().equals("") && !aSwitch.isChecked()) {
                    new ToastManager(MaterialRequestActivity.this).error("Please Enter valid Return IMEI/ESN number");

                } else {
                    if (sReturnIMEI.equals("")) {
                        alerts.OkAlert("You can't Close this Fault with Material. \nBecause Existing Serial Number is empty.Please Close this Fault Without Material");
                    } else {
                        checkBox.setChecked(true);

                        MaterialIssued issued = new MaterialIssued();
                        issued.setMATERIAL_ISSUED_PIM_ID(materialDetails.getsID());
                        issued.setMATERIAL_ISSUED_ITEM_NO(materialDetails.getItemCode());
                        issued.setMATERIAL_ISSUED_REQUESTED_ID(tvRequestID.getText().toString());
                        issued.setMATERIAL_ISSUED_ISSUE_NO(tvIssueNo.getText().toString());
                        issued.setMATERIAL_ISSUED_GIVEN_IMEI_ESN(tvIN_IMEI_ESN.getText().toString());
                        issued.setMATERIAL_ISSUED_RETURN_IMEI_ESN(tvReturn_IMEI_ESN.getText().toString());
                        issued.setMATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN(tvAvailableIMEIno.getText().toString());
                        issued.setMATERIAL_ISSUED_RETURN_STATUS("Re-usable");

                        issued.setMATERIAL_ISSUED_IS_ASR("0");
                        issued.setMATERIAL_ISSUED_IS_RETURN("1");

                        if (type.equals("OTHER")) {
                            issued.setMATERIAL_ISSUED_TYPE(materialDetails.getItemType());//type
                            issued.setMATERIAL_ISSUED_TYPE_OTHER(type);
                        } else {
                            issued.setMATERIAL_ISSUED_TYPE(type);
                        }


                        new MaterialIssuedDAO(MaterialRequestActivity.this).deleteByItemNo(materialDetails.getItemCode(), tvRequestID.getText().toString(), type);

                        int iStatus = new MaterialIssuedDAO(MaterialRequestActivity.this).insertORupdate(issued);
                        // checkBox.setChecked(true);
                        setRecyclerViewDate(type);
                        if (iStatus > 0) {
                            revealShow(dialogView, false, dialog);
                        }
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
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
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
        } else {
            if (dialog != null) {
                if (b) {

                    dialog.show();
                } else {
                    dialog.dismiss();
                }
            }
        }

    }

    private void ChangeToRepunConfirmation(final String fault_id, final String type) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you want to change this unit to repun")
                .setCancelText("No!")
                .setConfirmText("Yes,Change it!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        showChangeToRepun(fault_id, type);
                    }
                })
                .show();
    }

    @Override
    public void onSuccess(String id, TaskType type, String faultType) {
        switch (type) {

            case GET_PAST_ENS_IMEI: {
                // sReturnIMEI
                String result = id;
                String serial = faultType;
                Log.v("Results", result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());
                    String sStatus = jsonObject.getString("Data");

                    JSONObject jsonObject1 = new JSONObject(sStatus);

                    sReturnIMEI = jsonObject1.getString("Serial");
                    sItemCode = jsonObject1.getString("ItemCode");
                    System.out.println("* sReturnIMEI " + sReturnIMEI);
                    System.out.println("* sItemCode " + sItemCode);

                    if (sReturnIMEI.equals("")) {

                        new ToastManager(MaterialRequestActivity.this).error("Existing Serial Number is empty");
                    }

                    int up = new MaterialIssuedDAO(MaterialRequestActivity.this).updateIMEI_ESN_ItemCode(sItemCode, serial);
                    System.out.println("* up " + up);
                    if (up > 0) {
                        System.out.println("* IMEI updated ");
                    }
                } catch (JSONException e) {
                    new ToastManager(MaterialRequestActivity.this).error("Existing Serial Number is empty");
                    Log.e("Error ENS_IMEI", e.toString());
                    e.printStackTrace();
                }

            }
            break;
            case CHANGE_TO_REPUN: {
                try {

                    if (id.equals("1")) {
                        alerts.SuccessAlert("Successfuly Changed");
                        switch (faultType) {
                            case "CDMA": {
                                bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                list.clear();
                                recyclerView.getAdapter().notifyDataSetChanged();
                                setRecyclerViewDate("CPHN");
                            }
                            break;

                            case "LTE": {
                                bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                list.clear();
                                recyclerView.getAdapter().notifyDataSetChanged();
                                setRecyclerViewDate("LTEU");
                            }
                            break;

                            case "SIM": {
                                bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                list.clear();
                                recyclerView.getAdapter().notifyDataSetChanged();
                                setRecyclerViewDate("LTES");
                            }
                            break;
                        }
                    } else {
                        alerts.ErrorAlert("Error occured");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    alerts.ErrorAlert("Network Error");
                }
            }
            break;


            case IS_CDMA_VALIDATION: {
                Log.v("Results", id);
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                try {
                    switch (id) {
                        case "400": {
                            ChangeToRepunConfirmation(fault.getPF_REQUESTID(), "CDMA");
                        }
                        break;
                        case "200": {
                            System.out.println("* Enable");

                            bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            list.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();
                            setRecyclerViewDate("CPHN");
                        }
                        break;
                        case "600": {
                            alerts.ErrorAlert("This is not a CDMA Fault");
                            bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            //not a cdma fault
                        }
                        break;
                        case "800": {
                            alerts.ErrorAlert("Server Error ");
                        }
                        break;
                        default: {
                            alerts.ErrorAlert("Network Error");
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    alerts.ErrorAlert(e.getMessage());
                }
            }

            break;
            case IS_4GLTE_VALIDATION: {
                Log.v("Results 4GLTE", id);
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                try {


                    switch (id) {
                        case "400": {
                            //confirmation
                            ChangeToRepunConfirmation(fault.getPF_REQUESTID(), "LTE");
                        }
                        break;
                        case "200": {

                            System.out.println("* id " + id + " type " + type);
                            bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            list.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();

                            setRecyclerViewDate("LTEU");
                        }
                        break;
                        case "600": {
                            System.out.println("* id " + id + " type ");
                            alerts.ErrorAlert("This is not a LTE Fault");
                            bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            //not a cdma fault
                        }
                        break;
                        case "800": {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Server Error ");
                        }
                        break;
                        default: {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Network Error");
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    alerts.ErrorAlert(e.getMessage());
                }
            }
            break;
            case IS_4GLTE_VALIDATION_NEW: {
                Log.v("Results 4GLTE", id);
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                try {


                    switch (id) {
                        case "400": {
                            //confirmation
                            ChangeToRepunConfirmation(fault.getPF_REQUESTID(), "LTE");
                        }
                        break;
                        case "200": {

                            System.out.println("* id " + id + " type " + type);
                            bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            list.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();

                            setRecyclerViewDate("LTEU");
                        }
                        break;
                        case "600": {
                            System.out.println("* id " + id + " type ");
                            b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            list.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();
                            //not a cdma fault
                        }
                        break;
                        case "800": {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Server Error ");
                        }
                        break;
                        default: {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Network Error");
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    alerts.ErrorAlert(e.getMessage());
                }
            }
            break;
            case IS_SIM_VALIDATION: {
                Log.v("Results", id);
                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                try {


                    System.out.println("* id " + id + " type " + type);
                    switch (id) {
                        case "400": {
                            //confirmation
                            ChangeToRepunConfirmation(fault.getPF_REQUESTID(), "SIM");
                        }
                        break;
                        case "200": {

                            bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            list.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();
                            setRecyclerViewDate("LTES");
                        }
                        break;
                        case "600": {
                            alerts.ErrorAlert("This is not a LTE Fault");
                            bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                        break;
                        case "800": {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Server Error ");
                        }
                        break;
                        default: {
                            System.out.println("* id " + id + " type " + type + "faultType" + faultType);
                            alerts.ErrorAlert("Network Error");
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    alerts.ErrorAlert(e.getMessage());
                }
            }
            break;


        }

    }

    @Override
    public void onSuccess(String result, TaskType type) {

        switch (type) {

            case UPLOAD_COMPLETED_JOBS: {

                Log.v("UPLOAD_COMPLETED_JOBS", result);
                pDialog.hide();
                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int ID = Integer.parseInt(jsonObject.getString("ID"));

                    Log.v("Json COMPLETED ", jsonArray.toString().replace("\\", ""));

                    if (ID == 200) {

                        JSONArray RMeterial = new MaterialIssuedDAO(MaterialRequestActivity.this).getReturnMaterial(fault.getPF_REQUESTID());

                        System.out.println("* RMeterial " + RMeterial.length());

                        //alerts.SuccessAlert("Successfully Closed");

                      //  for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(0);

                            int iStatus = data.getInt("status");
                            final String requestId = data.getString("requestId");
                            System.out.println("iStatus " + iStatus + " req_id " + requestId);

                            if (iStatus >= 1) {

                                //update sync_id
                                int iUpdated = new CompetedJobDAO(MaterialRequestActivity.this).updateIsSynced(requestId);
                                System.out.println("* updated " + iUpdated);
                              //  alerts.SuccessAlert("Fault Closed Successfully");

                                if (RMeterial.length() > 0) {
                                    InsertASR(fault.getPF_REQUESTID());
                                    new ToastManager(MaterialRequestActivity.this).success("Successfully Fault Closed");
//                                    new SweetAlertDialog(MaterialRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
//                                            .setTitleText("Do you want to Create ASR?")
//                                            .setConfirmText("Yes")
//                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sDialog) {
//
//                                                    new ToastManager(MaterialRequestActivity.this).success("Successfully Fault Closed");
//                                                    InsertASR(fault.getPF_REQUESTID());
//
//                                                    sDialog.dismissWithAnimation();
//                                                }
//                                            })
//                                            .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sDialog) {
//                                                    alerts.SuccessAlert("Successfully Fault Closed");
//                                                    updateVisitLog(fault.getPF_REQUESTID());
//                                                    sDialog.dismissWithAnimation();
//                                                }
//                                            })
//                                            .show();


                                } else {
                                    alerts.SuccessAlert("Successfully Fault Closed");
                                    updateVisitLog(fault.getPF_REQUESTID());
                                }

                                //  int iUpdated = new CompetedJobDAO(FaultClosingActivity.this).updateIsSynced(data.getString("requestId"));

                            } else {
                                new ToastManager(MaterialRequestActivity.this).error("Fault Closing is Failed");
                                Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }


                    } else {

                        new ToastManager(MaterialRequestActivity.this).error("Fault Closing is Failed");

                        Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    new ToastManager(MaterialRequestActivity.this).error("Fault Closing is Failed");
                    Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            break;
            case VIST_LOG: {

                Log.v("VIST_LOG success", result);

                try {


                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));
                    new ToastManager(MaterialRequestActivity.this).success("Successfully Closed");
                    if (id == 200) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = (JSONObject) jsonArray.get(i);

                            int iStatus = data.getInt("status");
                            String requestId = data.getString("requestId");

                            if (iStatus >= 1) {
                                int iUpdated = new CompetedJobDAO(MaterialRequestActivity.this).updateIsVisitLogSynced(data.getString("requestId"));

//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                    }
//                                }, 6000);

                                System.out.println("* iUpdated " + iUpdated);
                            }
                        }
                    } else {
                        System.out.println("* visit log error with id" + id);
                    }
                    Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                    Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
            break;
            case ASR_CREATE: {

                Log.v("ASR_CREATE success", result);

                try {

                    JSONObject jsonObject = new JSONObject(result.toString());

                    final JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    final int id = Integer.parseInt(jsonObject.getString("ID"));
                    // String requestId = jsonObject.getString("requestId");

                    if (id == 200) {
                        alerts.SuccessAlert("ASR is Created Successfully");
                        //new ToastManager(MaterialRequestActivity.this).success(" ASR is Created Successfully");
                    } else {
                        //    alerts.ErrorAlert("ASR Created is Failed");
                        new ToastManager(MaterialRequestActivity.this).error("ASR Created is Failed");
                        System.out.println("* ASR_CREATED error with id" + id);
                    }
                    updateVisitLog(fault.getPF_REQUESTID());
                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                }

            }
            break;
        }

    }

    @Override
    public void onError(String message, TaskType type) {
        switch (type) {
            case IS_4GLTE_VALIDATION: {
                System.out.println("*  4GLTE error " + message);
            }
            break;

            case IS_CDMA_VALIDATION: {
                System.out.println("*  CDMA error " + message);
            }
            break;
            case IS_SIM_VALIDATION: {
                System.out.println("*  SIM error " + message);
            }
            break;
            case VIST_LOG: {
                if (!new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(MaterialRequestActivity.this).error("Connection Failed");
                }
                Intent intent = new Intent(MaterialRequestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case UPLOAD_COMPLETED_JOBS: {
                pDialog.hide();
//                JSONArray RMeterial = new MaterialIssuedDAO(MaterialRequestActivity.this).getReturnMaterial(fault.getPF_REQUESTID());
//                if (RMeterial.length() > 0) {
//                    InsertASR(fault.getPF_REQUESTID());
//                }
//                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
//                    new ToastManager(MaterialRequestActivity.this).error("Fault Closing is failed");
//                } else {
//                    new ToastManager(MaterialRequestActivity.this).error("Connection Failed");
//                }
               finish();
            }
            break;
            case ASR_CREATE: {
                updateVisitLog(fault.getPF_REQUESTID());
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(MaterialRequestActivity.this).error("ASR create is Failed");
                } else {
                    new ToastManager(MaterialRequestActivity.this).error("Connection Failed");
                }

            }
            break;
            case GET_PAST_ENS_IMEI: {
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                    new ToastManager(MaterialRequestActivity.this).error("Existing Serial Number is empty");
                } else {
                    new ToastManager(MaterialRequestActivity.this).error("Connection Failed");
                }

            }
            break;
        }

    }
}