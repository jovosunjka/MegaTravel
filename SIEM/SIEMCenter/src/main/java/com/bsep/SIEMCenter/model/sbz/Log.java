package com.bsep.SIEMCenter.model.sbz;


import com.bsep.SIEMCenter.model.sbz.enums.LogCategory;
import com.bsep.SIEMCenter.model.sbz.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.Objects;

public class Log {
    private Long id;
    private LogLevel type;
    private LogCategory category;
    private LocalDateTime timestamp;
    private String source;
    private String hostAddress;
    private String message;

    public Log() {

    }

    public Log(Long id, LogLevel type, LogCategory category, LocalDateTime timestamp, String source, String hostAddress, String message) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.source = source;
        this.hostAddress = hostAddress;
        this.message = message;
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

    public LogCategory getCategory() {
        return category;
    }

    public void setCategory(LogCategory category) {
        this.category = category;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return id.equals(log.id) &&
                type == log.type &&
                category == log.category &&
                timestamp.equals(log.timestamp) &&
                source.equals(log.source) &&
                hostAddress.equals(log.hostAddress) &&
                message.equals(log.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, category, timestamp, source, hostAddress, message);
    }
}
