package com.lk.lankabell.fault.model;

public class UnitInHand {
    /*{
	"itemCode": "LTCUHU020000",
	"itemDescription": "LTE CPE WITH ONE LAN AND VOICE PORT WITH WIFI-WITH TWO 1DBI",
	"itemCategory": "LTEU",
	"serial": "866855023706351"
}*/
    public String ItemCode;

    public String itemDescription;

    public String itemCategory;

    public String Serial;



    public UnitInHand() {
    }

    public UnitInHand(String serial, String itemCode, String itemCategory, String itemDescription,  String itemType) {
        Serial = serial;
        ItemCode = itemCode;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;

    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
