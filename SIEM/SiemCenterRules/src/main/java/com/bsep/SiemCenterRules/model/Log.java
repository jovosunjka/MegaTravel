package com.bsep.SiemCenterRules.model;

import com.bsep.SiemCenterRules.model.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Log {
    protected Long id;
    protected LogLevel logLevel;
    protected LocalDateTime timestamp;
    protected String hostAddress;

    public Log() {

    }

    public Log(Long id, LogLevel logLevel, LocalDateTime timestamp, String hostAddress) {
        this.id = id;
        this.logLevel = logLevel;
        this.timestamp = timestamp;
        this.hostAddress = hostAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id) &&
                logLevel == log.logLevel &&
                Objects.equals(timestamp, log.timestamp) &&
                Objects.equals(hostAddress, log.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, logLevel, timestamp, hostAddress);
    }
}
