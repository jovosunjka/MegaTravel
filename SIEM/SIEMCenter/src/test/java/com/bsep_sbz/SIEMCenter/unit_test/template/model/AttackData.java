package com.bsep_sbz.SIEMCenter.unit_test.template.model;

public class AttackData {
    private String category;
    private int loginAttemptCount;
    private String time;

    public AttackData(String category, int loginAttemptCount, String time) {
        this.category = category;
        this.loginAttemptCount = loginAttemptCount;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    public void setLoginAttemptCount(int loginAttemptCount) {
        this.loginAttemptCount = loginAttemptCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
