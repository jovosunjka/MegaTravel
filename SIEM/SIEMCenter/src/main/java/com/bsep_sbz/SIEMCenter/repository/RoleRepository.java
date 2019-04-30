
package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.bsep_sbz.SIEMCenter.model.Role;


public interface RoleRepository extends Repository<Role, Long> {
	
	Role save(Role role);
	List<Role> findAll();
	Role findById(Long id);
	Role findByName(String name);
	


}
