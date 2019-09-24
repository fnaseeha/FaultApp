package com.lk.lankabell.fault.model;

public class UserProfile {

    private String Pword;
    private String Epf;
    private String Emp;
    private String SimNo;
    private String CordinatorNumber;

 /* + U_EPF + " TEXT PRIMARY KEY, " // core app
            + U_PWORD+ " TEXT, "
            + U_CORDINATOR_NUMBER+ " TEXT, "
            + U_EMP_NO+ " TEXT, " //fault app
            + U_SIM_NO +" TEXT); ";*/

    public String getEmp() {
        return Emp;
    }

    public void setEmp(String emp) {
        Emp = emp;
    }

    public String getPword() {
        return Pword;
    }

    public void setPword(String pword) {
        Pword = pword;
    }

    public String getEpf() {
        return Epf;
    }

    public void setEpf(String epf) {
        Epf = epf;
    }

    public String getSimNo() {
        return SimNo;
    }

    public void setSimNo(String simNo) {
        SimNo = simNo;
    }

    public String getCordinatorNumber() {
        return CordinatorNumber;
    }

    public void setCordinatorNumber(String cordinatorNumber) {
        CordinatorNumber = cordinatorNumber;
    }

}
