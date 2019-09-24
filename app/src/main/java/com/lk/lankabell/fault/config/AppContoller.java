package com.lk.lankabell.fault.config;

import android.content.Context;

import com.lk.lankabell.fault.adapter.SharedPreferencesHelper;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.control.Data.UserProfileDAO;
import com.lk.lankabell.fault.model.UserProfile;

import java.util.ArrayList;

public class AppContoller {
    //SHARED PREFERENCES VARIABLES
    public static final String SETTINGS = "SETTINGS";
    //SP ID
    //public static final String LOGON_DRIVER_ID = "LOGON_DRIVER_ID";

    //SP_REMEMBER_ME_KEY
    private static String SP_REMEMBER_ME_KEY = "LOGIN_REMEMBER_ME";

    public static String getEMPNO(Context context) {

        ArrayList<UserProfile> userProfiles = new ArrayList<>();
        userProfiles = new UserProfileDAO(context).getUserProfile();
        if(userProfiles.size()>0){
            System.out.println("* emp_no "+userProfiles.get(0).getEmp());
            return userProfiles.get(0).getEmp();
        }else{

           // System.out.println("* emp_no:: 121086");
      //return "2453";
      // return "119627"; //1039
      return "121086";
       //return "120057";
             // return "120667";
             // return "5487";
            //return "0";
           //   return "119732";
            //return "124358";
        }
        //
    }
    public static String getEpfNo(Context context) {

        ArrayList<UserProfile> userProfiles = new ArrayList<>();
        userProfiles = new UserProfileDAO(context).getUserProfile();
        if(userProfiles.size()>0){
            System.out.println("* emp_no "+userProfiles.get(0).getEpf());
            return userProfiles.get(0).getEmp();
        }else{

           // System.out.println("* emp_no:: 121086");
              return "0";
             // return "120667";
             // return "5487";
             // return "0";
             // return "121086";
        }
        //return "121086";
    }

    public static final String TodayLogedIn = "TodayLogedIn";

    public static String getPhoneNumber(Context context) {

        ArrayList<UserProfile> userProfiles = new ArrayList<>();
        userProfiles = new UserProfileDAO(context).getUserProfile();
        if(userProfiles.size()>0){
            System.out.println("* emp_no "+userProfiles.get(0).getCordinatorNumber());
            return userProfiles.get(0).getCordinatorNumber();
        }else{
            System.out.println("* emp_no:: 2453");
             //   return "727186402";
                return "0111111111";
        }
        //return "121086";
    }

    public static String getSpRememberMeKey() {
        return SP_REMEMBER_ME_KEY;
    }

    public static final String APP_PACKAGE_NAME = "com.lk.lankabell.fault";

    public static final String RUNNING = "runningInBackground";

    public  static String getSPRememberMeValue(Context context){
        return new SharedPreferencesHelper().getLocalSharedPreference(context, getSpRememberMeKey());
    }

    public  static void setSPRememberMe(Context context, String value){
        new SharedPreferencesHelper().setLocalSharedPreference(context, getSpRememberMeKey(),value);
    }

    public static boolean STOP_SENDING_SMS = false;

    //---------------- SP Last Sync Date and time  -------------
    public static String SP_LAST_SYNC_PENDING_JOBS = "SP_LAST_SYNC_PENDING_JOBS";
    public static String SP_LAST_SYNC_ACCEPTED_JOBS = "SP_LAST_SYNC_ACCEPTED_JOBS";
    public static String SP_LAST_SYNC_REJECTED_JOBS = "SP_LAST_SYNC_REJECTED_JOBS";
    public static String SP_FETCH_PENDING_ISSUE_DETAILS = "SP_FETCH_PENDING_ISSUE_DETAILS";
    public static String SP_LAST_SYNC_ACCPTED_UNITS = "SP_LAST_SYNC_ACCPTED_UNITS";
    public static String SP_LAST_SYNC_REJECTED_UNITS = "SP_LAST_SYNC_REJECTED_UNITS";
    public static String SP_LAST_SYNC_COMPLETED_JOBS = "SP_LAST_SYNC_COMPLETED_JOBS";
    public static String SP_LAST_SYNC_VISIT_LOG = "SP_LAST_SYNC_VISIT_LOG";
    public static String SP_FETCH_CDMA_ISSUE_DETAILS = "SP_FETCH_CDMA_ISSUE_DETAILS";
}
