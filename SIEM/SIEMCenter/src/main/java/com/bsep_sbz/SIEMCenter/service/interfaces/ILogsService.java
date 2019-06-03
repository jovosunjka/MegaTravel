package com.bsep_sbz.SIEMCenter.service.interfaces;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface ILogsService {
    void save(List<Log> logs);

    Page<Log> getRange(Pageable pageable);

    Page<Log> getFilteredLogs(String column, String regExp, Pageable pageable);
}
