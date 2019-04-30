package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import com.bsep_sbz.SIEMCenter.model.Permission;
import org.springframework.data.repository.Repository;

public interface PermissionRepository extends Repository<Permission, Long> {
	
	Permission save(Permission permission);
	List<Permission> findAll();
	Permission findById(Long id);
	Permission findBypermissionName(String name);
	


}
