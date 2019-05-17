package com.bsep_sbz.WindowsAgent.service.interfaces;

import java.io.IOException;
import java.util.List;

public interface ILogsService {
    List<String> logFilter(List<String> logs);
    void sendLogs(List<String> logs) throws IOException;
}
