package com.bsep.PKI.repository;

import com.bsep.PKI.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}
