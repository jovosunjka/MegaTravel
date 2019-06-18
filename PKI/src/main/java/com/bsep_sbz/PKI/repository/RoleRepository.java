package com.bsep_sbz.PKI.repository;

import com.bsep_sbz.PKI.model.authentication_and_authorization_entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByName(String name);
}
