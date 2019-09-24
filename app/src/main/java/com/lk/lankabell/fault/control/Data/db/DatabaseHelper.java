package com.lk.lankabell.fault.control.Data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bv_faults_data.db";
    public static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //#################### PENDING FAULTS  TABLE ######################
    // TABLE
    public static final String TABLE_PENDING_FAULTS = "pending_faults_tbl";
    // TABLE ATTRIBUTES
    public static final String PF_ID = "_Id";
    public static final String PF_REQUESTID = "RequestId";
    public static final String PF_REQUESTBATCHID = "RequestBatchId";
    public static final String PF_REQUESTTOREFID = "RequestToRefId";
    public static final String PF_REQUESTTONAME = "RequestToName";
    public static final String PF_REQUESTTOCONTACT = "RequestToContact";
    public static final String PF_REQUESTTOADD1 = "RequestToAdd1";
    public static final String PF_REQUESTTOADD2 = "RequestToAdd2";
    public static final String PF_REQUESTTOADD3 = "RequestToAdd3";
    public static final String PF_REQUESTASSIGNEDTO = "RequestAssignedTo";
    public static final String PF_REQUESTASSIGNEDDATE = "RequestAssignedDate";
    public static final String PF_STATUS = "Status";
    public static final String PF_PRIORITY = "Priority";
    public static final String PF_REQUESTTYPE = "RequestType";
    public static final String PF_REQUESTSUBTYPE = "RequestSubType";
    public static final String PF_REQUESTCATEGORY = "RequestCategory";
    public static final String PF_ISACCEPT = "IsAccept";
    public static final String PF_REQUESTTOLOCATION = "RequestToLocation";
    public static final String PF_SERVICETYPE = "ServiceType";
    public static final String PF_CUSTOMERCATEGORY = "CustomerCategory";
    public static final String PF_CUSTOMERRATINGS = "CustomerRatings";
    public static final String PF_CUSTOMERREMARKS = "CustomerRemarks";
    public static final String PF_DIRECTION = "Direction";
    //NIGHT, DAY DATA FOR LTE
    public static final String PF_LTE_NIGHT = "LteNight";
    public static final String PF_LTE_DAY = "LteDay";


    //TABLE CREATING STRING
    private static final String CREATE_PENDING_FAULTS_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_PENDING_FAULTS + " ("
            + PF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PF_REQUESTID + " TEXT, "
            + PF_REQUESTBATCHID + " TEXT, "
            + PF_REQUESTTOREFID + " TEXT, "
            + PF_REQUESTTONAME + " TEXT, "
            + PF_REQUESTTOCONTACT + " TEXT, "
            + PF_REQUESTTOADD1 + " TEXT, "
            + PF_REQUESTTOADD2 + " TEXT, "
            + PF_REQUESTTOADD3 + " TEXT, "
            + PF_REQUESTASSIGNEDTO + " TEXT, "
            + PF_REQUESTASSIGNEDDATE + " TEXT, "
            + PF_STATUS + " TEXT, "
            + PF_PRIORITY + " TEXT, "
            + PF_REQUESTTYPE + " TEXT, "
            + PF_REQUESTSUBTYPE + " TEXT, "
            + PF_REQUESTCATEGORY + " TEXT, "
            + PF_ISACCEPT + " TEXT, "
            + PF_REQUESTTOLOCATION + " TEXT, "
            + PF_SERVICETYPE + " TEXT, "
            + PF_CUSTOMERCATEGORY + " TEXT, "
            + PF_CUSTOMERRATINGS + " TEXT, "
            + PF_CUSTOMERREMARKS + " TEXT, "
            + PF_LTE_NIGHT + " TEXT, "
            + PF_LTE_DAY + " TEXT, "
            + PF_DIRECTION + " TEXT); ";


    //#################### ACCEPTED FAULT TABLE ######################
    // TABLE
    public static final String TABLE_ACCEPTED_FAULT = "accepted_fault_tbl";
    // TABLE ATTRIBUTES
    public static final String AF_ID = "_Id";
    public static final String AF_REQUESTID = "RequestId";
    public static final String AF_REQUESTBATCHID = "RequestBatchId";
    public static final String AF_REQUESTTOREFID = "RequestToRefId";
    public static final String AF_REQUESTTONAME = "RequestToName";
    public static final String AF_REQUESTTOCONTACT = "RequestToContact";
    public static final String AF_REQUESTTOADD1 = "RequestToAdd1";
    public static final String AF_REQUESTTOADD2 = "RequestToAdd2";
    public static final String AF_REQUESTTOADD3 = "RequestToAdd3";
    public static final String AF_REQUESTASSIGNEDTO = "RequestAssignedTo";
    public static final String AF_REQUESTASSIGNEDDATE = "RequestAssignedDate";
    public static final String AF_STATUS = "Status";
    public static final String AF_PRIORITY = "Priority";
    public static final String AF_REQUESTTYPE = "RequestType";
    public static final String AF_REQUESTSUBTYPE = "RequestSubType";
    public static final String AF_REQUESTCATEGORY = "RequestCategory";
    public static final String AF_ISACCEPT = "IsAccept";
    public static final String AF_REQUESTTOLOCATION = "RequestToLocation";
    public static final String AF_SERVICETYPE = "ServiceType";
    public static final String AF_CUSTOMERCATEGORY = "CustomerCategory";
    public static final String AF_CUSTOMERRATINGS = "CustomerRatings";
    public static final String AF_CUSTOMERREMARKS = "CustomerRemarks";
    public static final String AF_DIRECTION = "Direction";
    //SPECIF ACCEPTED VARIABLES
    public static final String AF_EMPNO = "empNo";
    public static final String AF_ACCEPTEDO_DATE = "accepted_date";
    public static final String AF_IS_SYCN = "is_sync";
    //NIGHT, DAY DATA FOR LTE
    public static final String AF_LTE_NIGHT = "LteNight";
    public static final String AF_LTE_DAY = "LteDay";


    //TABLE CREATING STRING
    private static final String CREATE_ACCEPTED_FAULT_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_ACCEPTED_FAULT + " ("
            + AF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AF_REQUESTID + " TEXT, "
            + AF_REQUESTBATCHID + " TEXT, "
            + AF_REQUESTTOREFID + " TEXT, "
            + AF_REQUESTTONAME + " TEXT, "
            + AF_REQUESTTOCONTACT + " TEXT, "
            + AF_REQUESTTOADD1 + " TEXT, "
            + AF_REQUESTTOADD2 + " TEXT, "
            + AF_REQUESTTOADD3 + " TEXT, "
            + AF_REQUESTASSIGNEDTO + " TEXT, "
            + AF_REQUESTASSIGNEDDATE + " TEXT, "
            + AF_STATUS + " TEXT, "
            + AF_PRIORITY + " TEXT, "
            + AF_REQUESTTYPE + " TEXT, "
            + AF_REQUESTSUBTYPE + " TEXT, "
            + AF_REQUESTCATEGORY + " TEXT, "
            + AF_ISACCEPT + " TEXT, "
            + AF_REQUESTTOLOCATION + " TEXT, "
            + AF_SERVICETYPE + " TEXT, "
            + AF_CUSTOMERCATEGORY + " TEXT, "
            + AF_CUSTOMERRATINGS + " TEXT, "
            + AF_CUSTOMERREMARKS + " TEXT, "
            + AF_DIRECTION + " TEXT, "
            + AF_EMPNO + " TEXT, "
            + AF_IS_SYCN + " TEXT, "
            + AF_LTE_NIGHT + " TEXT, "
            + AF_LTE_DAY + " TEXT, "
            + AF_ACCEPTEDO_DATE + " TEXT); ";


    //#################### REJECT FAULT TABLE ######################
    // TABLE
    public static final String TABLE_REJECT_FAULT = "reject_fault_tbl";
    // TABLE ATTRIBUTES
    public static final String RF_ID = "_Id";
    public static final String RF_REQUESTID = "RequestId";
    public static final String RF_REQUESTBATCHID = "RequestBatchId";
    public static final String RF_REQUESTTOREFID = "RequestToRefId";
    public static final String RF_REQUESTTONAME = "RequestToName";
    public static final String RF_REQUESTTOCONTACT = "RequestToContact";
    public static final String RF_REQUESTTOADD1 = "RequestToAdd1";
    public static final String RF_REQUESTTOADD2 = "RequestToAdd2";
    public static final String RF_REQUESTTOADD3 = "RequestToAdd3";
    public static final String RF_REQUESTASSIGNEDTO = "RequestAssignedTo";
    public static final String RF_REQUESTASSIGNEDDATE = "RequestAssignedDate";
    public static final String RF_STATUS = "Status";
    public static final String RF_PRIORITY = "Priority";
    public static final String RF_REQUESTTYPE = "RequestType";
    public static final String RF_REQUESTSUBTYPE = "RequestSubType";
    public static final String RF_REQUESTCATEGORY = "RequestCategory";
    public static final String RF_ISACCEPT = "IsAccept";
    public static final String RF_REQUESTTOLOCATION = "RequestToLocation";
    public static final String RF_SERVICETYPE = "ServiceType";
    public static final String RF_CUSTOMERCATEGORY = "CustomerCategory";
    public static final String RF_CUSTOMERRATINGS = "CustomerRatings";
    public static final String RF_CUSTOMERREMARKS = "CustomerRemarks";
    public static final String RF_DIRECTION = "Direction";
    //SPECIF ACCEPTED VARIABLES
    public static final String RF_EMPNO = "empNo";
    public static final String RF_REJECTED_DATE = "rejected_date";
    public static final String RF_IS_SYCN = "is_sync";


    //TABLE CREATING STRING
    private static final String CREATE_REJECT_FAULT_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_REJECT_FAULT + " ("
            + RF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RF_REQUESTID + " TEXT, "
            + RF_REQUESTBATCHID + " TEXT, "
            + RF_REQUESTTOREFID + " TEXT, "
            + RF_REQUESTTONAME + " TEXT, "
            + RF_REQUESTTOCONTACT + " TEXT, "
            + RF_REQUESTTOADD1 + " TEXT, "
            + RF_REQUESTTOADD2 + " TEXT, "
            + RF_REQUESTTOADD3 + " TEXT, "
            + RF_REQUESTASSIGNEDTO + " TEXT, "
            + RF_REQUESTASSIGNEDDATE + " TEXT, "
            + RF_STATUS + " TEXT, "
            + RF_PRIORITY + " TEXT, "
            + RF_REQUESTTYPE + " TEXT, "
            + RF_REQUESTSUBTYPE + " TEXT, "
            + RF_REQUESTCATEGORY + " TEXT, "
            + RF_ISACCEPT + " TEXT, "
            + RF_REQUESTTOLOCATION + " TEXT, "
            + RF_SERVICETYPE + " TEXT, "
            + RF_CUSTOMERCATEGORY + " TEXT, "
            + RF_CUSTOMERRATINGS + " TEXT, "
            + RF_CUSTOMERREMARKS + " TEXT, "
            + RF_DIRECTION + " TEXT, "
            + RF_EMPNO + " TEXT, "
            + RF_IS_SYCN + " TEXT, "
            + RF_REJECTED_DATE + " TEXT ); ";

    //#################### FETCH PENDING ISSUE DETAILS ######################
    // TABLE
    public static final String TABLE_ISSUE_DETAILS = "issue_details_tbl";
    // TABLE ATTRIBUTES
    public static final String PID_ID = "id";
    public static final String PID_ITEMISSUE_NO = "ItemIssueNo";
    public static final String PID_ITEM_ISSUED_DATE = "ItemIssuedDate";
    public static final String PID_ITEM_ISSUE_REMARK = "ItemIssueRemark";
    public static final String PID_FROM_LOCATION_TYPE = "FromLocationType";
    public static final String PID_FROM_LOCATION = "FromLocation";
    public static final String PID_ITEM_ISSUE_STATUS = "ItemIssueStatus";
    public static final String PID_STOCK_TYPE = "StockType";
    public static final String PID_TOTAL_UNITS = "TotalUnits";


    //TABLE CREATING STRING
    private static final String CREATE_ISSUE_DETAILS_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_ISSUE_DETAILS + " ("
            + PID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PID_ITEMISSUE_NO + " TEXT, "
            + PID_ITEM_ISSUED_DATE + " TEXT, "
            + PID_ITEM_ISSUE_REMARK + " TEXT, "
            + PID_FROM_LOCATION_TYPE + " TEXT, "
            + PID_FROM_LOCATION + " TEXT, "
            + PID_ITEM_ISSUE_STATUS + " TEXT, "
            + PID_STOCK_TYPE + " TEXT, "
            + PID_TOTAL_UNITS + " TEXT); ";


    //#################### ETCH PENDING ISSUED MATERIAL ######################
    // TABLE
    public static final String TABLE_PENDING_ISSUED_MATERIAL = "pending_issued_material_tbl";
    // TABLE ATTRIBUTES
    public static final String PIM_ID = "id";
    public static final String PIM_ISSUENO = "IssueNo";
    public static final String PIM_ISSUETYPE = "IssueType";
    public static final String PIM_ITEMTYPE = "ItemType";
    public static final String PIM_ITEMCODE = "ItemCode";
    public static final String PIM_ITEMDESCRIPTION = "ItemDescription";
    public static final String PIM_ISSUEDQTY = "IssuedQty";
    public static final String PIM_ITEMCATEGORY = "ItemCategory";
    public static final String PIM_SERIAL = "Serial";
    public static final String PIM_EXISTING_ITEM_CODE = "ExitingItemCodeM";

    //TABLE CREATING STRING
    private static final String CREATE_PENDING_ISSUED_MATERIAL_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_PENDING_ISSUED_MATERIAL + " ("
            + PIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PIM_ISSUENO + " TEXT, "
            + PIM_ISSUETYPE + " TEXT, "
            + PIM_ITEMTYPE + " TEXT, "
            + PIM_ITEMCODE + " TEXT, "
            + PIM_ITEMDESCRIPTION + " TEXT, "
            + PIM_ISSUEDQTY + " TEXT, "
            + PIM_ITEMCATEGORY + " TEXT, "
            + PIM_EXISTING_ITEM_CODE + " TEXT ,"
            + PIM_SERIAL + " TEXT); ";


    //#################### ACCEPTED PENDING ISSUE DETAILS ######################
    // TABLE
    public static final String TABLE_ACCEPTED_ISSUE_DETAILS = "accepted_issue_details_tbl";
    // TABLE ATTRIBUTES
    public static final String APID_ID = "id";
    public static final String APID_ITEMISSUE_NO = "ItemIssueNo";
    public static final String APID_ITEM_ISSUED_DATE = "ItemIssuedDate";
    public static final String APID_ITEM_ISSUE_REMARK = "ItemIssueRemark";
    public static final String APID_FROM_LOCATION_TYPE = "FromLocationType";
    public static final String APID_FROM_LOCATION = "FromLocation";
    public static final String APID_ITEM_ISSUE_STATUS = "ItemIssueStatus";
    public static final String APID_STOCK_TYPE = "StockType";
    public static final String APID_TOTAL_UNITS = "TotalUnits";
    public static final String APID_IS_SYNC = "is_sync";


    //TABLE CREATING STRING
    private static final String CREATE_ACCEPTED_ISSUE_DETAILS_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_ACCEPTED_ISSUE_DETAILS + " ("
            + APID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + APID_ITEMISSUE_NO + " TEXT, "
            + APID_ITEM_ISSUED_DATE + " TEXT, "
            + APID_ITEM_ISSUE_REMARK + " TEXT, "
            + APID_FROM_LOCATION_TYPE + " TEXT, "
            + APID_FROM_LOCATION + " TEXT, "
            + APID_ITEM_ISSUE_STATUS + " TEXT, "
            + APID_STOCK_TYPE + " TEXT, "
            + APID_TOTAL_UNITS + " TEXT, "
            + APID_IS_SYNC + " TEXT); ";

    //#################### REJECTED PENDING ISSUE DETAILS ######################
    // TABLE
    public static final String TABLE_REJECTED_ISSUE_DETAILS = "rejected_issue_details_tbl";
    // TABLE ATTRIBUTES
    public static final String RPID_ID = "id";
    public static final String RPID_ITEMISSUE_NO = "ItemIssueNo";
    public static final String RPID_ITEM_ISSUED_DATE = "ItemIssuedDate";
    public static final String RPID_ITEM_ISSUE_REMARK = "ItemIssueRemark";
    public static final String RPID_FROM_LOCATION_TYPE = "FromLocationType";
    public static final String RPID_FROM_LOCATION = "FromLocation";
    public static final String RPID_ITEM_ISSUE_STATUS = "ItemIssueStatus";
    public static final String RPID_STOCK_TYPE = "StockType";
    public static final String RPID_TOTAL_UNITS = "TotalUnits";
    public static final String RPID_IS_SYNC = "is_sync";


    //TABLE CREATING STRING
    private static final String CREATE_REJECTED_ISSUE_DETAILS_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_REJECTED_ISSUE_DETAILS + " ("
            + RPID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RPID_ITEMISSUE_NO + " TEXT, "
            + RPID_ITEM_ISSUED_DATE + " TEXT, "
            + RPID_ITEM_ISSUE_REMARK + " TEXT, "
            + RPID_FROM_LOCATION_TYPE + " TEXT, "
            + RPID_FROM_LOCATION + " TEXT, "
            + RPID_ITEM_ISSUE_STATUS + " TEXT, "
            + RPID_STOCK_TYPE + " TEXT, "
            + RPID_TOTAL_UNITS + " TEXT, "
            + RPID_IS_SYNC + " TEXT); ";


    //#################### COMPETED JOB ######################
    // TABLE
    public static final String TABLE_COMPETED_JOB = "completed_job_tbl";
    // TABLE ATTRIBUTES
    public static final String COMPLETED_JOB_ID = "id";
    public static final String COMPLETED_JOB_REQUESTED_ID = "requested_id";
    public static final String COMPLETED_JOB_REQUEST_TO_REFID = "requested_to_ref_id";
    public static final String COMPLETED_JOB_IS_MATERIAL_REQUEST = "is_material_requested";
    public static final String COMPLETED_JOB_ACTION_TAKEN = "action_taken";
    public static final String COMPLETED_JOB_DATE = "date";
    public static final String COMPLETED_JOB_LATITUDE = "job_latitude";
    public static final String COMPLETED_JOB_LONGITUTE = "job_logitute";
    public static final String COMPLETED_JOB_REMARK = "job_remark";

    public static final String COMPLETED_JOB_IS_SYNC = "is_sync";
    public static final String COMPLETED_JOB_VISIT_LOG_IS_SYNC = "visit_log_is_sync";

    //TABLE CREATING STRING
    private static final String CREATE_COMPETED_JOB_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_COMPETED_JOB + " ("
            + COMPLETED_JOB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COMPLETED_JOB_REQUESTED_ID + " TEXT, "
            + COMPLETED_JOB_REQUEST_TO_REFID + " TEXT, "
            + COMPLETED_JOB_IS_MATERIAL_REQUEST + " TEXT, "
            + COMPLETED_JOB_ACTION_TAKEN + " TEXT, "
            + COMPLETED_JOB_DATE + " TEXT, "
            + COMPLETED_JOB_LATITUDE + " TEXT, "
            + COMPLETED_JOB_LONGITUTE + " TEXT, "
            + COMPLETED_JOB_REMARK + " TEXT, "
            + COMPLETED_JOB_VISIT_LOG_IS_SYNC + " TEXT, "
            + COMPLETED_JOB_IS_SYNC + " TEXT); ";


    //#################### COMPETED JOB ######################
    // TABLE
    public static final String TABLE_MATERIAL_ISSUED = "material_issued_tbl";
    // TABLE ATTRIBUTES
    public static final String MATERIAL_ISSUED_ID = "id";
    public static final String MATERIAL_ISSUED_PIM_ID = "PIM_ID";
    public static final String MATERIAL_ISSUED_ITEM_NO = "item_no";
    public static final String MATERIAL_ISSUED_REQUESTED_ID = "requested_id";
    public static final String MATERIAL_ISSUED_ISSUE_NO = "issue_no";
    public static final String MATERIAL_ISSUED_GIVEN_IMEI_ESN = "given_imei_esn";
    public static final String MATERIAL_ISSUED_RETURN_IMEI_ESN = "return_imei_esn";
    public static final String MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN = "entered_return_imei_esn";
    public static final String MATERIAL_ISSUED_RETURN_STATUS = "return_status";
    public static final String MATERIAL_ISSUED_IS_ASR = "is_asr";
    public static final String MATERIAL_ISSUED_IS_RETURN = "is_return";
    public static final String MATERIAL_ISSUED_TYPE = "type";
    public static final String MATERIAL_ISSUED_TYPE_OTHER = "type_other";
    public static final String MATERIAL_ISSUED_EXISTING_ITEM_CODE = "existing_item_code";
    public static final String MATERIAL_ISSUED_DATE = "date";
    public static final String MATERIAL_ISSUED_IS_SYNC = "is_sync";

    //TABLE CREATING STRING
    private static final String CREATE_MATERIAL_ISSUED_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MATERIAL_ISSUED + " ("
            + MATERIAL_ISSUED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MATERIAL_ISSUED_PIM_ID + " TEXT, "
            + MATERIAL_ISSUED_ITEM_NO + " TEXT, "
            + MATERIAL_ISSUED_REQUESTED_ID + " TEXT, "
            + MATERIAL_ISSUED_ISSUE_NO + " TEXT, "
            + MATERIAL_ISSUED_GIVEN_IMEI_ESN + " TEXT, "
            + MATERIAL_ISSUED_RETURN_IMEI_ESN + " TEXT, "
            + MATERIAL_ISSUED_ENTERED_RETURN_IMEI_ESN + " TEXT, "
            + MATERIAL_ISSUED_RETURN_STATUS + " TEXT, "
            + MATERIAL_ISSUED_IS_ASR + " TEXT, "
            + MATERIAL_ISSUED_IS_RETURN + " TEXT, "
            + MATERIAL_ISSUED_TYPE + " TEXT, "
            + MATERIAL_ISSUED_TYPE_OTHER + " TEXT, "
            + MATERIAL_ISSUED_EXISTING_ITEM_CODE + " TEXT, "
            + MATERIAL_ISSUED_DATE + " TEXT, "
            + MATERIAL_ISSUED_IS_SYNC + " TEXT); ";


    //#################### ACTION TAKEN ######################
    // TABLE
    public static final String TABLE_ACTION_TAKEN = "action_taken_tbl";
    // TABLE ATTRIBUTES
    public static final String ACTION_TAKEN_ID = "id";
    public static final String ACTION_TAKEN_CODE = "action_taken_code";
    public static final String ACTION_DESCRIPTION = "action_description";


    //TABLE CREATING STRING
    private static final String CREATE_ACTION_TAKEN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTION_TAKEN + " ("
            + ACTION_TAKEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACTION_TAKEN_CODE + " TEXT, "
            + ACTION_DESCRIPTION + " TEXT); ";


    //#################### CUSTOMER PRIORITY ######################
    // TABLE
    public static final String TABLE_CUSTOMER_PRIORITY = "customer_priority_tbl";
    // TABLE ATTRIBUTES
    public static final String CP_ID = "id";
    public static final String CP_CUSTOMERSTAT = "CustomerStat";
    public static final String CP_STAT_LEVEL = "StatLevel";
    public static final String CP_STAT_LEVEL_TIME = "StatLevelTime";


    //TABLE CREATING STRING
    private static final String CREATE_CUSTOMER_PRIORITY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_PRIORITY + " ("
            + CP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CP_CUSTOMERSTAT + " TEXT, "
            + CP_STAT_LEVEL + " TEXT, "
            + CP_STAT_LEVEL_TIME + " TEXT); ";

    //#################### USER PROFILE ######################
    // TABLE
    public static final String TABLE_USER_PROFILE = "UserProfile";
    // TABLE ATTRIBUTES
    public static final String U_PWORD = "pword";
    public static final String U_EPF = "epf";
    public static final String U_EMP_NO = "emp";
    public static final String U_SIM_NO = "simNo";
    public static final String U_CORDINATOR_NUMBER = "CordinatorNumber";

    //TABLE CREATING STRING
    private static final String CREATE_CUSTOMER_USER_PROFILE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PROFILE + " ("
            + U_EPF + " TEXT PRIMARY KEY, " // core app
            + U_PWORD + " TEXT, "
            + U_CORDINATOR_NUMBER + " TEXT, "
            + U_EMP_NO + " TEXT, " //fault app
            + U_SIM_NO + " TEXT); ";

    //#################### LOCATION ######################
    // TABLE
    public static final String TABLE_USER_LOCATION = "UserLocation";
    // TABLE ATTRIBUTES
    public static final String L_LATITUDE = "latitude";
    public static final String L_LONGITUTE = "longitute";
    public static final String L_EMP_NO = "empNo";
    public static final String L_ID = "Lid";

    //TABLE CREATING STRING
    private static final String CREATE_USER_LOCATION = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_LOCATION + " ("
            + L_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + L_EMP_NO + " TEXT, "
            + L_LATITUDE + " DOUBLE, "
            + L_LONGITUTE + " DOUBLE); ";


    //#################### LOCATION ######################
    // TABLE
    public static final String TABLE_TIME_CAP = "TimeCap_time";
    // TABLE ATTRIBUTES
    public static final String T_ID = "time_id";
    public static final String T_RAW_NAME = "time_raw_name";
    public static final String T_DATE = "time_date";

    //TABLE CREATING STRING
    private static final String CREATE_TIME_CAP = "CREATE TABLE IF NOT EXISTS " + TABLE_TIME_CAP + " ("
            + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + T_RAW_NAME + " TEXT, "
            + T_DATE + " TEXT); ";

    //#################### UNIT IN HAND ######################
    // TABLE
    public static final String TABLE_UNIT_IN_HAND = "UnitInHand";
    // TABLE ATTRIBUTES


    public static final String Un_ID = "Un_Id";
    public static final String Un_SerialNo = "Un_SerialNo";
    public static final String Un_ItemCode = "Un_ItemCode";
    public static final String Un_ItemCategory = "Un_ItemCategory";
    public static final String Un_ItemDescription = "Un_ItemDescription";

    //TABLE CREATING STRING
    private static final String CREATE_UNIT_IN_HAND = "CREATE TABLE IF NOT EXISTS " + TABLE_UNIT_IN_HAND + " ("
            + Un_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Un_SerialNo + " TEXT, "
            + Un_ItemCode + " TEXT, "
            + Un_ItemCategory + " TEXT, "
            + Un_ItemDescription + " TEXT); ";

    //#################### BI ITEMS ######################
    // TABLE

    public static final String TABLE_BI_ITEMS = "BI_ITEMS_TBL";
    // TABLE ATTRIBUTES
    public static final String BI_ITEM_ID = "BI_ID";
    public static final String BI_SERVICE_CODE = "BI_SERVICE_CODE";
    public static final String BI_SERVICE_NAME = "BI_SERVICE_NAME";

    //TABLE CREATING STRING
    private static final String CREATE_TABLE_BI_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_BI_ITEMS + " ("
            + BI_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BI_SERVICE_CODE + " TEXT, "
            + BI_SERVICE_NAME + " TEXT); ";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PENDING_FAULTS_TABLE);
        db.execSQL(CREATE_ACCEPTED_FAULT_TABLE);
        db.execSQL(CREATE_REJECT_FAULT_TABLE);
        db.execSQL(CREATE_ISSUE_DETAILS_TABLE);
        db.execSQL(CREATE_PENDING_ISSUED_MATERIAL_TABLE);
        db.execSQL(CREATE_ACCEPTED_ISSUE_DETAILS_TABLE);
        db.execSQL(CREATE_REJECTED_ISSUE_DETAILS_TABLE);
        db.execSQL(CREATE_COMPETED_JOB_TABLE);
        db.execSQL(CREATE_MATERIAL_ISSUED_TABLE);
        db.execSQL(CREATE_ACTION_TAKEN_TABLE);
        db.execSQL(CREATE_CUSTOMER_PRIORITY_TABLE);
        db.execSQL(CREATE_CUSTOMER_USER_PROFILE);
        db.execSQL(CREATE_USER_LOCATION);
        db.execSQL(CREATE_TIME_CAP);
        db.execSQL(CREATE_UNIT_IN_HAND);
        db.execSQL(CREATE_TABLE_BI_ITEMS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }


}
