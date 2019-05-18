package com.bsep_sbz.SIEMCenter.model.sbz.rule;

public class Condition
{
    private Field field;
    private Occurrence occurrence;
    private Interval interval;

    public Condition(Field field, Occurrence occurrence, Interval interval) {
        this.field = field;
        this.occurrence = occurrence;
        this.interval = interval;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Occurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Occurrence occurrence) {
        this.occurrence = occurrence;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
