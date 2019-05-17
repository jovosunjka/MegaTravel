package com.bsep_sbz.SIEMCenter.service.interfaces;

import com.bsep_sbz.SIEMCenter.model.sbz.Log;

import java.util.List;

public interface IRuleService {

    void initialize();

    List<Log> makeLogs(List<String> logs);
}
