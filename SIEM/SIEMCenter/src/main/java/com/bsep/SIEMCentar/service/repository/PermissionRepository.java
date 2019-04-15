package com.bsep.SIEMCentar.service.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.bsep.SIEMCenter.model.Permission;

public interface PermissionRepository extends Repository<Permission, Long> {
	
	Permission savePermission(Permission permission);
	List<Permission> findAll();
	Permission findById(Long id);
	Permission findBypermissionName(String name);
	


}
