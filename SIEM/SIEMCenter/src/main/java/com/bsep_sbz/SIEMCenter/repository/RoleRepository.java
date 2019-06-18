
package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.RoleEntity;
import org.springframework.data.repository.Repository;


public interface RoleRepository extends Repository<RoleEntity, Long> {
	
	RoleEntity save(RoleEntity role);
	List<RoleEntity> findAll();
	RoleEntity findById(Long id);
	RoleEntity findByName(String name);
	


}
