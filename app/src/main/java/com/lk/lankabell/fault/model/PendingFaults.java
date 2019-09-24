package com.lk.lankabell.fault.model;

import android.support.annotation.NonNull;

public class PendingFaults implements Comparable<PendingFaults>{

    private String PF_ID;
    private String PF_REQUESTID;
    private String PF_REQUESTBATCHID;
    private String PF_REQUESTTOREFID;
    private String PF_REQUESTTONAME;
    private String PF_REQUESTTOCONTACT;
    private String PF_REQUESTTOADD1;
    private String PF_REQUESTTOADD2;
    private String PF_REQUESTTOADD3;
    private String PF_REQUESTASSIGNEDTO;
    private String PF_REQUESTASSIGNEDDATE;
    private String PF_STATUS;
    private String PF_PRIORITY;
    private String PF_REQUESTTYPE;
    private String PF_REQUESTSUBTYPE;
    private String PF_REQUESTCATEGORY;
    private String PF_ISACCEPT;
    private String PF_REQUESTTOLOCATION;
    private String PF_SERVICETYPE;
    private String PF_CUSTOMERCATEGORY;
    private String PF_CUSTOMERRATINGS;
    private String PF_CUSTOMERREMARKS;
    private String PF_DIRECTION;
    private double PF_RESP_TIME;
    private String ACTIVE_SCREEN;
    private String AF_EMPNO;
    private String AF_ACCEPTEDO_DATE;
    private String PF_LTE_NIGHT;
    private String PF_LTE_DAY;

    public String getACTIVE_SCREEN() {
        return ACTIVE_SCREEN;
    }

    public void setACTIVE_SCREEN(String ACTIVE_SCREEN) {
        this.ACTIVE_SCREEN = ACTIVE_SCREEN;
    }



    public String getPF_LTE_NIGHT() {
        return PF_LTE_NIGHT;
    }

    public void setPF_LTE_NIGHT(String PF_LTE_NIGHT) {
        this.PF_LTE_NIGHT = PF_LTE_NIGHT;
    }

    public String getPF_LTE_DAY() {
        return PF_LTE_DAY;
    }

    public void setPF_LTE_DAY(String PF_LTE_DAY) {
        this.PF_LTE_DAY = PF_LTE_DAY;
    }

    public String getAF_EMPNO() {
        return AF_EMPNO;
    }

    public void setAF_EMPNO(String AF_EMPNO) {
        this.AF_EMPNO = AF_EMPNO;
    }

    public String getAF_ACCEPTEDO_DATE() {
        return AF_ACCEPTEDO_DATE;
    }

    public void setAF_ACCEPTEDO_DATE(String AF_ACCEPTEDO_DATE) {
        this.AF_ACCEPTEDO_DATE = AF_ACCEPTEDO_DATE;
    }

    public String getPF_ID() {
        return PF_ID;
    }

    public void setPF_ID(String PF_ID) {
        this.PF_ID = PF_ID;
    }

    public String getPF_REQUESTID() {
        return PF_REQUESTID;
    }

    public void setPF_REQUESTID(String PF_REQUESTID) {
        this.PF_REQUESTID = PF_REQUESTID;
    }

    public String getPF_REQUESTBATCHID() {
        return PF_REQUESTBATCHID;
    }

    public void setPF_REQUESTBATCHID(String PF_REQUESTBATCHID) {
        this.PF_REQUESTBATCHID = PF_REQUESTBATCHID;
    }

    public String getPF_REQUESTTOREFID() {
        return PF_REQUESTTOREFID;
    }

    public void setPF_REQUESTTOREFID(String PF_REQUESTTOREFID) {
        this.PF_REQUESTTOREFID = PF_REQUESTTOREFID;
    }

    public String getPF_REQUESTTONAME() {
        return PF_REQUESTTONAME;
    }

    public void setPF_REQUESTTONAME(String PF_REQUESTTONAME) {
        this.PF_REQUESTTONAME = PF_REQUESTTONAME;
    }

    public String getPF_REQUESTTOCONTACT() {
        return PF_REQUESTTOCONTACT;
    }

    public void setPF_REQUESTTOCONTACT(String PF_REQUESTTOCONTACT) {
        this.PF_REQUESTTOCONTACT = PF_REQUESTTOCONTACT;
    }

    public String getPF_REQUESTTOADD1() {
        return PF_REQUESTTOADD1;
    }

    public void setPF_REQUESTTOADD1(String PF_REQUESTTOADD1) {
        this.PF_REQUESTTOADD1 = PF_REQUESTTOADD1;
    }

    public String getPF_REQUESTTOADD2() {
        return PF_REQUESTTOADD2;
    }

