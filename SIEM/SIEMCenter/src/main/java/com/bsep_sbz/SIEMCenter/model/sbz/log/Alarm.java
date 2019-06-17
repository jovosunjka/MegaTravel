package com.bsep_sbz.SIEMCenter.model.sbz.log;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Alarm {
    @Transient
    private static Long sequencer = 0L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "priority")
    private int priority;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Log> logs;

    @Column(name = "alarm_producer_Type")
    private LogCategory alarmProducerType;

    @Transient
    private boolean isRetrievedFromSession;

    public Alarm() {
        id = ++sequencer;
        logs = new ArrayList<>();
        timestamp = LocalDateTime.now();
        isRetrievedFromSession = false;
    }

    public Alarm(Long id, String message, LocalDateTime timestamp, List<Log> logs) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.logs = logs;
        this.isRetrievedFromSession = false;
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

    public boolean getIsRetrievedFromSession() {
        return isRetrievedFromSession;
    }

    public void setIsRetrievedFromSession(boolean retrievedFromSession) {
        isRetrievedFromSession = retrievedFromSession;
    }

    public LogCategory getAlarmProducerType() {
        return alarmProducerType;
    }

    public void setAlarmProducerType(LogCategory alarmProducerType) {
        this.alarmProducerType = alarmProducerType;
    }
}
