package com.bsep_sbz.SIEMCenter.unit_test.template.model;

public class LoginData
{
    private int logCount;
    private int timeCount;
    private String timeUnit; // s, m, h, d, M, y

    public LoginData(int logCount, int timeCount, String timeUnit) {
        this.logCount = logCount;
        this.timeCount = timeCount;
        this.timeUnit = timeUnit;
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
}