    public void setPF_REQUESTTOADD2(String PF_REQUESTTOADD2) {
        this.PF_REQUESTTOADD2 = PF_REQUESTTOADD2;
    }

    public String getPF_REQUESTTOADD3() {
        return PF_REQUESTTOADD3;
    }

    public void setPF_REQUESTTOADD3(String PF_REQUESTTOADD3) {
        this.PF_REQUESTTOADD3 = PF_REQUESTTOADD3;
    }

    public String getPF_REQUESTASSIGNEDTO() {
        return PF_REQUESTASSIGNEDTO;
    }

    public void setPF_REQUESTASSIGNEDTO(String PF_REQUESTASSIGNEDTO) {
        this.PF_REQUESTASSIGNEDTO = PF_REQUESTASSIGNEDTO;
    }

    public String getPF_REQUESTASSIGNEDDATE() {
        return PF_REQUESTASSIGNEDDATE;
    }

    public void setPF_REQUESTASSIGNEDDATE(String PF_REQUESTASSIGNEDDATE) {
        this.PF_REQUESTASSIGNEDDATE = PF_REQUESTASSIGNEDDATE;
    }

    public String getPF_STATUS() {
        return PF_STATUS;
    }

    public void setPF_STATUS(String PF_STATUS) {
        this.PF_STATUS = PF_STATUS;
    }

    public String getPF_PRIORITY() {
        return PF_PRIORITY;
    }

    public void setPF_PRIORITY(String PF_PRIORITY) {
        this.PF_PRIORITY = PF_PRIORITY;
    }

    public String getPF_REQUESTTYPE() {
        return PF_REQUESTTYPE;
    }

    public void setPF_REQUESTTYPE(String PF_REQUESTTYPE) {
        this.PF_REQUESTTYPE = PF_REQUESTTYPE;
    }

    public String getPF_REQUESTSUBTYPE() {
        return PF_REQUESTSUBTYPE;
    }

    public void setPF_REQUESTSUBTYPE(String PF_REQUESTSUBTYPE) {
        this.PF_REQUESTSUBTYPE = PF_REQUESTSUBTYPE;
    }

    public String getPF_REQUESTCATEGORY() {
        return PF_REQUESTCATEGORY;
    }

    public void setPF_REQUESTCATEGORY(String PF_REQUESTCATEGORY) {
        this.PF_REQUESTCATEGORY = PF_REQUESTCATEGORY;
    }

    public String getPF_ISACCEPT() {
        return PF_ISACCEPT;
    }

    public void setPF_ISACCEPT(String PF_ISACCEPT) {
        this.PF_ISACCEPT = PF_ISACCEPT;
    }

    public String getPF_REQUESTTOLOCATION() {
        return PF_REQUESTTOLOCATION;
    }

    public void setPF_REQUESTTOLOCATION(String PF_REQUESTTOLOCATION) {
        this.PF_REQUESTTOLOCATION = PF_REQUESTTOLOCATION;
    }

    public String getPF_SERVICETYPE() {
        return PF_SERVICETYPE;
    }

    public void setPF_SERVICETYPE(String PF_SERVICETYPE) {
        this.PF_SERVICETYPE = PF_SERVICETYPE;
    }

    public String getPF_CUSTOMERCATEGORY() {
        return PF_CUSTOMERCATEGORY;
    }

    public void setPF_CUSTOMERCATEGORY(String PF_CUSTOMERCATEGORY) {
        this.PF_CUSTOMERCATEGORY = PF_CUSTOMERCATEGORY;
    }

    public String getPF_CUSTOMERRATINGS() {
        return PF_CUSTOMERRATINGS;
    }

    public void setPF_CUSTOMERRATINGS(String PF_CUSTOMERRATINGS) {
        this.PF_CUSTOMERRATINGS = PF_CUSTOMERRATINGS;
    }

    public String getPF_CUSTOMERREMARKS() {
        return PF_CUSTOMERREMARKS;
    }

    public void setPF_CUSTOMERREMARKS(String PF_CUSTOMERREMARKS) {
        this.PF_CUSTOMERREMARKS = PF_CUSTOMERREMARKS;
    }

    public String getPF_DIRECTION() {
        return PF_DIRECTION;
    }

    public void setPF_DIRECTION(String PF_DIRECTION) {
        this.PF_DIRECTION = PF_DIRECTION;
    }

    @Override
    public int compareTo(@NonNull PendingFaults PendingFaults) {
        return this.getPF_RESP_TIME() < PendingFaults.getPF_RESP_TIME() ? -1 : 1;
    }

    public double getPF_RESP_TIME() {
        return PF_RESP_TIME;
    }

    public void setPF_RESP_TIME(double PF_RESP_TIME) {
        this.PF_RESP_TIME = PF_RESP_TIME;
    }
}
