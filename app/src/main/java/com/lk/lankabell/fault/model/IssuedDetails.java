package com.lk.lankabell.fault.model;

public class IssuedDetails {


    private String PID_ID ;
    private String PID_ITEMISSUE_NO ;
    private String PID_ITEM_ISSUED_DATE ;
    private String PID_ITEM_ISSUE_REMARK ;
    private String PID_FROM_LOCATION_TYPE ;
    private String PID_FROM_LOCATION ;
    private String PID_ITEM_ISSUE_STATUS ;
    private String PID_STOCK_TYPE ;
    private String PID_TOTAL_UNITS ;

    public String getPID_ID() {
        return PID_ID;
    }

    public void setPID_ID(String PID_ID) {
        this.PID_ID = PID_ID;
    }

    public String getPID_ITEMISSUE_NO() {
        return PID_ITEMISSUE_NO;
    }

    public void setPID_ITEMISSUE_NO(String PID_ITEMISSUE_NO) {
        this.PID_ITEMISSUE_NO = PID_ITEMISSUE_NO;
    }

    public String getPID_ITEM_ISSUED_DATE() {
        return PID_ITEM_ISSUED_DATE;
    }

    public void setPID_ITEM_ISSUED_DATE(String PID_ITEM_ISSUED_DATE) {
        this.PID_ITEM_ISSUED_DATE = PID_ITEM_ISSUED_DATE;
    }

    public String getPID_ITEM_ISSUE_REMARK() {
        return PID_ITEM_ISSUE_REMARK;
    }

    public void setPID_ITEM_ISSUE_REMARK(String PID_ITEM_ISSUE_REMARK) {
        this.PID_ITEM_ISSUE_REMARK = PID_ITEM_ISSUE_REMARK;
    }

    public String getPID_FROM_LOCATION_TYPE() {
        return PID_FROM_LOCATION_TYPE;
    }

    public void setPID_FROM_LOCATION_TYPE(String PID_FROM_LOCATION_TYPE) {
        this.PID_FROM_LOCATION_TYPE = PID_FROM_LOCATION_TYPE;
    }

    public String getPID_FROM_LOCATION() {
        return PID_FROM_LOCATION;
    }

    public void setPID_FROM_LOCATION(String PID_FROM_LOCATION) {
        this.PID_FROM_LOCATION = PID_FROM_LOCATION;
    }

    public String getPID_ITEM_ISSUE_STATUS() {
        return PID_ITEM_ISSUE_STATUS;
    }

    public void setPID_ITEM_ISSUE_STATUS(String PID_ITEM_ISSUE_STATUS) {
        this.PID_ITEM_ISSUE_STATUS = PID_ITEM_ISSUE_STATUS;
    }

    public String getPID_STOCK_TYPE() {
        return PID_STOCK_TYPE;
    }

    public void setPID_STOCK_TYPE(String PID_STOCK_TYPE) {
        this.PID_STOCK_TYPE = PID_STOCK_TYPE;
    }

    public String getPID_TOTAL_UNITS() {
        return PID_TOTAL_UNITS;
    }

    public void setPID_TOTAL_UNITS(String PID_TOTAL_UNITS) {
        this.PID_TOTAL_UNITS = PID_TOTAL_UNITS;
    }

    @Override
    public String toString() {
        return "IssuedDetails{" +
                "PID_ID='" + PID_ID + '\'' +
                ", PID_ITEMISSUE_NO='" + PID_ITEMISSUE_NO + '\'' +
                ", PID_ITEM_ISSUED_DATE='" + PID_ITEM_ISSUED_DATE + '\'' +
                ", PID_ITEM_ISSUE_REMARK='" + PID_ITEM_ISSUE_REMARK + '\'' +
                ", PID_FROM_LOCATION_TYPE='" + PID_FROM_LOCATION_TYPE + '\'' +
                ", PID_FROM_LOCATION='" + PID_FROM_LOCATION + '\'' +
                ", PID_ITEM_ISSUE_STATUS='" + PID_ITEM_ISSUE_STATUS + '\'' +
                ", PID_STOCK_TYPE='" + PID_STOCK_TYPE + '\'' +
                ", PID_TOTAL_UNITS='" + PID_TOTAL_UNITS + '\'' +
                '}';
    }
}
