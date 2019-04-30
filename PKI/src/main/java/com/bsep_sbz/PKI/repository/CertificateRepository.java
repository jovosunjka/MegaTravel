package com.bsep_sbz.PKI.repository;

import com.bsep_sbz.PKI.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}
