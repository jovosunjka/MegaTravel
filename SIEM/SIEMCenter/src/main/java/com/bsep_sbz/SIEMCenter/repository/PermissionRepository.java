package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.PermissionEntity;
import org.springframework.data.repository.Repository;

public interface PermissionRepository extends Repository<PermissionEntity, Long> {

	PermissionEntity save(PermissionEntity permission);
	List<PermissionEntity> findAll();
	PermissionEntity findById(Long id);
	PermissionEntity findByPermissionName(String permissionName);
	


}
