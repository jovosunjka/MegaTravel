package com.bsep_sbz.SIEMCenter.model.sbz;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AntivirusMessage extends Message
{
    private String threat;
    private boolean isSolved;

    public static long diffrenceInHours(LocalDateTime ld1, LocalDateTime ld2) {
        long difference = ChronoUnit.HOURS.between(ld1, ld2);
        System.out.println(difference);
        return difference;
    }

    public String getThreat() {
        return threat;
    }

    public void setThreat(String threat) {
        this.threat = threat;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
}
