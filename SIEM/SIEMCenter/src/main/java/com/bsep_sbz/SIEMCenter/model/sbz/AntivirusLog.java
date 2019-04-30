package com.bsep_sbz.SIEMCenter.model.sbz;


import com.bsep_sbz.SIEMCenter.model.sbz.enums.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.LogLevel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class AntivirusLog extends Log {
    private AntivirusLog relatedLog;

    public static long diffrenceInHours(LocalDateTime ld1, LocalDateTime ld2) {
        long difference = ChronoUnit.HOURS.between(ld1, ld2);
        System.out.println(difference);
        return difference;
    }

    public AntivirusLog() {

    }

    public AntivirusLog(AntivirusLog relatedLog) {
        this.relatedLog = relatedLog;
    }

    public AntivirusLog(Long id, LogLevel type, LogCategory category, String timestampStr, String source, String hostAddress, String message, AntivirusLog relatedLog) {
        super(id, type, category, timestampStr, source, hostAddress, message);
        this.relatedLog = relatedLog;
    }

    public AntivirusLog getRelatedLog() {
        return relatedLog;
    }

    public void setRelatedLog(AntivirusLog relatedLog) {
        this.relatedLog = relatedLog;
    }
}
