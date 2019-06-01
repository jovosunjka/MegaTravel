package com.bsep_sbz.PKI.repository;

import com.bsep_sbz.PKI.model.FtpApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FtpApplicationRepository extends JpaRepository<FtpApplication, Long> {

    FtpApplication findByOrganizationalUnitName(String organizationalUnitName);
}
