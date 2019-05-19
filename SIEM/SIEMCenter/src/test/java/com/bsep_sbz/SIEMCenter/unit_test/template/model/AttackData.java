package com.bsep_sbz.SIEMCenter.unit_test.template.model;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;

public class AttackData {
    private String category;
    private int logCount;
    private String time;

    public AttackData(String category, int logCount, String time) {
        this.category = category;
        this.logCount = logCount;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLogCount() {
        return logCount;
    }

    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
