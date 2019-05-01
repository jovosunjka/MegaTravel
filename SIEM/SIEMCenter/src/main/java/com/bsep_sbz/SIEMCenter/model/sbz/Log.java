package com.bsep_sbz.SIEMCenter.model.sbz;


import com.bsep_sbz.SIEMCenter.model.sbz.enums.LogLevel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Log {
    private Long id;
    private LogLevel type;
    private Date timestamp;
    // username is read from log, other data is loaded from db
    private UserAccount userAccount;
    private String hostAddress;
    private Message message;
    // used for antivirus logs
    private Log relatedLog;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");

    public Log() {

    }

    public Log(Long id, LogLevel type, String timestampStr, String hostAddress, Message message) {
        this.id = id;
        this.type = type;
        try {
            this.timestamp = sdf.parse(timestampStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.hostAddress = hostAddress;
        this.message = message;
    }

    public static long getDaysOfInactivity(long d1Long, long d2Long) {
        Date d1 = new Date(d1Long);
        long diffInMillies = Math.abs(d2Long - d1Long);
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        System.out.println("getDaysOfInactivity(): " + diff);
        return diff;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogLevel getType() {
        return type;
    }

    public void setType(LogLevel type) {
        this.type = type;
    }

    /*public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    */

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return id.equals(log.id) &&
                type == log.type &&
                timestamp.equals(log.timestamp) &&
                hostAddress.equals(log.hostAddress) &&
                message.equals(log.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, timestamp, hostAddress, message);
    }
}
