package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.model.FtpApplication;

public interface FtpApplicationService {

    FtpApplication getFtpApplication(String organizationalUnitName);

    FtpApplication getFtpApplication(Long id);
}
