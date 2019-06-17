package com.bsep_sbz.SIEMCenter.model.sbz.rule;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;

public class LoginData {
    private int loginAttemptCount;
    private int timeCount;
    private String timeUnit; // s, m, h, d, M, y
    private String hostRelation;
    private String sourceRelation;
    private String type;
    private String loginTemplateNumber;

    public LoginData(int loginAttemptCount, int timeCount, String timeUnit, String hostRelation, String sourceRelation,
                     boolean loginSuccess, int loginTemplateNumber) {
        this.loginAttemptCount = loginAttemptCount;
        this.timeCount = timeCount;
        this.timeUnit = timeUnit;
        this.hostRelation = hostRelation;
        this.sourceRelation = sourceRelation;
        if(loginSuccess) {
            type = LogLevel.INFO.name();
        } else {
            type = LogLevel.WARN.name();
        }
        this.loginTemplateNumber = (new Integer(loginTemplateNumber)).toString();
    }

    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    public void setLoginAttemptCount(int loginAttemptCount) {
        this.loginAttemptCount = loginAttemptCount;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLoginTemplateNumber() {
        return loginTemplateNumber;
    }

    public void setLoginTemplateNumber(String loginTemplateNumber) {
        this.loginTemplateNumber = loginTemplateNumber;
    }
}
