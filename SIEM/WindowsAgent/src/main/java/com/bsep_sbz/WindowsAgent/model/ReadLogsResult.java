package com.bsep_sbz.WindowsAgent.model;

import java.util.ArrayList;
import java.util.List;

public class ReadLogsResult {
    private List<String> logs;
    private long numberOfCharactersOrBytesRead;

    public ReadLogsResult() {

    }

    public ReadLogsResult(List<String> logs, long numberOfCharactersOrBytesRead) {
        this.logs = logs;
        this.numberOfCharactersOrBytesRead = numberOfCharactersOrBytesRead;
    }

    public ReadLogsResult(long numberOfCharactersOrBytesRead) {
        this.logs = new ArrayList<>();
        this.numberOfCharactersOrBytesRead = numberOfCharactersOrBytesRead;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public long getNumberOfCharactersOrBytesRead() {
        return numberOfCharactersOrBytesRead;
    }

    public void setNumberOfCharactersOrBytesRead(long numberOfCharactersOrBytesRead) {
        this.numberOfCharactersOrBytesRead = numberOfCharactersOrBytesRead;
    }
}
