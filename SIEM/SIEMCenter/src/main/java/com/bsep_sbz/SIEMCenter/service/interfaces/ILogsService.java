package com.bsep_sbz.SIEMCenter.service.interfaces;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import java.util.*;

public interface ILogsService {
    void save(List<Log> logs);
}
