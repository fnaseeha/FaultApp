package com.lk.lankabell.fault.model;

public class BiItems {

    private String BI_ITEM_ID;
    private String BI_SERVICE_CODE;
    private String BI_SERVICE_NAME;

    public String getBI_ITEM_ID() {
        return BI_ITEM_ID;
    }

    public void setBI_ITEM_ID(String BI_ITEM_ID) {
        this.BI_ITEM_ID = BI_ITEM_ID;
    }

    public String getBI_SERVICE_CODE() {
        return BI_SERVICE_CODE;
    }

    public void setBI_SERVICE_CODE(String BI_SERVICE_CODE) {
        this.BI_SERVICE_CODE = BI_SERVICE_CODE;
    }

    public String getBI_SERVICE_NAME() {
        return BI_SERVICE_NAME;
    }

    public void setBI_SERVICE_NAME(String BI_SERVICE_NAME) {
        this.BI_SERVICE_NAME = BI_SERVICE_NAME;
    }

    public BiItems() {
    }

    public BiItems(String BI_ITEM_ID, String BI_SERVICE_CODE, String SERVICE_NAME) {
        this.BI_ITEM_ID = BI_ITEM_ID;
        this.BI_SERVICE_CODE = BI_SERVICE_CODE;
        this.BI_SERVICE_NAME = SERVICE_NAME;
    }

    @Override
    public String toString() {
        return "BiItems{" +
                "BI_ITEM_ID='" + BI_ITEM_ID + '\'' +
                ", BI_SERVICE_CODE='" + BI_SERVICE_CODE + '\'' +
                ", BI_SERVICE_NAME='" + BI_SERVICE_NAME + '\'' +
                '}';
    }
}
