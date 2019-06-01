package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.repository.LogsRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogsService implements ILogsService
{
    @Autowired
    private LogsRepository logsRepository;

    public void save(List<Log> logs) {
        logs.forEach(logsRepository::save);
    }
}
