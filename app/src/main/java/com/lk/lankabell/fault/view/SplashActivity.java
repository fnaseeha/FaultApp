package com.lk.lankabell.fault.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.control.Data.UserProfileDAO;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.service.SyncData;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.lk.lankabell.fault.control.TaskType.GET_TSR;

public class SplashActivity  extends AppCompatActivity implements VolleyCallback {

    String fromPackage, username, pword, SimNo, CollectorCode, attendenceIn, attendenceOut,empNo;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
         intent = new Intent(this, MainActivity.class);


        Bundle b = getIntent().getExtras();

        if (b != null) {
            fromPackage = b.getString("package");
            username = b.getString("username");
            pword = b.getString("pword");
            SimNo = b.getString("SimNo");
            CollectorCode = b.getString("CollectorCode");
            attendenceIn = b.getString("attendenceIn");
            attendenceOut = b.getString("attendenceOut");

            System.out.println(username);

            try {
                new SyncData(SplashActivity.this).getTSR(SplashActivity.this, GET_TSR,username);

            } catch (Exception e) {
                new ToastManager(SplashActivity.this).error("Login Failed because of "+e.getMessage());
                e.printStackTrace();
            }

        } else {
            startActivity(intent);
        }
    }
    private void insertToDb(String epf, String emp,String pword, String simNo) {

        int res = new UserProfileDAO(SplashActivity.this).insert(epf,emp,pword,simNo);
        if(res>0){
            System.out.println("* Inserted user profile");
        }else{
            System.out.println("* not inserted user profile");
        }
    }

    @Override
    public void onSuccess(String result, TaskType type) {
        if(type== GET_TSR){

            try {
                JSONObject data = new JSONObject(result.toString());

                empNo = data.getString("EmpNo");
                System.out.println("* emp no here "+empNo);
                clearData();
                insertToDb(username,empNo,pword,SimNo);
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();

                overridePendingTransition(R.anim.slide_to_left, R.anim.pull_out_left);
            }catch (Exception e){
                new ToastManager(SplashActivity.this).error("Login is failed because of "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearData() {
        new UserProfileDAO(SplashActivity.this).delete();
    }

    @Override
    public void onError(String message, TaskType type) {
        if(type== GET_TSR){
            if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {
                new ToastManager(SplashActivity.this).error("Login is failed because of "+message);
            }else{
                new ToastManager(SplashActivity.this).error("Connection Failed");
            }
        }

    }

    @Override
    public void onSuccess(String id, TaskType type, String faultType) {

    }
}
