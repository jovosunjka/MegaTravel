package com.bsep_sbz.SIEMCenter.controller.dto;

public class LoginTemplateDto
{
    private boolean loginSuccess;
    private int loginAttemptCount;
    private String hostRelation;
    private String sourceRelation;
    private int timeCount;
    private String timeUnit;

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    public void setLoginAttemptCount(int loginAttemptCount) {
        this.loginAttemptCount = loginAttemptCount;
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
}
