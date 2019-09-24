package com.lk.lankabell.fault.view;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.config.AppConfig;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.Data.UserProfileDAO;
import com.lk.lankabell.fault.model.UserProfile;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_my_profile_epf;
    TextView tv_my_profile_sim_no;
    TextView tv_my_profile_app_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
         tv_my_profile_epf = findViewById(R.id.tv_my_profile_epf);
         tv_my_profile_sim_no  = findViewById(R.id.tv_my_profile_sim_no);
         tv_my_profile_app_version  = findViewById(R.id.tv_my_profile_app_version);


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
        tv_my_profile_app_version.setText(newversion);

        tv_my_profile_epf.setText(AppContoller.getEMPNO(ProfileActivity.this));

        ArrayList<UserProfile> userProfiles = new ArrayList<>();
        String SimSerial = "N/A";
        userProfiles = new UserProfileDAO(ProfileActivity.this).getUserProfile();
        if(userProfiles.size()>0) {
            SimSerial =  userProfiles.get(0).getSimNo();
        }
        tv_my_profile_sim_no.setText(SimSerial);
    }
}
