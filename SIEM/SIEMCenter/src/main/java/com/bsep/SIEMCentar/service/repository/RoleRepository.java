
package com.bsep.SIEMCentar.service.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.bsep.SIEMCenter.model.Role;


public interface RoleRepository extends Repository<Role, Long> {
	
	Role saveRole(Role role);
	List<Role> findAll();
	Role findById(Long id);
	Role findByName(String name);
	


}
