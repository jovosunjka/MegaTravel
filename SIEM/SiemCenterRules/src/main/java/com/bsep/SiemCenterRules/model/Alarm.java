package com.bsep.SiemCenterRules.model;

import java.time.LocalDateTime;
import java.util.List;

public class Alarm {
    private Long id;
    private String message;
    private int priority;
    private LocalDateTime dateTime;
    private List<Log> logs;

    public Alarm() {

    }

    public Alarm(Long id, String message, LocalDateTime dateTime, List<Log> logs) {
        this.id = id;
        this.message = message;
        this.dateTime = dateTime;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }
}
