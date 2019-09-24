package com.lk.lankabell.fault.model;

public class IssuedMaterialDetails {
    @Override
    public String toString() {
        return "IssuedMaterialDetails{" +
                "sID='" + sID + '\'' +
                ", IssueNo='" + IssueNo + '\'' +
                ", IssueType='" + IssueType + '\'' +
                ", ItemType='" + ItemType + '\'' +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItemDescription='" + ItemDescription + '\'' +
                ", IssuedQty='" + IssuedQty + '\'' +
                ", ItemCategory='" + ItemCategory + '\'' +
                ", Serial='" + Serial + '\'' +
                ", ExistingItemCode='" + ExistingItemCode + '\'' +
                '}';
    }

    private String sID;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    private String IssueNo;
    private String IssueType;
    private String ItemType;
    private String ItemCode;
    private String ItemDescription;
    private String IssuedQty;
    private String ItemCategory;
    private String Serial;
    private String ExistingItemCode;

    public String getIssueNo() {
        return IssueNo;
    }

    public void setIssueNo(String issueNo) {
        IssueNo = issueNo;
    }

    public String getIssueType() {
        return IssueType;
    }

    public void setIssueType(String issueType) {
        IssueType = issueType;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public String getIssuedQty() {
        return IssuedQty;
    }

    public void setIssuedQty(String issuedQty) {
        IssuedQty = issuedQty;
    }

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getExistingItemCode() {
        return ExistingItemCode;
    }

    public void setExistingItemCode(String existingItemCode) {
        ExistingItemCode = existingItemCode;
    }
}
