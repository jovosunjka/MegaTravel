package com.bsep_sbz.SIEMCenter.unit_test.template.model;

public class LoginData
{
    private int logCount;
    private int timeCount;
    private String timeUnit; // s, m, h, d, M, y
    private String hostRelation;
    private String sourceRelation;
    private String loginSuccess;

    public LoginData(int logCount, int timeCount, String timeUnit, String hostRelation, String sourceRelation, String loginSuccess) {
        this.logCount = logCount;
        this.timeCount = timeCount;
        this.timeUnit = timeUnit;
        this.hostRelation = hostRelation;
        this.sourceRelation = sourceRelation;
        this.loginSuccess = loginSuccess;
    }

    public int getLogCount() {
        return logCount;
    }

    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getHostRelation() {
        return hostRelation;
    }

    public void setHostRelation(String hostRelation) {
        this.hostRelation = hostRelation;
    }

    public String getSourceRelation() {
        return sourceRelation;
    }

    public void setSourceRelation(String sourceRelation) {
        this.sourceRelation = sourceRelation;
    }

    public String getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(String loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
}
