package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.helper.Constants;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.repository.LogsRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LogsService implements ILogsService
{
    @Autowired
    private LogsRepository logsRepository;

    public void save(List<Log> logs) {
        logs.forEach(logsRepository::save);
    }

    @Override
    public Page<Log> getRange(Pageable pageable) {
        return logsRepository.findAll(pageable);
    }

    @Override
    public Page<Log> getFilteredLogs(String column, String regExp, Pageable pageable) {
        if(regExp == null) {
            return null;
        }
        if(regExp.isEmpty()) {
            return null;
        }
        if(pageable.getPageSize() < 1) {
            return null;
        }
        if(pageable.getPageNumber() < 0) {
            return null;
        }

        switch (column) {
            case Constants.id:
                return logsRepository.findByIdRegexAndPagination(regExp, pageable);
            case Constants.type:
                return logsRepository.findByTypeRegexAndPagination(regExp, pageable);
            case Constants.category:
                return logsRepository.findByCategoryRegexAndPagination(regExp, pageable);
            case Constants.source:
                return logsRepository.findBySourceRegexAndPagination(regExp, pageable);
            case Constants.timestamp:
                return logsRepository.findByTimestampRegexAndPagination(regExp, pageable);
            case Constants.host_address:
                return logsRepository.findByHostAddressRegexAndPagination(regExp, pageable);
            case Constants.message:
                return logsRepository.findByMessageRegexAndPagination(regExp, pageable);
            default:
                return null;
        }
    }
}
