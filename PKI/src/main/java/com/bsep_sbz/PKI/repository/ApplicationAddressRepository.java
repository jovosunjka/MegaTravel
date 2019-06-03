package com.bsep_sbz.PKI.repository;

import com.bsep_sbz.PKI.model.ApplicationAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationAddressRepository extends JpaRepository<ApplicationAddress, Long> {

    ApplicationAddress findByOrganizationalUnitName(String rganizationalUnitName);
}
