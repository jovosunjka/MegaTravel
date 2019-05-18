package com.bsep_sbz.SIEMCenter.model.sbz.log;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.AlarmProducerType;

import java.time.LocalDateTime;
import java.util.List;

public class Alarm {
    private Long id;
    // this will be set in rules?
    private String message;
    private int priority;
    private LocalDateTime timestamp;
    private List<Log> logs;
    private AlarmProducerType alarmProducerType;

    public Alarm() {

    }

    public Alarm(Long id, String message, LocalDateTime timestamp, List<Log> logs) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.logs = logs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
