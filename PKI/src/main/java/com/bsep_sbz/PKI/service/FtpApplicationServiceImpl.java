package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.model.FtpApplication;
import com.bsep_sbz.PKI.repository.FtpApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FtpApplicationServiceImpl implements FtpApplicationService {

    @Autowired
    private FtpApplicationRepository ftpApplicationRepository;

    @Override
    public FtpApplication getFtpApplication(String organizationalUnitName) {
        return ftpApplicationRepository.findByOrganizationalUnitName(organizationalUnitName);
    }

    @Override
    public FtpApplication getFtpApplication(Long id) {
        return ftpApplicationRepository.findById(id).get();
    }
}
