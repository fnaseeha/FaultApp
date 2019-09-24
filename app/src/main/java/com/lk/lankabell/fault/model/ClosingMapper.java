package com.lk.lankabell.fault.model;

import java.util.ArrayList;

public class ClosingMapper {

    private String empNo;
    private String requestId;
    private String actionCode;
    private String availMaterials;

    private ArrayList<MaterialList> materialList;

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getAvailMaterials() {
        return availMaterials;
    }

    public void setAvailMaterials(String availMaterials) {
        this.availMaterials = availMaterials;
    }

    public ArrayList<MaterialList> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(ArrayList<MaterialList> materialList) {
        this.materialList = materialList;
    }
}
