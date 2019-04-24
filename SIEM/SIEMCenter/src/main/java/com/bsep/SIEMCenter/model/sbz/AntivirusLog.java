package com.bsep.SIEMCenter.model.sbz;


import com.bsep.SIEMCenter.model.sbz.enums.LogCategory;
import com.bsep.SIEMCenter.model.sbz.enums.LogLevel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public AntivirusLog(Long id, LogLevel type, LogCategory category, LocalDateTime timestamp, String source, String hostAddress, String message, AntivirusLog relatedLog) {
        super(id, type, category, timestamp, source, hostAddress, message);
        this.relatedLog = relatedLog;
    }

    public AntivirusLog getRelatedLog() {
        return relatedLog;
    }

    public void setRelatedLog(AntivirusLog relatedLog) {
        this.relatedLog = relatedLog;
    }
}
