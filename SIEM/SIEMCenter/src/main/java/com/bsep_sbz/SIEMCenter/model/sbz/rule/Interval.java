package com.bsep_sbz.SIEMCenter.model.sbz.rule;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.rule.IntervalType;

public class Interval
{
    private int count;
    private IntervalType type;

    public Interval(int count, IntervalType type) {
        this.count = count;
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public IntervalType getType() {
        return type;
    }

    public void setType(IntervalType type) {
        this.type = type;
    }
}
